package com.application.controller.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import com.application.model.dao.SeitouModel;
import com.common.Constant;
import com.common.StringBuilderPlus;
import com.dao.SQliteDAO;

public class SeitouDao {

	/**
	 * 最大行を得る
	 * @param db_name
	 * @return
	 */
	public int get_seitou_max_row_no(String db_name)
	{	
		int max_row_no = 0;
		
		SQliteDAO dao = new SQliteDAO();
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("select max(row_no) as row_no from seitou limit 1;");
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
	 * @param seitou_list
	 * @return
	 */
	public List<SeitouModel> select_seitou_list(String db_name, List<SeitouModel> seitou_list)
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
		  sql.appendLine(" from seitou;");
		
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
	    	  SeitouModel seitou = new SeitouModel();
		      // 行番号
	    	  seitou.setRow_no(rs.getInt("row_no"));
		      // 正答ID
	    	  seitou.setS_id(rs.getString("s_id"));
		      // QA ID
	    	  seitou.setQa_id(rs.getString("qa_id"));
		      // QA内での正答の順番
	    	  seitou.setJunban(rs.getInt("junban"));
		      // 正答
	    	  seitou.setSeitou(rs.getString("seitou"));
		      // 重要度（５段階）
	    	  seitou.setJuyoudo(rs.getInt("juyoudo"));
		      // 難易度（５段階）
	    	  seitou.setNanido(rs.getInt("nanido"));
	    	  // 言語
	    	  seitou.setLanguage(rs.getString("language"));
	    	  // 削除フラグ
		      seitou.setDel_flg(rs.getInt("del_flg"));
		      // 作成者
		      seitou.setCreate_owner(rs.getString("create_owner"));
		      // 更新者
		      seitou.setUpdate_owner(rs.getString("update_owner"));
		      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		      seitou.setUpdate_timestamp(rs.getString("create_timestamp"));
		      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		      seitou.setUpdate_timestamp(rs.getString("update_timestamp"));

		      seitou_list.add(seitou);
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
		
		return seitou_list;
	}
	
	/**
	 * 正答テーブルに１件レコードを追加する
	 * @param seitou
	 * @return
	 */
	public void insert_seitou(String db_name, SeitouModel seitou)
	{
		int max_row_no = get_seitou_max_row_no(db_name) + 1;
		
		SQliteDAO dao = new SQliteDAO();
	    Connection connection = null;
		String db_save_path = Constant.SQLITE_OWNER_DB_FOLDEDR_PATH + "/";
		String connection_str = "jdbc:sqlite:" 
				  				+ db_save_path
				  				+ db_name;
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("insert into seitou (");
		// 行番号
		sql.appendLine("  row_no,");

		// 正答ID
		sql.appendLine("	s_id text primary key unique not null,");
		// QA ID
		sql.appendLine("	qa_id text not null,");
		// QA内での正答の順番
		sql.appendLine("	junban integer default 1,");
		// 正答
		sql.appendLine("	seitou text,");
		// 重要度（５段階）
		sql.appendLine("	juyoudo integer default 3,");
		// 難易度（５段階）
		sql.appendLine("	nanido integer default 3,");
		
		
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
		sql.appendLine(max_row_no + ",");
	    // 正答ID
		sql.appendLine("'s_id_" + max_row_no + "_" + seitou.getCreate_owner() + "',");
		// QA ID
		sql.appendLine("'" + seitou.getQa_id() + "',");
		// QA内での正答の順番
		sql.appendLine("" + seitou.getJunban() + ",");
		// 正答
		sql.appendLine("'" + seitou.getSeitou() + "',");
		// 重要度（５段階）
		sql.appendLine("" + seitou.getJuyoudo() + ",");
		// 難易度（５段階）
		sql.appendLine("" + seitou.getNanido() + ",");
		// 言語
		sql.appendLine("'" + seitou.getLanguage() + "',");
		// 削除フラグ
		sql.appendLine("" + seitou.getDel_flg() + ",");
		// 作成者
		sql.appendLine("'" + seitou.getCreate_owner() + "',");
		// 更新者
		sql.appendLine("'" + seitou.getUpdate_owner() + "',");
		// レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("'" + seitou.getCreate_timestamp() + "',");
		// レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("'" + seitou.getUpdate_timestamp() + "'");
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
	 * @param seitou
	 * @return 
	 */
	public void update_seitou(String db_name, SeitouModel seitou)
	{
		SQliteDAO dao = new SQliteDAO();
	    Connection connection = null;
		String db_save_path = Constant.SQLITE_OWNER_DB_FOLDEDR_PATH + "/";
		String connection_str = "jdbc:sqlite:" 
				  				+ db_save_path
				  				+ db_name;
		
		StringBuilderPlus sql = new StringBuilderPlus();
				
		sql.appendLine("update seitou ");
		sql.appendLine("values (");
	    // 行番号
		sql.appendLine("  row_no = " + seitou.getRow_no() + ",");
	    // 正答ID
		sql.appendLine("  s_id = '" + seitou.getS_id() + "',");
	    // QA ID
		sql.appendLine("  qa_id = '" + seitou.getQa_id() + "',");
	    // QA内での正答の順番
		sql.appendLine("  junban = " + seitou.getJunban() + ",");
	    // 正答
		sql.appendLine("  seitou = '" + seitou.getSeitou() + "',");
	    // 重要度（５段階）
		sql.appendLine("  juyoudo = " + seitou.getJuyoudo() + ",");
	    // 難易度（５段階）
		sql.appendLine("  nanido = " + seitou.getNanido() + ",");
	    // 言語
		sql.appendLine("  language = '" + seitou.getLanguage() + "',");
		// 削除フラグ
		sql.appendLine("  del_flg = " + seitou.getDel_flg() + ",");
		// 作成者
		sql.appendLine("  create_owner = '" + seitou.getCreate_owner() + "',");
		// 更新者
		sql.appendLine("  update_owner = '" + seitou.getUpdate_owner() + "',");
		// レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("  create_timestamp = '" + seitou.getCreate_timestamp() + "',");
		// レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("  update_timestamp = '" + seitou.getUpdate_timestamp() + "'");
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
