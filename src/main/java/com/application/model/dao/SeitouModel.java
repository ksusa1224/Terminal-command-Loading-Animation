package com.application.model.dao;

import lombok.Data;

public @Data class SeitouModel {
    // 行番号
    private int row_no;
    // 正答ID
    private String s_id;
    // QA ID
    private String qa_id;
    // QA内での正答の順番
    private int junban;
    // 正答
    private String seitou;
    // 重要度（５段階）
    private int juyoudo;
    // 難易度（５段階）
    private int nanido;
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