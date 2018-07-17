package com.packsendme.microservice.iam.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "User")
public class User {

	private @Id String id;
    
    private String name;
    
    private String username;
    
    private String password;

    private boolean activated;
    
    private String activationKey;
	
    private String resetPasswordKey;
    
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public String getActivationKey() {
		return activationKey;
	}

	public void setActivationKey(String activationKey) {
		this.activationKey = activationKey;
	}

	public String getResetPasswordKey() {
		return resetPasswordKey;
	}

	public void setResetPasswordKey(String resetPasswordKey) {
		this.resetPasswordKey = resetPasswordKey;
	}

	public User() {
    }
    
    public User(String id, String name, String username,String password, boolean activated, String activationKey,String resetPasswordKey) {
		super();
		this.id = id;
		this.name = name;
		this.username = username;
		this.password = password;
		this.activated = activated;
		this.activationKey = activationKey;
		this.resetPasswordKey = resetPasswordKey;
	}
    
    
    

}
