package com.application.model.dao;

import lombok.Data;

public @Data class QaTagRelationModel {
	
    // 行番号
	private int row_no;
    // QA ID
	private String qa_id;
    // タグID
	private String tag_id;
    // タグ内でのQAの順番
	private int junban;
    // 公開範囲
	private int koukai_level;
    // 作成者
	private String create_owner;
    // 更新者
	private String update_owner;
    // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
	private String create_timestamp;
    // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
	private String update_timestamp;    
}
