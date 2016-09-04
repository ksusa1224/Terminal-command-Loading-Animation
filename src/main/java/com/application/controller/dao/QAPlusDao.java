package com.application.controller.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.application.model.dao.MondaiModel;
import com.application.model.dao.QAModel;
import com.application.model.dao.QAPlusModel;
import com.application.model.dao.SeitouModel;
import com.common.Constant;
import com.common.StopWatch;
import com.common.StringBuilderPlus;
import com.dao.SQliteDAO;

public class QAPlusDao extends QADao {
	
//	public MondaiDao mondai_dao = new MondaiDao();
//	public SeitouDao seitou_dao = new SeitouDao();
	
	/**
	 * @param db_name
	 * @param qa_list
	 * @return
	 */
	public List<QAPlusModel> select_qa_plus_list(String db_name, List<QAPlusModel> qa_plus_list)
	{
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		
		SQliteDAO dao = new SQliteDAO();
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("select ");
		/**
		 * QAテーブル
		 */
		// 行番号
		sql.appendLine("  qa.row_no,");
		// QA ID
		sql.appendLine("	qa.qa_id,");
		// QAタイプ
		sql.appendLine("	qa.qa_type,");
		// 読むだけ問題フラグ
		sql.appendLine("	qa.yomudake_flg,");
	    // 問題と正答を入れ替えた結果生成された問題かどうか
	    sql.appendLine("    qa.is_reversible,");
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
		sql.appendLine("	qa.update_timestamp,");
		
		/**
		 * 問題テーブル
		 */
		// 行番号
		sql.appendLine("  mondai.row_no,");
		// 問題ID
		sql.appendLine("  mondai.q_id,");
		// QA ID
		sql.appendLine("	mondai.qa_id,");
		// QA内での問題パーツの順番
		sql.appendLine("	mondai.junban,");
		// 問題パーツが文字であるかのフラグ
		sql.appendLine("  mondai.is_text_flg,");
		// 問題パーツがバイナリであるかのフラグ
		sql.appendLine("  mondai.is_binary_flg,");
		// 分割された問題文
		sql.appendLine("  mondai.q_parts_text,");
		// QAの中に出てくる音声や画像などのバイナリファイル
		sql.appendLine("  mondai.q_parts_binary,");
		// 言語
		sql.appendLine("  mondai.language,");
		// テキスト読み上げデータ
		sql.appendLine("  mondai.yomiage,");
		// 削除フラグ
		sql.appendLine("	mondai.del_flg,");
		// 作成者
		sql.appendLine("  mondai.create_owner,");
		// 更新者
		sql.appendLine("  mondai.update_owner,");
		// レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("	mondai.create_timestamp,");
		// レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("	mondai.update_timestamp,");
		
		/**
		 * 正答テーブル
		 */
		// 行番号
		sql.appendLine("  seitou.row_no,");
		// QA ID
		sql.appendLine("	seitou.qa_id,");
		// QA内での問題パーツの順番
		sql.appendLine("	seitou.junban,");
		// 問題パーツが文字であるかのフラグ
		sql.appendLine("  seitou.is_text_flg,");
		// 問題パーツがバイナリであるかのフラグ
		sql.appendLine("  seitou.is_binary_flg,");
		// 分割された問題文
		sql.appendLine("  seitou.q_parts_text,");
		// QAの中に出てくる音声や画像などのバイナリファイル
		sql.appendLine("  seitou.q_parts_binary,");
		// 言語
		sql.appendLine("  seitou.language,");
		// テキスト読み上げデータ
		sql.appendLine("  seitou.yomiage,");
		// 削除フラグ
		sql.appendLine("	seitou.del_flg,");
		// 作成者
		sql.appendLine("  seitou.create_owner,");
		// 更新者
		sql.appendLine("  seitou.update_owner,");
		// レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("	seitou.create_timestamp,");
		// レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("	seitou.update_timestamp,");
		
		sql.appendLine(" from qa");
		sql.appendLine(" inner join mondai");
		sql.appendLine(" on mondai.qa_id = qa.qa_id");
		sql.appendLine(" inner join seitou");
		sql.appendLine(" on seitou.qa_id = qa.qa_id");
		sql.appendLine(" where qa.del_flg = 0");
		sql.appendLine(" and mondai.del_flg = 0");
		sql.appendLine(" and seitou.del_flg = 0");
		
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
	    	  QAPlusModel qa_plus = new QAPlusModel();
	    	  
	    	  /**
	    	   * QAテーブル
	    	   */
		      // 行番号
	    	  qa_plus.setRow_no(rs.getInt("qa.row_no"));
  	  		  // QA ID
	    	  qa_plus.setQa_id(rs.getString("qa.qa_id"));
  	  		  // QAタイプ
	    	  qa_plus.setQa_type(rs.getInt("qa.qa_type"));
		      // 読むだけ問題フラグ
	    	  qa_plus.setYomudake_flg(rs.getInt("qa.yomudake_flg"));
	  	      // 問題と正答を入れ替えた結果生成された問題かどうか
	    	  qa_plus.setIs_reversible(rs.getInt("qa.is_reversible"));
		      // 重要度（５段階）
	    	  qa_plus.setJuyoudo(rs.getInt("qa.juyoudo"));
		      // 難易度（５段階）
	    	  qa_plus.setNanido(rs.getInt("qa.nanido"));
		      // 問題文と正答のうち問題から始まるかのフラグ
	    	  qa_plus.setIs_start_with_q(rs.getInt("qa.is_start_with_q"));
		      // 正答がたくさんある場合の問題文を分割した時の個数
	    	  qa_plus.setQ_split_cnt(rs.getInt("qa.q_split_cnt"));
		      // 問題に紐づく正答の個数
		      qa_plus.setSeitou_cnt(rs.getInt("qa.seitou_cnt"));
		      // 公開範囲
		      qa_plus.setKoukai_level(rs.getInt("qa.koukai_level"));
		      // 無料販売フラグ
		      qa_plus.setFree_flg(rs.getInt("qa.free_flg"));
		      // 無料配布した数
		      qa_plus.setFree_sold_num(rs.getInt("qa.free_sold_num"));
		      // 有料販売フラグ
		      qa_plus.setCharge_sold_num(rs.getInt("qa.charge_flg"));
		      // 有料で売った数
		      qa_plus.setCharge_sold_num(rs.getInt("qa.charge_sold_num"));
		      // 削除フラグ
		      qa_plus.setDel_flg(rs.getInt("qa.del_flg"));
		      // 作成者
		      qa_plus.setCreate_owner(rs.getString("qa.create_owner"));
		      // 更新者
		      qa_plus.setUpdate_owner(rs.getString("qa.update_owner"));
		      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		      qa_plus.setUpdate_timestamp(rs.getString("qa.create_timestamp"));
		      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		      qa_plus.setUpdate_timestamp(rs.getString("qa.update_timestamp"));
		      
		      /**
		       * 問題テーブル
		       */
	    	  MondaiModel mondai = new MondaiModel();
		      // 行番号
	    	  mondai.setRow_no(rs.getInt("mondai.row_no"));
	    	  // 問題ID
	    	  mondai.setQ_id(rs.getString("mondai.q_id"));
	    	  // QA ID
	    	  mondai.setQa_id(rs.getString("mondai.qa_id"));
	    	  // QA内での問題パーツの順番
	    	  mondai.setJunban(rs.getInt("mondai.junban"));
	    	  // 問題パーツが文字であるかのフラグ
	    	  mondai.setIs_text_flg(rs.getInt("mondai.is_text_flg"));
	    	  // 問題パーツがバイナリであるかのフラグ	    	  
	    	  mondai.setIs_binary_flg(rs.getInt("mondai.is_binary_flg"));
	    	  // 分割された問題文	    	  
	    	  mondai.setQ_parts_text(rs.getString("mondai.q_parts_text"));
	    	  // QAの中に出てくる音声や画像などのバイナリファイル	    	  
	    	  mondai.setQ_parts_binary(rs.getBytes("mondai.q_parts_binary"));
	    	  // 言語
	    	  mondai.setLanguage(rs.getString("mondai.language"));
	    	  // テキスト読み上げデータ
	    	  mondai.setYomiage(rs.getBytes("mondai.yomiage"));
	    	  // 削除フラグ
		      mondai.setDel_flg(rs.getInt("mondai.del_flg"));
		      // 作成者
		      mondai.setCreate_owner(rs.getString("mondai.create_owner"));
		      // 更新者
		      mondai.setUpdate_owner(rs.getString("mondai.update_owner"));
		      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		      mondai.setUpdate_timestamp(rs.getString("mondai.create_timestamp"));
		      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		      mondai.setUpdate_timestamp(rs.getString("mondai.update_timestamp"));

		      List<MondaiModel> mondai_list = new ArrayList<MondaiModel>();
		      mondai_list.add(mondai);
		      qa_plus.setMondai_list(mondai_list);
		      
		      /**
		       * 正答テーブル
		       */
	    	  SeitouModel seitou = new SeitouModel();
		      // 行番号
	    	  seitou.setRow_no(rs.getInt("seitou.row_no"));
		      // 正答ID
	    	  seitou.setS_id(rs.getString("seitou.s_id"));
		      // QA ID
	    	  seitou.setQa_id(rs.getString("seitou.qa_id"));
		      // QA内での正答の順番
	    	  seitou.setJunban(rs.getInt("seitou.junban"));
	    	  // 正答が文字であるかのフラグ
	    	  seitou.setIs_text_flg(rs.getInt("seitou.is_text_flg"));
	    	  // 正答がバイナリであるかのフラグ
	    	  seitou.setIs_binary_flg(rs.getInt("seitou.is_binary_flg"));
		      // 正答
	    	  seitou.setSeitou(rs.getString("seitou.seitou"));
	    	  // 正答が画像などのバイナリである場合に格納する
	    	  seitou.setSeitou_binary(rs.getBytes("seitou.seitou_binary"));
		      // 重要度（５段階）
	    	  seitou.setJuyoudo(rs.getInt("seitou.juyoudo"));
		      // 難易度（５段階）
	    	  seitou.setNanido(rs.getInt("seitou.nanido"));
	    	  // 言語
	    	  seitou.setLanguage(rs.getString("seitou.language"));
	    	  // テキスト読み上げデータ
	    	  seitou.setYomiage(rs.getBytes("seitou.yomiage"));
	    	  // 削除フラグ
		      seitou.setDel_flg(rs.getInt("seitou.del_flg"));
		      // 作成者
		      seitou.setCreate_owner(rs.getString("seitou.create_owner"));
		      // 更新者
		      seitou.setUpdate_owner(rs.getString("seitou.update_owner"));
		      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		      seitou.setUpdate_timestamp(rs.getString("seitou.create_timestamp"));
		      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		      seitou.setUpdate_timestamp(rs.getString("seitou.update_timestamp"));
		      
		      List<SeitouModel> seitou_list = new ArrayList<SeitouModel>();
		      seitou_list.add(seitou);
		      qa_plus.setSeitou_list(seitou_list);
		      
		      qa_plus_list.add(qa_plus);
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
		return qa_plus_list;
	}
	
