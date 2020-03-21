package com.github.sawied.microservice.gateway.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class AccountAuthenticationToken extends UsernamePasswordAuthenticationToken{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 267727741074955317L;
	
	
	private String timestamp;

	
	public AccountAuthenticationToken(Object principal, Object credentials, String timestamp) {
		super(principal, credentials);
		this.timestamp = timestamp;
	}


	public String getTimestamp() {
		return timestamp;
	}
	
	
}
