package com.github.sawied.microservice.gateway.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityContext {

	@RequestMapping("")
	public String launch(@RequestParam(name="redirect") String to,HttpServletRequest request) {
		
		return null;
	}
	
}
