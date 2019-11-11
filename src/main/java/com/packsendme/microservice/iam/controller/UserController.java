package com.packsendme.microservice.iam.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.packsendme.microservice.iam.dto.UserDto;
import com.packsendme.microservice.iam.service.UserFirstAccessService;
import com.packsendme.microservice.iam.service.UserService;

@RestController
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserFirstAccessService firstAccessService;


	//** BEGIN OPERATION: USER FIRST ACCESS *************************************************//
	
	
	@GetMapping("/iam/identity/{username}/{dtAction}")
	public ResponseEntity<?> validateFirstUserAccess(@Validated @PathVariable("username") String username,
			@Validated @PathVariable("dtAction") String dtAction) {
		return firstAccessService.findUserToFirstAccess(username,dtAction);
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
	
 	@PutMapping("/iam/manager/{username}/{usernamenew}/{smscode}/{dtAction}")
	public ResponseEntity<?> changeUsername(@Validated @PathVariable("username") String username, 
			@PathVariable("usernamenew") String usernamenew,@PathVariable("smscode") String smscode,
			@PathVariable("dtAction") String dtAction) {
			return userService.updateUsernameByValidateSMSCode(username, usernamenew, smscode, dtAction);
	}

 
 	@PutMapping("/iam/manager/")
	public ResponseEntity<?> changePasswordToUser(@Validated @RequestBody UserDto user) {
		return userService.updatePasswordByUsername(user);
	}

}
