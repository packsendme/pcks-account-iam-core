package com.packsendme.microservice.iam.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class AuthController {

	
	   
	@RequestMapping("/user")
	public Principal getCurrentLoggedInUser(Principal user) {
		return user;
	}
	  

	
}
