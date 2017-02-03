package com.application.model.dao;

import lombok.Data;

public @Data class SystemModel {
	
    // 行番号
	private int row_no;
    // 設定ID
	private String sys_id;
    // 項目グループID
    private String sys_group_id;
    // 項目グループ名
    private String sys_group_name;
    // キー
    private String key;
    // 値
    private String value;
    // 削除フラグ
	private int del_flg;
    // 作成者
	private String create_owner;
    // 更新者
	private String update_owner;
    // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
	private String create_timestamp;
    // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
	private String update_timestamp;   
	
	/**
	 * 所定のフォーマットでsys_idを生成する
	 * @param row_no
	 * @param owner_id
	 * @return
	 */
	public String generate_sys_id(int row_no, String owner_id)
	{
		// %09d・・・１億桁でゼロ埋め		
		return "sys_id_" + String.format("%09d", row_no) + "_" + owner_id;
	}	
}
