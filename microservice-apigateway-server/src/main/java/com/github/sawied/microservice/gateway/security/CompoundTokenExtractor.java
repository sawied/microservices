package com.github.sawied.microservice.gateway.security;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;

public class CompoundTokenExtractor extends BearerTokenExtractor {

	private final static Logger logger = LoggerFactory.getLogger(CompoundTokenExtractor.class);
	
	
	private Boolean sessionAssociate=false;

	@Override
	protected String extractToken(HttpServletRequest request) {

		String token = null;

		// first try to extract token from session
		if(sessionAssociate) {	
			logger.info("session associate model, so ignore token extracat");
			//return null;
		}

		// then attempt extract token in header and request parameters.

		token = super.extractToken(request);
		
		return token;
	}

	public void setSessionAssociate(Boolean sessionAssociate) {
		this.sessionAssociate = sessionAssociate;
	}
	
	
	

}
