package com.github.sawied.social.apis;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserInfo {

	private static final Logger LOG = LoggerFactory.getLogger(UserInfo.class);
	
	@RequestMapping(produces= {MediaType.APPLICATION_JSON.APPLICATION_JSON_VALUE},method=RequestMethod.GET)
	public String get(Principal principal) {
		LOG.info("obtain user info success.");
		System.out.println("hello world");
		 return "the name--:"+principal.getName();
	}
	
}
