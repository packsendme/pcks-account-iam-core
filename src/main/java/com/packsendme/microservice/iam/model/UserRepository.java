package com.packsendme.microservice.iam.model;

import org.springframework.data.mongodb.repository.MongoRepository;


public interface UserRepository extends MongoRepository<User, String> {
	
	public User findByUsername(String username);
	
}
