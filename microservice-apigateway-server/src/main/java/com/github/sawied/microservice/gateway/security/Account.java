package com.github.sawied.microservice.gateway.security;

import java.io.Serializable;
import java.util.Map;

public class Account implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 96982119034970680L;
	

	private String principle;
	
	private String username;
	
	private Map<String,Object> additionalInfo;


	public Account(String username,String principle) {
		super();
		this.principle = principle;
		this.username = username;
	}

	public Map<String, Object> getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(Map<String, Object> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public String getPrinciple() {
		return principle;
	}

	public String getUsername() {
		return username;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((principle == null) ? 0 : principle.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		if (principle == null) {
			if (other.principle != null)
				return false;
		} else if (!principle.equals(other.principle))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	
}
