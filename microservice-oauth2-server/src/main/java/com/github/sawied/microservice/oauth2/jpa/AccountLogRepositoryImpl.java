package com.github.sawied.microservice.oauth2.jpa;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class AccountLogRepositoryImpl implements AccountLogRepositoryCutom {

	@PersistenceContext
	private EntityManager em;
	
	@Override
	public Long countLogByNameAndTimestamp(String username, String timestamp) {
		 Long num=em.createQuery(
				"select count(dl) from AccountLog as dl where dl.name=:name and dl.timestamp=:timestamp",
				Long.class).setParameter("name", username).setParameter("timestamp", new Date(Long.parseLong(timestamp))).getSingleResult();
		 return num==null?0L:num;
		
	}
	
}
