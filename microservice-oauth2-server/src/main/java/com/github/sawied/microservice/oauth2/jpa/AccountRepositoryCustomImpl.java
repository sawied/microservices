package com.github.sawied.microservice.oauth2.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.github.sawied.microservice.oauth2.jpa.entity.Account;

public class AccountRepositoryCustomImpl implements AccountRepositoryCustom {

	@PersistenceContext
	private EntityManager em;

	@Override
	public Account getAccountByName(String username) {
		Account account = em.createQuery("from Account as a join fetch a.authorities where a.name=:name", Account.class)
				.setParameter("name", username).getSingleResult();
		return account;
	}

	

}
