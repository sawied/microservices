package com.github.sawied.microservice.oauth2.jpa.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import com.github.sawied.microservice.oauth2.jpa.AccountRevisionEntityListener;

@RevisionEntity(AccountRevisionEntityListener.class)
@Entity(name = "AccountRevisionEntity")
@Table(name = "ACCOUNT_REV_INFO")
public class AccountRevisionEntity extends DefaultRevisionEntity {
	
	
	private static final long serialVersionUID = -5253374979524097971L;
	
	private String operator;

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}
	

}
