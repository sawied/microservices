package com.github.sawied.microservice.gateway.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;

public class CompoundTokenExtractor extends BearerTokenExtractor {

	private final static Logger logger = LoggerFactory.getLogger(CompoundTokenExtractor.class);

	@Override
	protected String extractToken(HttpServletRequest request) {

		String token = null;

		// first try to extract token from session
		HttpSession session = request.getSession(false);
		if (session != null) {
			token = (String) session.getAttribute("access_token");
			logger.debug("extract token {} from session ", token);
		}

		// then attempt extract token in header and request parameters.

		if (token == null) {
			token = super.extractToken(request);
		}

		return token;
	}

}
