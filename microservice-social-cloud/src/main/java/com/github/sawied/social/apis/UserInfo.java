package com.github.sawied.social.apis;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserInfo {

	private static final Logger LOG = LoggerFactory.getLogger(UserInfo.class);

	@RequestMapping(produces= {MediaType.APPLICATION_JSON_VALUE},method=RequestMethod.GET)
	public String get(Principal principal) {
		String detail = principal.getName();
		 LOG.info("obtain user info {} success.",principal.getClass());
		if (principal instanceof OAuth2AuthenticationToken){
			OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) principal;
			OAuth2User oauth2Principal = token.getPrincipal();
			detail=oauth2Principal.getAttributes().toString();
		}
		return "sign in with detail " + detail;
	}
	
}
