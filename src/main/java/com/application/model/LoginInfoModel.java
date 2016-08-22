package com.application.model;

import lombok.Data;

public @Data class LoginInfoModel {

	private String owner_id;
	private String email;
	private String password;
	private byte[] encrypted_password;
	private String owner_name;
	private String db_name;
	private byte[] encrypted_db_name;
	private String db_version;
	private int kakin_type;

}
