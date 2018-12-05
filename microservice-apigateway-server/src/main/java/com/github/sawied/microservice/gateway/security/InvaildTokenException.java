package com.github.sawied.microservice.gateway.security;

import org.springframework.security.core.AuthenticationException;

public class InvaildTokenException extends AuthenticationException {

	
	private static final long serialVersionUID = 7371448383226601706L;

	public InvaildTokenException(String msg, Throwable t) {
		super(msg, t);
	}

	public InvaildTokenException(String msg) {
		super(msg);
	}

	
}
