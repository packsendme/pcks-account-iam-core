package com.packsendme.microservice.iam.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.packsendme.lib.common.constants.HttpExceptionPackSend;
import com.packsendme.lib.common.response.Response;
import com.packsendme.microservice.iam.repository.UserModel;
import com.packsendme.microservice.iam.service.SMSCache;
import com.packsendme.microservice.iam.service.UserFirstAccessService;
import com.packsendme.microservice.iam.service.UserService;

@RestController
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserFirstAccessService firstAccessService;

	
	@Autowired
	private SMSCache userSMSO;
	

	//** BEGIN OPERATION: USER FIRST ACCESS *************************************************//
	
	
	@GetMapping("/iam/identity/{username}/{dtAction}")
	public ResponseEntity<?> validateFirstUserAccess(@Validated @PathVariable("username") String username,
			@Validated @PathVariable("dtAction") String dtAction) {
		return firstAccessService.findUserToFirstAccess(username,dtAction);
	}

	@GetMapping("/iam/identity/sms/{username}/{smscode}")
	public ResponseEntity<?> validateSMSCodeFirstUserAccess(@Validated @PathVariable("username") String username, 
			@Validated @PathVariable("smscode") String smscode) throws Exception {
		return firstAccessService.findSMSCodeToFirstAccess(username,smscode);
	}
	
	
	@PutMapping("/iam/identity/{username}/{password}/{dtAction}")
	public ResponseEntity<?> createUser(@Validated @PathVariable("username") String username, 
			@Validated @PathVariable("password") String password,
			@Validated @PathVariable ("dtAction") String dtAction) throws Exception {
		return firstAccessService.registerUser(username,password, dtAction);
	}

	@GetMapping("/iam/access/{username}/{password}")
	public ResponseEntity<?> loginPacksendme(@Validated @PathVariable("username") String username, @Validated @PathVariable("password") String password){
		return userService.findUserToLogin(username,password);
	}

	
	//** BEGIN OPERATION CRUD *************************************************//
	
 	
 	@DeleteMapping("/iam/manager/{username}/{dtAction}")
	public ResponseEntity<?> cancelAccountUserAccess(@Validated @PathVariable("username") String username,
			@Validated @PathVariable("dtAction") String dtAction) throws Exception {
			return userService.cancelUserAccessByUsername(username,dtAction);
	}
	
 	@GetMapping("/iam/manager/sms/{username}/{usernamenew}/{smscode}/{dtAction}")
	public ResponseEntity<?> validateSMSCodeToUpdateUser(@Validated @PathVariable("username") String username, 
			@PathVariable("usernamenew") String usernamenew,@PathVariable("smscode") String smscode,
			@PathVariable("dtAction") String dtAction) {
			return userService.getSMSCodeToUpdateUser(username, usernamenew, smscode, dtAction);
	}

 
 	@PutMapping("/iam/manager/{username}/{password}/{dtAction}")
	public ResponseEntity<?> changePasswordToUser(@Validated @PathVariable("username") String username,
			@Validated @PathVariable("password") String password,
			@Validated @PathVariable("dtAction") String dtAction) {
		return userService.updatePasswordByUsername(username,password,dtAction);
	}
 	
 	@DeleteMapping("/iam/manager/sms/{username}")
	public ResponseEntity<?> deleteSMSCode(@Validated @PathVariable("username") String username) {
 		Response<UserModel> responseObj = new Response<UserModel>(HttpExceptionPackSend.NOT_FOUND_SMS_CODE.getAction(), null);
 		userSMSO.evict(username);
 		return new ResponseEntity<>(responseObj, HttpStatus.NOT_FOUND);
 		
	}

}
