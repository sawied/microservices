package com.github.sawied.microservice.oauth2.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.github.sawied.microservice.oauth2.jpa.entity.User;

public class UserRepositoryCustomImpl implements UserRepositoryCustom {

	@PersistenceContext
	private EntityManager em;

	@Override
	public User getUserByName(String username){
		User account = em.createQuery("from User as a left join fetch a.authorities where a.name=:name", User.class)
				.setParameter("name", username).getSingleResult();
		return account;
	}
	

	
	@Transactional
	@Override
	public void modifyUserByProperties(User user){
		Assert.notNull(user.getId(),"user's id can't null");
		User load=em.getReference(User.class, user.getId());
		if(StringUtils.hasText(user.getEmail())) {
			load.setEmail(user.getEmail());
		}
		if(user.getEnabled()!=null) {
			load.setEnabled(user.getEnabled());
		}
		if(StringUtils.hasText(user.getName())) {
			load.setName(user.getName());
		}
		em.persist(load);
	}


	

}
