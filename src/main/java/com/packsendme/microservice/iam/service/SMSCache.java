package com.packsendme.microservice.iam.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
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
    
	
	@Cacheable(value="SMSCache", key="{#smscode}")    
	public SMSDto createSMSCodeUser(String smscode) throws Exception {
		Timestamp timeCreate = new Timestamp(System.currentTimeMillis());
		System.out.println("-----------------------------------------");
		System.out.println("CreateCache--Username+SMS :: "+ smscode);
        System.out.println("CreateCache-Username HOURS/MINUTES :: "+ timeCreate.getHours() +" "+timeCreate.getMinutes());
		SMSDto smsObj = null;

		try{
			System.out.println("CreateCache-- Creating :: ");
            Thread.sleep(1000); 
        
			smsObj = storeSMS.get(smscode);
			if(smsObj != null) {
				System.out.println("find... :: "+ smscode);
				storeSMS.remove(smscode);
				//evict(smsObj.getUsername(),smsObj.getSmsCode());
			}
			else {
				storeSMS.put(smscode,new SMSDto(smscode, timeCreate.getTime()));
				smsObj = storeSMS.get(smscode);
				System.out.println("CreateCache-Username ...:: OK :: smsCode "+ smscode);
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++");
			}
		}
		catch(Exception e){
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++"+ e);
		}
		// CALL METHOD SEND SMS TO CLIENT //
		return smsObj;
	}
	
	@Cacheable(value="SMSCache", key="{#smscode}")   
	public SMSDto findSMSCodeUser(String smscode) throws Exception {
		SMSDto smsObj = null;
		try{
	    	System.out.println("-----------------------------------------------------------");
	    	System.out.println("find...:: USERNAME_NEW :: "+ smscode);
	    	System.out.println("-----------------------------------------------------------");
			Thread.sleep(1000); 
	     
			smsObj = storeSMS.get(smscode);
			if(smsObj != null) {
				if(smsObj.getSmsCodeUsername().equals(smscode)) {
					System.out.println("Result FIND  ...:: FOUND:: ");
					return smsObj;
				}
				else {
					smsObj = null;
					System.out.println("Result FIND  ...:: 	NOT-FOUND:: "+ smsObj.getSmsCodeUsername());
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
	
			
	
    //@Scheduled(cron = "0/55 * * * * *")
	//@Scheduled(cron = "0 * * * * *")
	
	@Scheduled(fixedRate = 1000)
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
       		   System.out.println("checkCacheDelete-UsernameSMS ::"+ smsObj.getSmsCodeUsername());
       		   System.out.println("checkCacheDelete-Username HOURS/MINUTES :: "+ timestampCache.getHours() +" "+timestampCache.getMinutes());
       		   System.out.println("checkCacheDelete-Minutes "+ minutes);
       		   storeSMS.remove(itr);
       		   itr.remove();
       		   deleteCacheSMS(smsObj.getSmsCodeUsername());
       		   System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++");
       	   }
    	}
    }
	
    @CacheEvict(value="SMSCache",key = "{#smscode}")
	public void deleteCacheSMS(String smscode){
		   System.out.println("DELETE CACHE DELETE -- ");

//    	Ehcache ehcache = cacheManager.getEhcache("SMSCache");
//    	ehcache.remove(id, true);
    	//ehcache.removeAll();
    }
    		
}
