package com.github.sawied.microservice.oauth2.security;

import org.springframework.security.core.AuthenticationException;

public class BadTimestampException extends AuthenticationException {

	
	public BadTimestampException(String msg) {
		super(msg);
	}
	

	public BadTimestampException(String msg, Throwable t) {
		super(msg, t);
	}



	private static final long serialVersionUID = 7372387810832304786L;

}
