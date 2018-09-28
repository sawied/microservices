package com.github.sawied.microservice.oauth2.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

import com.github.sawied.microservice.oauth2.security.AccountRequestDetails;

@Configuration
public class ServletWebConfig {

	@Bean
	@RequestScope
	public AccountRequestDetails accountDetails(@Value("#{request.getHeader('Host')}") String host,@Value("#{request.getHeader('x-forward-for')}") String xforwardfor) {
		String real=host;
		if(xforwardfor!=null) {
			String[] ips=xforwardfor.split(",");
			if(ips.length>0) {
				real=ips[0].trim();
			}
		}
		return new AccountRequestDetails(real);
	}

}
