package com.github.sawied.microservice.oauth2.jpa.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.sawied.microservice.oauth2.jpa.AccountLogRepository;
import com.github.sawied.microservice.oauth2.jpa.AccountRepository;
import com.github.sawied.microservice.oauth2.jpa.entity.Account;
import com.github.sawied.microservice.oauth2.jpa.entity.AccountLog;

@Component
public class AccountService {

	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private AccountLogRepository accountLogRepository;


	public Account getAccountByName(String username) {
		return accountRepository.getAccountByName(username);
	}


	public Long countLogByNameAndTimestamp(String principal, String timestamp) {
		return accountLogRepository.countLogByNameAndTimestamp(principal, timestamp);
	}


	public void writeLogonLog(String principal, String timestamp,String ip) {
		AccountLog ac=new AccountLog();
		ac.setName(principal);
		ac.setTimestamp(new Date(Long.parseLong(timestamp)));
		ac.setIp(ip);
		accountLogRepository.save(ac);
	}
	
	
	
	
}
