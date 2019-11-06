package com.packsendme.microservice.iam.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="packsendme-sms-server")
public interface SMSCodeClient {
	
	@GetMapping("/sms/{username}")
	public ResponseEntity<?> generatorSMSCode(
			@Validated @PathVariable("username") String username);
	

	@GetMapping("/sms/{username}/{smscode}")
	public ResponseEntity<?> validateSMSCode(
			@Validated @PathVariable("username") String username, 
			@Validated @PathVariable("smscode") String smscode);

}
