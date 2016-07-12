package com.model;

import lombok.Data;

@Data
public class TopPageModel {
	private String user_id;
	private String create_user;
	public String getCreateUser()
	{
		return this.create_user;
		
	}
}
