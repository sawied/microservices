package com.github.sawied.microservice.oauth2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

import com.github.sawied.microservice.oauth2.security.CookieTokenExtractor;

@Configuration
public class SecurityConfiguration {


	@Configuration
	@EnableResourceServer
	protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

		@Override
		public void configure(ResourceServerSecurityConfigurer resources) {

			resources.resourceId("OAUTH2_RESOURCE_ID").stateless(true).tokenExtractor(new CookieTokenExtractor());

		}

		@Override
		public void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests().antMatchers("/**").access("hasRole('USER')").and().csrf().disable()
					.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
		}

	}

}
