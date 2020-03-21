package com.github.sawied.microservice.trade.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.sawied.microservice.trade.api.bean.Trade;
import com.github.sawied.microservice.trade.service.RemoteApiService;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("trades")
@Api(tags= {"trade"},value="trade Restful API")
public class TradesApi {
	

	
	private static final Logger LOG = LoggerFactory.getLogger(TradesApi.class);
	
	@Autowired
	private RemoteApiService apiService;

	@RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_UTF8_VALUE })
	public Trade create() {
		return new Trade();
	}

	@RequestMapping(path = "/{id}", method = RequestMethod.DELETE, consumes = {MediaType.APPLICATION_JSON_VALUE})
	public Trade delete(@PathVariable("id") Long id) {
		String response = apiService.getRestApiResponse(id);
		LOG.debug("rest template response result : {}", response);
		return new Trade();
	}

	
	

}
