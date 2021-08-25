package com.packsendme.microservice.account.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.packsendme.microservice.account.dto.UserDto;
import com.packsendme.microservice.account.service.UserFirstAccessService;
import com.packsendme.microservice.account.service.UserService;

@RestController
@RequestMapping("/account/iam")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserFirstAccessService firstAccessService;


	//** BEGIN OPERATION: USER FIRST ACCESS *************************************************//
	
	
	@GetMapping("/identity/{username}/{dtAction}")
	public ResponseEntity<?> validateFirstUserAccess(@Validated @PathVariable("username") String username,
			@Validated @PathVariable("dtAction") String dtAction) {
		return firstAccessService.findUserToFirstAccess(username,dtAction);
	}

	@PutMapping("/identity/{username}/{password}/{dtAction}")
	public ResponseEntity<?> createUser(@Validated @PathVariable("username") String username, 
			@Validated @PathVariable("password") String password,
			@Validated @PathVariable ("dtAction") String dtAction) throws Exception {
		return firstAccessService.registerUser(username,password, dtAction);
	}

	@GetMapping("/access/{username}/{password}")
	public ResponseEntity<?> loginPacksendme(@Validated @PathVariable("username") String username, @Validated @PathVariable("password") String password){
		return userService.findUserToLogin(username,password);
	}

	
	//** BEGIN OPERATION CRUD *************************************************//
	
 	
 	@DeleteMapping("/manager/{username}/{dtAction}")
	public ResponseEntity<?> cancelAccountUserAccess(@Validated @PathVariable("username") String username,
			@Validated @PathVariable("dtAction") String dtAction) throws Exception {
			return userService.cancelUserAccessByUsername(username,dtAction);
	}
	
 	@PutMapping("/manager/{username}/{usernamenew}/{smscode}/{dtAction}")
	public ResponseEntity<?> updateUsernameAccess(@Validated @PathVariable("username") String username, 
			@PathVariable("usernamenew") String usernamenew,@PathVariable("smscode") String smscode,
			@PathVariable("dtAction") String dtAction) {
			return userService.updateUsernameByValidateSMSCode(username, usernamenew, smscode, dtAction);
	}

 
 	@PutMapping("/manager/password/{username}/{password}/{dtUpdate}")
	public ResponseEntity<?> updatePasswordAccess(@Validated @PathVariable("username") String username,
			@Validated @PathVariable("password") String password, @PathVariable("dtUpdate") String dtUpdate) {
		return userService.updatePasswordByUsername(username,password,dtUpdate);
	}

}
