package com.application.controller.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import com.application.model.dao.QAModel;
import com.common.Constant;
import com.common.StopWatch;
import com.common.StringBuilderPlus;
import com.dao.SQliteDAO;

public class QADao {

	/**
	 * 最大行を得る
	 * @param db_name
	 * @return
	 */
	public int get_qa_max_row_no(String db_name)
	{	
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		
		int max_row_no = 0;
		
		SQliteDAO dao = new SQliteDAO();
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("select max(row_no) as row_no from qa limit 1;");
		dao.loadDriver();
		
		System.out.println(sql.toString());

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
	    stopwatch.stop(new Object(){}.getClass().getEnclosingMethod().getName());
		return max_row_no;
	}
	
	
	/**
	 * @param db_name
	 * @param qa_list
	 * @return
	 */
	public List<QAModel> select_qa_list(String db_name, List<QAModel> qa_list)
	{
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		
		SQliteDAO dao = new SQliteDAO();
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("select ");
		// 行番号
		sql.appendLine("  row_no,");
		// QA ID
		sql.appendLine("	qa_id,");
		// QAタイプ
		sql.appendLine("	qa_type,");
		// 読むだけ問題フラグ
		sql.appendLine("	yomudake_flg,");
	    // 問題と正答を入れ替えた結果生成された問題かどうか
	    sql.appendLine("    is_reversible,");
	    // 広告問題フラグ
	    sql.appendLine("  koukoku_flg,");
		// 重要度（５段階）
		sql.appendLine("	juyoudo,");
		// 難易度（５段階）
		sql.appendLine("	nanido,");
		// 問題文と正答のうち問題から始まるかのフラグ
		sql.appendLine("	is_start_with_q,");
		// 正答がたくさんある場合の問題文を分割した時の個数
		sql.appendLine("	q_split_cnt,");
		// 問題に紐づく正答の個数
		sql.appendLine("	seitou_cnt,");
		// 公開範囲
		sql.appendLine("  koukai_level,");
		// 無料販売フラグ
		sql.appendLine("  free_flg,");
		// 無料配布した数
		sql.appendLine("  free_sold_num,");
		// 有料販売フラグ
		sql.appendLine("  charge_flg,");
		// 有料で売った数
		sql.appendLine("  charge_sold_num,");
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
		sql.appendLine(" from qa");
		sql.appendLine(" where del_flg = 0");
		sql.appendLine(" order by update_timestamp desc;");
		
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
	    	  QAModel qa = new QAModel();
		      // 行番号
	    	  qa.setRow_no(rs.getInt("row_no"));
  	  		  // QA ID
	    	  qa.setQa_id(rs.getString("qa_id"));
  	  		  // QAタイプ
	    	  qa.setQa_type(rs.getInt("qa_type"));
		      // 読むだけ問題フラグ
	    	  qa.setYomudake_flg(rs.getInt("yomudake_flg"));
	  	      // 問題と正答を入れ替えた結果生成された問題かどうか
	    	  qa.setIs_reversible(rs.getInt("is_reversible"));
		      // 広告問題フラグ
	    	  qa.setKoukoku_flg(rs.getInt("koukoku_flg"));
		      // 重要度（５段階）
	    	  qa.setJuyoudo(rs.getInt("juyoudo"));
		      // 難易度（５段階）
	    	  qa.setNanido(rs.getInt("nanido"));
		      // 問題文と正答のうち問題から始まるかのフラグ
	    	  qa.setIs_start_with_q(rs.getInt("is_start_with_q"));
		      // 正答がたくさんある場合の問題文を分割した時の個数
	    	  qa.setQ_split_cnt(rs.getInt("q_split_cnt"));
		      // 問題に紐づく正答の個数
		      qa.setSeitou_cnt(rs.getInt("seitou_cnt"));
		      // 公開範囲
		      qa.setKoukai_level(rs.getInt("koukai_level"));
		      // 無料販売フラグ
		      qa.setFree_flg(rs.getInt("free_flg"));
		      // 無料配布した数
		      qa.setFree_sold_num(rs.getInt("free_sold_num"));
		      // 有料販売フラグ
		      qa.setCharge_sold_num(rs.getInt("charge_flg"));
		      // 有料で売った数
		      qa.setCharge_sold_num(rs.getInt("charge_sold_num"));
		      // 削除フラグ
		      qa.setDel_flg(rs.getInt("del_flg"));
		      // 作成者
		      qa.setCreate_owner(rs.getString("create_owner"));
		      // 更新者
		      qa.setUpdate_owner(rs.getString("update_owner"));
		      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		      qa.setUpdate_timestamp(rs.getString("create_timestamp"));
		      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		      qa.setUpdate_timestamp(rs.getString("update_timestamp"));

		      qa_list.add(qa);
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
	    stopwatch.stop(new Object(){}.getClass().getEnclosingMethod().getName());
		return qa_list;
	}
	
