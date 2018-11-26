package com.packsendme.microservice.iam.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.packsendme.lib.common.constants.HttpExceptionPackSend;
import com.packsendme.lib.common.constants.MicroservicesConstants;
import com.packsendme.lib.common.response.Response;
import com.packsendme.lib.utility.ConvertFormat;
import com.packsendme.microservice.iam.component.SMSCodeManagement;
import com.packsendme.microservice.iam.controller.AccountClient;
import com.packsendme.microservice.iam.dao.UserDAO;
import com.packsendme.microservice.iam.dto.SMSDto;
import com.packsendme.microservice.iam.repository.UserModel;

@Service
@ComponentScan("com.packsendme.lib.utility")
@CacheConfig(cacheNames={"SMS"})
public class UserFirstAccessService {

	@Autowired
	UserDAO userDAO;
	
	@Autowired
	AccountClient accountCliente;
	
	@Autowired
	SMSCodeManagement smsObj;
	
	@Autowired
	ConvertFormat formatObj;
	
	private static Map<String, SMSDto> storeSMS = new HashMap<String, SMSDto>();

	
	public ResponseEntity<?> findUserToFirstAccess(String username, String dtAction) {
		UserModel userFind = new UserModel();
		userFind.setUsername(username);
		Response<UserModel> responseObj = new Response<UserModel>(HttpExceptionPackSend.USERNAME_VALIDATE_ACCESS.getAction(), userFind);
		try{
			userFind = userDAO.find(userFind);
			// FirstAccess: User does not exist in user base that generator SMSCode
			if(userFind == null) {
				String smsCode = smsObj.generateSMSCode();
				SMSDto smsObj = createSMSUserFirstAccess(smsCode,username);
				if(smsObj != null) {
					Response<SMSDto> responseSMS = new Response<SMSDto>(HttpExceptionPackSend.GENERATOR_SMSCODE.getAction(), smsObj);
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
	
	@Cacheable(key="#username,#smsCode")    
	private SMSDto createSMSUserFirstAccess(String username, String smsCode) throws Exception {
		Timestamp timeCreate = new Timestamp(System.currentTimeMillis());
		System.out.println("-----------------------------------------"+ smsCode);
		System.out.println("createSMSUserFirstAccess"+ username +" - "+ smsCode);

		SMSDto smsObj = null;
		try{
            Thread.sleep(1000); 
        }catch(Exception e){
        }
		System.out.println("SMS CODE "+ smsCode);
		
		smsObj = storeSMS.get(username);
		if(smsObj != null) {
			System.out.println("find...:: OK :: ");
			storeSMS.remove(username);
			evict(smsObj.getUsername());
		}
		storeSMS.put(username,new SMSDto(smsCode, username, timeCreate.getTime()));
		smsObj = storeSMS.get(username);
		// CALL METHOD SEND SMS TO CLIENT //
		return smsObj;
	}
	
	@Cacheable(key="#username")    
	public ResponseEntity<?> findSMSCodeUserToFirstAccess(String username, String smscode) throws Exception {
		Response<UserModel> responseObj = new Response<UserModel>(HttpExceptionPackSend.FOUND_SMS_CODE.getAction(), null);
		try{
			 Thread.sleep(1000); 
	     }catch(Exception e){
	 		return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
    	System.out.println("-----------------------------------------------------------");
		
    	System.out.println("find...:: USERNAME :: "+ username);
		System.out.println("find...:: SMS :: "+ smscode);
		
		SMSDto smsObj = storeSMS.get(username);
		if(smsObj != null) {
			if(smsObj.getUsername().equals(username) && smsObj.getSmsCode().equals(smscode)) {
		    	System.out.println("find...:: FOUND:: "+ smsObj.getUsername().equals(username));
		    	storeSMS.remove(username);
		    	evict(smsObj.getUsername());
		    	System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		    	return new ResponseEntity<>(responseObj, HttpStatus.FOUND);
			}
			else {
				System.out.println("find...:: NOT-FOUND:: ");
		    	System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				return new ResponseEntity<>(responseObj, HttpStatus.NOT_FOUND);
			}
		}
		else{
	    	System.out.println("find...:: NOT-FOUND:: ");
	    	System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	    	return new ResponseEntity<>(responseObj, HttpStatus.NOT_FOUND);
		}
	}
	
	   
    @Scheduled(cron = "0 * * ? * *")
    public void checkCacheDelete(){
		Timestamp timestampCache = new Timestamp(System.currentTimeMillis());
		
        System.out.println("checkCacheDelete...HOURS :: "+ timestampCache.getHours());
    	System.out.println("checkCacheDelete...MINUTES :: "+ timestampCache.getMinutes());
		
    	Iterator<Map.Entry<String, SMSDto>> itr = storeSMS.entrySet().iterator();
    	while(itr.hasNext())
    	{
    	   Map.Entry<String, SMSDto> entry = itr.next();
    	   SMSDto smsObj = entry.getValue();
    	   
    	   long milliseconds = timestampCache.getTime() - smsObj.getTimeCreate();
       	   int seconds = (int) milliseconds / 1000;
       	   int minutes = (seconds % 3600) / 60;
       	   System.out.println("USERNAME "+ smsObj.getUsername());
       	   System.out.println("Minutes "+ minutes);
       	   if(minutes >= 1) {
       		   evict(smsObj.getUsername());
       		   itr.remove();
       	   }
    	}
    }

    @CacheEvict(key="#username") 
    public void evict(String username){
        System.out.println("DELETE..."+ username);
    }

	
	// Method Call (AccountService) After register Account to enable User Access
	public ResponseEntity<?> registerUser(String username, String password, String dtAction) {
		Response<UserModel> responseObj = new Response<UserModel>(HttpExceptionPackSend.USER_ACCESS_CREATED.getAction(), null);
		try {
			Date dtCreation = formatObj.convertStringToDate(dtAction);
			UserModel entity = new UserModel(username, password, MicroservicesConstants.USERNAME_ACCOUNT_ACTIVE,
			MicroservicesConstants.ACTIVATIONKEY,false,dtCreation,null);
			entity = userDAO.add(entity);
			return new ResponseEntity<>(responseObj, HttpStatus.ACCEPTED);
		}
		catch (Exception e) {
			return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
		
}
