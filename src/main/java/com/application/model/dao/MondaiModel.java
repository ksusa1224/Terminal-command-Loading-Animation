package com.application.model.dao;

import lombok.Data;

public @Data class MondaiModel {
    // 行番号
    private int row_no;
    // 問題ID
    private String q_id;
    // QA ID
    private String qa_id;
    // QA内での問題パーツの順番
    private int junban;
    // 問題パーツが文字であるかのフラグ
    private int is_text_flg;
    // 問題パーツがバイナリであるかのフラグ
    private int is_binary_flg;
    // 分割された問題文
    private String q_parts_text;
    // QAの中に出てくる音声や画像などのバイナリファイル
    private byte[] q_parts_binary;
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
}
