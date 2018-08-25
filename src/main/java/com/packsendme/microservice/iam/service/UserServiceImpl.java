package com.packsendme.microservice.iam.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mongodb.MongoClientException;
import com.packsendme.microservice.iam.component.SMSCodeManagement;
import com.packsendme.microservice.iam.model.User;
import com.packsendme.microservice.iam.model.UserRepository;

@Service(value = "userService")
public class UserServiceImpl  implements UserDetailsService
{

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	SMSCodeManagement smsObj;

	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		//username = "ricardomarzochi";
		System.out.println(" D LOADUSERBYUSERNAME B "+ username);
		
		User user = userRepository.findByUsername(username);

		if (user != null) {
			System.out.println(" TESTE userdetails C ");
			return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), getAuthority());
		}
		else {
			System.out.println(" TESTE userdetails D ");
			throw new UsernameNotFoundException("could not find the user '"
	                  + username + "'");
		}
	}
	
	private List getAuthority() {
		return Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
	}
	
	public User firstRegisterAccessUser(String username) {
		String codSMS = smsObj.generateSMSCode();
		User user = new User("1",username, "#######", false, codSMS, false);
		return addUser(user);
	}

	public User getSMSCode(String username, String smsCode) {
		try {
			User user = userRepository.findUserByUsernameAndSMSCode(username,smsCode);
			return user;
		}
		catch (MongoClientException e ) {
			e.printStackTrace();
			return null;
		}
	}
	
	public User getFindUserBtUsername(String username) {
		try {
			User user = userRepository.findByUsername(username);
			return user;
		}
		catch (MongoClientException e ) {
			e.printStackTrace();
			return null;
		}
	}

	public User addUser(User entity) {
		try {
			return  entity = userRepository.insert(entity);
		}
		catch (MongoClientException e ) {
			e.printStackTrace();
			return null;
		}
	}

	public User updateUserAccess(String username, String password) {
		try {
			User user = getFindUserBtUsername(username);
			System.out.println(" updateUserAccess ACCESS  "+user.getUsername());

			user.setPassword(password);
			user.setActivated(true);
			user.setActivationKey("######");
			user.setResetPasswordKey(false);
			return user = userRepository.save(user);
		}
		catch (MongoClientException e ) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public void deleteUserAccess(String username) {
		try {
			User user = getFindUserBtUsername(username);
			if(user != null) {
				userRepository.delete(user);
				System.out.println(" DELETE USER  "+ user.getUsername());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
