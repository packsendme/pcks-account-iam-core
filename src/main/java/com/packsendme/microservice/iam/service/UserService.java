package com.packsendme.microservice.iam.service;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.packsendme.lib.common.constants.HttpExceptionPackSend;
import com.packsendme.lib.common.constants.MicroservicesConstants;
import com.packsendme.lib.common.response.Response;
import com.packsendme.lib.utility.ConvertFormat;
import com.packsendme.microservice.iam.controller.AccountClient;
import com.packsendme.microservice.iam.controller.SMSCodeClient;
import com.packsendme.microservice.iam.dao.UserDAO;
import com.packsendme.microservice.iam.dto.NamesAccountDto;
import com.packsendme.microservice.iam.dto.UserDto;
import com.packsendme.microservice.iam.repository.UserModel;

@Service
@ComponentScan("com.packsendme.lib.utility")
public class UserService {

	@Autowired
	UserDAO userDAO;
	
	@Autowired
	AccountClient accountCliente;

	@Autowired
	SMSCodeClient smscodeClient;

	
	@Autowired
	ConvertFormat formatObj;
	
	public ResponseEntity<?> findUserToLogin(String username, String password) {
		Response<UserModel> responseObj = new Response<UserModel>(0,HttpExceptionPackSend.LOGIN_USER.getAction(), null);
		UserModel entity = new UserModel(); 
		Gson gson = new Gson();
		
		try {
			entity.setUsername(username);
			entity.setPassword(password);
			entity = userDAO.find(entity);
			if(entity != null) {
				
			ResponseEntity<?> opResultAccount = accountCliente.loadFirstNameAccount(username);
			
			System.out.print(" 2 MAP MAP -------------------->>> "+ opResultAccount.getStatusCode());

			if(opResultAccount.getStatusCode() == HttpStatus.OK) {
					String json = opResultAccount.getBody().toString();
					System.out.println(" <<< -----  N A M E - FIRST -->> "+ json);

				//	NamesAccountDto namesDto = gson.fromJson(json, NamesAccountDto.class);
					//Object mapper instance
					ObjectMapper mapper = new ObjectMapper();
					 
					//Convert JSON to POJO
					NamesAccountDto namesDto = mapper.readValue(json, NamesAccountDto.class);
					System.out.println(" <<< -----  N A M E - FIRST -->> "+ namesDto.getFirstName());

				}
				responseObj = new Response<UserModel>(0,HttpExceptionPackSend.LOGIN_USER.getAction(), entity);
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
	
	public ResponseEntity<?> getSMSCodeToUpdateUser(String username, String usernameNew, String smsCode, String dtAction) {
		Response<UserModel> responseUpdateObj = new Response<UserModel>(0,HttpExceptionPackSend.UPDATE_ACCOUNT.getAction(), null);
		Response<UserModel> responseSMSObj = new Response<UserModel>(0,HttpExceptionPackSend.FOUND_SMS_CODE.getAction(), null);

		try {
			ResponseEntity<?> opResultSMS = smscodeClient.validateSMSCode(username, smsCode);
			
			if(opResultSMS.getStatusCode() == HttpStatus.FOUND) {
				
				UserModel entityFind = new UserModel();
				entityFind.setUsername(username);
				UserModel entity = userDAO.find(entityFind);

				if(entity != null) {
					entity.setUsername(usernameNew);
					entity.setActivationKey(MicroservicesConstants.ACTIVATIONKEY);
					entity.setDateUpdate(formatObj.convertStringToDate(dtAction));
					userDAO.update(entity);
					
					// Call AccountMicroservice - Update Username - Account
					ResponseEntity<?> opResultAccount = accountCliente.changeUsernameForAccount(username,usernameNew,dtAction);
					if(opResultAccount.getStatusCode() == HttpStatus.OK) {
						return new ResponseEntity<>(responseUpdateObj, HttpStatus.OK);
					}
					// Erro AccountService - Compensaçao de resultado
					else {
						entity.setUsername(username);
						entity.setDateUpdate(formatObj.convertStringToDate(dtAction));
						userDAO.update(entity);
						return new ResponseEntity<>(responseUpdateObj, HttpStatus.INTERNAL_SERVER_ERROR);
					}
				}
				else {
					return new ResponseEntity<>(responseUpdateObj, HttpStatus.NOT_FOUND);
				}
			}
			else{
				return new ResponseEntity<>(responseSMSObj, HttpStatus.NOT_FOUND);
			}
		}
		catch (Exception e) {
			return new ResponseEntity<>(responseUpdateObj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<?> updatePasswordByUsername(UserDto user) {
		Response<UserModel> responseObj = new Response<UserModel>(0,HttpExceptionPackSend.UPDATE_PASSWORD.getAction(), null);

		UserModel entityFind = new UserModel();
		entityFind.setUsername(user.getUsername());
		System.out.println(" updatePasswordByUsername - USERNAME "+ entityFind.getUsername());

		try {
			UserModel entity = userDAO.find(entityFind);
			System.out.println(" updatePasswordByUsername - find "+ entityFind.getUsername());

			if(entity != null) {
				entity.setPassword(user.getPassword());
				entity.setDateUpdate(formatObj.convertStringToDate(user.getDateOperation()));
				System.out.println(" updatePasswordByUsername - password "+ entityFind.getPassword());

				userDAO.update(entity);
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
	
	
	public ResponseEntity<?> cancelUserAccessByUsername(String username, String dtAction) throws Exception {
		Response<UserModel> responseObj = new Response<UserModel>(0,HttpExceptionPackSend.CANCEL_USERNAME.getAction(), null);
		//Convert Date from String to Date
		Date dtNow = formatObj.convertStringToDate(dtAction);
		UserModel entity = new UserModel(); 

		try {
			entity.setUsername(username);
			entity = userDAO.find(entity);

			if(entity != null) {
				entity.setActivated(MicroservicesConstants.USERNAME_ACCOUNT_DISABLED);
				entity.setDateUpdate(dtNow);
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
