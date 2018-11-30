package com.github.sawied.microservice.gateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.sawied.microservice.gateway.config.GatewayConfig;

public class RemoteSecureOauth2Service implements RemoteAccountService {

	private static final Logger LOG = LoggerFactory.getLogger(RemoteSecureOauth2Service.class);

	
	private RestTemplate restTemplate;

	

	

	public RemoteSecureOauth2Service(RestTemplate restTemplate) {
		super();
		this.restTemplate = restTemplate;
	}

	@Override
	public ResponseEntity<String> acquireAccount(AccountAuthenticationToken token) {
		String name = (String) token.getPrincipal();
		String password = (String) token.getCredentials();
		String timestamp = token.getTimestamp();
		ResponseEntity<String> response = requestToken(name, password, timestamp);
		return response;
	}


	private ResponseEntity<String> requestToken(String username, String password, String timestamp) {
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add("username", username);
		parameters.add("password", password);
		parameters.add("timestamp", timestamp);
		RequestEntity<HttpEntity<MultiValueMap<String, String>>> requestEntity = buildPassowrdAuthRequest(parameters);
		ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
		return response;
	}

	private RequestEntity<HttpEntity<MultiValueMap<String, String>>> buildPassowrdAuthRequest(
			MultiValueMap<String, String> parameters) {

		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>(parameters);
		params.add("grant_type", "password");

		UriComponents url = UriComponentsBuilder.newInstance().scheme("http").host(GatewayConfig.OAUTH2_SERVICE_NAME)
				.path("oauth/token").queryParams(params).build();

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(parameters);
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
		return RequestEntity.post(url.toUri()).accept(MediaType.APPLICATION_JSON).body(httpEntity);
	}

}
