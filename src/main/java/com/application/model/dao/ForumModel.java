package com.application.model.dao;

import lombok.Data;

public @Data class ForumModel {
	
	private Integer forum_id;
	private Integer count;
	private String forum_name;
	private String file_name;
	private String create_date;
	private String update_date;
	private int delete_flag;

}
