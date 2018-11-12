package com.packsendme.microservice.iam.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


public interface UserRepository extends MongoRepository<UserModel, String> {
	
	@Query("{'username' :  {$eq: ?0}}")
	public UserModel findUserByUsername(String username);
	
    @Query("{'username' : ?0, activationKey : {$eq: ?1}}")
	public UserModel findUserByUsernameAndSMSCode(String username, String smsCode);
	
    @Query("{'username' : ?0, 'activated' : true, password : {$eq: ?1}}")
	public UserModel findUserByLogin(String username, String status);
	
	
}
