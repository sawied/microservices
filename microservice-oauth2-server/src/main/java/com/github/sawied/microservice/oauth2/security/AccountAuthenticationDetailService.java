package com.github.sawied.microservice.oauth2.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.github.sawied.microservice.oauth2.jpa.service.AccountService;

public class AccountAuthenticationDetailService implements UserDetailsService {

	
	private AccountService accountService;
	
	private static final Logger log = LoggerFactory.getLogger(AccountAuthenticationDetailService.class);
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return new AccountDetails(accountService.getUserByName(username));
	}


	public Boolean check(String principal, String timestamp) {
		Long i=accountService.countLogByNameAndTimestamp(principal, timestamp);
		log.info("check from data repository , the timestamp {} used for sign-on {} counts is {}", timestamp,principal,i);
		return !(i>0);	
	}
	
	
	public void traceLogon(String principal, String timestamp,String ip) {
		accountService.writeLogonLog(principal,timestamp,ip);
	}


	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}
	

}
