package com.packsendme.microservice.iam.dto;

import java.io.Serializable;

public class NamesAccountDto implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String firstName;
	public String lastName;

	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	
	

}
