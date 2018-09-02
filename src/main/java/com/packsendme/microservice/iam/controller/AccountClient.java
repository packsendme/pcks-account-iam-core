package com.packsendme.microservice.iam.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name="packsendme-account-server")
public interface AccountClient {
	
	@RequestMapping(method=RequestMethod.PUT, value="/account/api/update/username/{username}/{usernamenew}", consumes = {MediaType.APPLICATION_JSON_VALUE})
	ResponseEntity<?> changeUsernameAccount(@PathVariable("username") String username,  @PathVariable("usernamenew") String usernamenew);

}
