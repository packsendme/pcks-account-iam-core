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
public class UserFirstAccessService {

	@Autowired
	UserDAO userDAO;
	
	@Autowired
	AccountClient accountCliente;
	
	@Autowired
	SMSCodeManagement smsObj;
	
	@Autowired
	ConvertFormat formatObj;
	
	
	public ResponseEntity<?> findUserToFirstAccess(String username, String dtAction) {
		UserModel userFind = new UserModel();
		Response<UserModel> responseObj = new Response<UserModel>(HttpExceptionPackSend.REGISTER_USERNAME.getAction(), userFind);
		try{
			userFind.setUsername(username);
			userFind = userDAO.find(userFind);
			
			// FirstAccess: User does not exist in user base that create User Access Register
			if(userFind == null) {
				userFind = createUserFirstAccess(username, dtAction);
				return new ResponseEntity<>(responseObj, HttpStatus.ACCEPTED);
			}
			// User already exists in the database not first User Access go to Login
			else {
				return new ResponseEntity<>(responseObj,HttpStatus.FOUND);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);		
		}
	}
	
	private UserModel createUserFirstAccess(String username, String dtAction) throws Exception {
		UserModel entity = new UserModel();
		// Generate SMSCode to validate
		String smsCode = smsObj.generateSMSCode();
		//Convert Date from String to Date
		Date dtNow = formatObj.convertStringToDate(dtAction);
		entity = new UserModel(username, "#######", MicroservicesConstants.USERNAME_ACCOUNT_DISABLED, smsCode, false, dtNow, null);
		return userDAO.add(entity);
		// CALL METHOD SEND SMS TO CLIENT //
	}
	
	public ResponseEntity<?> findSMSCodeUserToFirstAccess(String username, String smscode) throws Exception {
		Response<UserModel> responseObj = new Response<UserModel>(HttpExceptionPackSend.FOUND_SMS_CODE.getAction(), null);
		UserModel entity = new UserModel(); 
		entity.setUsername(username);
		entity.setActivationKey(smscode);
		entity = userDAO.find(entity);
		
		if(entity != null) {
			return new ResponseEntity<>(responseObj, HttpStatus.FOUND);
		}
		else{
			return new ResponseEntity<>(responseObj, HttpStatus.NOT_FOUND);
		}
	}
	
	// Method Call (AccountService) After register Account to enable User Access
	public ResponseEntity<?> enableFirstUserAccess(String username, String password) {
		UserModel entity = new UserModel(username, null, false, null, false, null, null);
		Response<UserModel> responseObj = new Response<UserModel>(HttpExceptionPackSend.USER_ACCESS_CREATED.getAction(), null);
		System.out.println(" enableFirstUserAccess "+ username +" "+password);
		try {
			entity = userDAO.find(entity);
			if(entity != null) {
				System.out.println(" enableFirstUserAccess entity "+ username +" "+password);

				entity.setPassword(password);
				entity.setActivated(MicroservicesConstants.USERNAME_ACCOUNT_ACTIVE);
				entity.setActivationKey(MicroservicesConstants.ACTIVATIONKEY);
				entity = userDAO.update(entity);
				return new ResponseEntity<>(responseObj, HttpStatus.OK);
			}
			else {
				return new ResponseEntity<>(responseObj, HttpStatus.NOT_FOUND);
			}
		}
		catch (Exception e) {
			return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
		
}
