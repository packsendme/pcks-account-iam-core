package com.packsendme.microservice.iam.dto;

import java.io.Serializable;

public class SMSDto implements Serializable  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3844970754786153763L;

	private long smsCode;
	private String username;
	private long timeCreate;
	
	
	

	public SMSDto(long smsCode, String username, long timeCreate) {
		super();
		this.smsCode = smsCode;
		this.username = username;
		this.timeCreate = timeCreate;
	}

	public long getSmsCode() {
		return smsCode;
	}

	public void setSmsCode(long smsCode) {
		this.smsCode = smsCode;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public long getTimeCreate() {
		return timeCreate;
	}

	public void setTimeCreate(long timeCreate) {
		this.timeCreate = timeCreate;
	}
	
	
	
}
