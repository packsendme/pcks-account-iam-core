package com.packsendme.microservice.iam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.packsendme.lib.common.constants.HttpExceptionPackSend;
import com.packsendme.lib.common.constants.MicroservicesConstants;
import com.packsendme.lib.common.response.Response;
import com.packsendme.microservice.iam.component.SMSCodeManagement;
import com.packsendme.microservice.iam.controller.AccountClient;
import com.packsendme.microservice.iam.dao.UserDAO;
import com.packsendme.microservice.iam.repository.UserModel;

@Service
@ComponentScan("com.packsendme.microservice.dao")
public class UserService {

	@Autowired
	UserDAO userDAO;
	
	@Autowired
	AccountClient accountCliente;
	
	@Autowired
	SMSCodeManagement smsObj;
	
	public ResponseEntity<?> getUsernameAccessCheck(String username, String usernamenew) {
		UserModel userFind = new UserModel();
		String operation = null;
		try{
			if(usernamenew == null) {
				userFind.setUsername(username);
				operation = MicroservicesConstants.ADD_OP_USERNAME;
			}
			else {
				userFind.setUsername(usernamenew);
				operation = MicroservicesConstants.UPDATE_OP_USERNAME;
			}
				
			userFind = userDAO.find(userFind);
			
			// User does not exist in user base 
			if(userFind == null) {
				userFind = registerOrUpdateUserAccess(username, usernamenew, operation);
				Response<UserModel> responseObj = new Response<UserModel>(HttpExceptionPackSend.USERNAME_REGISTER.value(), HttpExceptionPackSend.USERNAME_REGISTER.getAction(), userFind);
				return new ResponseEntity<>(responseObj, HttpStatus.ACCEPTED);
			}
			// User already exists in the database 
			else {
				Response<UserModel> responseObj = new Response<UserModel>(HttpExceptionPackSend.FOUND_USER.value(), HttpExceptionPackSend.FOUND_USER.getAction(), null);
				return new ResponseEntity<>(responseObj,HttpStatus.FOUND);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			Response<UserModel> responseObj = new Response<UserModel>(HttpExceptionPackSend.FAIL_EXECUTION.value(), HttpExceptionPackSend.FAIL_EXECUTION.getAction(), null);
			return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);		
		}
	}
	
