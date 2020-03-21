package com.github.sawied.microservice.gateway.service;

import java.util.HashMap;
import java.util.Map;

import javax.cache.Cache;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.github.sawied.microservice.gateway.security.Account;
import com.github.sawied.microservice.gateway.security.AccountAuthenticationFilter;

@RestController
@RequestMapping("security")
public class SecureOauth {

	public static final String ACCESS_TOKEN_VALUE = OAuth2AuthenticationDetails.class.getSimpleName() + ".ACCESS_TOKEN_VALUE";
	
	@Autowired
	@Qualifier("oauth2RestTemplate")
	private RestTemplate restTemplate;
	
	@Value(value="${associate_token_with_session:false}")
	private Boolean sessionAssociate=false;
	
	@Autowired
	@Qualifier("customerInfo")
	private Cache<String, Account> cache = null;
	
	
	@RequestMapping("/info")
	public ResponseEntity<?> info(HttpServletRequest request){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Account account = null;
		if(authentication!=null && authentication instanceof OAuth2Authentication) {
			account=(Account)((OAuth2Authentication)authentication).getUserAuthentication().getDetails();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cache-Control", "no-store");
		headers.set("Pragma", "no-cache");
		
		if(account!=null) {
			Map<String,Object> map=new HashMap<String,Object>();
			map.putAll(account.getAdditionalInfo());
			if(Boolean.TRUE.equals(request.getAttribute(AccountAuthenticationFilter.TOKEN_IS_PRESENT))) {			
				map.put("access_token", account.getAccess_token());
				map.put("expiration", account.getExpiration());
				map.put("expiresIn", account.getExpiresIn());
			}
			return new ResponseEntity<>(map,headers,HttpStatus.OK);
			
		}else {
			Map<String,String> errors = new HashMap<String,String>();
			errors.put("error", "no_account");
			errors.put("description", "no account info");
			return new ResponseEntity<>(errors,headers,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping("/error")
	public ResponseEntity<?> error(HttpServletRequest request){
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cache-Control", "no-store");
		headers.set("Pragma", "no-cache");
		
		Map<String, String> errors = new HashMap<String, String>();
		errors.put("error", "INTERNAL_SERVER_ERROR");
		errors.put("description", "attempt authentication ,but failed ,the detail please see the system log");
		return new ResponseEntity<>(errors, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		
	}
	
	


	

}
