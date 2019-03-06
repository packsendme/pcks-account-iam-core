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
import com.packsendme.microservice.iam.controller.AccountClient;
import com.packsendme.microservice.iam.dao.UserDAO;
import com.packsendme.microservice.iam.dto.SMSDto;
import com.packsendme.microservice.iam.repository.UserModel;

@Service
@ComponentScan("com.packsendme.lib.utility")
public class UserService {

	@Autowired
	UserDAO userDAO;
	
	@Autowired
	AccountClient accountCliente;

	@Autowired
	SMSCache smsObj;
	
	
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

	
	
	public ResponseEntity<?> getSMSCodeToUpdateUser(String username, String usernameNew, String smscode, String dtAction) {
		Response<UserModel> responseObj = new Response<UserModel>(HttpExceptionPackSend.UPDATE_ACCOUNT.getAction(), null);
		UserModel entityFind = new UserModel();
		SMSDto smsDto = new SMSDto();
		entityFind.setUsername(username);
		try {
			
			smsDto =  smsObj.findSMSCodeUser(usernameNew, smscode);
			
			//System.out.println(" ===== VALOR CACHE ====== "+ smsDto.getUsername() +" - "+smsDto.getSmsCode());
			//smsDto =  smsObj.createSMSCodeUser(usernameNew, smscode);
			
			if(smsDto != null){
				
				System.out.println("USERNAME "+ smsDto.getUsername());
				System.out.println("SMSCODE "+ smsDto.getSmsCode());
				
				System.out.println("USERNAME_NEW "+ usernameNew);
				System.out.println("SMSCODE "+ smscode);
				
				if(smsDto.getUsername().equals(usernameNew) && smsDto.getSmsCode().equals(smscode)) {
					System.out.println(" ===== VALOR CACHE ====== "+ smsDto.getUsername() +" - "+smsDto.getSmsCode());

					UserModel entity = userDAO.find(entityFind);
					if(entity != null) {
						entity.setUsername(usernameNew);
						entity.setActivationKey(MicroservicesConstants.ACTIVATIONKEY);
						entity.setDateUpdate(formatObj.convertStringToDate(dtAction));
						userDAO.update(entity);
						// Call AccountMicroservice - Update Username - Account
						ResponseEntity<?> opResultAccount = accountCliente.changeUsernameForAccount(username,usernameNew,dtAction);
						if(opResultAccount.getStatusCode() == HttpStatus.OK) {
							return new ResponseEntity<>(responseObj, HttpStatus.OK);
						}
						// Erro AccountService - Compensaçao de resultado
						else {
							entity.setUsername(username);
							entity.setDateUpdate(formatObj.convertStringToDate(dtAction));
							userDAO.update(entity);
							return new ResponseEntity<>(responseObj, HttpStatus.FORBIDDEN);
						}
					}
					else {
						return new ResponseEntity<>(responseObj, HttpStatus.FORBIDDEN);
					}
				}
				else{
					return new ResponseEntity<>(responseObj, HttpStatus.NOT_FOUND);
				}
			} // Erro SMSCODE Not Found
			else{
				return new ResponseEntity<>(responseObj, HttpStatus.NOT_FOUND);
			}
		}
		catch (Exception e) {
		//	UserModel entityRollback = new UserModel();
	//		entityRollback.setUsername(usernameNew);
	//		UserModel entity = userDAO.find(entityRollback);
	//		entity.setUsername(username);
	//		userDAO.update(entity);
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
				entity.setDateUpdate(formatObj.convertStringToDate(dtAction));
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
