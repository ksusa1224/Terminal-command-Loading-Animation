package com.application.model.dao;

import lombok.Data;

public @Data class QAModel {
	
    /**
     *  行番号
     */
	private int row_no;

    /**
     *  QA ID
     */
	private String qa_id;
	
    /**
     *  QAタイプ
     */
	private int qa_type;
	
    /**
     *  読むだけ問題フラグ
     */
	private int yomudake_flg;
	
    /**
     *  重要度（５段階）
     */
	private int juyoudo;
	
    /**
     *  難易度（５段階）
     */
	private int nanido;
	
    /**
     *  問題文と正答のうち問題から始まるかのフラグ
     */
	private int is_start_with_q;

	/**
     *  正答がたくさんある場合の問題文を分割した時の個数
     */
	private int q_split_cnt;
	
    /**
     *  問題に紐づく正答の個数
     */
	private int seitou_cnt;
	
    /**
     *  公開範囲
     */
	private int koukai_level;
	
    /**
     *  無料販売フラグ
     */
    private int free_flg;
	
    /**
     *  無料配布した数
     */
    private int free_sold_num;

    /**
     *  有料販売フラグ
     */
    private int charge_flg;
	
    /**
     *  有料で売った数
     */
	private int charge_sold_num;
	
    /**
     *  削除フラグ
     */
	private int del_flg;
	
    /**
     *  作成者
     */
	private String create_owner;
	
    /**
     *  更新者
     */
	private String update_owner;
	
    /**
     *  レコード作成日時（H2DBのtimestampと同じフォーマットにする）
     */
	private String create_timestamp;
	
    /**
     *  レコード更新日時（H2DBのtimestampと同じフォーマットにする）
     */
	private String update_timestamp;	
}
