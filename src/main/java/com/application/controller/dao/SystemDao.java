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
import com.application.model.dao.SystemModel;
import com.application.model.dao.TagModel;
import com.common.Constant;
import com.common.Log;
import com.common.StringBuilderPlus;
import com.common.Util;
import com.dao.H2dbDao;
import com.dao.SQliteDAO;

public class SystemDao {

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
		sql.appendLine("select max(row_no) as row_no from system limit 1;");
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
	
	/**
	 * 値の取得
	 * @param db_name
	 * @param sys_group_id
	 * @param key
	 * @return
	 */
	public String get_value(String db_name, String sys_group_id, String key)
	{	
		String value = "";
		
		SQliteDAO dao = new SQliteDAO();
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("select value from system");
		sql.appendLine(" where sys_group_id = '" + sys_group_id + "'");
		sql.appendLine(" and key = '" + key + "'");
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
	      if (rs.next()) 
	      {
	    	  value = rs.getString(1);
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
	    return value;
	}
	
	/**
	 * 値を更新する
	 * @param db_name
	 * @param sys_group_id
	 * @param key
	 * @param value
	 */
	public void update_value(
			String db_name, 
			String sys_group_id, 
			String key,
			String value)
	{	
		SQliteDAO dao = new SQliteDAO();
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("update system set value = '" + value + "'" 
						+ " where sys_group_id = '" + sys_group_id + "'"
								+ " and key = '" + key + "';");			
		
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
	
	public Boolean check_deplicate(String db_name, String sys_group_id, String key)
	{	
		Boolean is_deplicate = false;
		
		SQliteDAO dao = new SQliteDAO();
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine(" select count('x') from system");
		sql.appendLine(" where sys_group_id = '" + sys_group_id + "'");
		sql.appendLine(" and key = '" + key + "'");
	
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
	      if (rs.next()) 
	      {
	    	  if (rs.getInt(1) > 0)
	    	  {
	    		  is_deplicate = true;
	    	  }
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
		
		return is_deplicate;
	}			
	
	/**
	 * システムテーブルに１件レコードを追加する
	 * @param tag
	 * @return
	 */
	public void insert_system(String db_name, SystemModel system)
	{
		SQliteDAO dao = new SQliteDAO();
	    Connection connection = null;
		String db_save_path = Constant.SQLITE_OWNER_DB_FOLDEDR_PATH + "/";
		String connection_str = "jdbc:sqlite:" 
				  				+ db_save_path
				  				+ db_name;
				
		StringBuilderPlus sql = new StringBuilderPlus();
				
		sql.appendLine("insert into system (");
		// 行番号
		sql.appendLine("  row_no,");
	    // 設定ID
	    sql.appendLine("	sys_id,");
	    // 項目グループID
	    sql.appendLine("	sys_group_id,");
	    // 項目グループ名
	    sql.appendLine("	sys_group_name,");
	    // キー
	    sql.appendLine("	key,");
	    // 値
	    sql.appendLine("	value,");
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
		sql.appendLine("" + system.getRow_no() + ",");
	    // 設定ID
		sql.appendLine("'" + system.getSys_id() + "',");
	    // 項目グループID
		sql.appendLine("'" + system.getSys_group_id() + "',");
	    // 項目グループ名
		sql.appendLine("'" + system.getSys_group_name() + "',");
	    // キー
		sql.appendLine("'" + system.getKey() + "',");
	    // 値
		sql.appendLine("'" + system.getValue() + "',");
		// 削除フラグ
		sql.appendLine("" + system.getDel_flg() + ",");
		// 作成者
		sql.appendLine("'" + system.getCreate_owner() + "',");
		// 更新者
		sql.appendLine("'" + system.getUpdate_owner() + "',");
		// レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("'" + system.getCreate_timestamp() + "',");
		// レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("'" + system.getUpdate_timestamp() + "'");
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
	 * システムIDをもとに、システムレコードを１件削除する
	 * @param db_name
	 * @param tag_id
	 */
	public void delete_sys_record(String db_name, String sys_id)
	{
		SQliteDAO dao = new SQliteDAO();
	    Connection connection = null;
		String db_save_path = Constant.SQLITE_OWNER_DB_FOLDEDR_PATH + "/";
		String connection_str = "jdbc:sqlite:" 
				  				+ db_save_path
				  				+ db_name;
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("delete from system where sys_id = '" + sys_id + "'");
	    
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
}
