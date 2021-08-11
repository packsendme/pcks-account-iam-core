package com.packsendme.microservice.account.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name="pcks-account-customer-core")
public interface AccountClient {
	
	@PutMapping("/account/customer/{username}/{usernamenew}/{dtAction}")
	public ResponseEntity<?> changeUsernameForAccount(
			@Validated @PathVariable ("username") String username,
			@Validated @PathVariable ("usernamenew") String usernamenew,
			@Validated @PathVariable ("dtAction") String dtAction);
	
	
	@GetMapping("/account/customer/personalname/{username}")
	public ResponseEntity<?> loadFirstNameAccount(
			@Validated @PathVariable ("username") String username);

}
