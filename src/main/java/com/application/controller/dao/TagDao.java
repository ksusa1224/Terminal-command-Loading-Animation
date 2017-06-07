package com.application.controller.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.application.model.dao.MondaiModel;
import com.application.model.dao.TagModel;
import com.common.Constant;
import com.common.Log;
import com.common.StringBuilderPlus;
import com.common.Util;
import com.dao.H2dbDao;
import com.dao.SQliteDAO;

public class TagDao {

	/**
	 * 最大行を得る
	 * @param db_name
	 * @return
	 */
	public int get_max_row_no(String db_name)
	{	
		int max_row_no = 0;
		
		SQliteDAO dao = new SQliteDAO();
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("select max(row_no) as row_no from tag limit 1;");
		dao.loadDriver();
		
		//System.out.println(db_name);

	    Connection connection = null;
		String db_save_path = Constant.SQLITE_OWNER_DB_FOLDEDR_PATH + "/";
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
	    	  max_row_no = rs.getInt("row_no");
	    	  System.out.println(max_row_no);
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
	      dao.close(connection);
	    }	    
		
		return max_row_no;
	}

	public int get_max_junban(String db_name)
	{	
		int max_junban = 0;
		
		SQliteDAO dao = new SQliteDAO();
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("select max(junban) as junban from tag limit 1;");
		dao.loadDriver();
		
		//System.out.println(db_name);

	    Connection connection = null;
		String db_save_path = Constant.SQLITE_OWNER_DB_FOLDEDR_PATH + "/";
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
	    	  max_junban = rs.getInt("junban");
	    	  System.out.println(max_junban);
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
	      dao.close(connection);
	    }	    
		
		return max_junban;
	}

	/**
	 * 
	 * @param db_name
	 * @param tag
	 */
	public void refresh_tags_junban(String db_name, TagModel tag)
	{	
		SQliteDAO dao = new SQliteDAO();
		
		List<TagModel> tag_list = new ArrayList<TagModel>();
		tag_list = select_tag_list(db_name, tag_list);
		
		StringBuilderPlus sql = new StringBuilderPlus();
		int idx = 0;
		for (TagModel tag_model : tag_list)
		{
			idx++;
			sql.appendLine("update tag set junban = " + idx 
							+ " where tag_id = '" + tag_model.getTag_id() + "';");			
		}
		
		dao.loadDriver();
		
		//System.out.println(db_name);

	    Connection connection = null;
		String db_save_path = Constant.SQLITE_OWNER_DB_FOLDEDR_PATH + "/";
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
	      
	      /**
	       *  SQL実行
	       */
	      dao.transaction(stmt, sql);
	    }
	    catch(Exception ex)
	    {
			Log log = new Log();
			log.insert_error_log("ERROR", ex.getStackTrace().toString());
		    System.err.println(ex.getMessage());
	    }
	    finally
	    {
	      dao.close(connection);
	    }	    
	}	

	/**
	 * タグ名を編集
	 * @param db_name
	 * @param tag_id
	 * @param tag_name
	 */
	public void update_tag_name(String db_name, String tag_id, String tag_name)
	{	
		SQliteDAO dao = new SQliteDAO();
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("update tag set tag_name = '" + tag_name + "'" 
						+ " where tag_id = '" + tag_id + "';");			
		
		dao.loadDriver();
		
	    Connection connection = null;
		String db_save_path = Constant.SQLITE_OWNER_DB_FOLDEDR_PATH + "/";
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
	      
	      /**
	       *  SQL実行
	       */
	      dao.transaction(stmt, sql);
	    }
	    catch(Exception ex)
	    {
			Log log = new Log();
			log.insert_error_log("ERROR", ex.getStackTrace().toString());
	    }
	    finally
	    {
	      dao.close(connection);
	    }	    
	}	
	
	public List<TagModel> order_tag(String db_name, String husen_ids_in_order)
	{	
		System.out.println(husen_ids_in_order);
		Map<String, Integer> id_junban = new HashMap<String, Integer>();
		String[] husen_id_in_order = husen_ids_in_order.split(",");
		for (String id_order : husen_id_in_order)
		{
			id_junban.put(id_order.split(":")[0], Integer.parseInt(id_order.split(":")[1]));
		}
		
		SQliteDAO dao = new SQliteDAO();
				
		StringBuilderPlus sql = new StringBuilderPlus();
		
		for (Map.Entry<String, Integer> entry : id_junban.entrySet())
		{
			sql.appendLine("update tag set junban = " + entry.getValue() 
						+ " where tag_id = '" + entry.getKey() + "';");
		}
		
		dao.loadDriver();
		
		//System.out.println(db_name);

	    Connection connection = null;
		String db_save_path = Constant.SQLITE_OWNER_DB_FOLDEDR_PATH + "/";
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
	      
	      /**
	       *  SQL実行
	       */
	      dao.transaction(stmt, sql);	      
	    }
	    catch(Exception ex)
	    {
			Log log = new Log();
			log.insert_error_log("ERROR", ex.getStackTrace().toString());
		    System.err.println(ex.getMessage());
	    }
	    finally
	    {
	      dao.close(connection);
	    }	    
		List<TagModel> tag_list = new ArrayList<TagModel>();
		tag_list = select_tag_list(db_name, tag_list);
		return tag_list;
	}	
	
	public String select_tag_id(String db_name, String tag_name)
	{
		String tag_id = "";
		
		SQliteDAO dao = new SQliteDAO();
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("select tag_id from tag where tag_name = '" + tag_name + "'");
		dao.loadDriver();		

	    Connection connection = null;
		String db_save_path = Constant.SQLITE_OWNER_DB_FOLDEDR_PATH + "/";
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
	    	  tag_id = rs.getString("tag_id");
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
	      dao.close(connection);
	    }	    
		
		return tag_id;
	}
	
	
	public Boolean is_exist(String db_name, String tag_name)
	{
		Boolean is_exist = false;
		SQliteDAO dao = new SQliteDAO();
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("select tag_name from tag where tag_name = '" + tag_name + "'");
		dao.loadDriver();		

	    Connection connection = null;
		String db_save_path = Constant.SQLITE_OWNER_DB_FOLDEDR_PATH + "/";
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
	    	  is_exist = true;
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
	      dao.close(connection);
	    }	    
		
		return is_exist;
	}
	
	/**
	 * 
	 * @param db_name
	 * @param mondai_list
	 * @return
	 */
	public List<TagModel> select_yellow_tag_list(String db_name, List<TagModel> tag_list)
	{		
		SQliteDAO dao = new SQliteDAO();
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("select ");
		// 行番号
		sql.appendLine("  row_no,");
	    // タグID
		sql.appendLine("  tag_id,");
	    // タグ名
		sql.appendLine("  tag_name,");
	    // 表示順
		sql.appendLine("  junban,");
	    // 表示フラグ
		sql.appendLine("  display_flg,");
	    // 重要度（５段階）
		sql.appendLine("  juyoudo,");
	    // 難易度（５段階）
		sql.appendLine("  nanido,");
	    // システムタグフラグ
		sql.appendLine("  system_tag_flg,");
	    // タグ種別
		sql.appendLine("  tag_type,");
	    // デザイン種別
	    sql.appendLine("	design_type,");
	    // 公開範囲
		sql.appendLine("  koukai_level,");
	    // 言語
		sql.appendLine("  language,");
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
		sql.appendLine(" from tag");
		sql.appendLine(" where ");
		sql.appendLine(" del_flg = 0");
		sql.appendLine(" and system_tag_flg = 0");
		sql.appendLine(" order by junban asc;");
		
		dao.loadDriver();
		
	    Connection connection = null;
		String db_save_path = Constant.SQLITE_OWNER_DB_FOLDEDR_PATH + "/";
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
	    	  TagModel tag = new TagModel();
		      // 行番号
	    	  tag.setRow_no(rs.getInt("row_no"));
	    	  // タグID
	    	  tag.setTag_id(rs.getString("tag_id"));
	    	  // タグ名
	    	  tag.setTag_name(rs.getString("tag_name"));
	    	  // 表示順
	    	  tag.setJunban(rs.getInt("junban"));
	    	  // 表示フラグ
	    	  tag.setDisplay_flg(rs.getInt("display_flg"));
	    	  // 重要度（５段階）
	    	  tag.setJunban(rs.getInt("juyoudo"));
	    	  // 難易度（５段階）
	    	  tag.setNanido(rs.getInt("nanido"));
	    	  // システムタグフラグ
	    	  tag.setSystem_tag_flg(rs.getInt("system_tag_flg"));
	    	  // タグ種別
	    	  tag.setTag_type(rs.getInt("tag_type"));
		      // デザイン種別
	    	  tag.setDesign_type(rs.getInt("design_type"));		     
	    	  // 公開範囲
	    	  tag.setKoukai_level(rs.getInt("koukai_level"));
	    	  // 言語
	    	  tag.setLanguage(rs.getString("language"));
	    	  // 削除フラグ
		      tag.setDel_flg(rs.getInt("del_flg"));
		      // 作成者
		      tag.setCreate_owner(rs.getString("create_owner"));
		      // 更新者
		      tag.setUpdate_owner(rs.getString("update_owner"));
		      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		      tag.setUpdate_timestamp(rs.getString("create_timestamp"));
		      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		      tag.setUpdate_timestamp(rs.getString("update_timestamp"));

		      tag_list.add(tag);
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
	      dao.close(connection);
	    }	    
		
		return tag_list;
	}

	public List<TagModel> select_tag_list(String db_name, List<TagModel> tag_list)
	{		
		SQliteDAO dao = new SQliteDAO();
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("select ");
		// 行番号
		sql.appendLine("  row_no,");
	    // タグID
		sql.appendLine("  tag_id,");
	    // タグ名
		sql.appendLine("  tag_name,");
	    // 表示順
		sql.appendLine("  junban,");
	    // 表示フラグ
		sql.appendLine("  display_flg,");
	    // 重要度（５段階）
		sql.appendLine("  juyoudo,");
	    // 難易度（５段階）
		sql.appendLine("  nanido,");
	    // システムタグフラグ
		sql.appendLine("  system_tag_flg,");
	    // タグ種別
		sql.appendLine("  tag_type,");
	    // デザイン種別
	    sql.appendLine("	design_type,");
	    // 公開範囲
		sql.appendLine("  koukai_level,");
	    // 言語
		sql.appendLine("  language,");
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
		sql.appendLine(" from tag");
		sql.appendLine(" where ");
		sql.appendLine(" del_flg = 0");
		sql.appendLine(" order by junban asc;");
		
		dao.loadDriver();
		
	    Connection connection = null;
		String db_save_path = Constant.SQLITE_OWNER_DB_FOLDEDR_PATH + "/";
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
	    	  TagModel tag = new TagModel();
		      // 行番号
	    	  tag.setRow_no(rs.getInt("row_no"));
	    	  // タグID
	    	  tag.setTag_id(rs.getString("tag_id"));
	    	  // タグ名
	    	  tag.setTag_name(rs.getString("tag_name"));
	    	  // 表示順
	    	  tag.setJunban(rs.getInt("junban"));
	    	  // 表示フラグ
	    	  tag.setDisplay_flg(rs.getInt("display_flg"));
	    	  // 重要度（５段階）
	    	  tag.setJunban(rs.getInt("juyoudo"));
	    	  // 難易度（５段階）
	    	  tag.setNanido(rs.getInt("nanido"));
	    	  // システムタグフラグ
	    	  tag.setSystem_tag_flg(rs.getInt("system_tag_flg"));
	    	  // タグ種別
	    	  tag.setTag_type(rs.getInt("tag_type"));
		      // デザイン種別
	    	  tag.setDesign_type(rs.getInt("design_type"));		     
	    	  // 公開範囲
	    	  tag.setKoukai_level(rs.getInt("koukai_level"));
	    	  // 言語
	    	  tag.setLanguage(rs.getString("language"));
	    	  // 削除フラグ
		      tag.setDel_flg(rs.getInt("del_flg"));
		      // 作成者
		      tag.setCreate_owner(rs.getString("create_owner"));
		      // 更新者
		      tag.setUpdate_owner(rs.getString("update_owner"));
		      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		      tag.setUpdate_timestamp(rs.getString("create_timestamp"));
		      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		      tag.setUpdate_timestamp(rs.getString("update_timestamp"));

		      tag_list.add(tag);
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
	      dao.close(connection);
	    }	    
		
		return tag_list;
	}
	
	/**
	 * タグテーブルに１件レコードを追加する
	 * @param tag
	 * @return
	 */
	public void insert_tag(String db_name, TagModel tag)
	{
		SQliteDAO dao = new SQliteDAO();
	    Connection connection = null;
		String db_save_path = Constant.SQLITE_OWNER_DB_FOLDEDR_PATH + "/";
		String connection_str = "jdbc:sqlite:" 
				  				+ db_save_path
				  				+ db_name;
		
		H2dbDao h2dao = new H2dbDao();
	    Connection conn = null;				
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("insert into tag (");
		// 行番号
		sql.appendLine("  row_no,");
	    // タグID
		sql.appendLine("  tag_id,");
	    // タグ名
		sql.appendLine("  tag_name,");
	    // 表示順
		sql.appendLine("  junban,");
	    // 表示フラグ
		sql.appendLine("  display_flg,");
	    // 重要度（５段階）
		sql.appendLine("  juyoudo,");
	    // 難易度（５段階）
		sql.appendLine("  nanido,");
	    // システムタグフラグ
		sql.appendLine("  system_tag_flg,");
	    // タグ種別
		sql.appendLine("  tag_type,");
	    // デザイン種別
		sql.appendLine("  design_type,");
	    // 公開範囲
		sql.appendLine("  koukai_level,");
	    // 言語
		sql.appendLine("  language,");
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
		sql.appendLine("" + tag.getRow_no() + ",");
	    // タグID
		sql.appendLine("'" + tag.getTag_id() + "',");
	    // タグ名
		sql.appendLine("'" + tag.getTag_name() + "',");
	    // 表示順
		sql.appendLine("" + tag.getJunban() + ",");
	    // 表示フラグ
		sql.appendLine("" + tag.getDisplay_flg() + ",");
	    // 重要度（５段階）
		sql.appendLine("" + tag.getJuyoudo() + ",");
	    // 難易度（５段階）
		sql.appendLine("" + tag.getNanido() + ",");
	    // システムタグフラグ
		sql.appendLine("" + tag.getSystem_tag_flg() + ",");
	    // タグ種別
		sql.appendLine("" + tag.getTag_type() + ",");
	    // デザイン種別
		sql.appendLine("" + tag.getDesign_type() + ",");
	    // 公開範囲
		sql.appendLine("" + tag.getKoukai_level() + ",");
	    // 言語
		sql.appendLine("'" + tag.getLanguage() + "',");
		// 削除フラグ
		sql.appendLine("" + tag.getDel_flg() + ",");
		// 作成者
		sql.appendLine("'" + tag.getCreate_owner() + "',");
		// 更新者
		sql.appendLine("'" + tag.getUpdate_owner() + "',");
		// レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("'" + tag.getCreate_timestamp() + "',");
		// レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("'" + tag.getUpdate_timestamp() + "'");
		sql.appendLine(");");
	    
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
	      dao.transaction(stmt, sql);
	      
	      /**
	       * h2dbにもinsert
	       */
	      conn = h2dao.connect();
	      Statement h2stmt = conn.createStatement();

	      //1行ずつコミットしない
	      h2stmt.getConnection().setAutoCommit(false);
	      h2dao.transaction(h2stmt, sql);	      
	    }
	    catch(Exception ex)
	    {
			Log log = new Log();
			log.insert_error_log("ERROR", ex.getStackTrace().toString());
		    System.err.println(ex.getMessage());
	    }
	    finally
	    {
	      dao.close(connection);
		  h2dao.disconnect(conn);
	    }	    
		
	}	
	
	/**
	 * タグIDをもとに、タグを１件削除する
	 * @param db_name
	 * @param tag_id
	 */
	public void delete_tag(String db_name, String tag_id)
	{
		SQliteDAO dao = new SQliteDAO();
	    Connection connection = null;
		String db_save_path = Constant.SQLITE_OWNER_DB_FOLDEDR_PATH + "/";
		String connection_str = "jdbc:sqlite:" 
				  				+ db_save_path
				  				+ db_name;
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("delete from tag where tag_id = '" + tag_id + "'");
	    
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
	      dao.transaction(stmt, sql);
	    }
	    catch(Exception ex)
	    {
			Log log = new Log();
			log.insert_error_log("ERROR", ex.getStackTrace().toString());
		    //System.err.println(ex.getMessage());
	    }
	    finally
	    {
	      dao.close(connection);
	    }	    
	}
	
	public void add_system_tags(String owner_db, String owner_id)
	{
		add_system_tag(owner_db, owner_id, "復習のタイミング", 1);
		add_system_tag(owner_db, owner_id, "未正解", 2);
		add_system_tag(owner_db, owner_id, "正解", 3);
		add_system_tag(owner_db, owner_id, "読むだけ問題", 4);
		add_system_tag(owner_db, owner_id, "未分類", 5);
		add_system_tag(owner_db, owner_id, "問題と解答を反転", 6);
		add_system_tag(owner_db, owner_id, "新着順", 7);
		add_system_tag(owner_db, owner_id, "登録順", 8);
		add_system_tag(owner_db, owner_id, "ランダム順", 9);
	}
	
	/**
	 * 
	 * @param db_name
	 * @param owner_id
	 * @param tag_name
	 * @param junban
	 */
	public void add_system_tag(String db_name, String owner_id, String tag_name, int junban) {
		/**
		 * DBパッチ
		 */
		try
		{
			if (is_exist(db_name, tag_name))
			{
				return;
			}
			
			TagModel tag = new TagModel();
		    // 行番号
			tag.setRow_no(get_max_row_no(db_name)+1);
		    // タグID
			tag.setTag_id(tag.generate_tag_id(get_max_row_no(db_name) + 1, owner_id));
		    // タグ名
			tag.setTag_name(tag_name);
		    // 表示順
			tag.setJunban(junban);
		    // 表示フラグ
			tag.setDisplay_flg(1);
		    // 重要度（５段階）
			tag.setJuyoudo(3);
		    // 難易度（５段階）
			tag.setNanido(3);
		    // システムタグフラグ
			tag.setSystem_tag_flg(1);
		    // タグ種別
			tag.setTag_type(junban);
		    // デザイン種別
			tag.setDesign_type(0);
		    // 公開範囲
			tag.setKoukai_level(Constant.KOUKAI_LEVEL_SELF_ONLY);
		    // 言語
			tag.setLanguage(Util.check_japanese_or_english(tag_name));
		    // 削除フラグ
			tag.setDel_flg(0);
		    // 作成者
			tag.setCreate_owner(owner_id);
		    // 更新者
			tag.setUpdate_owner(owner_id);
		    // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
			tag.setCreate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
		    // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
			tag.setUpdate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
		
			insert_tag(db_name, tag);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}		
}
