package com.github.sawied.microservice.gateway.service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.RequestEntity.HeadersBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

@RestController
@RequestMapping("system")
public class DirectRouter {
	

	private static final String X_FORWORDED_FOR = "X-Forworded-For";

	private static final Logger LOG = LoggerFactory.getLogger(DirectRouter.class);
	
	@Autowired
	private EurekaClient eurekaClient;
	
	
	@Autowired
	@Qualifier("simpleRestTemplate")
	private RestTemplate restTemplate;
	
	
	@RequestMapping(path="info",method= {RequestMethod.GET})
	public ResponseEntity<?> dircetRequest(HttpServletRequest request, @RequestParam("instanceId") String instanceId){
		Assert.hasText(instanceId, "dircet request don't contains requied param instanceId");
		InstanceInfo serviceInstance=findInstanceById(instanceId);
		
		RequestEntity<Void> requestEntity=buildRequestEntity(request,serviceInstance);
		ResponseEntity<String> response=restTemplate.exchange(requestEntity, String.class);
		
		 MultiValueMap<String, String> headers = new LinkedMultiValueMap<String,String>();
		 headers.add(HttpHeaders.CONTENT_TYPE,MediaType.APPLICATION_JSON.toString());
		 
		return new ResponseEntity<String>(response.getBody(),headers,response.getStatusCode());
	}
	
	
	
	/**
	 * build request entity from service instance  
	 * @param request
	 * @param serviceInstance
	 * @return
	 */
	private RequestEntity<Void>  buildRequestEntity(HttpServletRequest request,InstanceInfo serviceInstance) {
		
		UriComponents uriComponents=UriComponentsBuilder.fromHttpUrl(serviceInstance.getStatusPageUrl()).build();
		LOG.debug("build call uri {}", uriComponents.toUriString());
		
	
		HeadersBuilder<?> headerBuild= RequestEntity.get(uriComponents.toUri()).accept(MediaType.APPLICATION_JSON)
				.header(X_FORWORDED_FOR, request.getHeader(X_FORWORDED_FOR) != null ? request.getHeader(X_FORWORDED_FOR)
						: request.getRemoteHost());
		
		Map<String,String> params=serviceInstance.getMetadata();
		if(params.containsKey("system.security.user.name")) {
			String system_user = params.get("system.security.user.name");
			String system_password = params.get("system.security.user.password");
			
			String token = Base64Utils.encodeToString(
					(system_user + ":" + system_password).getBytes(StandardCharsets.UTF_8));
			headerBuild.header(HttpHeaders.AUTHORIZATION, "Basic "+token);
		}
		
		return headerBuild.build();
	}
	
	

	private InstanceInfo findInstanceById(String id){
		
		@SuppressWarnings("unchecked")
		List<InstanceInfo> objects=this.eurekaClient.getInstancesById(id);
		if(!objects.isEmpty()) {
			return objects.get(0);
		}
		return null;
	}
	
	
	
}
