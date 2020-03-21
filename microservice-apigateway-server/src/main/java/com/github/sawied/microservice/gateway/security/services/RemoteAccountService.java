package com.github.sawied.microservice.gateway.security.services;

import org.springframework.http.ResponseEntity;

import com.github.sawied.microservice.gateway.security.AccountAuthenticationToken;

public interface RemoteAccountService {

	ResponseEntity<String> acquireAccount(AccountAuthenticationToken token);
}
