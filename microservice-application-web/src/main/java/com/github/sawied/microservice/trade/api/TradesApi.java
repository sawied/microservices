package com.github.sawied.microservice.trade.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.sawied.microservice.trade.api.bean.Trade;

@RestController
@RequestMapping("trades")
public class TradesApi {

	@RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_UTF8_VALUE })
	public Trade create() {
		return new Trade();
	}

	@RequestMapping(path = "/{id}", method = RequestMethod.DELETE, consumes = {})
	public Trade delete(@PathVariable("id") Long id) {
		return new Trade();
	}

}
