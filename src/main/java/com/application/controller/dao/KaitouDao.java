package com.application.controller.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import com.application.model.dao.KaitouModel;
import com.application.model.dao.SeitouModel;
import com.common.Constant;
import com.common.Log;
import com.common.StopWatch;
import com.common.StringBuilderPlus;
import com.dao.SQliteDAO;

public class KaitouDao {

	/**
	 * 最大行を得る
	 * @param db_name
	 * @return
	 */
	public int get_kaitou_max_row_no(String db_name)
	{	
		int max_row_no = 0;
		
		SQliteDAO dao = new SQliteDAO();
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("select max(row_no) as row_no from kaitou limit 1;");
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
	    	  //System.out.println(max_row_no);
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
	 * 正解数を得る
	 * @param db_name
	 * @return
	 */
	public int get_seikai_cnt(String db_name)
	{	
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();

		int max_row_no = 0;
		
		SQliteDAO dao = new SQliteDAO();
		
		StringBuilderPlus sql = new StringBuilderPlus();
		
		sql.appendLine("SELECT  count(mi.seikai_flg) ");
		sql.appendLine("FROM    (");
		sql.appendLine("        SELECT  MAX(action_timestamp) AS mid");
		sql.appendLine("        FROM    kaitou");
		sql.appendLine("        GROUP BY");
		sql.appendLine("                s_id");
		sql.appendLine("        ) mo ");
		sql.appendLine("JOIN    kaitou mi ");
		sql.appendLine("ON      mi.action_timestamp = mo.mid");
		sql.appendLine("AND mi.seikai_flg = 1");
		sql.appendLine("JOIN    seitou ");
		sql.appendLine("ON      seitou.s_id = mi.s_id");
		sql.appendLine("AND seitou.seitou != '' AND seitou.seitou is not null");
		
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
	    	  max_row_no = rs.getInt(1);
	    	  //System.out.println(max_row_no);
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
		
	    stopwatch.stop(new Object(){}.getClass().getEnclosingMethod().getName());

	    return max_row_no;
	}
	
	public int is_seikai(String db_name, String s_id)
	{
		int is_seikai = 0;
		
		SQliteDAO dao = new SQliteDAO();
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("select seikai_flg from kaitou where s_id = '" + s_id + "' order by action_timestamp desc limit 1;");
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
	    	  is_seikai = rs.getInt(1);
	    	  System.out.println("is_seikai"+is_seikai);
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
		
		return is_seikai;
	}
	
	/**
	 * 
	 * @param db_name
	 * @param seitou_list
	 * @return
	 */
	public List<KaitouModel> select_kaitou_list(String db_name, List<KaitouModel> kaitou_list)
	{		
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();

		SQliteDAO dao = new SQliteDAO();
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("select ");
	    // 行番号
		sql.appendLine("  row_no,");
	    // 回答ID
		sql.appendLine("  k_id,");
	    // QA ID
		sql.appendLine("  qa_id,");
	    // 正答ID
		sql.appendLine("  s_id,");
		// 正解フラグ
		sql.appendLine("  seikai_flg,");
		// アクション・・・チェックを入れた、外した、解いて正解した、解いて不正解、等
		sql.appendLine("  action,");
	    // アクション日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("  action_timestamp,");
		// ユーザーが入力した回答
		sql.appendLine("  kaitou,");
		// 言語
		sql.appendLine("  language,");
		// 削除フラグ
		sql.appendLine("  del_flg,");
	    // 作成者
		sql.appendLine("  create_owner,");
	    // 更新者
		sql.appendLine("  update_owner,");
	    // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("  create_timestamp,");
	    // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("  update_timestamp");
		sql.appendLine(" from kaitou");
		sql.appendLine(" where ");
		sql.appendLine(" del_flg = 0");
		sql.appendLine(" order by qa_id,s_id,action_timestamp desc;");
		
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
	    	  KaitouModel kaitou = new KaitouModel();
	    	  // 行番号
	    	  kaitou.setRow_no(rs.getInt("row_no"));
	    	  // 回答ID
	    	  kaitou.setK_id(rs.getString("k_id"));
	    	  // QA ID
	    	  kaitou.setQa_id(rs.getString("qa_id"));
	    	  // 正答ID
	    	  kaitou.setS_id(rs.getString("s_id"));
	    	  // 正解フラグ
	    	  kaitou.setSeikai_flg(rs.getInt("seikai_flg"));
	    	  // アクション・・・チェックを入れた、外した、解いて正解した、解いて不正解、等
	    	  kaitou.setKaitou(rs.getString("action"));
	    	  // アクション日時（H2DBのtimestampと同じフォーマットにする）
	    	  kaitou.setAction_timestamp(rs.getString("action_timestamp"));
	    	  // ユーザーが入力した回答
	    	  kaitou.setKaitou(rs.getString("kaitou"));
	    	  // 言語
	    	  kaitou.setLanguage(rs.getString("language"));	    	
	    	  // 削除フラグ
		      kaitou.setDel_flg(rs.getInt("del_flg"));
		      // 作成者
		      kaitou.setCreate_owner(rs.getString("create_owner"));
		      // 更新者
		      kaitou.setUpdate_owner(rs.getString("update_owner"));
		      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		      kaitou.setUpdate_timestamp(rs.getString("create_timestamp"));
		      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		      kaitou.setUpdate_timestamp(rs.getString("update_timestamp"));

		      kaitou_list.add(kaitou);
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
	    stopwatch.stop(new Object(){}.getClass().getEnclosingMethod().getName());		
		return kaitou_list;
	}
		
	/**
	 * 回答テーブルに１件レコードを追加する
	 * @param seitou
	 * @return
	 */
	public void insert_kaitou(String db_name, KaitouModel kaitou)
	{
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();

		SQliteDAO dao = new SQliteDAO();
	    Connection connection = null;
		String db_save_path = Constant.SQLITE_OWNER_DB_FOLDEDR_PATH + "/";
		String connection_str = "jdbc:sqlite:" 
				  				+ db_save_path
				  				+ db_name;
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("replace into kaitou (");
		// 行番号
		sql.appendLine("  row_no,");
		// 回答ID
		sql.appendLine("	k_id,");
		// QA ID
		sql.appendLine("	qa_id,");
		// 正答ID
		sql.appendLine("	s_id,");
	    // 正解フラグ
		sql.appendLine("	seikai_flg,");
	    // アクション・・・チェックを入れた、外した、解いて正解した、解いて不正解、等
		sql.appendLine("	action,");
	    // アクション日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("	action_timestamp,");
	    // ユーザーが入力した回答
		sql.appendLine("	kaitou,");
	    // 言語
		sql.appendLine("	language,");
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
		sql.appendLine("" + kaitou.getRow_no() + ",");
	    // 回答ID
		sql.appendLine("'" + kaitou.getK_id() + "',");
	    // QA ID
		sql.appendLine("'" + kaitou.getQa_id() + "',");
	    // 正答ID
		sql.appendLine("'" + kaitou.getS_id() + "',");
	    // 正解フラグ
		sql.appendLine("'" + kaitou.getSeikai_flg() + "',");
	    // アクション・・・チェックを入れた、外した、解いて正解した、解いて不正解、等
		sql.appendLine("'" + kaitou.getAction() + "',");
	    // アクション日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("'" + kaitou.getAction_timestamp() + "',");
	    // ユーザーが入力した回答
		sql.appendLine("'" + kaitou.getKaitou() + "',");
	    // 言語
		sql.appendLine("'" + kaitou.getLanguage() + "',");
		// 削除フラグ
		sql.appendLine("" + kaitou.getDel_flg() + ",");
		// 作成者
		sql.appendLine("'" + kaitou.getCreate_owner() + "',");
		// 更新者
		sql.appendLine("'" + kaitou.getUpdate_owner() + "',");
		// レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("'" + kaitou.getCreate_timestamp() + "',");
		// レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("'" + kaitou.getUpdate_timestamp() + "'");
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
		  stopwatch.stop(new Object(){}.getClass().getEnclosingMethod().getName());		
	    }
	}	
	
