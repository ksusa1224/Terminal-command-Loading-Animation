package com.application.controller.dao;

import com.application.model.dao.QAModel;
import com.common.StringBuilderPlus;

public class QADao {

	/**
	 * QAテーブルに１件レコードを追加するSQL文を返却
	 * @param qa
	 * @return
	 */
	public StringBuilderPlus insert_qa(QAModel qa)
	{
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("insert into qa (");
		// 行番号
		sql.appendLine("  row_no,");
		// QA ID
		sql.appendLine("	qa_id,");
		// QAタイプ
		sql.appendLine("	qa_type,");
		// 読むだけ問題フラグ
		sql.appendLine("	yomudake_flg,");
		// 重要度（５段階）
		sql.appendLine("	juyoudo,");
		// 難易度（５段階）
		sql.appendLine("	nanido,");
		// 問題文と正答のうち問題から始まるかのフラグ
		sql.appendLine("	is_start_with_q,");
		// 正答がたくさんある場合の問題文を分割した時の個数
		sql.appendLine("	q_split_cnt,");
		// 問題に紐づく正答の個数
		sql.appendLine("	seitou_cnt,");
		// 公開範囲
		sql.appendLine("  koukai_level,");
		// 無料販売フラグ
		sql.appendLine("  free_flg,");
		// 無料配布した数
		sql.appendLine("  free_sold_num,");
		// 有料販売フラグ
		sql.appendLine("  charge_flg,");
		// 有料で売った数
		sql.appendLine("  charge_sold_num,");
		// 削除フラグ
		sql.appendLine("	del_flg,");
		// 作成者
		sql.appendLine("  create_owner,");
		// 更新者
		sql.appendLine("  update_owner,");
		// レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("	create_timestamp,");
		// レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("	update_timestamp");
		sql.appendLine(") ");
		
		sql.appendLine("values (");
	    // 行番号
		sql.appendLine("" + qa.getRow_no() + ",");
		// QA ID
		sql.appendLine("'" + qa.getQa_id() + "',");
		// QAタイプ
		sql.appendLine("" + qa.getQa_type() + ",");
		// 読むだけ問題フラグ
		sql.appendLine("" + qa.getYomudake_flg() + ",");
		// 重要度（５段階）
		sql.appendLine("" + qa.getJuyoudo() + ",");
		// 難易度（５段階）
		sql.appendLine("" + qa.getNanido() + ",");
		// 問題文と正答のうち問題から始まるかのフラグ
		sql.appendLine("" + qa.getIs_start_with_q() + ",");
		// 正答がたくさんある場合の問題文を分割した時の個数
		sql.appendLine("" + qa.getQ_split_cnt() + ",");
		// 問題に紐づく正答の個数
		sql.appendLine("" + qa.getSeitou_cnt() + ",");
		// 公開範囲
		sql.appendLine("" + qa.getKoukai_level() + ",");
		// 無料販売フラグ
		sql.appendLine("" + qa.getFree_flg() + ",");
		// 無料配布した数
		sql.appendLine("" + qa.getFree_sold_num() + ",");
		// 有料販売フラグ
		sql.appendLine("" + qa.getCharge_flg() + ",");
		// 有料で売った数
		sql.appendLine("" + qa.getCharge_sold_num() + ",");
		// 削除フラグ
		sql.appendLine("" + qa.getDel_flg() + ",");
		// 作成者
		sql.appendLine("'" + qa.getCreate_owner() + "',");
		// 更新者
		sql.appendLine("'" + qa.getUpdate_owner() + "',");
		// レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("'" + qa.getCreate_timestamp() + "',");
		// レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("'" + qa.getUpdate_timestamp() + "'");
		sql.appendLine(");");
		
		return sql;
	}	
	
	/**
	 * QAテーブルを１件更新するSQLを返却
	 * @param qa
	 * @return 
	 */
	public StringBuilderPlus update_qa(QAModel qa)
	{
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("update qa ");
		sql.appendLine("values (");
	    // 行番号
		sql.appendLine("  row_no = " + qa.getRow_no() + ",");
		// QA ID
		sql.appendLine("  qa_id = '" + qa.getQa_id() + "',");
		// QAタイプ
		sql.appendLine("  qa_type = " + qa.getQa_type() + ",");
		// 読むだけ問題フラグ
		sql.appendLine("  yomudake_flg = " + qa.getYomudake_flg() + ",");
		// 重要度（５段階）
		sql.appendLine("  juyoudo = " + qa.getJuyoudo() + ",");
		// 難易度（５段階）
		sql.appendLine("  nanido = " + qa.getNanido() + ",");
		// 問題文と正答のうち問題から始まるかのフラグ
		sql.appendLine("  is_start_with_q = " + qa.getIs_start_with_q() + ",");
		// 正答がたくさんある場合の問題文を分割した時の個数
		sql.appendLine("  q_split_cnt = " + qa.getQ_split_cnt() + ",");
		// 問題に紐づく正答の個数
		sql.appendLine("  seitou_cnt = " + qa.getSeitou_cnt() + ",");
		// 公開範囲
		sql.appendLine("  koukai_level = " + qa.getKoukai_level() + ",");
		// 無料販売フラグ
		sql.appendLine("  free_flg = " + qa.getFree_flg() + ",");
		// 無料配布した数
		sql.appendLine("  free_sold_num = " + qa.getFree_sold_num() + ",");
		// 有料販売フラグ
		sql.appendLine("  charge_flg = " + qa.getCharge_flg() + ",");
		// 有料で売った数
		sql.appendLine("  charge_sold_num = " + qa.getCharge_sold_num() + ",");
		// 削除フラグ
		sql.appendLine("  del_flg = " + qa.getDel_flg() + ",");
		// 作成者
		sql.appendLine("  create_owner = '" + qa.getCreate_owner() + "',");
		// 更新者
		sql.appendLine("  update_owner = '" + qa.getUpdate_owner() + "',");
		// レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("  create_timestamp = '" + qa.getCreate_timestamp() + "',");
		// レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("  update_timestamp = '" + qa.getUpdate_timestamp() + "'");
		sql.appendLine(");");
		
		return sql;
	}
}
