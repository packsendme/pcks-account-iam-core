package com.packsendme.microservice.iam.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClientException;
import com.packsendme.microservice.iam.repository.UserModel;
import com.packsendme.microservice.iam.repository.UserRepository;

@Component("userDAO")
public class UserDAO implements IUserMongo {
	
	@Autowired
	UserRepository userRepository;

	@Override
	public UserModel add(UserModel entity) {
		try {
			return  entity = userRepository.insert(entity);
		}
		catch (MongoClientException e ) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public UserModel find(UserModel entity) {
		UserModel user = null;
		
		if(entity.getUsername() != null && entity.getActivationKey() == null) {
			user = userRepository.findUserByUsername(entity.getUsername());
		}
		else if(entity.getUsername() != null && entity.getActivationKey() != null) {
			user = userRepository.findUserByUsernameAndSMSCode(entity.getUsername(), entity.getActivationKey());
		}
		return user;
	}

	@Override
	public List<UserModel> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(UserModel entity) {
		try {
			userRepository.delete(entity);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public UserModel update(UserModel entity) {
		try {
			return  entity = userRepository.save(entity);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	
	

}