	/**
	 * QAテーブルを１件更新する
	 * @param seitou
	 * @return 
	 */
	public void update_kaitou(String db_name, KaitouModel kaitou)
	{
		SQliteDAO dao = new SQliteDAO();
	    Connection connection = null;
		String db_save_path = Constant.SQLITE_OWNER_DB_FOLDEDR_PATH + "/";
		String connection_str = "jdbc:sqlite:" 
				  				+ db_save_path
				  				+ db_name;
		
		StringBuilderPlus sql = new StringBuilderPlus();
				
		sql.appendLine("update kaitou ");
		sql.appendLine("values (");
	    // 行番号
		sql.appendLine("  row_no = " + kaitou.getRow_no() + ",");
	    // QA ID
		sql.appendLine("  qa_id = '" + kaitou.getQa_id() + "',");
	    // 正答ID
		sql.appendLine("  s_id = '" + kaitou.getS_id() + "',");
	    // 正解フラグ
		sql.appendLine("  seikai_flg = '" + kaitou + "',");
		// アクション・・・チェックを入れた、外した、解いて正解した、解いて不正解、等
		sql.appendLine("  action = '" + kaitou.getAction() + "',");
		// アクション日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("  action_timestamp = '" + kaitou.getAction_timestamp() + "',");
		// ユーザーが入力した回答
		sql.appendLine("  kaitou = '" + kaitou.getKaitou() + "',");
		// 言語
		sql.appendLine("  language = '" + kaitou.getLanguage() + "',");
		// 削除フラグ
		sql.appendLine("  del_flg = " + kaitou.getDel_flg() + ",");
		// 作成者
		sql.appendLine("  create_owner = '" + kaitou.getCreate_owner() + "',");
		// 更新者
		sql.appendLine("  update_owner = '" + kaitou.getUpdate_owner() + "',");
		// レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("  create_timestamp = '" + kaitou.getCreate_timestamp() + "',");
		// レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("  update_timestamp = '" + kaitou.getUpdate_timestamp() + "'");
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
}
