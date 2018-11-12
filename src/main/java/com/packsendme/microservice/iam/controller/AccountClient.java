package com.packsendme.microservice.iam.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name="packsendme-account-server")
public interface AccountClient {
	
	//@RequestMapping(method=RequestMethod.PUT, value="/account/{username}/{usernamenew}/{dtChange}")
	@PutMapping("/account/{username}/{usernamenew}/{dtAction}")
	public ResponseEntity<?> changeUsernameForAccount(
			@Validated @PathVariable ("username") String username,
			@Validated @PathVariable ("usernamenew") String usernamenew,
			@Validated @PathVariable ("dtAction") String dtAction);

}
