package com.github.sawied.microservice.gateway.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sawied.microservice.gateway.security.services.RemoteAccountService;

public class AccountRemoteAuthenticationProvider  implements AuthenticationProvider,
InitializingBean{
	
	private RemoteAccountService remoteAccountService;

	ObjectMapper om = new ObjectMapper();
	
	private TokenStore tokenStore;
	
	private static final Logger LOG = LoggerFactory.getLogger(AccountRemoteAuthenticationProvider.class);
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(remoteAccountService, "remote account Service is mandatory");
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		//oops
		String oAuth2Body=null;
		String accessToken=null;
		Authentication oauth2Authentication=null;
		try {			
			ResponseEntity<String> oAuth2Response = remoteAccountService.acquireAccount((AccountAuthenticationToken)authentication);
			oAuth2Body = oAuth2Response.getBody();
			accessToken = parseToken(oAuth2Body);
			oauth2Authentication = buildAccountFromResponse(accessToken);
		}catch(Exception e ) {
			LOG.error("oops,some error occured when  building access token. the message is ", e);
			throw new AuthenticationServiceException("maybe system error be going on when building access token", e);
		}
		
		return oauth2Authentication;
	}
	
	private String parseToken(String body) throws IOException {
		JsonNode jsonNode = om.readTree(body);
		return jsonNode.path("access_token").asText();
	}
	
	private OAuth2Authentication buildAccountFromResponse(String accessToken) throws IOException {
		OAuth2AccessToken oauthToken = tokenStore.readAccessToken(accessToken);
		OAuth2Authentication oauth2Authentication = tokenStore.readAuthentication(oauthToken);
		
		if(oauth2Authentication!=null && oauth2Authentication.getUserAuthentication().getDetails()!=null) {
			Account account = (Account) oauth2Authentication.getUserAuthentication().getDetails();
			account.setAccess_token(accessToken);
			account.setExpiresIn(oauthToken.getExpiresIn());
			account.setExpiration(oauthToken.getExpiration());
			account.setRefresh_token(accessToken);
		}
		
		
		 if (oauth2Authentication.getUserAuthentication().getDetails() instanceof Account) {
		}
		return oauth2Authentication;
	}


	@Override
	public boolean supports(Class<?> authentication) {
		return (AccountAuthenticationToken.class
				.isAssignableFrom(authentication));
	}

	public void setRemoteAccountService(RemoteAccountService remoteAccountService) {
		this.remoteAccountService = remoteAccountService;
	}

	public AccountRemoteAuthenticationProvider accountService(RemoteAccountService remoteAccountService) {
		this.remoteAccountService=remoteAccountService;
		return this;
	}

	public void setTokenStore(TokenStore tokenStore) {
		this.tokenStore = tokenStore;
	}
	
	
	
	

}
