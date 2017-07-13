package com.application.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.controller.dao.TagDao;
import com.application.model.dao.MondaishuTagRelationModel;
import com.application.model.dao.TagModel;

@Service
public class WorkbookService {

	@Autowired
	TagDao tagDao;
	
	/**
	 * 
	 * @param db_name
	 * @param qa_id_list
	 * @return
	 */
	public List<TagModel> getTagsKouhoForMondaishu(String db_name, List<String> qa_id_list)
	{
		List<TagModel> tags_kouho_list = tagDao.select_tags_related_qa(db_name, qa_id_list);
		return tags_kouho_list;
	}
	
	public String createTagsKouhoForMondaishuHtml(List<TagModel> tags_kouho_list)
	{
		String husen_html = "";
		for (TagModel tag : tags_kouho_list)
		{
			husen_html += ("<div id='" + tag.getTag_id() + "' class='husen'>" + tag.getTag_name() + "</div>");
		}
		return husen_html;
	}
	
}
