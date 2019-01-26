package com.github.sawied.microservice.oauth2.jpa;

import org.hibernate.envers.RevisionListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.sawied.microservice.oauth2.UserContext;
import com.github.sawied.microservice.oauth2.jpa.entity.AccountRevisionEntity;

public class AccountRevisionEntityListener implements RevisionListener{

	@Autowired
	private UserContext userContext;
	
	public AccountRevisionEntityListener() {
		super();
	}

	@Override
	public void newRevision(Object revisionEntity) {
		AccountRevisionEntity are= (AccountRevisionEntity) revisionEntity;
		are.setOperator(userContext.getCurrentUser());
	}
	
	public AccountRevisionEntityListener userContext(UserContext userContext){
		this.userContext=userContext;
		return this;
	}

}
