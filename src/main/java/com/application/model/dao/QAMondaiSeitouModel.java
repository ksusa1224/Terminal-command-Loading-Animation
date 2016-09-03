package com.application.model.dao;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
public @Data class QAMondaiSeitouModel extends QAModel {
	private List<MondaiModel> mondai_list = new ArrayList<MondaiModel>();
	private List<SeitouModel> seitou_list = new ArrayList<SeitouModel>();
}