	public ResponseEntity<?> allowUserAccessFirstRegister(String username, String password) {
		UserModel entity = new UserModel(username, null, false, null, false);
		try {
			entity = userDAO.find(entity);
			entity.setPassword(password);
			entity.setActivated(true);
			entity.setActivationKey(MicroservicesConstants.ACTIVATIONKEY_FALSE);
			entity = userDAO.update(entity);
			Response<UserModel> responseObj = new Response<UserModel>(HttpExceptionPackSend.USER_ACCESS_CREATED.value(), HttpExceptionPackSend.USER_ACCESS_CREATED.getAction(), null);
			return new ResponseEntity<>(responseObj, HttpStatus.ACCEPTED);
		}
		catch (Exception e) {
			Response<UserModel> responseObj = new Response<UserModel>(HttpExceptionPackSend.FAIL_EXECUTION.value(), HttpExceptionPackSend.FAIL_EXECUTION.getAction(), null);
			return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<?> allowUserAccessChangeUsername(String username,String usernamenew) {
		UserModel entity = new UserModel(username, null, false, null, false);
		try {
			System.out.println(" allowUserAccessChangeUsername username "+ username);
			System.out.println(" allowUserAccessChangeUsername usernamenew "+ usernamenew);

			entity = userDAO.find(entity);
			
			if(entity != null) {
				System.out.println(" ENTOU IF ");
			}
			entity.setUsername(usernamenew);
			entity.setActivated(true);
			entity.setActivationKey(MicroservicesConstants.ACTIVATIONKEY_FALSE);
			entity = userDAO.update(entity);
			Response<UserModel> responseObj = new Response<UserModel>(HttpExceptionPackSend.USER_ACCESS_CREATED.value(), HttpExceptionPackSend.USER_ACCESS_CREATED.getAction(), null);
			return new ResponseEntity<>(responseObj, HttpStatus.ACCEPTED);
		}
		catch (Exception e) {
			Response<UserModel> responseObj = new Response<UserModel>(HttpExceptionPackSend.FAIL_EXECUTION.value(), HttpExceptionPackSend.FAIL_EXECUTION.getAction(), null);
			return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	
	private UserModel registerOrUpdateUserAccess(String username, String usernamenew, String operation) {
		String smsCode = generateSMSCode();
		UserModel entity = new UserModel();
		if(operation == MicroservicesConstants.ADD_OP_USERNAME) {
			// Create New User for first Access
			entity = new UserModel(username, "#######", false, smsCode, false);
			return userDAO.add(entity);
		}
		else if(operation == MicroservicesConstants.UPDATE_OP_USERNAME) {
			// Update in field UsernameCurrent for UsernameNew. The User already created.
			entity.setUsername(username);
			entity = userDAO.find(entity);
			entity.setActivationKey(smsCode);
			entity.setActivated(false);
			return entity = userDAO.update(entity);
		}
		return entity;
		// CALL METHOD SEND SMS TO CLIENT //
	}
	
	public ResponseEntity<?> cancelUserAccess(String username) {
		try {
			UserModel entity = new UserModel(); 
			entity.setUsername(username);
			entity = userDAO.find(entity);
			
			if(entity != null) {
				userDAO.remove(entity);
			}
			Response<UserModel> responseObj = new Response<UserModel>(HttpExceptionPackSend.USER_DELETE.value(), HttpExceptionPackSend.USER_DELETE.getAction(), null);
			return new ResponseEntity<>(responseObj, HttpStatus.ACCEPTED);
		}
		catch (Exception e) {
			e.printStackTrace();
			Response<UserModel> responseObj = new Response<UserModel>(HttpExceptionPackSend.FAIL_EXECUTION.value(), HttpExceptionPackSend.FAIL_EXECUTION.getAction(), null);
			return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public String generateSMSCode() {
		return smsObj.generateSMSCode();
	}
	
	public ResponseEntity<?> getSMSValidateProcess(String username, String usernameNew, String smscode) {
		try {
			if(getSMSCodeCheck(username,smscode) == MicroservicesConstants.SMS_VALIDATE_FOUND) {
				// Validate Operation Update 
				if(usernameNew != null) {
					System.out.println(" getSMSValidateProcess "+ usernameNew);
					ResponseEntity<?> opResultAccount = accountCliente.changeUsernameAccount(username,usernameNew);
					// Result Operation - Update Account Collection
					if(opResultAccount.getStatusCode() == HttpStatus.ACCEPTED) {
						ResponseEntity<?> opResultUser = allowUserAccessChangeUsername(username,usernameNew);
						// Result Operation - Update User Collection
						if(opResultUser.getStatusCode()  == HttpStatus.ACCEPTED) {
							Response<UserModel> responseObj = new Response<UserModel>(HttpExceptionPackSend.UPDATE_ACCOUNT.value(), HttpExceptionPackSend.UPDATE_ACCOUNT.getAction(), null);
							return new ResponseEntity<>(responseObj, HttpStatus.OK);
						}
						// ERRO UPDATE USRENAME COLLECTION USER
						else {
							ResponseEntity<?> opResultAccountUp = accountCliente.changeUsernameAccount(usernameNew,username);
							Response<UserModel> responseObj = new Response<UserModel>(HttpExceptionPackSend.FAIL_EXECUTION.value(), HttpExceptionPackSend.FAIL_EXECUTION.getAction(), null);
							return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);
						}
					}
					else {
						Response<UserModel> responseObj = new Response<UserModel>(HttpExceptionPackSend.FAIL_EXECUTION.value(), HttpExceptionPackSend.FAIL_EXECUTION.getAction(), null);
						return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);
					}
				}
				// Validate Operation Create Access first Register
				else {
					Response<UserModel> responseObj = new Response<UserModel>(HttpExceptionPackSend.FOUND_SMS_CODE.value(), HttpExceptionPackSend.FOUND_SMS_CODE.getAction(), null);
					return new ResponseEntity<>(responseObj, HttpStatus.FOUND);
				}
	
			}
			else {
				Response<UserModel> responseObj = new Response<UserModel>(HttpExceptionPackSend.NOT_FOUND_SMS_CODE.value(), HttpExceptionPackSend.NOT_FOUND_SMS_CODE.getAction(), null);
				return new ResponseEntity<>(responseObj, HttpStatus.NOT_FOUND);
			}
		}
		catch (Exception e) {
			ResponseEntity<?> opResultAccountUp = accountCliente.changeUsernameAccount(usernameNew,username);
			Response<UserModel> responseObj = new Response<UserModel>(HttpExceptionPackSend.FAIL_EXECUTION.value(), HttpExceptionPackSend.FAIL_EXECUTION.getAction(), null);
			return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public boolean getSMSCodeCheck(String username, String sms) throws Exception {
			UserModel entity = new UserModel(); 
			entity.setUsername(username);
			entity.setActivationKey(sms);
			entity = userDAO.find(entity);
			
			if(entity == null) {
				return MicroservicesConstants.SMS_VALIDATE_NOTFOUND;
			}
			else{
				return MicroservicesConstants.SMS_VALIDATE_FOUND;
			}
	}
		
}
