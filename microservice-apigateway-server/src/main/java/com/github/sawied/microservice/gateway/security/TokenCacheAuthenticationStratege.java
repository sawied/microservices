package com.github.sawied.microservice.gateway.security;

import javax.cache.Cache;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

public class TokenCacheAuthenticationStratege implements SessionAuthenticationStrategy {

	private static final Logger LOG = LoggerFactory.getLogger(TokenCacheAuthenticationStratege.class);

	private Cache<String, String> tokenCache;

	public TokenCacheAuthenticationStratege(Cache<String, String> tokenCache) {
		super();
		this.tokenCache = tokenCache;
	}

	@Override
	public void onAuthentication(Authentication authentication, HttpServletRequest request,
			HttpServletResponse response) throws SessionAuthenticationException {
		Object principal = authentication.getPrincipal();
		Account account = null;
		// authentication;
		if (authentication instanceof OAuth2Authentication) {
			OAuth2Authentication oauth2Authentication = (OAuth2Authentication) authentication;
			Authentication userAuthentication = oauth2Authentication.getUserAuthentication();
			if (userAuthentication != null && userAuthentication.getDetails() != null
					&& userAuthentication.getDetails() instanceof Account) {
				account = (Account) userAuthentication.getDetails();
			}
		}

		LOG.info("attempt to store token into cache ,will be used to validate token in consequece request {}",
				principal);

		if (account != null && account.getAccess_token() != null) {
			tokenCache.put((String) authentication.getPrincipal(), account.getAccess_token());
		}
	}

}
