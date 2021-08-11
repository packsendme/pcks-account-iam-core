package com.packsendme.microservice.account.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="pcks-3rpart-sms-api")
public interface SMSCodeClient {
	
	@GetMapping("/3rpart/sms/{username}")
	public ResponseEntity<?> generatorSMSCode(
			@Validated @PathVariable("username") String username);
	

	@GetMapping("/3rpart/sms/{username}/{smscode}")
	public ResponseEntity<?> validateSMSCode(
			@Validated @PathVariable("username") String username, 
			@Validated @PathVariable("smscode") String smscode);

}
