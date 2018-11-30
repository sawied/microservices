package com.github.sawied.microservice.gateway.security;

import org.springframework.http.ResponseEntity;

public interface RemoteAccountService {

	ResponseEntity<String> acquireAccount(AccountAuthenticationToken token);
}
