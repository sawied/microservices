package com.github.sawied.microservice.gateway.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.cache.Cache;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sawied.microservice.gateway.config.GatewayConfig;
import com.github.sawied.microservice.gateway.security.Account;

@RestController
@RequestMapping("oauth2")
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
	
	@Autowired
	private ResourceServerTokenServices tokenService;

	@RequestMapping(value="/secure",method=RequestMethod.POST)
	public ResponseEntity<?> authByCiphertext(@RequestParam Map<String, String> requestParam) {

		String username = requestParam.get("username");
		String password = requestParam.get("password");
		String timestamp = requestParam.get("timestamp");
		Assert.hasText(username, "password grant type ,username is required.");
		Assert.hasText(password, "password grant type ,password is required.");
		Assert.hasText(timestamp, "password grant type ,password is timestamp.");
		
		ResponseEntity<String> response = requestToken(username, password, timestamp);
		return response;
	}

	@RequestMapping(value="/simple",method=RequestMethod.POST)
	public ResponseEntity<?> authByUsernameAndPassword(@RequestParam Map<String, String> requestParam,HttpServletRequest request) throws IOException{
		String username = requestParam.get("username");
		String password = requestParam.get("password");
		Assert.hasText(username, "simple authentication model ,username is required.");
		Assert.hasText(password, "simple authentication model ,password is required.");
		
		OAuth2AccessToken oauth2AccessToken = null;
		OAuth2Authentication authentication =null;
		Account account = null;
		if(SecurityContextHolder.getContext().getAuthentication()!=null && SecurityContextHolder.getContext().getAuthentication() instanceof OAuth2Authentication) {
			
				authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
				account = (Account) authentication.getUserAuthentication().getDetails();
		}else {
			
			ResponseEntity<String> response = requestToken(username,password,String.valueOf(new Date().getTime()));
			//first,store access token into session
			String accessToken = parseToken(response.getBody());
			//second, expose useful info for client
			//oauth2AccessToken = tokenService.readAccessToken(accessToken);
			authentication=tokenService.loadAuthentication(accessToken);
			account = (Account)authentication.getUserAuthentication().getDetails();
			if(sessionAssociate) {
				//save authentication into session
				authentication.setAuthenticated(true);
				//authentication.setDetails(oauth2AccessToken);
				request.getSession(true);
				request.setAttribute(ACCESS_TOKEN_VALUE, accessToken);
				authentication.setDetails(new OAuth2AuthenticationDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		
		if(authentication!=null) {
			OAuth2AuthenticationDetails detail = (OAuth2AuthenticationDetails)authentication.getDetails();
			String accessToken = detail.getTokenValue();
			oauth2AccessToken = tokenService.readAccessToken(accessToken);
		}
		
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cache-Control", "no-store");
		headers.set("Pragma", "no-cache");
		
		
		
		Map<String,Object> map=new HashMap<String,Object>();
		map.putAll(account.getAdditionalInfo());
		if(!sessionAssociate&&oauth2AccessToken!=null) {			
			map.put("access_token", oauth2AccessToken.getValue());
			map.put("expiration", oauth2AccessToken.getExpiration());
			map.put("expiresIn", oauth2AccessToken.getExpiresIn());
		}
		
		cache.put(username, new Account(username, sessionAssociate?request.getSession(false).getId():oauth2AccessToken.getValue()));
		
		return new ResponseEntity<>(map,headers, HttpStatus.OK);
	}
	
	
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
			return new ResponseEntity<>(account,headers,HttpStatus.OK);
		}else {
			Map<String,String> errors = new HashMap<String,String>();
			errors.put("error", "no_account");
			errors.put("description", "no account info");
			return new ResponseEntity<>(errors,headers,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	private String parseToken(String body) throws IOException {
		ObjectMapper om =new ObjectMapper();
		JsonNode jsonNode=om.readTree(body);
		return jsonNode.path("access_token").asText();		
	}

	private ResponseEntity<String> requestToken(String username, String password, String timestamp) {
		MultiValueMap<String,String> parameters = new LinkedMultiValueMap<String,String>();
		parameters.add("username", username);
		parameters.add("password", password);
		parameters.add("timestamp", timestamp);
		RequestEntity<HttpEntity<MultiValueMap<String,String>>> requestEntity = buildPassowrdAuthRequest(parameters);
		ResponseEntity<String> response=restTemplate.exchange(requestEntity, String.class);
		return response;
	}
	


	private RequestEntity<HttpEntity<MultiValueMap<String,String>>> buildPassowrdAuthRequest(MultiValueMap<String,String> parameters) {
		
		MultiValueMap<String,String> params = new LinkedMultiValueMap<String,String>(parameters);
		params.add("grant_type", "password");
		
		UriComponents url = UriComponentsBuilder.newInstance().scheme("http").host(GatewayConfig.OAUTH2_SERVICE_NAME)
				.path("oauth/token").queryParams(params).build();
		
		MultiValueMap<String,String> headers = new LinkedMultiValueMap<String,String>(parameters);
		headers.add(HttpHeaders.CONTENT_TYPE,MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		HttpEntity<MultiValueMap<String,String>> httpEntity = new HttpEntity<>(params,headers);
		return RequestEntity.post(url.toUri()).accept(MediaType.APPLICATION_JSON).body(httpEntity);
	}

}
