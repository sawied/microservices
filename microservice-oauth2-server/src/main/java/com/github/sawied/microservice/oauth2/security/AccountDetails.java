package com.github.sawied.microservice.oauth2.security;

import com.github.sawied.microservice.oauth2.jpa.entity.Account;
import com.github.sawied.microservice.oauth2.jpa.entity.Device;
import com.github.sawied.microservice.oauth2.jpa.entity.User;

public class AccountDetails extends org.springframework.security.core.userdetails.User{
	
	

	private static final long serialVersionUID = 4637089898891535983L;
	
	/**
	 * User properties
	 */
	private String email;
	
	private String address;
	
	
	/**
	 * device properties
	 */
	private String version;
	
	
	public AccountDetails(Account account) {
		super(account.getName(), account.getPassword(),account.getGrantedAuthorities());
		if(account instanceof Device){
			Device device = (Device)account;
			this.version = device.getVersionNumber();
		}
		
		if(account instanceof User ) {
			User user = (User)account;
			this.address = user.getAddress();
			this.email = user.getEmail();
		}
		
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getVersion() {
		return version;
	}


	public void setVersion(String version) {
		this.version = version;
	}

	
}
