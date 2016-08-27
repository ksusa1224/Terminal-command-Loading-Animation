package com.application.model.dao;

import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "qa")
public @Data class QAModel {
	
    /**
     *  行番号
     */
	@Column(name = "row_no")
	private int row_no;

    /**
     *  QA ID
     */
	@Column(name = "qa_id")
	private String qa_id;
	
    /**
     *  QAタイプ
     */
	@Column(name = "qa_type")
	private int qa_type;
	
    /**
     *  読むだけ問題フラグ
     */
	@Column(name = "yomudake_flg")
	private int yomudake_flg;
	
    /**
     *  重要度（５段階）
     */
	@Column(name = "juyoudo")
	private int juyoudo;
	
    /**
     *  難易度（５段階）
     */
	@Column(name = "nanido")
	private int nanido;
	
    /**
     *  問題文と正答のうち問題から始まるかのフラグ
     */
	@Column(name = "is_start_with_q")
	private int is_start_with_q;
    /**
     *  正答がたくさんある場合の問題文を分割した時の個数
     */
	@Column(name = "q_split_cnt")
	private int q_split_cnt;
	
    /**
     *  問題に紐づく正答の個数
     */
	@Column(name = "seitou_cnt")
	private int seitou_cnt;
	
    /**
     *  公開範囲
     */
	@Column(name = "koukai_level")
	private int koukai_level;
	
    /**
     *  無料販売フラグ
     */
	@Column(name = "free_flg")
    private int free_flg;
	
    /**
     *  無料配布した数
     */
	@Column(name = "free_sold_num")
    private int free_sold_num;
    /**
     *  有料販売フラグ
     */
	@Column(name = "charge_flg")
    private int charge_flg;
	
    /**
     *  有料で売った数
     */
	@Column(name = "charge_sold_num")
	private int charge_sold_num;
	
    /**
     *  削除フラグ
     */
	@Column(name = "del_flg")
	private int del_flg;
	
    /**
     *  作成者
     */
	@Column(name = "create_owner")
	private String create_owner;
	
    /**
     *  更新者
     */
	@Column(name = "update_owner")
	private String update_owner;
	
    /**
     *  レコード作成日時（H2DBのtimestampと同じフォーマットにする）
     */
	@Column(name = "create_timestamp")
	private String create_timestamp;
	
    /**
     *  レコード更新日時（H2DBのtimestampと同じフォーマットにする）
     */
	@Column(name = "update_timestamp")
	private String update_timestamp;
	
}
