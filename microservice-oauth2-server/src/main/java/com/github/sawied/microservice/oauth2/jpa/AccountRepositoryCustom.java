package com.github.sawied.microservice.oauth2.jpa;


import com.github.sawied.microservice.oauth2.jpa.entity.Account;

public interface AccountRepositoryCustom {

	public Account getAccountByName(String username);
		
}
