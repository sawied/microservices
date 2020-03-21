package com.github.sawied.microservice.gateway.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class SessionTokenAuthenticationStratege implements SessionAuthenticationStrategy,InitializingBean{

	private SessionAuthenticationStrategy sessionStratege;
	
	private SessionAuthenticationStrategy tokenStratege;
	
	private static final Logger LOG = LoggerFactory.getLogger(SessionTokenAuthenticationStratege.class);
	
	
	
	/**
	 * make sure the sequence of parameters passed in
	 * @param sessionStratege
	 * @param tokenStratege
	 */
	public SessionTokenAuthenticationStratege(SessionAuthenticationStrategy sessionStratege,
			SessionAuthenticationStrategy tokenStratege) {
		super();
		this.sessionStratege = sessionStratege;
		this.tokenStratege = tokenStratege;
	}


	@Override
	public void onAuthentication(Authentication authentication, HttpServletRequest request,
			HttpServletResponse response) throws SessionAuthenticationException {
		//TODO
		String token=(String) request.getAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_VALUE);
		Object tokenIsPresent = request.getAttribute(AccountAuthenticationFilter.TOKEN_IS_PRESENT);
		
		if(!StringUtils.isEmpty(token)&&Boolean.TRUE.equals(tokenIsPresent)) {
			LOG.debug("the authentication model should be token model, try to do token authentication stratege.");
			tokenStratege.onAuthentication(authentication, request, response);
		}else{
			LOG.debug("the authentication model should be session model, try to do session authentication stratege.");
			sessionStratege.onAuthentication(authentication, request, response);
		}
		
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(sessionStratege, "session authentication stratege is required");
		Assert.notNull(tokenStratege, "token authentication stratege is required");
	}


	public void setSessionStratege(SessionAuthenticationStrategy sessionStratege) {
		this.sessionStratege = sessionStratege;
	}


	public void setTokenStratege(SessionAuthenticationStrategy tokenStratege) {
		this.tokenStratege = tokenStratege;
	}
	
	
	

}
