package com.github.sawied.microservice.oauth2.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configurers.ldap.LdapAuthenticationProviderConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.github.sawied.microservice.oauth2.jpa.service.AccountService;
import com.github.sawied.microservice.oauth2.security.AccountAuthenticationDetailService;
import com.github.sawied.microservice.oauth2.security.AdditionInfoTokenEnhancer;
import com.github.sawied.microservice.oauth2.security.JpaAuthenticationProvider;



@Configuration
@EnableAuthorizationServer
public class Oauth2ServerConfig extends AuthorizationServerConfigurerAdapter {

	
	
	@Autowired
	private UserApprovalHandler approvalHandler;
	@Autowired
	@Qualifier("jwtTokenStore")
	private TokenStore tokenStore;
	@Autowired
	@Qualifier("tokenServices")
	private AuthorizationServerTokenServices tokenServices;
	@Autowired
	@Qualifier("jpaAuthenticationProvider")
	private AuthenticationProvider jpaAuthenticationProvider;
	

	
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.allowFormAuthenticationForClients().checkTokenAccess("isAuthenticated()");
	}
	

	/**
	 * if authentication manager object is present,then spring oauth2 security framework will enable the feather of password grant. 
	 */
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		List<AuthenticationProvider> AuthenticationProviders = new ArrayList<AuthenticationProvider>();
		AuthenticationProviders.add(jpaAuthenticationProvider);
		ProviderManager pm = new ProviderManager(AuthenticationProviders);
		endpoints.tokenStore(tokenStore).userApprovalHandler(approvalHandler).authenticationManager(pm).tokenServices(tokenServices);
	}
	
	
	
	@Bean
	public AccountAuthenticationDetailService userDetailService(AccountService accountService){
		AccountAuthenticationDetailService userDetailService = new AccountAuthenticationDetailService();
		userDetailService.setAccountService(accountService);
		return userDetailService;
	}

	@Bean
	public JpaAuthenticationProvider jpaAuthenticationProvider(AccountAuthenticationDetailService userDetailService) {
		JpaAuthenticationProvider jpaAuthenticationProvider = new JpaAuthenticationProvider();
		jpaAuthenticationProvider.setUserDetailsService(userDetailService);
		return jpaAuthenticationProvider;
	}
	
	
	public LdapAuthenticationProvider ldapAuthenticationProvider() {
		//LdapAuthenticator authenticator=new BindAuthenticator();
		//new LdapAuthenticationProvider(authenticator);
		return null;
	}

	/**
	 * specific client detail service
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.withClientDetails(clientDetailsService());
	}
	
	@Bean
    public TokenEnhancerChain tokenEnhancerChain(){
      List<TokenEnhancer> list = new ArrayList<>();
      list.add(new AdditionInfoTokenEnhancer());
      list.add(jwtAccessTokenConverter());
      TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
      tokenEnhancerChain.setTokenEnhancers(list);
      return tokenEnhancerChain;
      
    }
    @Bean
    @Primary
    public DefaultTokenServices tokenServices(){
      DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
      defaultTokenServices.setTokenStore(tokenStore);
      defaultTokenServices.setTokenEnhancer(tokenEnhancerChain());
      defaultTokenServices.setSupportRefreshToken(true);
      defaultTokenServices.setAccessTokenValiditySeconds(60*60*9);
      return defaultTokenServices;
      
    }
	

	@Bean
	public OAuth2RequestFactory requestFactory(ClientDetailsService clientDetailsService) {
		OAuth2RequestFactory requestFactory = new DefaultOAuth2RequestFactory(clientDetailsService);
		return requestFactory;
	}

	@Bean
	public UserApprovalHandler userApprovalHandler(OAuth2RequestFactory requestFactory,ClientDetailsService clientDetailsService,TokenStore tokenStore) {
		TokenStoreUserApprovalHandler UserApprovalHandler = new TokenStoreUserApprovalHandler();
		UserApprovalHandler.setClientDetailsService(clientDetailsService);
		UserApprovalHandler.setRequestFactory(requestFactory);
		UserApprovalHandler.setTokenStore(tokenStore);
		return UserApprovalHandler;
	}
	
	@Bean
	public JwtAccessTokenConverter jwtAccessTokenConverter(){		
		JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
		jwtAccessTokenConverter.setSigningKey("secret");
		return jwtAccessTokenConverter;
	}
	
	
	@Bean
	public TokenStore jwtTokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
		return new JwtTokenStore(jwtAccessTokenConverter);
		
	}

	@Bean
	public ClientDetailsService clientDetailsService() throws Exception {
		InMemoryClientDetailsServiceBuilder clientDetailsBuilder = new InMemoryClientDetailsServiceBuilder();
		clientDetailsBuilder.withClient("system").resourceIds("OAUTH2_RESOURCE_ID","API_GATEWAY_RESOURCE").autoApprove(true).authorities("ROLE_CLIENT")
				.authorizedGrantTypes("authorization_code", "refresh_token", "password").scopes("read", "write", "user")
				.secret("{noop}password");
		return clientDetailsBuilder.build();
	}

}
