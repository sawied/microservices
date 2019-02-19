package com.github.sawied.microservice.oauth2.jpa.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.AuditMappedBy;
import org.hibernate.envers.Audited;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Audited
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type",discriminatorType=DiscriminatorType.STRING,length=20)
@DiscriminatorValue("A")
public class Account {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(length=50,name="name",unique=true,nullable=false)
	private String name;
	
	@Column(length=500,name="password",nullable=false)
	private String password;
	
	@Column(name="enabled",nullable=false)
	private Boolean enabled;
	
	/**
	@OneToMany(targetEntity=Authoritie.class,mappedBy="account")
	//@JoinColumn(name="account_id")
	@NotAudited
	private Set<Authoritie> authorities = new HashSet<Authoritie>();
	**/
	
	@ManyToMany()
	@JoinTable(name = "account_group",  joinColumns = {
			@JoinColumn(name = "account_id", referencedColumnName = "ID") }, inverseJoinColumns = {
					@JoinColumn(name = "group_id", referencedColumnName = "ID")})
	//@AuditMappedBy(mappedBy="accounts")
	//@AuditJoinTable(name="account_group",inverseJoinColumns= {@JoinColumn(name = "group_id", referencedColumnName = "ID",insertable=false,updatable=false)})
	//@NotFound(action=NotFoundAction.IGNORE)
	private List<Group> groups = new ArrayList<Group>();
	
	/**
	public List<GrantedAuthority> getGrantedAuthorities(){
		return this.authorities.stream().map(a -> new SimpleGrantedAuthority(a.getAuthority())).collect(Collectors.toList());
	}**/

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public List<GrantedAuthority> getGrantedAuthorities(){
		return null;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
	
	

	/**
	public Set<Authoritie> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Set<Authoritie> authorities) {
		this.authorities = authorities;
	}**/

}