	/**
	 * QAテーブル、問題テーブル、正答テーブルにレコードを追加する
	 * @param qa_plus
	 * @return
	 */
	public void insert_qa_plus(String db_name, QAPlusModel qa_plus)
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
		sql.appendLine("" + qa_plus.getRow_no() + ",");
		// QA ID
		sql.appendLine("'" + qa_plus.getQa_id() + "',");
		// QAタイプ
		sql.appendLine("" + qa_plus.getQa_type() + ",");
		// 読むだけ問題フラグ
		sql.appendLine("" + qa_plus.getYomudake_flg() + ",");
		// 問題と正答を入れ替えた結果生成された問題かどうか
		sql.appendLine("" + qa_plus.getIs_reversible() + ",");
		// 重要度（５段階）
		sql.appendLine("" + qa_plus.getJuyoudo() + ",");
		// 難易度（５段階）
		sql.appendLine("" + qa_plus.getNanido() + ",");
		// 問題文と正答のうち問題から始まるかのフラグ
		sql.appendLine("" + qa_plus.getIs_start_with_q() + ",");
		// 正答がたくさんある場合の問題文を分割した時の個数
		sql.appendLine("" + qa_plus.getQ_split_cnt() + ",");
		// 問題に紐づく正答の個数
		sql.appendLine("" + qa_plus.getSeitou_cnt() + ",");
		// 公開範囲
		sql.appendLine("" + qa_plus.getKoukai_level() + ",");
		// 無料販売フラグ
		sql.appendLine("" + qa_plus.getFree_flg() + ",");
		// 無料配布した数
		sql.appendLine("" + qa_plus.getFree_sold_num() + ",");
		// 有料販売フラグ
		sql.appendLine("" + qa_plus.getCharge_flg() + ",");
		// 有料で売った数
		sql.appendLine("" + qa_plus.getCharge_sold_num() + ",");
		// 削除フラグ
		sql.appendLine("" + qa_plus.getDel_flg() + ",");
		// 作成者
		sql.appendLine("'" + qa_plus.getCreate_owner() + "',");
		// 更新者
		sql.appendLine("'" + qa_plus.getUpdate_owner() + "',");
		// レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("'" + qa_plus.getCreate_timestamp() + "',");
		// レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("'" + qa_plus.getUpdate_timestamp() + "'");
		sql.appendLine(");");
		
