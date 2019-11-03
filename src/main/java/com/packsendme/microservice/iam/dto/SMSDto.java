package com.packsendme.microservice.iam.dto;

import java.io.Serializable;

public class SMSDto implements Serializable  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3844970754786153763L;

	private String smsCodeUsername;
	private long timeCreate;
	
	
	public SMSDto() {
		super();
	}

	public SMSDto(String smsCodeUsername, long timeCreate) {
		super();
		this.smsCodeUsername = smsCodeUsername;
		this.timeCreate = timeCreate;
	}

	public String getSmsCodeUsername() {
		return smsCodeUsername;
	}

	public void setSmsCodeUsername(String smsCodeUsername) {
		this.smsCodeUsername = smsCodeUsername;
	}

	public long getTimeCreate() {
		return timeCreate;
	}

	public void setTimeCreate(long timeCreate) {
		this.timeCreate = timeCreate;
	}
	
	
	
}
