package com.packsendme.microservice.iam.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.packsendme.lib.utility.ConvertFormat;
import com.packsendme.microservice.iam.dto.SMSDto;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;


@Service
//@CacheConfig(cacheNames="SMSCache")
public class SMSCache {

	@Autowired
	ConvertFormat formatObj;
	
	private static Map<String, SMSDto> storeSMS = new HashMap<String, SMSDto>();

	
    @Autowired 
    private CacheManager cacheManager;   
    
	
	@Cacheable(value="SMSCache", key="{#username}")    
	public SMSDto createSMSCodeUser(String username, String smsCode) throws Exception {
		Timestamp timeCreate = new Timestamp(System.currentTimeMillis());
		System.out.println("-----------------------------------------");
		System.out.println("CreateCache--Username :: "+ username +" - "+ smsCode);
        System.out.println("CreateCache-Username HOURS/MINUTES :: "+ timeCreate.getHours() +" "+timeCreate.getMinutes());
		SMSDto smsObj = null;

		try{
			System.out.println("CreateCache-- Creating :: ");
            Thread.sleep(1000); 
        
			smsObj = storeSMS.get(username);
			if(smsObj != null) {
				System.out.println("find... :: "+ username);
				storeSMS.remove(username);
				//evict(smsObj.getUsername(),smsObj.getSmsCode());
			}
			else {
				storeSMS.put(username,new SMSDto(smsCode, username, timeCreate.getTime()));
				smsObj = storeSMS.get(username);
				System.out.println("CreateCache-Username ...:: OK :: smsCode "+ smsCode);
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++");
			}
		}
		catch(Exception e){
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++"+ e);
		}
		// CALL METHOD SEND SMS TO CLIENT //
		return smsObj;
	}
	
	@Cacheable(value="SMSCache", key="{#usernames}")   
	public SMSDto findSMSCodeUser(String username, String smsCode) throws Exception {
		SMSDto smsObj = null;
		try{
	    	System.out.println("-----------------------------------------------------------");
	    	System.out.println("find...:: USERNAME_NEW :: "+ username);
			System.out.println("find...:: SMS :: "+ smsCode);
	    	System.out.println("-----------------------------------------------------------");
			Thread.sleep(1000); 
	     
			smsObj = storeSMS.get(username);
			if(smsObj != null) {
				if(smsObj.getUsername().equals(username) && smsObj.getSmsCode().equals(smsCode)) {
					System.out.println("Result FIND  ...:: FOUND:: ");
					return smsObj;
				}
				else {
					smsObj = null;
					System.out.println("Result FIND  ...:: 	NOT-FOUND:: "+ smsObj.getUsername());
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
		}catch(Exception e){
		    System.out.println("------------------------E R R O R-----------------------------------");
		    System.out.println(" EXCEPTION "+ e);
		}
		return smsObj;
	}
	
		/*
		if(smsObj != null) {

			if(smsObj.getUsername().equals(username) && smsObj.getSmsCode().equals(smscode)) {
		    	System.out.println("find...:: FOUND:: "+ smsObj.getUsername().equals(username));
		    	storeSMS.remove(username);
		    	evict(smsObj.getUsername(),smsObj.getSmsCode());
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
		}*/
	
		
	
    //@Scheduled(cron = "0/55 * * * * *")
	@Scheduled(cron = "0 * * * * *")
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
       		   System.out.println("CHECK CACHE DELETE -- "+ minutes);
       		   System.out.println("checkCacheDelete-Username ::"+ smsObj.getUsername());
       		   System.out.println("checkCacheDelete-smsCode ::"+ smsObj.getSmsCode());
       		   System.out.println("checkCacheDelete-Username HOURS/MINUTES :: "+ timestampCache.getHours() +" "+timestampCache.getMinutes());
       		   System.out.println("checkCacheDelete-Minutes "+ minutes);
       		   System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++");
       		   storeSMS.remove(itr);
       		   itr.remove();
       		   deleteCacheSMS(smsObj.getUsername());
       	   }
    	}
    }
  
    public void deleteCacheSMS(String username){
    	Ehcache ehcache = cacheManager.getEhcache("SMSCache");
    	ehcache.remove(username, true);
    }
    


		
}
