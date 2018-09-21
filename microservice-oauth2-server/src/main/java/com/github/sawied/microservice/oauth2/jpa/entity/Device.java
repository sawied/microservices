package com.github.sawied.microservice.oauth2.jpa.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * describe device information 
 * @author sawied
 *
 */
@Entity(name = "device")
@DiscriminatorValue(value="D")
public class Device extends Account{


	@Transient
	private Boolean vaildTimestamp;

	@Column
	private String versionNumber;
	

	public Boolean getVaildTimestamp() {
		return vaildTimestamp;
	}

	public void setVaildTimestamp(Boolean vaildTimestamp) {
		this.vaildTimestamp = vaildTimestamp;
	}

	public String getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}

}
