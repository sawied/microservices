package com.github.sawied.microservice.oauth2.security;

public class AccountRequestDetails {
	
	
	private String clientIp;
	
	

	public AccountRequestDetails(String clientIp) {
		super();
		this.clientIp = clientIp;
	}



	public String getClientIp() {
		return clientIp;
	}
	

}
