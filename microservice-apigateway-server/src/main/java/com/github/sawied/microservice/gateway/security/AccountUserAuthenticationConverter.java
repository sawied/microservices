package com.github.sawied.microservice.gateway.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;

public class AccountUserAuthenticationConverter extends DefaultUserAuthenticationConverter{

	@Override
	public Authentication extractAuthentication(Map<String, ?> map) {
	
		Authentication authentication =super.extractAuthentication(map);
		
		
		Account account= null;
		if(map.containsKey("user_name") && authentication instanceof UsernamePasswordAuthenticationToken) {
			account= new Account(map.get("user_name").toString(),"N/A",authentication.getAuthorities());
			//account.setAccess_token(authentication.);
			Map<String, Object> additionInfo = new HashMap<String,Object>();
			additionInfo.put("user_name", (String)map.get("user_name"));
			if(map.containsKey("email")) {
				additionInfo.put("email", map.get("email"));
			}
			
			account.setAdditionalInfo(additionInfo);
			((UsernamePasswordAuthenticationToken)authentication).setDetails(account);
		}
		
		return authentication;
	}

	
	
}
