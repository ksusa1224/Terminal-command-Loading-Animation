package com.application.controller.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.application.model.dao.QAModel;
import com.common.Constant;
import com.common.Log;
import com.common.StopWatch;
import com.common.StringBuilderPlus;
import com.dao.H2dbDao;
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
		
		//System.out.println(sql.toString());

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
	
	/**
	 * 正答数を得る
	 * @param db_name
	 * @return
	 */
	public int get_seitou_sum(String db_name)
	{	
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		
		int seitou_sum = 0;
		
		SQliteDAO dao = new SQliteDAO();
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("select sum(seitou_cnt) from qa where del_flg = 0 limit 1;");
		dao.loadDriver();
		
		//System.out.println(sql.toString());

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
	    	  seitou_sum = rs.getInt(1);
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
		return seitou_sum;
	}

	/**
	 * 
	 * @param db_name
	 * @param tag_names
	 * @return
	 */
	public int get_pages(
			String db_name, 
			String tag_names)
	{
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		
		int page_cnt = 0;
		
		SQliteDAO dao = new SQliteDAO();
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("select count(qa.qa_id) ");
		sql.appendLine(" from qa");
        if (!tag_names.equals(""))
        {
        	sql.appendLine(", qa_tag_relation,tag");
        }
		sql.appendLine(" where qa.del_flg = 0");
        if (!tag_names.equals(""))
        {
			sql.appendLine(" and qa.qa_id = qa_tag_relation.qa_id");
			sql.appendLine(" and tag.tag_id = qa_tag_relation.tag_id");
			sql.appendLine(" and (");
			for (int i = 0; i < tag_names.split(",").length; i++)
			{
				sql.appendLine("tag.tag_name = '" + tag_names.split(",")[i] + "'");
				if (i < tag_names.split(",").length - 1)
				{
					sql.appendLine(" or ");
				}
			}
	        sql.appendLine(")");
        }
		
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
	    	  page_cnt = rs.getInt(1) / Constant.QA_NUM_PER_PAGE + 1;
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
		return page_cnt;
	}		
	
	/**
	 * 編集用に、QA入力エリアのHTMLを取得
	 * @param db_name
	 * @param qa_id
	 * @return
	 */
	public String get_qa_html(String db_name, String qa_id)
	{	
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		
		String qa_html = "";
		
		SQliteDAO dao = new SQliteDAO();
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("select qa_html from qa where qa_id = '" + qa_id + "'");
		dao.loadDriver();
		
		//System.out.println(sql.toString());

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
	    	  qa_html = rs.getString("qa_html");
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
		return qa_html;
	}
	
	/**
	 * 
	 * @param db_name
	 * @param qa_list
	 * @param limit
	 * @param offset
	 * @return
	 */
	public List<QAModel> select_qa_list(
			String db_name, 
			List<QAModel> qa_list,
			int limit,
			int offset)
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
	    // QA入力エリアのHTML
	    sql.appendLine("  qa_html,");		
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
		sql.appendLine(" order by create_timestamp desc");
		//sql.appendLine(" order by RANDOM()");
		sql.appendLine("  limit " + limit + " offset + " + offset + ";");
		
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
		      // QA入力エリアのHTML
	    	  qa.setQa_html(rs.getString("qa_html"));
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
		      qa.setCreate_timestamp(rs.getString("create_timestamp"));
		      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		      qa.setUpdate_timestamp(rs.getString("update_timestamp"));

		      qa_list.add(qa);
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
		return qa_list;
	}

	/**
	 * タグ名でQAを絞り込み検索する
	 * @param db_name
	 * @param qa_list
	 * @param tag_name
	 * @return
	 */
	public List<QAModel> select_qa_list_by_tag(
			String db_name, 
			List<QAModel> qa_list, 
			String tag_names,
			int limit,
			int offset)
	{
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		
		SQliteDAO dao = new SQliteDAO();
		
		List<String> tags_list = new ArrayList<String>();
		Boolean is_reversible = false;
		String order_by = "";
		for (int i = 0; i < tag_names.split(",").length; i++)
		{
			if (tag_names.split(",")[i].equals(""))
			{
				continue;
			}
			else if (tag_names.split(",")[i].equals("問題と解答を反転"))
			{
				is_reversible = true;
				continue;
			}
			else if (tag_names.split(",")[i].equals("ランダム順"))
			{
				order_by = "ランダム順";
				continue;
			}
			else if (tag_names.split(",")[i].equals("登録順"))
			{
				order_by = "登録順";
				continue;
			}
			else if (tag_names.split(",")[i].equals("新着順"))
			{
				order_by = "新着順";
				continue;
			}
			else
			{
				tags_list.add(tag_names.split(",")[i]);
			}
		}
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("select ");
		// 行番号
		sql.appendLine("  qa.row_no,");
		// QA ID
		sql.appendLine("	qa.qa_id,");
		// QAタイプ
		sql.appendLine("	qa.qa_type,");
	    // QA入力エリアのHTML
	    sql.appendLine("  qa.qa_html,");
		// 読むだけ問題フラグ
		sql.appendLine("	qa.yomudake_flg,");
	    // 問題と正答を入れ替えた結果生成された問題かどうか
	    sql.appendLine("    qa.is_reversible,");
	    // 広告問題フラグ
	    sql.appendLine("  qa.koukoku_flg,");
		// 重要度（５段階）
		sql.appendLine("	qa.juyoudo,");
		// 難易度（５段階）
		sql.appendLine("	qa.nanido,");
		// 問題文と正答のうち問題から始まるかのフラグ
		sql.appendLine("	qa.is_start_with_q,");
		// 正答がたくさんある場合の問題文を分割した時の個数
		sql.appendLine("	qa.q_split_cnt,");
		// 問題に紐づく正答の個数
		sql.appendLine("	qa.seitou_cnt,");
		// 公開範囲
		sql.appendLine("  qa.koukai_level,");
		// 無料販売フラグ
		sql.appendLine("  qa.free_flg,");
		// 無料配布した数
		sql.appendLine("  qa.free_sold_num,");
		// 有料販売フラグ
		sql.appendLine("  qa.charge_flg,");
		// 有料で売った数
		sql.appendLine("  qa.charge_sold_num,");
		// 削除フラグ
		sql.appendLine("	qa.del_flg,");
		// 作成者
		sql.appendLine("  qa.create_owner,");
		// 更新者
		sql.appendLine("  qa.update_owner,");
		// レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("	qa.create_timestamp,");
		// レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("	qa.update_timestamp");
		sql.appendLine(" from qa");
        if (tags_list.contains("未正解") || tags_list.contains("正解"))
        {
        	sql.appendLine(", qa_tag_relation,tag,seitou");        	
        }
        else if (tags_list.size() > 0)
        {
        	sql.appendLine(", qa_tag_relation,tag");
        }
		sql.appendLine(" where qa.del_flg = 0");
        if (tags_list.contains("未正解"))
        {
        	sql.appendLine(" and qa.qa_id = seitou.qa_id");
        	sql.appendLine(" and (seitou.seikai_flg = 0 or seitou.seikai_flg is null)");
			sql.appendLine(" and qa.qa_id = qa_tag_relation.qa_id");
			sql.appendLine(" and tag.tag_id = qa_tag_relation.tag_id");

        	if (tags_list.size() > 0)
        	{
				sql.appendLine(" and (");
				for (int i = 0; i < tags_list.size(); i++)
				{
					if (tags_list.get(i).equals("未正解"))
					{
						sql.appendLine("1 = 1");
						continue;
					}
					sql.appendLine("tag.tag_name = '" + tags_list.get(i) + "'");
					if (i < tags_list.size() - 1)
					{
						sql.appendLine(" or ");
					}
				}
		        sql.appendLine(")");			
        	}
        }
        else if (tags_list.contains("正解"))
        {
        	sql.appendLine(" and qa.qa_id = seitou.qa_id");
        	sql.appendLine(" and seitou.seikai_flg = 1");
			sql.appendLine(" and qa.qa_id = qa_tag_relation.qa_id");
			sql.appendLine(" and tag.tag_id = qa_tag_relation.tag_id");

        	if (tags_list.size() > 1)
        	{
				sql.appendLine(" and (");
				for (int i = 0; i < tags_list.size(); i++)
				{
					if (tags_list.get(i).equals("正解"))
					{
						sql.appendLine("1 = 1");
						continue;
					}
					sql.appendLine("tag.tag_name = '" + tags_list.get(i) + "'");
					if (i < tags_list.size() - 1)
					{
						sql.appendLine(" or ");
					}
				}
		        sql.appendLine(")");			
        	}
        }
        else if (tags_list.size() > 0)
        {
			sql.appendLine(" and qa.qa_id = qa_tag_relation.qa_id");
			sql.appendLine(" and tag.tag_id = qa_tag_relation.tag_id");
			sql.appendLine(" and (");
			for (int i = 0; i < tags_list.size(); i++)
			{
				sql.appendLine("tag.tag_name = '" + tags_list.get(i) + "'");
				if (i < tags_list.size() - 1)
				{
					sql.appendLine(" or ");
				}
			}
	        sql.appendLine(")");
        }
        if (tags_list.contains("未正解") || tags_list.contains("正解"))
        {
        	sql.appendLine(" group by qa.qa_id");
        }
        if (order_by.equals(""))
        {
        	sql.appendLine(" order by qa.update_timestamp desc");
        }
        else if (order_by.equals("ランダム順"))
        {
        	sql.appendLine(" order by random()");        	
        }
        else if (order_by.equals("登録順"))
        {
        	sql.appendLine(" order by qa.create_timestamp");        	
        }
        else if (order_by.equals("新着順"))
        {
        	sql.appendLine(" order by qa.create_timestamp desc");        	
        }
		sql.appendLine("  limit " + limit + " offset " + offset + ";");
		
		System.out.println("sql:"+sql.toString());
		
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
		      // QA入力エリアのHTML
	    	  qa.setQa_html(rs.getString("qa_html"));
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
		      qa.setCreate_timestamp(rs.getString("create_timestamp"));
		      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		      qa.setUpdate_timestamp(rs.getString("update_timestamp"));

		      qa_list.add(qa);
	      }
	    }
	    catch(Exception ex)
	    {
			Log log = new Log();
			ex.printStackTrace();
			log.insert_error_log("ERROR", ex.getStackTrace().toString());
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
		//sql.appendLine("  row_no,");
		// QA ID
		sql.appendLine("	qa_id,");
		// QAタイプ
		sql.appendLine("	qa_type,");
	    // QA入力エリアのHTML
		sql.appendLine("   qa_html,");
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
		//sql.appendLine("" + qa.getRow_no() + ",");
		// QA ID
		sql.appendLine("'" + qa.getQa_id() + "',");
		// QAタイプ
		sql.appendLine("" + qa.getQa_type() + ",");
	    // QA入力エリアのHTML
		sql.appendLine("'" + qa.getQa_html().replace("'", "''") + "',");
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
	      
	      /**
	       * h2dbにもinsert
	       */
			Connection conn = null;
			H2dbDao h2dao = new H2dbDao();
			try {
				conn = h2dao.connect();
				Statement h2stmt = conn.createStatement();

				//1行ずつコミットしない
				h2stmt.getConnection().setAutoCommit(false);
				h2dao.transaction(h2stmt, sql);
			} catch (Exception e) {
				e.printStackTrace();
				Log log = new Log();
				log.insert_error_log("ERROR", e.getStackTrace().toString());
			}
			finally
			{
				h2dao.disconnect(conn);						
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
	    // QA入力エリアのHTML
		sql.appendLine("  qa_html = " + qa.getQa_html() + ",");
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
