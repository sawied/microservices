package com.github.sawied.microservice.oauth2.security;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.ClassUtils;
import org.springframework.util.TypeUtils;

public class JpaAuthenticationProvider extends DaoAuthenticationProvider{

	private static final Object TIMESTAMP = "timestamp";
	
	private static final Logger log = LoggerFactory.getLogger(JpaAuthenticationProvider.class);
	
	@Autowired
	private AccountRequestDetails accountRequestDetails;

	@Override
	@SuppressWarnings("unchecked")
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		
		super.additionalAuthenticationChecks(userDetails, authentication);
		
		//additional check for timestamp
		if(userDetails !=null && userDetails instanceof AccountDetails ) {
			if(authentication.getDetails() instanceof Map) {				
				Map<String,String> details = (Map<String, String>) authentication.getDetails();
				String timestamp = details.get(TIMESTAMP);
				AccountAuthenticationDetailService service=(AccountAuthenticationDetailService)this.getUserDetailsService();
				String principal = (String)authentication.getPrincipal();
				if(!service.check(principal,timestamp)){
					log.warn("notice: timestamp check failed for user :{} timestamp: {} ",authentication.getPrincipal(),timestamp);
					throw new BadTimestampException("invaild timestamp for authentication");
				}else {
					service.traceLogon(principal,timestamp,accountRequestDetails.getClientIp());
				}
			}
			
		}
	}
	

	
}
