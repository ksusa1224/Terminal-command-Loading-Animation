package com.application.controller.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import com.application.model.dao.MondaiModel;
import com.common.Constant;
import com.common.StringBuilderPlus;
import com.dao.SQliteDAO;

public class MondaiDao {

	/**
	 * 最大行を得る
	 * @param db_name
	 * @return
	 */
	public int get_mondai_max_row_no(String db_name)
	{	
		int max_row_no = 0;
		
		SQliteDAO dao = new SQliteDAO();
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("select max(row_no) as row_no from mondai limit 1;");
		dao.loadDriver();
		
		System.out.println(db_name);

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
	    	//TODO ログ出力
		    System.err.println(ex.getMessage());
	    }
	    finally
	    {
	      dao.close(connection);
	    }	    
		
		return max_row_no;
	}
	
	
	/**
	 * @param db_name
	 * @param mondai_list
	 * @return
	 */
	public List<MondaiModel> select_mondai_list(String db_name, List<MondaiModel> mondai_list)
	{		
		SQliteDAO dao = new SQliteDAO();
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("select ");
		// 行番号
		sql.appendLine("  row_no,");
		// 問題ID
		sql.appendLine("  q_id,");
		// QA ID
		sql.appendLine("	qa_id,");
		// QA内での問題パーツの順番
		sql.appendLine("	junban,");
		// 問題パーツが文字であるかのフラグ
		sql.appendLine("  is_text_flg,");
		// 問題パーツがバイナリであるかのフラグ
		sql.appendLine("  is_binary_flg,");
		// 分割された問題文
		sql.appendLine("  q_parts_text,");
		// QAの中に出てくる音声や画像などのバイナリファイル
		sql.appendLine("  q_parts_binary,");
		// 言語
		sql.appendLine("  language,");
		// テキスト読み上げデータ
		sql.appendLine("  yomiage,");
		// 削除フラグ
		sql.appendLine("	del_flg,");
		// 作成者
		sql.appendLine("  create_owner,");
		// 更新者
		sql.appendLine("  update_owner,");
		// レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("	create_timestamp,");
		// レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("	update_timestamp,");
		  sql.appendLine(" from mondai;");
		
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
	    	  MondaiModel mondai = new MondaiModel();
		      // 行番号
	    	  mondai.setRow_no(rs.getInt("row_no"));
	    	  // 問題ID
	    	  mondai.setQ_id(rs.getString("q_id"));
	    	  // QA ID
	    	  mondai.setQa_id(rs.getString("qa_id"));
	    	  // QA内での問題パーツの順番
	    	  mondai.setJunban(rs.getInt("junban"));
	    	  // 問題パーツが文字であるかのフラグ
	    	  mondai.setIs_text_flg(rs.getInt("is_text_flg"));
	    	  // 問題パーツがバイナリであるかのフラグ	    	  
	    	  mondai.setIs_binary_flg(rs.getInt("is_binary_flg"));
	    	  // 分割された問題文	    	  
	    	  mondai.setQ_parts_text(rs.getString("q_parts_text"));
	    	  // QAの中に出てくる音声や画像などのバイナリファイル	    	  
	    	  mondai.setQ_parts_binary(rs.getBytes("q_parts_binary"));
	    	  // 言語
	    	  mondai.setLanguage(rs.getString("language"));
	    	  // テキスト読み上げデータ
	    	  mondai.setYomiage(rs.getBytes("yomiage"));
	    	  // 削除フラグ
		      mondai.setDel_flg(rs.getInt("del_flg"));
		      // 作成者
		      mondai.setCreate_owner(rs.getString("create_owner"));
		      // 更新者
		      mondai.setUpdate_owner(rs.getString("update_owner"));
		      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		      mondai.setUpdate_timestamp(rs.getString("create_timestamp"));
		      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		      mondai.setUpdate_timestamp(rs.getString("update_timestamp"));

		      mondai_list.add(mondai);
	      }
	    }
	    catch(Exception ex)
	    {
	    	//TODO ログ出力
		    System.err.println(ex.getMessage());
	    }
	    finally
	    {
	      dao.close(connection);
	    }	    
		
		return mondai_list;
	}
	
	/**
	 * 問題テーブルに１件レコードを追加する
	 * @param mondai
	 * @return
	 */
	public void insert_mondai(String db_name, MondaiModel mondai)
	{
		int max_row_no = get_mondai_max_row_no(db_name) + 1;
		
		SQliteDAO dao = new SQliteDAO();
	    Connection connection = null;
		String db_save_path = Constant.SQLITE_OWNER_DB_FOLDEDR_PATH + "/";
		String connection_str = "jdbc:sqlite:" 
				  				+ db_save_path
				  				+ db_name;
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("insert into mondai (");
		// 行番号
		sql.appendLine("  row_no,");
		// 問題ID
		sql.appendLine("  q_id,");
		// QA ID
		sql.appendLine("	qa_id,");
		// QA内での問題パーツの順番
		sql.appendLine("	junban,");
		// 問題パーツが文字であるかのフラグ
		sql.appendLine("  is_text_flg,");
		// 問題パーツがバイナリであるかのフラグ
		sql.appendLine("  is_binary_flg,");
		// 分割された問題文
		sql.appendLine("  q_parts_text,");
		// QAの中に出てくる音声や画像などのバイナリファイル
		sql.appendLine("  q_parts_binary,");
		// 言語
		sql.appendLine("  language,");
		// テキスト読み上げデータ
		sql.appendLine("  yomiage,");		
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
		sql.appendLine(max_row_no + ",");
	    // 問題ID
		sql.appendLine("'q_id_" + max_row_no + "_" + mondai.getCreate_owner() + "',");
	    // QA ID
		sql.appendLine("'" + mondai.getQa_id() + "',");
	    // QA内での問題パーツの順番
		sql.appendLine("" + mondai.getJunban() + ",");
	    // 問題パーツが文字であるかのフラグ
		sql.appendLine("" + mondai.getIs_text_flg() + ",");
	    // 問題パーツがバイナリであるかのフラグ
		sql.appendLine("" + mondai.getIs_binary_flg() + ",");
	    // 分割された問題文
		sql.appendLine("'" + mondai.getQ_parts_text() + "',");
	    // QAの中に出てくる音声や画像などのバイナリファイル
		sql.appendLine("" + mondai.getQ_parts_binary() + ",");
	    // 言語
		sql.appendLine("'" + mondai.getLanguage() + "',");
		// テキスト読み上げデータ
		sql.appendLine("" + mondai.getYomiage() + ",");
		// 削除フラグ
		sql.appendLine("" + mondai.getDel_flg() + ",");
		// 作成者
		sql.appendLine("'" + mondai.getCreate_owner() + "',");
		// 更新者
		sql.appendLine("'" + mondai.getUpdate_owner() + "',");
		// レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("'" + mondai.getCreate_timestamp() + "',");
		// レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("'" + mondai.getUpdate_timestamp() + "'");
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
	    	//TODO ログ出力
		    System.err.println(ex.getMessage());
	    }
	    finally
	    {
	      dao.close(connection);
	    }	    
		
	}	
	
	/**
	 * QAテーブルを１件更新する
	 * @param mondai
	 * @return 
	 */
	public void update_mondai(String db_name, MondaiModel mondai)
	{
		SQliteDAO dao = new SQliteDAO();
	    Connection connection = null;
		String db_save_path = Constant.SQLITE_OWNER_DB_FOLDEDR_PATH + "/";
		String connection_str = "jdbc:sqlite:" 
				  				+ db_save_path
				  				+ db_name;
		
		StringBuilderPlus sql = new StringBuilderPlus();
				
		sql.appendLine("update mondai ");
		sql.appendLine("values (");
	    // 行番号
		sql.appendLine("  row_no = " + mondai.getRow_no() + ",");
		// QA ID
		sql.appendLine("  qa_id = '" + mondai.getQa_id() + "',");
	    // 問題ID
		sql.appendLine("  q_id = '" + mondai.getQ_id() + "',");
	    // QA内での問題パーツの順番
		sql.appendLine("  junban = " + mondai.getJunban() + ",");
	    // 問題パーツが文字であるかのフラグ
		sql.appendLine("  is_text_flg = " + mondai.getIs_text_flg() + ",");
	    // 問題パーツがバイナリであるかのフラグ
		sql.appendLine("  is_binary_flg = " + mondai.getIs_binary_flg() + ",");
	    // 分割された問題文
		sql.appendLine("  q_parts_text = '" + mondai.getQ_parts_text() + "',");
	    // QAの中に出てくる音声や画像などのバイナリファイル
		sql.appendLine("  q_parts_binary = " + mondai.getQ_parts_binary() + ",");
	    // 言語
		sql.appendLine("  language = '" + mondai.getLanguage() + "',");
		// テキスト読み上げデータ
		sql.appendLine("  yomiage = " + mondai.getYomiage() + ",");
		// 削除フラグ
		sql.appendLine("  del_flg = " + mondai.getDel_flg() + ",");
		// 作成者
		sql.appendLine("  create_owner = '" + mondai.getCreate_owner() + "',");
		// 更新者
		sql.appendLine("  update_owner = '" + mondai.getUpdate_owner() + "',");
		// レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("  create_timestamp = '" + mondai.getCreate_timestamp() + "',");
		// レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("  update_timestamp = '" + mondai.getUpdate_timestamp() + "'");
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
	    	//TODO ログ出力
		    System.err.println(ex.getMessage());
	    }
	    finally
	    {
	      dao.close(connection);
	    }		
	}

}
