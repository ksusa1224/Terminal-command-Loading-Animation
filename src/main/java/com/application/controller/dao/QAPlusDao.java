package com.application.controller.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
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
		
		// TODO SQLはQADaoのコピペのままなので、問題テーブルと正答テーブルとJOINしたものに書き直す必要あり
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
		sql.appendLine(" from qa;");
		
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
	    	  QAPlusModel qa = new QAPlusModel();
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

		      qa_plus_list.add(qa);
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
