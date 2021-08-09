package com.packsendme.microservice.account.dao;

import java.util.List;

import com.packsendme.microservice.account.repository.UserModel;

public interface IUserMongo {

	public UserModel add(UserModel entity);

	public UserModel find(UserModel entity);
	
	public List<UserModel> findAll();
	
	public void remove(UserModel entity);
	
	public UserModel update(UserModel entity);
	
	
		

}
