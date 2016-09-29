package com.application.model.dao;

import lombok.Data;

public @Data class TagModel {
	
    // 行番号
	private int row_no;
    // タグID
	private String tag_id;
    // タグ名
	private String tag_name;
    // 表示順
	private int junban;
    // 表示フラグ
	private int display_flg;
    // 重要度（５段階）
	private int juyoudo;
    // 難易度（５段階）
	private int nanido;
    // システムタグフラグ
	private int system_tag_flg;
    // タグ種別
	private int tag_type;
    // デザイン種別
    private int design_type;
    // 公開範囲
	private int koukai_level;
    // 言語
	private String language;
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
	 * 所定のフォーマットでtag_idを生成する
	 * @param row_no
	 * @param owner_id
	 * @return
	 */
	public String generate_tag_id(int row_no, String owner_id)
	{
		// %09d・・・１億桁でゼロ埋め		
		return "tag_id_" + String.format("%09d", row_no) + "_" + owner_id;
	}	
}
