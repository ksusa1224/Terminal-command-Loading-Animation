package com.application.model.dao;

import lombok.Data;

public @Data class KaitouModel {
    // 行番号
	private int row_no;
    // 回答ID
	private String k_id;
    // QA ID
	private String qa_id;
    // 正答ID
	private String s_id;
    // 正解フラグ
	private int seikai_flg;
    // アクション・・・チェックを入れた、外した、解いて正解した、解いて不正解、等
	private String action;
    // アクション日時（H2DBのtimestampと同じフォーマットにする）
	private String action_timestamp;
    // ユーザーが入力した回答
	private String kaitou;
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
}
