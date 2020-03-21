package com.github.sawied.microservice.gateway.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

public class AccountAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	public static final String TOKEN_IS_PRESENT = "TOKEN_IS_PRESENT";

	private static final String TIMSTAMP = "timestamp";
	
	private boolean postOnly = true;
	
	private RequestMatcher secureMatcher = new AntPathRequestMatcher("/oauth2/secure");
	
	private RequestMatcher simpleMatcher = new AntPathRequestMatcher("/oauth2/simple");
	
	
	private TextEncryptor textEncryptor = new NoOpTextEncryptor();
	
	
	private Boolean isAssociate=false;
	
	private static final Logger log = LoggerFactory.getLogger(AccountAuthenticationFilter.class);
	

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		if (postOnly && !request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		}

		String username = obtainUsername(request);
		String password = obtainPassword(request);
		String timestamp = obtainTimestamp(request);

		if (username == null) {
			username = "";
		}

		if (password == null) {
			password = "";
		}
		username = username.trim();
		
		if(secureMatcher.matches(request)) {
			if(StringUtils.isEmpty(timestamp)) {
				log.error("secure acquire access token ,the timestamp request parameter can't be null");
				throw new AuthenticationServiceException("in Secure authentication model the timestamp must be absent. ");
			}
			username=textEncryptor.decrypt(username);
			password=textEncryptor.decrypt(password);
			timestamp=textEncryptor.decrypt(timestamp);
		}else {
			if(StringUtils.isEmpty(timestamp)) {
				timestamp = String.valueOf(System.currentTimeMillis());
			}
		}
		
		
		request.setAttribute(TOKEN_IS_PRESENT,!(simpleMatcher.matches(request)&&Boolean.TRUE.equals(isAssociate)));
		
	
		
		AccountAuthenticationToken authRequest= new AccountAuthenticationToken(username,password,timestamp);
		

		// Allow subclasses to set the "details" property
		setDetails(request, authRequest);

		return this.getAuthenticationManager().authenticate(authRequest);

	}

	private String obtainTimestamp(HttpServletRequest request) {
		return request.getParameter(TIMSTAMP);
	}

	public void setPostOnly(boolean postOnly) {
			this.postOnly = postOnly;
	}
	
	public void setTextEncryptor(TextEncryptor textEncryptor) {
		this.textEncryptor = textEncryptor;
	}


	private static final class NoOpTextEncryptor implements TextEncryptor {

		public String encrypt(String text) {
			return text;
		}

		public String decrypt(String encryptedText) {
			return encryptedText;
		}

	}


	public void setIsAssociate(Boolean isAssociate) {
		this.isAssociate = isAssociate;
	}

	
	
	

}
