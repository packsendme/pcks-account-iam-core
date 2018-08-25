package com.packsendme.microservice.iam.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "User")
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private String username;
    private String password;
    private boolean activated;
    private String activationKey;
    private boolean resetPasswordKey;

	public User(String id,String username, String password, boolean activated, String activationKey,
			boolean resetPasswordKey) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.activated = activated;
		this.activationKey = activationKey;
		this.resetPasswordKey = resetPasswordKey;
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

	public boolean getResetPasswordKey() {
		return resetPasswordKey;
	}

	public void setResetPasswordKey(boolean resetPasswordKey) {
		this.resetPasswordKey = resetPasswordKey;
	}

}
