package com.github.sawied.microservice.oauth2.jpa;


import com.github.sawied.microservice.oauth2.jpa.entity.User;

public interface UserRepositoryCustom {

	public User getUserByName(String username);

	void modifyUserByProperties(User user);
	
	void createUser(User user);

		
}
