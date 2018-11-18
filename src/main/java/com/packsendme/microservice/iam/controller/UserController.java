package com.packsendme.microservice.iam.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.packsendme.microservice.iam.service.UserFirstAccessService;
import com.packsendme.microservice.iam.service.UserService;

@RestController
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserFirstAccessService firstAccessService;
	

	//** BEGIN OPERATION: USER FIRST ACCESS *************************************************//
	
	
	@PostMapping("/iam/identity/{username}/{dtAction}")
	public ResponseEntity<?> validateFirstUserAccess(@Validated @PathVariable("username") String username,
			@Validated @PathVariable("dtAction") String dtAction) {
		return firstAccessService.findUserToFirstAccess(username,dtAction);
	}

	@GetMapping("/iam/identity/sms/{username}/{smscode}")
	public ResponseEntity<?> validateSMSCodeFirstUserAccess(@Validated @PathVariable("username") String username, @Validated @PathVariable("smscode") long smscode) throws Exception {
		return firstAccessService.findSMSCodeUserToFirstAccess(username,smscode);
	}
	
	
	@PutMapping("/iam/access/{username}/{password}/{dtAction}")
	public ResponseEntity<?> allowsFirstUserAccess(@Validated @PathVariable("username") String username, 
			@Validated @PathVariable("password") String password,
			@Validated @PathVariable ("dtAction") String dtAction) throws Exception {
		return firstAccessService.enableFirstUserAccess(username,password, dtAction);
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
	
 	@PutMapping("/iam/manager/{username}/{usernamenew}")
	public ResponseEntity<?> validateChangeUsernameToUser(@Validated @PathVariable("username") String username, 
			@PathVariable("usernamenew") String usernamenew) {
		return userService.checkUsernameForChange(username,usernamenew);
	}

 	@PutMapping("/iam/manager/{username}/{usernamenew}/{smscode}/{dtAction}")
	public ResponseEntity<?> changeUsernameToUser(@Validated @PathVariable("username") String username, @PathVariable("usernamenew") String usernamenew,
			@Validated @PathVariable("smscode") Long smscode,@Validated @PathVariable("dtAction") String dtAction) {
		return userService.updateUserByUsernamenew(username,usernamenew,smscode,dtAction);
	}

 	@PutMapping("/iam/manager/{username}/{password}/{dtAction}")
	public ResponseEntity<?> changePasswordToUser(@Validated @PathVariable("username") String username,
			@Validated @PathVariable("password") String password,
			@Validated @PathVariable("dtAction") String dtAction) {
		return userService.updatePasswordByUsername(username,password,dtAction);
	}

}
