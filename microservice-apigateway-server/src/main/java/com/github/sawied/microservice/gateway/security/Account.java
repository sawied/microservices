package com.github.sawied.microservice.gateway.security;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class Account extends User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 96982119034970680L;
	
	private String access_token;
	
	private Date expiration;
	
	private int expiresIn;
	
	private String refresh_token;
	
	private Map<String,Object> additionalInfo;



	public Account(String username, String password,
			Collection<? extends GrantedAuthority> authorities) {
		super(username,password,authorities);
		
	}

	public Map<String, Object> getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(Map<String, Object> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public Date getExpiration() {
		return expiration;
	}

	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}

	public int getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}
	
	
	
}
