package com.github.sawied.microservice.oauth2.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.envers.Audited;


@Entity(name="authorities")
@Audited
public class Authoritie {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	/**
	@ManyToOne(targetEntity=Account.class)
	@JoinColumn(name="account_id")
	private  Account account;
	**/
	@Column(name="authority")
	private String authority;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	/**
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}**/

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}
	
	
}
