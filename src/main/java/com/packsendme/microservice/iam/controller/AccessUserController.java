package com.packsendme.microservice.iam.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.packsendme.lib.common.response.Response;
import com.packsendme.microservice.iam.model.User;
import com.packsendme.microservice.iam.service.UserServiceImpl;

@RestController
@RequestMapping("/iam/api/access")
public class AccessUserController {
	
	@Autowired
	private UserServiceImpl userService;

	@RequestMapping("/user/oauth")
	public Principal getCurrentLoggedInUser(Principal user) {
		return user;
	}
	
	@RequestMapping("/user/account")
	public User getUser(User user) {
		return user;
	}
	
	@RequestMapping(method = RequestMethod.POST, path="/user/create/{username}/{password}", 
	produces = {MediaType.APPLICATION_JSON_VALUE},
	consumes = {MediaType.APPLICATION_JSON_VALUE})
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> createUserAccess(@Validated @PathVariable String username,  @Validated @PathVariable String password) {
		try {
			System.out.println(" USER ACCESS  "+username + " || "+ password );
			userService.updateUserAccess(username,password);
			Response<User> responseObj = new Response<User>(HttpStatus.CREATED, HttpStatus.CREATED.toString(), null);
			return new ResponseEntity<>(responseObj, HttpStatus.OK);
		}
		catch (Exception e) {
			e.printStackTrace();
			Response<User> responseObj = new Response<User>(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.toString(), null);
			return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
		
	@RequestMapping(method = RequestMethod.POST, path="/user/delete/{username}", 
	produces = {MediaType.APPLICATION_JSON_VALUE},
	consumes = {MediaType.APPLICATION_JSON_VALUE})
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> deleteUserAccess(@Validated @PathVariable String username) {
		try {
			System.out.println(" USER ACCESS  "+username);
			userService.deleteUserAccess(username);
			Response<User> responseObj = new Response<User>(HttpStatus.ACCEPTED, HttpStatus.ACCEPTED.toString(), null);
			return new ResponseEntity<>(responseObj, HttpStatus.OK);
		}
		catch (Exception e) {
			e.printStackTrace();
			Response<User> responseObj = new Response<User>(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.toString(), null);
			return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
