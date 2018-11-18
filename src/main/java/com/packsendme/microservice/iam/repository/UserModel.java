package com.packsendme.microservice.iam.repository;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "User")
public class UserModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    private String id;
	private String username;
    private String password;
    private boolean activated;
    private String activationKey;
    private boolean resetPasswordKey;
    private Date dtCreation;
    private Date dtUpdate;
    
    
    public UserModel() {
	}

	public UserModel(String username, String password, boolean activated, String activationKey,
			boolean resetPasswordKey,Date dtCreation,Date dtUpdate) {
		super();
		this.username = username;
		this.password = password;
		this.activated = activated;
		this.activationKey = activationKey;
		this.resetPasswordKey = resetPasswordKey;
		this.setDtCreation(dtCreation);
		this.setDtUpdate(dtUpdate);
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

	public Date getDtCreation() {
		return dtCreation;
	}

	public void setDtCreation(Date dtCreation) {
		this.dtCreation = dtCreation;
	}

	public Date getDtUpdate() {
		return dtUpdate;
	}

	public void setDtUpdate(Date dtUpdate) {
		this.dtUpdate = dtUpdate;
	}

}
