package com.application.controller.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.application.model.dao.CommentModel;
import com.application.model.dao.ForumModel;
import com.common.Constant;
import com.common.Log;
import com.common.StringBuilderPlus;
import com.common.Util;
import com.dao.SQliteDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForumDao {
	
	public void create_forum_db()
	{
		SQliteDAO sqlite_dao = new SQliteDAO();
		sqlite_dao.loadDriver();
		
	    Connection connection = null;
		String db_name = "forum.db";
		String db_save_path = Constant.SQLITE_FOLDER_PATH + "/";
		String connection_str = "jdbc:sqlite:" 
				  				+ db_save_path
				  				+ db_name;
	    try
	    {
	      // DBが存在していたら接続、存在していなければ作成
	      connection = DriverManager.getConnection(connection_str);
	      Statement stmt = connection.createStatement();

	      //1行ずつコミットしない
	      stmt.getConnection().setAutoCommit(false);
	      
	      StringBuilderPlus sql = new StringBuilderPlus();
	      
	      /**
	       *  パフォーマンスチューニング　
	       *  参考URL:http://arbitrage.jpn.org/it/2015-07-07-2/
	       */
	      // TODO 記載位置が合っているか要確認　TODO DB BROWSER FOR SQLITEで開いたら、以下は反映されていなかった模様。
		  sql.appendLine("PRAGMA default_synchronous = OFF;");
		  sql.appendLine("PRAGMA journal_mode = PESIST;");

		  // 掲示板テーブル
		  sql.appendLine("create table if not exists forum (");
		  sql.appendLine("forum_id integer,");
		  sql.appendLine("forum_name text primary key,");
		  sql.appendLine("file_name text,");
		  sql.appendLine("create_date text,");
		  sql.appendLine("update_date text,");
		  sql.appendLine("delete_flag integer);");
		  
		  // コメントテーブル
		  sql.appendLine("create table if not exists comment(");
		  sql.appendLine("comment_id integer primary key,");
		  sql.appendLine("forum_id integer,");
		  sql.appendLine("name text,");
		  sql.appendLine("email text,");
		  sql.appendLine("comment text,");
		  sql.appendLine("comment_date text,");
		  sql.appendLine("create_date text,");
		  sql.appendLine("update_date text,");
		  sql.appendLine("delete_flag integer)");
		  
	      sqlite_dao.transaction(stmt, sql);
	      
	    }
	    catch(SQLException e)
	    {
			Log log = new Log();
			log.insert_error_log("ERROR", e.getStackTrace().toString());
	      System.err.println(e.getMessage());
	    }
	    finally
	    {
	      sqlite_dao.close(connection);
	    }		
	}
	
	public void insert_forum_data(
			String forum_name, 
			String file_name,
			Integer forum_id,
			String name,
			String email,
			String comment)
	{
		SQliteDAO sqlite_dao = new SQliteDAO();
	    Connection connection = null;
		String db_name = "forum.db";
		String db_save_path = Constant.SQLITE_FOLDER_PATH + "/";
		String connection_str = "jdbc:sqlite:" 
				  				+ db_save_path
				  				+ db_name;
		
		StringBuilderPlus sql = new StringBuilderPlus();
		
		// 掲示板テーブル
		sql.appendLine("replace into forum (");
		sql.appendLine("forum_id,");
		sql.appendLine("forum_name,");
		sql.appendLine("file_name,");
		sql.appendLine("create_date,");
		sql.appendLine("update_date,");
		sql.appendLine("delete_flag)");
		sql.appendLine("");
		sql.appendLine("values (");
		sql.appendLine(forum_id + ",");
		sql.appendLine("'" + forum_name + "',");
		sql.appendLine("'" + file_name + "',");
		sql.appendLine("'" + Util.getNow(Constant.DB_DATE_FORMAT) + "',");
		sql.appendLine("'" + Util.getNow(Constant.DB_DATE_FORMAT) + "',");
		sql.appendLine("0);");

		// コメントテーブル
		if (comment != null)
		{
			sql.appendLine("insert into comment (");
			sql.appendLine("forum_id,");
			sql.appendLine("name,");
			sql.appendLine("email,");
			sql.appendLine("comment,");
			sql.appendLine("comment_date,");
			sql.appendLine("create_date,");
			sql.appendLine("update_date,");
			sql.appendLine("delete_flag)");
			sql.appendLine("values (");
			sql.appendLine("'" + forum_id + "',");
			sql.appendLine("'" + name + "',");
			sql.appendLine("'" + email + "',");
			sql.appendLine("'" + comment + "',");
			sql.appendLine("'" + Util.getNow(Constant.DB_DATE_FORMAT) + "',");
			sql.appendLine("'" + Util.getNow(Constant.DB_DATE_FORMAT) + "',");
			sql.appendLine("'" + Util.getNow(Constant.DB_DATE_FORMAT) + "',");
			sql.appendLine("0)");
		}
		
		try
	    {
	      // DBが存在していたら接続、存在していなければ作成
	      connection = DriverManager.getConnection(connection_str);
	      Statement stmt = connection.createStatement();

	      //1行ずつコミットしない
	      stmt.getConnection().setAutoCommit(false);
	      
	      /**
	       *  SQL実行
	       */
	      sqlite_dao.transaction(stmt, sql);
	    }
	    catch(Exception ex)
	    {
			Log log = new Log();
			log.insert_error_log("ERROR", ex.getStackTrace().toString());
		    System.err.println(ex.getMessage());
	    }
	    finally
	    {
	    	sqlite_dao.close(connection);
	    }		
	}
	
	public List<ForumModel> get_forums()
	{
		List<ForumModel> forums = new ArrayList<ForumModel>();
		SQliteDAO sqlite_dao = new SQliteDAO();		
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("select forum_id, forum_name, file_name from forum");
		sql.appendLine(" where forum.delete_flag = 0");
		sql.appendLine(" order by forum.update_date desc;");
		sqlite_dao.loadDriver();
		
		System.out.println(sql.toString());
		
	    Connection connection = null;
		String db_name = "forum.db";
		String db_save_path = Constant.SQLITE_FOLDER_PATH + "/";
		String connection_str = "jdbc:sqlite:" 
				  				+ db_save_path
				  				+ db_name;
	    try
	    {
	      // DBが存在していたら接続、存在していなければ作成
	      connection = DriverManager.getConnection(connection_str);
	      Statement stmt = connection.createStatement();
	      ResultSet rs = stmt.executeQuery(sql.toString());
	      while (rs.next()) 
	      {
	    	  ForumModel forum = new ForumModel();
	    	  forum.setForum_name(rs.getString("forum_name"));
	    	  forum.setFile_name(rs.getString("file_name"));
	    	  forum.setCount(get_comments_count(rs.getInt("forum_id")));
	    	  forums.add(forum);
	      }
	    }
	    catch(Exception ex)
	    {
			Log log = new Log();
			log.insert_error_log("ERROR", ex.getStackTrace().toString());
		    System.err.println(ex.getMessage());
	    }
	    finally
	    {
	      sqlite_dao.close(connection);
	    }
	    
		return forums;
	}

	public Integer get_comments_count(Integer forum_id)
	{
		Integer count = 0;
		SQliteDAO sqlite_dao = new SQliteDAO();		
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("select count(comment_id) as count from comment");
		sql.appendLine(" where comment.delete_flag = 0");
		sql.appendLine(" and forum_id = " + forum_id);
		sqlite_dao.loadDriver();
		
		System.out.println(sql.toString());
		
	    Connection connection = null;
		String db_name = "forum.db";
		String db_save_path = Constant.SQLITE_FOLDER_PATH + "/";
		String connection_str = "jdbc:sqlite:" 
				  				+ db_save_path
				  				+ db_name;
	    try
	    {
	      // DBが存在していたら接続、存在していなければ作成
	      connection = DriverManager.getConnection(connection_str);
	      Statement stmt = connection.createStatement();
	      ResultSet rs = stmt.executeQuery(sql.toString());
	      while (rs.next()) 
	      {
	    	  count = rs.getInt("count");
	      }
	    }
	    catch(Exception ex)
	    {
			Log log = new Log();
			log.insert_error_log("ERROR", ex.getStackTrace().toString());
		    System.err.println(ex.getMessage());
	    }
	    finally
	    {
	      sqlite_dao.close(connection);
	    }
	    
		return count;
	}
	
	public List<CommentModel> get_comments(Integer forum_id)
	{	
		SQliteDAO sqlite_dao = new SQliteDAO();		
		List<CommentModel> comments = new ArrayList<CommentModel>();
				
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("select * from comment");
		sql.appendLine(" inner join forum");
		sql.appendLine(" on comment.forum_id = forum.forum_id");
		sql.appendLine(" where comment.forum_id = " + forum_id + "");
		sql.appendLine(" and comment.delete_flag = 0");
		sql.appendLine(" and forum.delete_flag = 0");
		sql.appendLine(" order by comment_date desc;");
		sqlite_dao.loadDriver();
		
		System.out.println(sql.toString());
		
	    Connection connection = null;
		String db_name = "forum.db";
		String db_save_path = Constant.SQLITE_FOLDER_PATH + "/";
		String connection_str = "jdbc:sqlite:" 
				  				+ db_save_path
				  				+ db_name;
	    try
	    {
	      // DBが存在していたら接続、存在していなければ作成
	      connection = DriverManager.getConnection(connection_str);
	      Statement stmt = connection.createStatement();
	      ResultSet rs = stmt.executeQuery(sql.toString());
	      while (rs.next()) 
	      {
	    	  CommentModel comment = new CommentModel();
	    	  comment.setName(rs.getString("name"));
	    	  comment.setEmail(rs.getString("email"));
	    	  comment.setComment(rs.getString("comment").replaceAll("\n","<br />"));
	    	  comment.setComment_date(rs.getString("comment_date"));
	    	  comments.add(comment);
	      }
	    }
	    catch(Exception ex)
	    {
			Log log = new Log();
			log.insert_error_log("ERROR", ex.getStackTrace().toString());
		    System.err.println(ex.getMessage());
	    }
	    finally
	    {
	      sqlite_dao.close(connection);
	    }	    
		
		return comments;
	}
}
