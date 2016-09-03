
package com.application.model.dao;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * QAとそれに紐づく１対多の問題モデルと１対多の正答モデルを定義するクラス
 * TODO 解答モデル等も紐付けさせるかは要検討
 * @author ksusa
 *
 */
@EqualsAndHashCode(callSuper=true)
public @Data class QAPlusModel extends QAModel {
	private List<MondaiModel> mondai_list = new ArrayList<MondaiModel>();
	private List<SeitouModel> seitou_list = new ArrayList<SeitouModel>();
}
