package com.application.model;

import lombok.Data;
import com.common.AES;

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
	private String token;
	
	/**
	 * 暗号化されたオーナーDB名から復号化されたオーナーDB名を取得する
	 * @param encrypted_db_name
	 * @return 
	 */
	public String getDecryptedDbName(byte[] encrypted_db_name)
	{
		AES aes = new AES();
		return aes.decrypt(encrypted_db_name);
	}

	/**
	 * 暗号化されたオーナーDB名を元に復号化されたオーナーDB名をセットする
	 * @param encrypted_db_name
	 */
	public void setDecryptedDbName(byte[] encrypted_db_name)
	{
		AES aes = new AES();
		this.db_name = aes.decrypt(encrypted_db_name);
	}
}
