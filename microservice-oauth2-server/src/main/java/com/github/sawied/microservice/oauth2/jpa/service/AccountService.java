package com.github.sawied.microservice.oauth2.jpa.service;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.github.sawied.microservice.oauth2.jpa.AccountLogRepository;
import com.github.sawied.microservice.oauth2.jpa.UserRepository;
import com.github.sawied.microservice.oauth2.jpa.entity.AccountLog;
import com.github.sawied.microservice.oauth2.jpa.entity.User;

@Component
public class AccountService {

	@Autowired
	private UserRepository userRepository;
	
	
	/**
	 * retrieve User infomation from database
	 * @param username
	 * @return
	 */
	@Transactional
	public User getUserByName(String username) {
		return userRepository.getUserByName(username);
	}
	
	@Transactional
	public void modifyUserByProperties(User user){
		Assert.notNull(user.getId(),"user's id can't null");
		User load=userRepository.getOne(user.getId());
		if(StringUtils.hasText(user.getEmail())) {
			load.setEmail(user.getEmail());
		}
		if(user.getEnabled()!=null) {
			load.setEnabled(user.getEnabled());
		}
		if(StringUtils.hasText(user.getName())) {
			load.setName(user.getName());
		}
		userRepository.save(load);
	}
	
	
	
	
	@Autowired
	private AccountLogRepository accountLogRepository;





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