		/**
		 * 問題
		 */
		for (MondaiModel mondai : qa_plus.getMondai_list())
		{
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
			sql.appendLine("" + mondai.getRow_no() + ",");
		    // 問題ID
			sql.appendLine("'" + mondai.getQ_id() + "',");
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
		}
		
		/**
		 * 正答
		 */
		for (SeitouModel seitou : qa_plus.getSeitou_list())
		{
			sql.appendLine("insert into seitou (");
			// 行番号
			sql.appendLine("  row_no,");
			// 正答ID
			sql.appendLine("	s_id,");
			// QA ID
			sql.appendLine("	qa_id,");
			// QA内での正答の順番
			sql.appendLine("	junban,");
			// 正答が文字であるかのフラグ
			sql.appendLine("	is_text_flg,");
			// 正答がバイナリであるかのフラグ
			sql.appendLine("	is_binary_flg,");
			// 正答
			sql.appendLine("	seitou,");
			// 正答が画像などのバイナリである場合に格納する
			sql.appendLine("	seitou_binary,");
			// 重要度（５段階）
			sql.appendLine("	juyoudo,");
			// 難易度（５段階）
			sql.appendLine("	nanido,");
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
			sql.appendLine("" + seitou.getRow_no() + ",");
		    // 正答ID
			sql.appendLine("'" + seitou.getS_id() + "',");
			// QA ID
			sql.appendLine("'" + seitou.getQa_id() + "',");
			// QA内での正答の順番
			sql.appendLine("" + seitou.getJunban() + ",");
			// 正答が文字であるかのフラグ
			sql.appendLine("" + seitou.getIs_text_flg() + ",");
			// 正答がバイナリであるかのフラグ
			sql.appendLine("" + seitou.getIs_binary_flg()+ ",");
			// 正答
			sql.appendLine("'" + seitou.getSeitou() + "',");
			// 正答が画像などのバイナリである場合に格納する
			sql.appendLine("" + seitou.getSeitou_binary() + ",");
			// 重要度（５段階）
			sql.appendLine("" + seitou.getJuyoudo() + ",");
			// 難易度（５段階）
			sql.appendLine("" + seitou.getNanido() + ",");
			// 言語
			sql.appendLine("'" + seitou.getLanguage() + "',");
			// テキスト読み上げデータ
			sql.appendLine("" + seitou.getYomiage() + ",");
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
		}
		
		System.out.println(sql.toString());
	    
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
}
