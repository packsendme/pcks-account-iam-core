package com.packsendme.microservice.iam.controller;

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
@RequestMapping("/iam/api/validate")
public class ValidateUserController {

	@Autowired
	private UserServiceImpl userService;
	
	@RequestMapping(method = RequestMethod.GET, path="/user/username/{username}", 
	produces = {MediaType.APPLICATION_JSON_VALUE},
	consumes = {MediaType.APPLICATION_JSON_VALUE})
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Response> validateUsername(@Validated @PathVariable("username") String username) {
		System.out.println(" USERNAME    "+ username);
		User userFind = userService.getFindUserBtUsername(username);
		System.out.println(" USERFIND    "+ userFind);

		// User does not exist in user base 
		if(userFind == null) {
			User userSave = userService.firstRegisterAccessUser(username);
			System.out.println(" USERSAVE    "+ userSave);
			if(userSave != null){
				Response<User> responseObj = new Response<User>(HttpStatus.CREATED, HttpStatus.CREATED.toString(), userSave);
				return new ResponseEntity<Response>(responseObj, HttpStatus.OK);
			}
			else {
				Response<User> responseObj = new Response<User>(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.toString(), null);
				return new ResponseEntity<Response>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		// User exist in user base 
		else {
			// User already exists in the database 
			Response<User> responseObj = new Response<User>(HttpStatus.FOUND, HttpStatus.FOUND.toString(), userFind);
			return new ResponseEntity<Response>(responseObj,HttpStatus.FOUND);
		}
	}
	
	@RequestMapping(method = RequestMethod.GET, path="/user/codeSMS/{username}/{codSMS}", 
	produces = {MediaType.APPLICATION_JSON_VALUE},
	consumes = {MediaType.APPLICATION_JSON_VALUE})
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Response> validateSMSCode(@Validated @PathVariable("username") String username,@Validated @PathVariable("codSMS") String codSMS) {
		User userFind = userService.getSMSCode(username, codSMS);

		// User does not exist in user base 
		if(userFind != null) {
				Response<User> responseObj = new Response<User>(HttpStatus.FOUND, HttpStatus.FOUND.toString(), userFind);
				return new ResponseEntity<Response>(responseObj, HttpStatus.FOUND);
		}
		else{
				Response<User> responseObj = new Response<User>(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.toString(), null);
				return new ResponseEntity<Response>(responseObj, HttpStatus.NOT_FOUND);
			}
	}

		
}
