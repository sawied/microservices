package com.github.sawied.microservice.trade.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RemoteApiService implements RemoteApi {

	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${remote.server:http://localhost}")
	private String url;

	@Override
	public String getRestApiResponse(Long id) {
		return restTemplate.getForObject(url+"/api",String.class);
	}

}
