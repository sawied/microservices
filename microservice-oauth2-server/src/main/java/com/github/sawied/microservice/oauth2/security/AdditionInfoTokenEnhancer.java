package com.github.sawied.microservice.oauth2.security;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

public class AdditionInfoTokenEnhancer implements TokenEnhancer {

	private static final Logger log = LoggerFactory.getLogger(AdditionInfoTokenEnhancer.class);

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		OAuth2AccessToken result = accessToken;
		if (authentication.getPrincipal() instanceof AccountDetails) {
			AccountDetails accountDetails = (AccountDetails) authentication.getPrincipal();
			result = new DefaultOAuth2AccessToken(accessToken);
			Map<String, Object> info = new LinkedHashMap<String, Object>(accessToken.getAdditionalInformation());
			info.put("email", accountDetails.getEmail());
			((DefaultOAuth2AccessToken) result).setAdditionalInformation(info);
			log.debug("user additionInfo added into access token : {}", accountDetails.toString());

		} else {
			log.debug("principal is {} and accessToken is {} ,that don't match the condition to add addition info.",
					authentication.getPrincipal(), accessToken);
		}
		return result;
	}

}
