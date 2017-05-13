package com.application.model.dao;

import lombok.Data;

public @Data class CommentModel {
	
	private Integer comment_id;
	private Integer forum_id;
	private String name;
	private String email;
	private String comment;
	private String comment_date;
	private String create_date;
	private String update_date;
	private int delete_flag;

}
