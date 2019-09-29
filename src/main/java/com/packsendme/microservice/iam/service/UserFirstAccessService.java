package com.packsendme.microservice.iam.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.packsendme.lib.common.constants.HttpExceptionPackSend;
import com.packsendme.lib.common.constants.MicroservicesConstants;
import com.packsendme.lib.common.response.Response;
import com.packsendme.lib.utility.ConvertFormat;
import com.packsendme.microservice.iam.component.GeneratorSMSCode;
import com.packsendme.microservice.iam.dao.UserDAO;
import com.packsendme.microservice.iam.dto.SMSDto;
import com.packsendme.microservice.iam.repository.UserModel;

@Service
public class UserFirstAccessService {

	@Autowired
	UserDAO userDAO;
	
	@Autowired
	SMSCache smsObj;
	
	@Autowired
	GeneratorSMSCode smscodeObj;
	
	@Autowired
	ConvertFormat formatObj;
	
	public ResponseEntity<?> findUserToFirstAccess(String username, String dtAction) {
		UserModel userFind = new UserModel();
		userFind.setUsername(username);
		Response<UserModel> responseObj = new Response<UserModel>(0,HttpExceptionPackSend.USERNAME_VALIDATE_ACCESS.getAction(), userFind);
		try{
			userFind = userDAO.find(userFind);
			// FirstAccess: User does not exist in user base that generator SMSCode
			if(userFind == null) {
				String smsCode = smscodeObj.generateSMSCode();
				SMSDto sms = smsObj.createSMSCodeUser(username,smsCode);
				if(sms != null) {
					Response<SMSDto> responseSMS = new Response<SMSDto>(0, HttpExceptionPackSend.GENERATOR_SMSCODE.getAction(), sms);
					return new ResponseEntity<>(responseSMS, HttpStatus.OK);
				}
				else
					return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);		
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
	
	@SuppressWarnings("unused")
	public ResponseEntity<?> findSMSCodeToFirstAccess(String username, String smscode) throws Exception {
		Response<UserModel> responseObj = new Response<UserModel>(0, HttpExceptionPackSend.FOUND_SMS_CODE.getAction(), null);
		SMSDto smsDto = null;

		try {
			smsDto = smsObj.findSMSCodeUser(username,smscode);
			if(smsDto == null) {
				System.out.println(" RESULT findSMSCodeToFirstAccess IS "+ HttpStatus.NOT_FOUND);
				return new ResponseEntity<>(responseObj, HttpStatus.FOUND);
			}
			else if(smsDto != null){
				System.out.println(" RESULT findSMSCodeToFirstAccess IS "+ HttpStatus.FOUND);
				return new ResponseEntity<>(responseObj, HttpStatus.FOUND);
			}
		}
		catch (Exception e) {
			System.out.println("EXCEPTION ERROR"+ e);
			return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);
	}



	private ResponseEntity<?> extracted() {
		return null;
	}
	
		
}
