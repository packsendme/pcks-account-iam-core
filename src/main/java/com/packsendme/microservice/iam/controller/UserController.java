package com.packsendme.microservice.iam.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.packsendme.microservice.iam.service.UserService;

@RestController
@RequestMapping("/iam/api/")
public class UserController {
	
	@Autowired
	private UserService userService;
 	@RequestMapping(method = RequestMethod.PUT, path="/enable/access/{username}/{password}", 
	produces = {MediaType.APPLICATION_JSON_VALUE},
	consumes = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> enableUserAccess(@Validated @PathVariable("username") String username,	@Validated @PathVariable("password") String password) {
		return userService.allowUserAccessFirstRegister(username,password);
	}
		
	@RequestMapping(method = RequestMethod.DELETE, path="/cancel/access/{username}", 
	produces = {MediaType.APPLICATION_JSON_VALUE},
	consumes = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> cancelUserAccessRegistration(@Validated @PathVariable("username") String username) {
			return userService.cancelUserAccess(username);
	}
	
	@RequestMapping(method = RequestMethod.POST, path="/validate/username/{username}", 
	produces = {MediaType.APPLICATION_JSON_VALUE},
	consumes = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> usernameFirstUserAccess(@Validated @PathVariable("username") String username) {
		return userService.getUsernameAccessCheck(username,null);
	}

	@RequestMapping(method = RequestMethod.POST, path="/validate/update/user/{username}/{usernamenew}", 
	produces = {MediaType.APPLICATION_JSON_VALUE},
	consumes = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> usernameUpdateAccess(@Validated @PathVariable("username") String username, 
			@PathVariable("usernamenew") String usernamenew) {
		System.out.println(" userAccessValidate ");
		return userService.getUsernameAccessCheck(username,usernamenew);
	}

	
	@RequestMapping(method = RequestMethod.GET, path="/validate/smscode/{username}/{smscode}", 
	produces = {MediaType.APPLICATION_JSON_VALUE},
	consumes = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> smsCodeFirstUserAccess(@Validated @PathVariable("username") String username, @Validated @PathVariable("smscode") String smscode) {
		return userService.getSMSValidateProcess(username,null,smscode);
	}
	
	@RequestMapping(method = RequestMethod.PUT, path="/validate/update/sms/{username}/{usernamenew}/{smscode}", 
	produces = {MediaType.APPLICATION_JSON_VALUE},
	consumes = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> smsCodeUpdateUserAccess(@Validated @PathVariable("username") String username, @PathVariable("usernamenew") String usernamenew,
			@Validated @PathVariable("smscode") String smscode) {
		return userService.getSMSValidateProcess(username,usernamenew,smscode);
	}


}
