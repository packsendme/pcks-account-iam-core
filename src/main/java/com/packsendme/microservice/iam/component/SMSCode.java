package com.packsendme.microservice.iam.component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.packsendme.lib.utility.ConvertFormat;
import com.packsendme.microservice.iam.dto.SMSDto;


//@ComponentScan("com.packsendme.lib.utility")
@Service
@ComponentScan("com.packsendme.microservice.iam.component")
@CacheConfig(cacheNames={"SMS"})
public class SMSCode {

	@Autowired
	ConvertFormat formatObj;
	
	private static Map<String, SMSDto> storeSMS = new HashMap<String, SMSDto>();

	
	@Cacheable(value="SMS")    
	public SMSDto createSMSCodeUser(String username, String smsCode) throws Exception {
		Timestamp timeCreate = new Timestamp(System.currentTimeMillis());
		System.out.println("-----------------------------------------");
		System.out.println("CreateCache--Username :: "+ username +" - "+ smsCode);
        System.out.println("CreateCache-Username HOURS/MINUTES :: "+ timeCreate.getHours() +" "+timeCreate.getMinutes());


		SMSDto smsObj = null;
		try{
            Thread.sleep(1000); 
        }catch(Exception e){
        }
		
		smsObj = storeSMS.get(username);
		if(smsObj != null) {
			System.out.println("find... :: "+ username);
			storeSMS.remove(username);
			evict(smsObj.getUsername());
		}
		storeSMS.put(username,new SMSDto(smsCode, username, timeCreate.getTime()));
		smsObj = storeSMS.get(username);
		System.out.println("CreateCache-Username ...:: OK :: ");
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++");


		// CALL METHOD SEND SMS TO CLIENT //
		return smsObj;
	}
	
	@Cacheable(value="SMS")    
	public SMSDto findSMSCodeUser(String usernameNew, String smscode) throws Exception {
		SMSDto smsObj = null;
		try{
	    	System.out.println("-----------------------------------------------------------");
	    	System.out.println("find...:: USERNAME_NEW :: "+ usernameNew);
			System.out.println("find...:: SMS :: "+ smscode);
	    	System.out.println("-----------------------------------------------------------");
			Thread.sleep(1000); 
	     }catch(Exception e){
		    System.out.println("------------------------E R R O R-----------------------------------");
	    }
		smsObj = storeSMS.get(usernameNew);
		if(smsObj != null) {

			if(smsObj.getUsername().equals(usernameNew) && smsObj.getSmsCode().equals(smscode)) {
		    	System.out.println("find...:: FOUND:: "+ smsObj.getUsername().equals(usernameNew));
		    	storeSMS.remove(usernameNew);
		    	evict(smsObj.getUsername());
		    	System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			}
			else {
		    	System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println("Result Validation ...:: NOT-FOUND:: ");
		    	System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				smsObj = null;
				return smsObj;			
			}
		}
		else{
	    	System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			System.out.println("Result FIND  ...:: NOT-FOUND:: ");
	    	System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			smsObj = null;
			return smsObj;
		}
		return smsObj;
	}
		
	   
    @Scheduled(cron = "0 * * ? * *")
    public void checkCacheDelete(){
		Timestamp timestampCache = new Timestamp(System.currentTimeMillis());
		
    	Iterator<Map.Entry<String, SMSDto>> itr = storeSMS.entrySet().iterator();
    	while(itr.hasNext())
    	{
    	   Map.Entry<String, SMSDto> entry = itr.next();
    	   SMSDto smsObj = entry.getValue();
    	   
    	   long milliseconds = timestampCache.getTime() - smsObj.getTimeCreate();
       	   int seconds = (int) milliseconds / 1000;
       	   int minutes = (seconds % 3600) / 60;
       	   if(minutes >= 1) {
       		   System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++");
       		   System.out.println("checkCacheDelete-Username HOURS/MINUTES :: "+ timestampCache.getHours() +" "+timestampCache.getMinutes());
       		   System.out.println("checkCacheDelete-Minutes "+ minutes);
       		   evict(smsObj.getUsername());
       		   itr.remove();
       	   }
    	}
    }

    @CacheEvict(value="SMS",key="#username") 
    public void evict(String username){
        System.out.println("<<<< DELETE >>>>..."+ username);
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++");
    }
    
	public String generateSMSCode() {
		String codSMS = "";
		// Metemos en una lista los números del 1 al 40.
		List<Integer> numbers = new ArrayList<>(40);
		for (int i=1;i<10;i++){
		   numbers.add(i);
		}
		Random random = new Random();
		for(int n = 1; n<=4;n++) {
			  int randomIndex = random.nextInt(numbers.size());
			  codSMS = randomIndex + codSMS;
			  numbers.remove(randomIndex);
		}
		return codSMS;
	}

		
}
