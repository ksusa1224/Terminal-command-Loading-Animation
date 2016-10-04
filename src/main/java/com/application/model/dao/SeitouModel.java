package com.application.model.dao;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

public @Data class SeitouModel {
	
    // 正答に紐づく回答オブジェクト
    private List<KaitouModel> kaitou_list = new ArrayList<KaitouModel>();
	
    // 行番号
    private int row_no;
    // 正答ID
    private String s_id;
    // QA ID
    private String qa_id;
    // QA内での正答の順番
    private int junban;
    // 正答が文字であるかのフラグ
    private int is_text_flg;
    // 正答がバイナリであるかのフラグ
    private int is_binary_flg;
    // 正答
    private String seitou;
    // 正解フラグ
    private int seikai_flg;
    // 正答が画像などのバイナリである場合に格納する
    private byte[] seitou_binary;
    // 重要度（５段階）
    private int juyoudo;
    // 難易度（５段階）
    private int nanido;
    // 言語
    private String language;
    // テキスト読み上げデータ
    private byte[] yomiage;
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
	 * 所定のフォーマットでs_idを生成する
	 * @param row_no
	 * @param owner_id
	 * @return
	 */
	public String generate_s_id(int row_no, String owner_id)
	{
		// %09d・・・１億桁でゼロ埋め		
		return "s_id_" + String.format("%09d", row_no) + "_" + owner_id;
	}
}
