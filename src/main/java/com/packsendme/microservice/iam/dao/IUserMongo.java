package com.packsendme.microservice.iam.dao;

import java.util.List;

import com.packsendme.microservice.iam.repository.UserModel;

public interface IUserMongo {

	public UserModel add(UserModel entity);

	public UserModel find(UserModel entity);
	
	public List<UserModel> findAll();
	
	public void remove(UserModel entity);
	
	public UserModel update(UserModel entity);
	
	
		

}
