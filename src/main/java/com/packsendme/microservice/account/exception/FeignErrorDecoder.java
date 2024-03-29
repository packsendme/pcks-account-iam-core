package com.packsendme.microservice.account.exception;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import feign.Response;
import feign.codec.ErrorDecoder;

public class FeignErrorDecoder implements ErrorDecoder {


	
	 private ErrorDecoder delegate = new ErrorDecoder.Default();

	    @Override
	    public Exception decode(String methodKey, Response response) {
	      
	    	
	    	HttpHeaders responseHeaders = new HttpHeaders();
	        response.headers().entrySet().stream()
	                .forEach(entry -> responseHeaders.put(entry.getKey(), new ArrayList<>(entry.getValue())));

	        HttpStatus statusCode = HttpStatus.valueOf(response.status());
	        String statusText = response.reason();
	        
	        System.out.println("===============================================================");
	        
	        System.out.println("  FeignErrorDecoder responseHeaders  "+ responseHeaders);
	        System.out.println("  FeignErrorDecoder statusCode  "+ statusCode);
	        System.out.println("  FeignErrorDecoder statusText  "+ statusText);


	        byte[] responseBody;
	        try {
	            responseBody = IOUtils.toByteArray(response.body().asInputStream());
		        System.out.println("  FeignErrorDecoder responseBody  "+ responseBody);
		        System.out.println("===============================================================");
		
	        } catch (IOException e) {
	            throw new RuntimeException("Failed to process response body.", e);
	        }

	        if (response.status() >= 400 && response.status() <= 499) {
	        	statusText = HttpStatus.NOT_FOUND.getReasonPhrase();
	            return new HttpClientErrorException(statusCode, statusText, responseHeaders, responseBody, null);
	        }

	        if (response.status() >= 500 && response.status() <= 599) {
	            return new HttpServerErrorException(statusCode, statusText, responseHeaders, responseBody, null);
	        }
	        return delegate.decode(methodKey, response);
	    }
	

}