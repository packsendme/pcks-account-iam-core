package com.packsendme.microservice.iam.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.packsendme.lib.common.constants.HttpExceptionPackSend;
import com.packsendme.lib.common.constants.MicroservicesConstants;
import com.packsendme.lib.common.response.Response;
import com.packsendme.lib.utility.ConvertFormat;
import com.packsendme.microservice.iam.component.SMSCodeManagement;
import com.packsendme.microservice.iam.controller.AccountClient;
import com.packsendme.microservice.iam.dao.UserDAO;
import com.packsendme.microservice.iam.repository.UserModel;

@Service
@ComponentScan("com.packsendme.lib.utility")
public class UserService {

	@Autowired
	UserDAO userDAO;
	
	@Autowired
	AccountClient accountCliente;
	
	@Autowired
	SMSCodeManagement smsObj;
	
	@Autowired
	ConvertFormat formatObj;
	
	public ResponseEntity<?> findUserToLogin(String username, String password) {
		Response<UserModel> responseObj = new Response<UserModel>(HttpExceptionPackSend.LOGIN_USER.getAction(), null);
		UserModel entity = new UserModel(); 

		try {
			entity.setUsername(username);
			entity.setPassword(password);
			entity = userDAO.find(entity);
			if(entity != null) {
				responseObj = new Response<UserModel>(HttpExceptionPackSend.LOGIN_USER.getAction(), entity);
				return new ResponseEntity<>(responseObj, HttpStatus.FOUND);
			}
			else {
				return new ResponseEntity<>(responseObj, HttpStatus.NOT_FOUND);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	
	public ResponseEntity<?> checkUsernameForChange(String username, String usernamenew) {
		UserModel userFind = new UserModel();
		Response<UserModel> responseObj = new Response<UserModel>(HttpExceptionPackSend.REGISTER_USERNAME.getAction(), userFind);
		try{
			userFind.setUsername(usernamenew);
			userFind = userDAO.find(userFind);
			
			// Username does exist in user base can be change for usernameNew
			if(userFind == null) {
				UserModel entity = new UserModel();
				entity.setUsername(username);
				entity = userDAO.find(entity);
				if(entity != null) {
					// Generate SMSCode to validate
					String smsCode = smsObj.generateSMSCode();
					entity.setActivationKey(smsCode);
					userDAO.update(entity);
					// CALL SMS SEND MOBILE
					return new ResponseEntity<>(responseObj, HttpStatus.OK);
				}
				else {
					return new ResponseEntity<>(responseObj, HttpStatus.NOT_FOUND);
				}
			}
			// User already exists in the database 
			else {
				return new ResponseEntity<>(responseObj,HttpStatus.FOUND);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);		
		}
	}
	
	public ResponseEntity<?> updateUserByUsernamenew(String username, String usernameNew, String smscode, String dtAction) {
		Response<UserModel> responseObj = new Response<UserModel>(HttpExceptionPackSend.UPDATE_ACCOUNT.getAction(), null);
		UserModel entityFind = new UserModel();
		entityFind.setUsername(username);
		try {
			if(findUserBySMSCodeUsername(username,smscode) == MicroservicesConstants.SMS_VALIDATE_FOUND) {
				UserModel entity = userDAO.find(entityFind);

				if(entity != null) {
					entity.setUsername(usernameNew);
					entity.setActivationKey(MicroservicesConstants.ACTIVATIONKEY);
					entity.setDtUpdate(formatObj.convertStringToDate(dtAction));
					userDAO.update(entity);
					// Call AccountMicroservice - Update Username - Account
					ResponseEntity<?> opResultAccount = accountCliente.changeUsernameForAccount(username,usernameNew,dtAction);
					if(opResultAccount.getStatusCode() == HttpStatus.OK) {
						return new ResponseEntity<>(responseObj, HttpStatus.OK);
					}
					// Erro AccountService - Compensaçao de resultado
					else {
						entity.setUsername(username);
						entity.setDtUpdate(formatObj.convertStringToDate(dtAction));
						userDAO.update(entity);
						return new ResponseEntity<>(responseObj, HttpStatus.FORBIDDEN);
					}
				}
				else {
					return new ResponseEntity<>(responseObj, HttpStatus.FORBIDDEN);
				}
			} // Erro SMSCODE Not Found
			else{
				return new ResponseEntity<>(responseObj, HttpStatus.NOT_FOUND);
			}
		}
		catch (Exception e) {
			UserModel entityRollback = new UserModel();
			entityRollback.setUsername(usernameNew);
			UserModel entity = userDAO.find(entityRollback);
			entity.setUsername(username);
			userDAO.update(entity);
			return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<?> updatePasswordByUsername(String username, String password, String dtAction) {
		Response<UserModel> responseObj = new Response<UserModel>(HttpExceptionPackSend.UPDATE_PASSWORD.getAction(), null);
		UserModel entityFind = new UserModel();
		entityFind.setUsername(username);
		
		try {
			entityFind.setUsername(username);
			UserModel entity = userDAO.find(entityFind);
			
			if(entity != null) {
				entity.setPassword(password);
				entity.setDtUpdate(formatObj.convertStringToDate(dtAction));
				userDAO.update(entity);
				return new ResponseEntity<>(responseObj, HttpStatus.OK);
			}
			else {
				return new ResponseEntity<>(responseObj, HttpStatus.FORBIDDEN);
			}
		}
		catch (Exception e) {
			return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<?> cancelUserAccessByUsername(String username, String dtAction) throws Exception {
		Response<UserModel> responseObj = new Response<UserModel>(HttpExceptionPackSend.CANCEL_USERNAME.getAction(), null);
		//Convert Date from String to Date
		Date dtNow = formatObj.convertStringToDate(dtAction);
		UserModel entity = new UserModel(); 

		try {
			entity.setUsername(username);
			entity = userDAO.find(entity);

			if(entity != null) {
				entity.setActivated(MicroservicesConstants.USERNAME_ACCOUNT_DISABLED);
				entity.setDtUpdate(dtNow);
				userDAO.update(entity);
				return new ResponseEntity<>(responseObj, HttpStatus.OK);
			}
			else {
				return new ResponseEntity<>(responseObj, HttpStatus.NOT_FOUND);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
		
	public boolean findUserBySMSCodeUsername(String username, String sms) throws Exception {
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
