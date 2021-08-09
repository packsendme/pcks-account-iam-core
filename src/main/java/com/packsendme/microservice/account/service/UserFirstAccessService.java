package com.packsendme.microservice.account.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.packsendme.lib.common.constants.generic.HttpExceptionPackSend;
import com.packsendme.lib.common.constants.generic.MicroservicesConstants;
import com.packsendme.lib.common.response.Response;
import com.packsendme.lib.utility.ConvertFormat;
import com.packsendme.microservice.account.controller.SMSCodeClient;
import com.packsendme.microservice.account.dao.UserDAO;
import com.packsendme.microservice.account.repository.UserModel;

@Service
public class UserFirstAccessService {

	@Autowired
	UserDAO userDAO;
		
	@Autowired
	ConvertFormat formatObj;
	
	@Autowired
	SMSCodeClient smscode;
	
	public ResponseEntity<?> findUserToFirstAccess(String username, String dtAction) {
		UserModel userFind = new UserModel();
		userFind.setUsername(username);
		Response<UserModel> responseObj = new Response<UserModel>(0,HttpExceptionPackSend.USERNAME_VALIDATE_ACCESS.getAction(), userFind);
		try{
			userFind = userDAO.find(userFind);
			
			// FirstAccess: User/phonenumber does not exist in  DATABASE, will be create new SMSCode
			if(userFind == null) {
				ResponseEntity<?> opResultSMS = smscode.generatorSMSCode(username);
				if(opResultSMS.getStatusCode() == HttpStatus.ACCEPTED) {
					return new ResponseEntity<>(responseObj, HttpStatus.OK);
				}
				else {
					return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);	
				}
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
	

	
	// Method Call (AccountService) After register Account to enable User Access
	public ResponseEntity<?> registerUser(String username, String password, String dateAction) {
		Response<UserModel> responseObj = new Response<UserModel>(0, HttpExceptionPackSend.USER_ACCESS_CREATED.getAction(), null);
		try {
			Date dateCreation = formatObj.convertStringToDate(dateAction);
			UserModel entity = new UserModel(username, password, MicroservicesConstants.USERNAME_ACCOUNT_ACTIVE,
			MicroservicesConstants.ACTIVATIONKEY,false,dateCreation);
			entity = userDAO.add(entity);
			return new ResponseEntity<>(responseObj, HttpStatus.ACCEPTED);
		}
		catch (Exception e) {
			return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
			
}
