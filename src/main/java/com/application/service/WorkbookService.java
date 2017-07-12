package com.application.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import com.application.model.dao.MondaishuTagRelationModel;
import com.application.model.dao.TagModel;

@Service
public class WorkbookService {

	public List<MondaishuTagRelationModel> getTagsKouhoForMondaishu(List<TagModel> tags_list)
	{
		List<MondaishuTagRelationModel> tags_kouho_list = new ArrayList<MondaishuTagRelationModel>();
		return tags_kouho_list;
	}
	
}
