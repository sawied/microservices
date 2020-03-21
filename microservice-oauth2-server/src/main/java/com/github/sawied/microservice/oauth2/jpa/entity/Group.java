package com.github.sawied.microservice.oauth2.jpa.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.envers.AuditMappedBy;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity(name = "group")
@Table(name="group_detail")
@Audited
public class Group {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 100, name = "entitlementName", unique = true, nullable = false)
	private String entitlementName;

	@Column(length = 30, name = "entitlementType")
	private String entitlementType;

	@Column(length = 255, name = "entitlementDescription")
	private String entitlementDescription;

	@JoinTable(name = "group_authoritie", joinColumns = {
			@JoinColumn(name = "group_id", referencedColumnName = "ID") }, inverseJoinColumns = {
					@JoinColumn(name = "authoritie_id", referencedColumnName = "ID") })
	@ManyToMany
	//@NotAudited
	private List<Authoritie> authorities = new ArrayList<Authoritie>();

	@JsonIgnore
	//@ManyToMany(mappedBy = "groups")
	//private List<Account> accounts=new ArrayList<Account>();
/**
	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}
**/
	@Column(length = 10, name = "entitlementStatus")
	private String entitlementStatus;

	@Column(length = 255, name = "entitlementLink")
	private String entitlementLink;

	public String getEntitlementLink() {
		return entitlementLink;
	}

	public void setEntitlementLink(String entitlementLink) {
		this.entitlementLink = entitlementLink;
	}

	public String getEntitlementStatus() {
		return entitlementStatus;
	}

	public void setEntitlementStatus(String entitlementStatus) {
		this.entitlementStatus = entitlementStatus;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEntitlementName() {
		return entitlementName;
	}

	public void setEntitlementName(String entitlementName) {
		this.entitlementName = entitlementName;
	}

	public String getEntitlementType() {
		return entitlementType;
	}

	public void setEntitlementType(String entitlementType) {
		this.entitlementType = entitlementType;
	}

	public String getEntitlementDescription() {
		return entitlementDescription;
	}

	public void setEntitlementDescription(String entitlementDescription) {
		this.entitlementDescription = entitlementDescription;
	}

	public List<Authoritie> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(List<Authoritie> authorities) {
		this.authorities = authorities;
	}

}