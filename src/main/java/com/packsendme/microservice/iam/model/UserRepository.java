package com.packsendme.microservice.iam.model;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


public interface UserRepository extends MongoRepository<User, String> {
	
	@Query("{'username' :  {$eq: ?0}}")
	public User findByUsername(String username);
	
    @Query("{'username' : ?0, activationKey : {$eq: ?1}}")
	public User findUserByUsernameAndSMSCode(String username, String smsCode);
	
	
}
