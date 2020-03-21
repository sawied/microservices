package com.github.sawied.microservice.oauth2.jpa;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.github.sawied.microservice.oauth2.jpa.entity.Group;
import com.github.sawied.microservice.oauth2.jpa.entity.User;

public class UserRepositoryCustomImpl implements UserRepositoryCustom {

	@PersistenceContext
	private EntityManager em;

	@Override
	public User getUserByName(String username){
		User account = em.createQuery("from User as a where a.name=:name", User.class)
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
		
		List<Group> groups = load.getGroups();
		Iterator<Group> iterator = groups.iterator();
		while (iterator.hasNext()) {
			iterator.next();
			iterator.remove();
		}
		em.flush();
		for(Group g : user.getGroups()) {			
			load.getGroups().add(g);
		}
		
		em.persist(load);
	}



	@Transactional
	@Override
	public void createUser(User user) {
		Group reference = em.getReference(Group.class, 2);
		user.getGroups().add(reference);
		//reference.getAccounts().add(user);
		em.persist(user);
	}


	

}
