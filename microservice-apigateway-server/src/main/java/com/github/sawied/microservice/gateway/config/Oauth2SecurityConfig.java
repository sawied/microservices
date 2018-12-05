package com.github.sawied.microservice.gateway.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.ForwardAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.client.RestTemplate;

import com.github.sawied.microservice.gateway.security.AccountAuthenticationFilter;
import com.github.sawied.microservice.gateway.security.AccountConcurrentSessionFilter;
import com.github.sawied.microservice.gateway.security.AccountRemoteAuthenticationProvider;
import com.github.sawied.microservice.gateway.security.AccountUserAuthenticationConverter;
import com.github.sawied.microservice.gateway.security.CompoundSecurityContextRepository;
import com.github.sawied.microservice.gateway.security.CompoundTokenExtractor;
import com.github.sawied.microservice.gateway.security.JCacheSessionRegistry;
import com.github.sawied.microservice.gateway.security.ResponseErrorSessionInformationExpiredStrategy;
import com.github.sawied.microservice.gateway.security.SessionTokenAuthenticationStratege;
import com.github.sawied.microservice.gateway.security.TokenCacheAuthenticationStratege;
import com.github.sawied.microservice.gateway.security.services.RemoteAccountService;
import com.github.sawied.microservice.gateway.security.services.RemoteSecureOauth2Service;


@Configuration
public class Oauth2SecurityConfig{
	
	@Bean
	@Qualifier("principalsCache")
	@SuppressWarnings("rawtypes")
	public Cache<Object,Set> principalsCache(CacheManager cacheManager) {
		
		CacheConfigurationBuilder<Object,Set> cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Set.class, ResourcePoolsBuilder.heap(1000))
				//.withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofMinutes(10)))
				;
		 