	/**
	 * QAテーブルに１件レコードを追加する
	 * @param qa
	 * @return
	 */
	public void insert_qa(String db_name, QAModel qa)
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
		sql.appendLine("insert into qa (");
		// 行番号
		sql.appendLine("  row_no,");
		// QA ID
		sql.appendLine("	qa_id,");
		// QAタイプ
		sql.appendLine("	qa_type,");
		// 読むだけ問題フラグ
		sql.appendLine("	yomudake_flg,");
		// 問題と正答を入れ替えた結果生成された問題かどうか
		sql.appendLine("    is_reversible,");
	    // 広告問題フラグ
	    sql.appendLine("  koukoku_flg,");	      
		// 重要度（５段階）
		sql.appendLine("	juyoudo,");
		// 難易度（５段階）
		sql.appendLine("	nanido,");
		// 問題文と正答のうち問題から始まるかのフラグ
		sql.appendLine("	is_start_with_q,");
		// 正答がたくさんある場合の問題文を分割した時の個数
		sql.appendLine("	q_split_cnt,");
		// 問題に紐づく正答の個数
		sql.appendLine("	seitou_cnt,");
		// 公開範囲
		sql.appendLine("  koukai_level,");
		// 無料販売フラグ
		sql.appendLine("  free_flg,");
		// 無料配布した数
		sql.appendLine("  free_sold_num,");
		// 有料販売フラグ
		sql.appendLine("  charge_flg,");
		// 有料で売った数
		sql.appendLine("  charge_sold_num,");
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
		sql.appendLine("" + qa.getRow_no() + ",");
		// QA ID
		sql.appendLine("'" + qa.getQa_id() + "',");
		// QAタイプ
		sql.appendLine("" + qa.getQa_type() + ",");
		// 読むだけ問題フラグ
		sql.appendLine("" + qa.getYomudake_flg() + ",");
		// 問題と正答を入れ替えた結果生成された問題かどうか
		sql.appendLine("" + qa.getIs_reversible() + ",");
	    // 広告問題フラグ
	    sql.appendLine("" + qa.getKoukoku_flg() + ",");    
		// 重要度（５段階）
		sql.appendLine("" + qa.getJuyoudo() + ",");
		// 難易度（５段階）
		sql.appendLine("" + qa.getNanido() + ",");
		// 問題文と正答のうち問題から始まるかのフラグ
		sql.appendLine("" + qa.getIs_start_with_q() + ",");
		// 正答がたくさんある場合の問題文を分割した時の個数
		sql.appendLine("" + qa.getQ_split_cnt() + ",");
		// 問題に紐づく正答の個数
		sql.appendLine("" + qa.getSeitou_cnt() + ",");
		// 公開範囲
		sql.appendLine("" + qa.getKoukai_level() + ",");
		// 無料販売フラグ
		sql.appendLine("" + qa.getFree_flg() + ",");
		// 無料配布した数
		sql.appendLine("" + qa.getFree_sold_num() + ",");
		// 有料販売フラグ
		sql.appendLine("" + qa.getCharge_flg() + ",");
		// 有料で売った数
		sql.appendLine("" + qa.getCharge_sold_num() + ",");
		// 削除フラグ
		sql.appendLine("" + qa.getDel_flg() + ",");
		// 作成者
		sql.appendLine("'" + qa.getCreate_owner() + "',");
		// 更新者
		sql.appendLine("'" + qa.getUpdate_owner() + "',");
		// レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("'" + qa.getCreate_timestamp() + "',");
		// レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("'" + qa.getUpdate_timestamp() + "'");
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
		  stopwatch.stop(new Object(){}.getClass().getEnclosingMethod().getName());
	    }		
	}	
	
	/**
	 * QAテーブルを１件更新する
	 * @param qa
	 * @return 
	 */
	public void update_qa(String db_name, QAModel qa)
	{
		SQliteDAO dao = new SQliteDAO();
	    Connection connection = null;
		String db_save_path = Constant.SQLITE_OWNER_DB_FOLDEDR_PATH + "/";
		String connection_str = "jdbc:sqlite:" 
				  				+ db_save_path
				  				+ db_name;
		
		StringBuilderPlus sql = new StringBuilderPlus();
				
		sql.appendLine("update qa ");
		sql.appendLine("values (");
	    // 行番号
		sql.appendLine("  row_no = " + qa.getRow_no() + ",");
		// QA ID
		sql.appendLine("  qa_id = '" + qa.getQa_id() + "',");
		// QAタイプ
		sql.appendLine("  qa_type = " + qa.getQa_type() + ",");
		// 読むだけ問題フラグ
		sql.appendLine("  yomudake_flg = " + qa.getYomudake_flg() + ",");
		// 問題と正答を入れ替えた結果生成された問題かどうか
		sql.appendLine("  is_reversible = " + qa.getIs_reversible() + ",");
	    // 広告問題フラグ
	    sql.appendLine("  koukoku_flg = " + qa.getKoukoku_flg() + ",");
		// 重要度（５段階）
		sql.appendLine("  juyoudo = " + qa.getJuyoudo() + ",");
		// 難易度（５段階）
		sql.appendLine("  nanido = " + qa.getNanido() + ",");
		// 問題文と正答のうち問題から始まるかのフラグ
		sql.appendLine("  is_start_with_q = " + qa.getIs_start_with_q() + ",");
		// 正答がたくさんある場合の問題文を分割した時の個数
		sql.appendLine("  q_split_cnt = " + qa.getQ_split_cnt() + ",");
		// 問題に紐づく正答の個数
		sql.appendLine("  seitou_cnt = " + qa.getSeitou_cnt() + ",");
		// 公開範囲
		sql.appendLine("  koukai_level = " + qa.getKoukai_level() + ",");
		// 無料販売フラグ
		sql.appendLine("  free_flg = " + qa.getFree_flg() + ",");
		// 無料配布した数
		sql.appendLine("  free_sold_num = " + qa.getFree_sold_num() + ",");
		// 有料販売フラグ
		sql.appendLine("  charge_flg = " + qa.getCharge_flg() + ",");
		// 有料で売った数
		sql.appendLine("  charge_sold_num = " + qa.getCharge_sold_num() + ",");
		// 削除フラグ
		sql.appendLine("  del_flg = " + qa.getDel_flg() + ",");
		// 作成者
		sql.appendLine("  create_owner = '" + qa.getCreate_owner() + "',");
		// 更新者
		sql.appendLine("  update_owner = '" + qa.getUpdate_owner() + "',");
		// レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("  create_timestamp = '" + qa.getCreate_timestamp() + "',");
		// レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("  update_timestamp = '" + qa.getUpdate_timestamp() + "'");
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
