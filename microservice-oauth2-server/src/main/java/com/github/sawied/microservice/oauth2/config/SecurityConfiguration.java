package com.github.sawied.microservice.oauth2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

import com.github.sawied.microservice.oauth2.security.JpaAuthenticationProvider;

@Configuration
//@EnableWebSecurity(debug=true)
public class SecurityConfiguration {

	@Autowired
	public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(jpaAuthenticationProvider());
	}

	public JpaAuthenticationProvider jpaAuthenticationProvider(){
		JpaAuthenticationProvider jpaAuthenticationProvider= new JpaAuthenticationProvider();
		jpaAuthenticationProvider.setUserDetailsService(new UserDetailsService() {
			
			@Override
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
				
				return new User(username, "{noop}password", AuthorityUtils.createAuthorityList("ROLE_USER"));
			}
		});
		return jpaAuthenticationProvider;
	}
	
	/**
	 * @Override public void configure(WebSecurity web) throws Exception {
	 *           web.ignoring().antMatchers("/webjars/**", "/images/**",
	 *           "/oauth/uncache_approvals", "/oauth/cache_approvals"); }
	 * 
	 * 
	 * @Override
	 * @Bean public AuthenticationManager authenticationManagerBean() throws
	 *       Exception { return super.authenticationManagerBean(); }
	 **/

	@Configuration
	@EnableResourceServer
	protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

		@Override
		public void configure(ResourceServerSecurityConfigurer resources) {
			resources.resourceId("OAUTH2_RESOURCE_ID").stateless(true);
		}

		/**
		 * @Override public void configure(HttpSecurity http) throws Exception {
		 *           http.authorizeRequests().antMatchers("/**").access("hasRole('USER')").and().csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
		 *           ; }
		 **/

	}

}
