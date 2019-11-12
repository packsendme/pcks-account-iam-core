package com.packsendme.microservice.iam.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import feign.Response;
import feign.codec.ErrorDecoder;

public class FeignErrorDecoder implements ErrorDecoder {


	
	@Override
	public Exception decode(String methodKey, Response response) {
		switch (response.status()){
	        case 400:
	            {
	            	System.out.println(" FeignErrorDecoder --> 400 ");
	            	return new ResponseStatusException(HttpStatus.valueOf(response.status()), " ERROR 400"); 

	            }
	        case 404:
            {
            	System.out.println(" FeignErrorDecoder --> 404 ");
            	return new ResponseStatusException(HttpStatus.valueOf(response.status()), " ERROR 404"); 

            }
	        default:
	            return new Exception("Generic error");
		}
	}
	
	

}