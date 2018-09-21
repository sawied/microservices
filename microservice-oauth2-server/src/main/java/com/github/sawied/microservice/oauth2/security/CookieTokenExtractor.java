package com.github.sawied.microservice.oauth2.security;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;

public class CookieTokenExtractor extends BearerTokenExtractor{
	
	private final static Logger logger = LoggerFactory.getLogger(CookieTokenExtractor.class);

	@Override
	protected String extractToken(HttpServletRequest request) {
		 
		String token =super.extractToken(request);
		 
		 if(token==null){
			 //try to fetch token from cookie
			 logger.info("Doesn't extact token from header and request parameters ,then try from cookie");
			 Cookie[] cookies=request.getCookies();
			 if(cookies!=null) {
				 for(int i =0 ;i < cookies.length;i++) {
					 Cookie cookie = cookies[i];
					 if(OAuth2AccessToken.ACCESS_TOKEN.equals(cookie.getName())){
						 token=cookie.getValue(); 
						 logger.debug("try to extact token from cookie success {}", token);
						 break;
					 }
				 }
			 }
		 }
		return token;
	}

	
	
	

}
