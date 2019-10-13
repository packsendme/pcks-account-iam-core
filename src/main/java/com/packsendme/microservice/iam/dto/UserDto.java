package com.packsendme.microservice.iam.dto;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

public class UserDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    private String id;
	private String username;
    private String password;
    private boolean activated;
    private String activationKey;
    private boolean resetPasswordKey;
    private String dateOperation;
    
    
    public UserDto() {
	}

	public UserDto(String username, String password, boolean activated, String activationKey,
			boolean resetPasswordKey, String dateOperation) {
		super();
		this.username = username;
		this.password = password;
		this.activated = activated;
		this.activationKey = activationKey;
		this.resetPasswordKey = resetPasswordKey;
		this.dateOperation = dateOperation;
		
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDateOperation() {
		return dateOperation;
	}

	public void setDateOperation(String dateOperation) {
		this.dateOperation = dateOperation;
	}

}
