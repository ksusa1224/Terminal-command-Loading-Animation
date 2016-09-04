
package com.application.model.dao;

import java.util.ArrayList;
import java.util.List;

import com.common.StringBuilderPlus;

import lombok.Data;

/**
 * QAとそれに紐づく１対多の問題モデルと１対多の正答モデルを定義するクラス
 * TODO 解答モデル等も紐付けさせるかは要検討
 * @author ksusa
 *
 */
public @Data class QAPlusModel {
	private QAModel qa = new QAModel();
	private List<MondaiModel> mondai_list = new ArrayList<MondaiModel>();
	private List<SeitouModel> seitou_list = new ArrayList<SeitouModel>();

	/**
	 * 所定のフォーマットでqa_idを生成する
	 * @param row_no
	 * @param owner_id
	 * @return
	 */
	public String generate_qa_id(int row_no, String owner_id)
	{
		// %09d・・・１億桁でゼロ埋め		
		return "qa_id_" + String.format("%09d", row_no) + "_" + owner_id;
	}	
}