				Cache<Object, Set> cache = cacheManager.createCache("principalsCache", Eh107Configuration.fromEhcacheCacheConfiguration(cacheConfiguration));
		return cache;
	}
	
	@Bean
	@Qualifier("sessionIdsCache")
	public Cache<String, SessionInformation> sessionIdsCache(CacheManager cacheManager) {
		CacheConfigurationBuilder<String, SessionInformation> cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, SessionInformation.class, ResourcePoolsBuilder.heap(1000))
				//.withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofMinutes(10)))
				;
		return cacheManager.createCache("sessionIdsCache", Eh107Configuration.fromEhcacheCacheConfiguration(cacheConfiguration));
	}
	
	
	
	@Bean
	@Qualifier("tokensCache")
	public Cache<String, String> tokensCache(CacheManager cacheManager) {
		CacheConfigurationBuilder<String, String> cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(1000))
				//.withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofMinutes(10)))
				;
		return cacheManager.createCache("tokensCache", Eh107Configuration.fromEhcacheCacheConfiguration(cacheConfiguration));
	}
	
	
	@Bean
	public TokenStore jwtTokenStore(@Autowired @Qualifier("jwtTokenConverter") JwtAccessTokenConverter jwtTokenConverter) {
		return new JwtTokenStore(jwtTokenConverter);
	}
	

	@Bean
	public JwtAccessTokenConverter jwtTokenConverter(@Value("${jwt.token.key:secret}") String key ) {
		JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
		DefaultAccessTokenConverter tokenConverter = new DefaultAccessTokenConverter();
		tokenConverter.setUserTokenConverter(new AccountUserAuthenticationConverter());
		jwtAccessTokenConverter.setAccessTokenConverter(tokenConverter);
		jwtAccessTokenConverter.setSigningKey(key);
		return jwtAccessTokenConverter;
	}
	
	@Bean
	@SuppressWarnings("rawtypes")
	public SessionRegistry sessionRegistry( 
			@Qualifier("principalsCache") Cache<Object,Set> principalsCache,
			@Qualifier("sessionIdsCache") Cache<String, SessionInformation> sessionIdsCache) {			
		return new JCacheSessionRegistry(principalsCache,sessionIdsCache);
	}
	
	@Bean
	@Qualifier("concurrentSessionFilter")
	public ConcurrentSessionFilter concurrentSessionFilter(@Qualifier("sessionRegistry") SessionRegistry sessionRegistry,@Qualifier("tokensCache")
	Cache<String, String> tokensCache) {
		AccountConcurrentSessionFilter concurrentSessionFilter = new AccountConcurrentSessionFilter(sessionRegistry,new ResponseErrorSessionInformationExpiredStrategy());
		concurrentSessionFilter.setTokenCache(tokensCache);
		LogoutHandler logoutHandler = new SecurityContextLogoutHandler();
		concurrentSessionFilter.setLogoutHandlers(new LogoutHandler[]{logoutHandler});
		return concurrentSessionFilter;
	}
	
	@Bean
	public SessionAuthenticationStrategy sessionTokenAuthenticationStrategy(@Qualifier("sessionRegistry") SessionRegistry sessionRegistry,@Qualifier("tokensCache")
	Cache<String, String> tokensCache) {
		//List<SessionAuthenticationStrategy> delegateStrategies = Arrays.asList(,new TokenCacheAuthenticationStratege(tokensCache));
		//session association operator
		ConcurrentSessionControlAuthenticationStrategy concurrentSessionControl=new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry);
		//at maximum 1 session is permitted
		concurrentSessionControl.setMaximumSessions(1);
		List<SessionAuthenticationStrategy> delegateSessionStrategies =Arrays.asList(new RegisterSessionAuthenticationStrategy(sessionRegistry),
				concurrentSessionControl);
		CompositeSessionAuthenticationStrategy compositeSessionStrategy = new CompositeSessionAuthenticationStrategy(delegateSessionStrategies);
		
		//token authentication stratege 	
		return new SessionTokenAuthenticationStratege(compositeSessionStrategy,new TokenCacheAuthenticationStratege(tokensCache));
	}
	

	
	@Configuration
	@EnableResourceServer
	@Order(1000)
	static class SecurityResourceConfig extends ResourceServerConfigurerAdapter{

		@Autowired
		@Qualifier("tokenService")
		private ResourceServerTokenServices tokenService;
		
		@Autowired
		private CompoundTokenExtractor compoundTokenExtractor;
		
		@Autowired
		@Qualifier("concurrentSessionFilter")
		private ConcurrentSessionFilter concurrentSessionFilter;
		
		
		@Override
		public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
			resources.tokenServices(tokenService).tokenExtractor(compoundTokenExtractor).resourceId("API_GATEWAY_RESOURCE").stateless(false);
		}
		
		@Bean
		CompoundTokenExtractor compoundTokenExtractor(@Value(value="${associate_token_with_session:false}") Boolean isAssociate) {
			 CompoundTokenExtractor extractor = new CompoundTokenExtractor();
			 extractor.setSessionAssociate(isAssociate);
			 return extractor;
		}
		
		
		@Override
		public void configure(HttpSecurity http) throws Exception {
			//super.configure(http);
			http.requestMatcher(new NotOauth2RequestMatcher()).authorizeRequests().anyRequest().authenticated().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
			.and().addFilter(concurrentSessionFilter);
			;
			
		}
		



		
		@Bean
		@Primary
		public ResourceServerTokenServices tokenService(TokenStore jwtTokenStore) {
			DefaultTokenServices tokenService = new DefaultTokenServices();
			tokenService.setTokenStore(jwtTokenStore);
			return tokenService;
		}
		
		private static class NotOauth2RequestMatcher implements RequestMatcher{

			@Override
			public boolean matches(HttpServletRequest request) {
				String path=getRequestPath(request);
				if(path!=null) {
					return !path.startsWith("/actuator");
				}
				
				return true;
			}
			
			
			private String getRequestPath(HttpServletRequest request) {
				String url = request.getServletPath();

				if (request.getPathInfo() != null) {
					url += request.getPathInfo();
				}

				return url;
			}
		}
		
	}
	
	
	@Configuration
	@Order(0)
	static class Oauth2AuthenticationConfig extends WebSecurityConfigurerAdapter {
		
		private static final String JSESSIONID = "JSESSIONID";


		@Autowired
		private RemoteAccountService remoteAccountService;
		
		
		@Value(value="${associate_token_with_session:false}") 
		private Boolean isAssociate;
		
		
		@Autowired 
		private TokenStore tokenStore;
		
		@Autowired 
		private AuthenticationFailureHandler authenticationFailureHandler;
		
		@Autowired
		private SessionRegistry sessionRegistry;
		
		/**
		@Autowired
		private AccountAuthenticationFilter accountAuthenticationFilter;
		
		@Autowired
		@Qualifier("oauth2ProviderManager")
		public ProviderManager oauth2ProviderManager;
		**/
		
		@Qualifier("tokensCache")
		@Autowired
		private Cache<String, String> tokensCache;
		
		
		@Autowired
		@Qualifier("concurrentSessionFilter")
		private ConcurrentSessionFilter concurrentSessionFilter;
		
		@Autowired
		@Qualifier("sessionTokenAuthenticationStrategy")
		private SessionAuthenticationStrategy sessionTokenAuthenticationStrategy;

		@Override
		public void configure(WebSecurity web) throws Exception {
			web.debug(true);
		}
		
		

		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			AccountRemoteAuthenticationProvider authenticationProvider = new AccountRemoteAuthenticationProvider().accountService(remoteAccountService);
			authenticationProvider.setTokenStore(tokenStore);
			auth.authenticationProvider(authenticationProvider);
		}
		


		@Override
		protected void configure(HttpSecurity http) throws Exception {
			
			CompoundSecurityContextRepository contextRepository= new CompoundSecurityContextRepository();
			contextRepository.setIsAssociate(isAssociate);
			
		
			AccountAuthenticationFilter accountAuthenticationFilter=new AccountAuthenticationFilter();
			accountAuthenticationFilter.setFilterProcessesUrl("/oauth2/s*");
			accountAuthenticationFilter.setAuthenticationSuccessHandler(new ForwardAuthenticationSuccessHandler("/security/info"));
			accountAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
			
			
			AccountRemoteAuthenticationProvider authenticationProvider = new AccountRemoteAuthenticationProvider();
			authenticationProvider.setTokenStore(tokenStore);
			List<AuthenticationProvider> providers=Collections.singletonList(authenticationProvider.accountService(remoteAccountService));
			ProviderManager providerManager= new ProviderManager(providers);
			accountAuthenticationFilter.setAuthenticationManager(providerManager);
			accountAuthenticationFilter.setIsAssociate(isAssociate);
			
			http.requestMatchers().antMatchers("/oauth2/**" /**,"/security/**"**/).and().authorizeRequests()
					.antMatchers("/oauth2/simple", "/oauth2/secure","/oauth2/logout").permitAll()
					//.antMatchers("/security/*").authenticated()
					.and().securityContext().securityContextRepository(contextRepository)
					.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).sessionAuthenticationStrategy(sessionTokenAuthenticationStrategy)
					.and().csrf().disable()
					.logout().logoutUrl("/oauth2/logout").invalidateHttpSession(true).deleteCookies(JSESSIONID).logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
					.and().addFilter(accountAuthenticationFilter).addFilter(concurrentSessionFilter);
			
			
							
		}
		
	
		
		@Bean
		public RemoteAccountService remoteAccountService(
				 @Autowired @Qualifier("oauth2RestTemplate") RestTemplate restTemplate
				) {
			return new RemoteSecureOauth2Service(restTemplate);
		}
		
		
		@Bean
		public AuthenticationSuccessHandler successHandler() {
			return new AuthenticationSuccessHandler() {
				
				@Override
				public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
						Authentication authentication) throws IOException, ServletException {
					
				}
				
			};
		}
		
		
		@Bean
		public AuthenticationFailureHandler authenticationFailureHandler() {
			return new AuthenticationFailureHandler() {

				@Override
				public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
						AuthenticationException exception) throws IOException, ServletException {
					new HttpStatusEntryPoint(HttpStatus.INTERNAL_SERVER_ERROR).commence(request, response, exception);
				}
				
			};
		}

	}
	
	
	
	
}



