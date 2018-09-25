package com.github.sawied.microservice.trade.api.bean;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description="trade model description")
public class Trade {
	
	@ApiModelProperty(example="1",value="trade id")
	private Long id;
	@ApiModelProperty(example="trade titile",value="trade title for use")
	@NotNull
	@Size(min=1,max=50)
	private String tile;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTile() {
		return tile;
	}

	public void setTile(String tile) {
		this.tile = tile;
	}
	
	
	

}
