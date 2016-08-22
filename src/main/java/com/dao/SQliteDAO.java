package com.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.common.*;

/**
 * 
 * @author ksusa
 *
 */
public class SQliteDAO {
	
	/**
	 * 1ユーザーにつき1個、会員登録のタイミングでSQliteのDBを作成する
	 * @return データベース名
	 */
	public String createOwnerDB(String user_id) {
		// load the sqlite-JDBC driver using the current class loader
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException ex) {
			// TODO log output
			ex.printStackTrace();
		}

	    Connection connection = null;
		String now = Util.getNow("yyyy_MM_dd_HH_mm_ss");
		String db_name = now + "_" + user_id + ".db";
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
	      
	      //DB作成
	      StringBuilderPlus sql = new StringBuilderPlus();
	      
	      /**
	       *  パフォーマンスチューニング　
	       *  参考URL:http://arbitrage.jpn.org/it/2015-07-07-2/
	       */
	      // TODO 記載位置が合っているか要確認　TODO DB BROWSER FOR SQLITEで開いたら、以下は反映されていなかった模様。
		  sql.appendLine("PRAGMA default_synchronous = OFF;");
		  sql.appendLine("PRAGMA journal_mode = PESIST;");
	      
	      /**
	       *  QAテーブル
	       */
	      sql.appendLine("create table qa (");
	      // 行番号
	      sql.appendLine("  row_no integer auto increment unique not null /*行番号*/,");
	      // QA ID
	      sql.appendLine("	qa_id text primary key unique not null,");
	      // QAタイプ
	      sql.appendLine("	qa_type integer,");
	      // 読むだけ問題フラグ
	      sql.appendLine("	yomudake_flg integer default 0,");
	      // 重要度（５段階）
	      sql.appendLine("	juyoudo integer default 3,");
	      // 難易度（５段階）
	      sql.appendLine("	nanido integer default 3,");
	      // 問題文と正答のうち問題から始まるかのフラグ
	      sql.appendLine("	is_start_with_q integer default 1,");
	      // 正答がたくさんある場合の問題文を分割した時の個数
	      sql.appendLine("	q_split_cnt integer default 1,");
	      // 問題に紐づく正答の個数
	      sql.appendLine("	seitou_cnt integer default 1,");
	      // 公開範囲
	      sql.appendLine("  koukai_level integer,");
	      // 無料販売フラグ
	      sql.appendLine("  free_flg integer,");
	      // 有料販売フラグ
	      sql.appendLine("  charge_flg integer,");
	      // 削除フラグ
	      sql.appendLine("	del_flg integer default 0,");
	      // 作成者
	      sql.appendLine("  create_owner text,");
	      // 更新者
	      sql.appendLine("  update_owner text,");
	      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	create_timestamp text,");
	      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	update_timestamp text");
	      sql.appendLine(");");
	      // INDEX
	      sql.appendLine("create unique index qa_idx on qa (qa_id asc);");
	      
	      /**
	       * 問題テーブル
	       * QAに紐づく分割された問題のテキストまたはバイナリ（画像など）
	       */
	      sql.appendLine("create table mondai (");
	      // 行番号
	      sql.appendLine("  row_no integer auto increment unique not null,");
	      // 問題ID
	      sql.appendLine("  q_id text primary key unique not null,");
	      // QA ID
	      sql.appendLine("	qa_id text not null,");
	      // QA内での問題パーツの順番
	      sql.appendLine("	junban integer default 1,");
	      // 問題パーツが文字であるかのフラグ
	      sql.appendLine("  is_text_flg integer default 1,");
	      // 問題パーツがバイナリであるかのフラグ
	      sql.appendLine("  is_binary_flg integer default 0,");
	      // 分割された問題文
	      sql.appendLine("  q_parts_text text,");
	      // QAの中に出てくる音声や画像などのバイナリファイル
	      sql.appendLine("  q_parts_binary blob default null,");
	      // 言語
	      sql.appendLine("  language text,");
	      // 削除フラグ
	      sql.appendLine("	del_flg integer default 0,");
	      // 作成者
	      sql.appendLine("  create_owner text,");
	      // 更新者
	      sql.appendLine("  update_owner text,");
	      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	create_timestamp text,");
	      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	update_timestamp text,");
	      // 問題ID, QA ID, QA内での順番でuniqueにする
	      sql.appendLine("	unique (q_id, qa_id, junban)");
	      sql.appendLine(");");
	      // INDEX
	      sql.appendLine("create unique index mondai_idx on mondai (");
	      sql.appendLine(" q_id asc,");
	      sql.appendLine(" qa_id asc,");
	      sql.appendLine(" junban asc");
	      sql.appendLine(");");
	      
	      /**
	       *  正答テーブル
	       */
	      sql.appendLine("create table seitou (");
	      // 行番号
	      sql.appendLine("  row_no integer auto increment unique not null,");
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
	      sql.appendLine("  language text,");
	      // 削除フラグ
	      sql.appendLine("	del_flg integer default 0,");
	      // 作成者
	      sql.appendLine("  create_owner text,");
	      // 更新者
	      sql.appendLine("  update_owner text,");
	      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	create_timestamp text,");
	      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	update_timestamp text,");
	      // 正答ID, QA ID, QA内での順番でuniqueにする
	      sql.appendLine("	unique (s_id, qa_id, junban)");
	      sql.appendLine(");");
	      // INDEX
	      sql.appendLine("create unique index seitou_idx on seitou (");
	      sql.appendLine(" s_id asc,");
	      sql.appendLine(" qa_id asc,");
	      sql.appendLine(" junban asc");
	      sql.appendLine(");");
	      
	      /**
	       *  回答テーブル
	       */
	      sql.appendLine("create table kaitou (");
	      // 行番号
	      sql.appendLine("  row_no integer auto increment unique not null,");
	      // 回答ID
	      sql.appendLine("	k_id text primary key unique not null,");
	      // QA ID
	      sql.appendLine("	qa_id text not null,");
	      // 正答ID
	      sql.appendLine("	s_id text,");
	      // アクション・・・チェックを入れた、外した、解いて正解した、解いて不正解、等
	      sql.appendLine("	action text,");
	      // アクション日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	action_timestamp,");
	      // ユーザーが入力した回答
	      sql.appendLine("	kaitou text,");
	      // 言語
	      sql.appendLine("  language text,");
	      // 削除フラグ
	      sql.appendLine("	del_flg integer default 0,");
	      // 作成者
	      sql.appendLine("  create_owner text,");
	      // 更新者
	      sql.appendLine("  update_owner text,");
	      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	create_timestamp text,");
	      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	update_timestamp text");
	      sql.appendLine(");");
	      // INDEX
	      sql.appendLine("create unique index kaitou_idx on kaitou (");
	      sql.appendLine("	k_id asc,");
	      sql.appendLine("	qa_id asc,");
	      sql.appendLine("	s_id asc");
	      sql.appendLine("");
	      sql.appendLine(");");
	      
	      /**
	       *  タグテーブル
	       */
	      sql.appendLine("create table tag (");
	      // 行番号
	      sql.appendLine("  row_no integer auto increment unique not null,");
	      // タグID
	      sql.appendLine("	tag_id text primary key unique not null,");
	      // タグ名
	      sql.appendLine("	tag_name text,");
	      // 表示順
	      sql.appendLine("	junban integer,");
	      // 表示フラグ
	      sql.appendLine("	display_flg integer,");
	      // 重要度（５段階）
	      sql.appendLine("	juyoudo integer default 3,");
	      // 難易度（５段階）
	      sql.appendLine("	nanido integer default 3,");
	      // システムタグフラグ
	      sql.appendLine("	system_tag_flg integer default 0,");
	      // タグ種別
	      sql.appendLine("	tag_type text,");
	      // 公開範囲
	      sql.appendLine("  koukai_level integer,");
	      // 言語
	      sql.appendLine("  language text,");
	      // 削除フラグ
	      sql.appendLine("	del_flg integer default 0,");
	      // 作成者
	      sql.appendLine("  create_owner text,");
	      // 更新者
	      sql.appendLine("  update_owner text,");
	      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	create_timestamp text,");
	      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	update_timestamp text");
	      sql.appendLine(");");
	      // INDEX
	      sql.appendLine("create unique index tag_idx on tag (");
	      sql.appendLine(" tag_id asc,");
	      sql.appendLine(" tag_name");
	      sql.appendLine(");");
	      
	      /**
	       *  QAとタグの紐付けテーブル
	       */
	      sql.appendLine("create table qa_tag_relation (");
	      // 行番号
	      sql.appendLine("  row_no integer auto increment unique not null,");
	      // QA ID
	      sql.appendLine("	qa_id text,");
	      // タグID
	      sql.appendLine("	tag_id text,");
	      // 公開範囲
	      sql.appendLine("  koukai_level integer,");
	      // 作成者
	      sql.appendLine("  create_owner text,");
	      // 更新者
	      sql.appendLine("  update_owner text,");
	      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	create_timestamp text,");
	      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	update_timestamp text,");
	      sql.appendLine("	unique (qa_id, tag_id, create_owner, update_owner)");
	      sql.appendLine(");");
	      // INDEX
	      sql.appendLine("create unique index qa_tag_relation_idx on qa_tag_relation (");
	      sql.appendLine(" qa_id asc,");
	      sql.appendLine(" tag_id asc");
	      sql.appendLine(");");
	      
	      /**
	       *  タグ同士の関連テーブル
	       */
	      sql.appendLine("create table tags_relation (");
	      // 行番号
	      sql.appendLine("  row_no integer auto increment unique not null,");
	      // 親タグID
	      sql.appendLine("	parent_tag_id text,");
	      // 子タグID
	      sql.appendLine("	child_tag_id text,");
	      // 公開範囲
	      sql.appendLine("  koukai_level integer,");
	      // 作成者
	      sql.appendLine("  create_owner text,");
	      // 更新者
	      sql.appendLine("  update_owner text,");
	      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	create_timestamp text,");
	      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	update_timestamp text,");
	      sql.appendLine("	unique (parent_tag_id, child_tag_id, create_owner, update_owner)");
	      sql.appendLine(");");
	      // INDEX
	      sql.appendLine("create unique index tags_relation_idx on tags_relation (");
	      sql.appendLine(" parent_tag_id asc,");
	      sql.appendLine(" child_tag_id asc");
	      sql.appendLine(");");
	      
	      /**
	       *  暗記状態テーブル
	       */
	      sql.appendLine("create table anki_state (");
	      // 行番号
	      sql.appendLine("  row_no integer auto increment unique not null,");
	      // 状態ID
	      sql.appendLine("	state_id text primary key unique not null,");
	      // QA ID
	      sql.appendLine("	qa_id text,");
	      // 正答ID
	      sql.appendLine("	s_id text,");
	      // 暗記状態
	      sql.appendLine("	anki_state integer,");
	      // 次回の復習のタイミング
	      sql.appendLine("	next_hukushu_date text,");
	      // 削除フラグ
	      sql.appendLine("	del_flg integer default 0,");
	      // 作成者
	      sql.appendLine("  create_owner text,");
	      // 更新者
	      sql.appendLine("  update_owner text,");
	      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	create_timestamp text,");
	      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	update_timestamp text");
	      sql.appendLine(");");
	      // INDEX
	      sql.appendLine("create unique index anki_state_idx on anki_state (state_id asc);");
	            
	      /**
	       *  システム設定テーブル
	       */
	      sql.appendLine("create table system (");
	      // 行番号
	      sql.appendLine("  row_no integer auto increment unique not null,");
	      // 設定ID
	      sql.appendLine("	sys_id text primary key unique not null,");
	      // 項目グループID
	      sql.appendLine("	sys_group_id text,");
	      // 項目グループ名
	      sql.appendLine("	sys_group_name text,");
	      // キー
	      sql.appendLine("	key text,");
	      // 値
	      sql.appendLine("	value text,");
	      // 削除フラグ
	      sql.appendLine("	del_flg integer default 0,");
	      // 作成者
	      sql.appendLine("  create_owner text,");
	      // 更新者
	      sql.appendLine("  update_owner text,");
	      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	create_timestamp text,");
	      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	update_timestamp text");
	      sql.appendLine(");");
	      // INDEX
	      sql.appendLine("create unique index system_idx on system (sys_id asc);");
	      
	      /**
	       *  SQL実行
	       */
	      transaction(stmt, sql);
	    }
	    catch(SQLException e)
	    {
	    	//TODO ログ出力
	      // if the error message is "out of memory", 
	      // it probably means no database file is found
	      System.err.println(e.getMessage());
	    }
	    finally
	    {
	      try
	      {
	        if(connection != null)
	          connection.close();
	      }
	      catch(SQLException e)
	      {
	        // connection close failed.
	        System.err.println(e);
	      }
	    }
	    return db_name;
	}
	
	/**
	 * 作成、更新、削除等、トランザクション管理が必要なSQLを発行する
	 * @param stmt
	 * @param sql
	 * @throws SQLException
	 */
	public void transaction(Statement stmt, StringBuilderPlus sql) 
			throws SQLException {
		try
	      {
	    	  stmt.executeUpdate(sql.toString());
	      }
	      catch(Exception e)
	      {
	    	  System.err.println(e);
	    	  stmt.getConnection().rollback();
	      }
	      finally
	      {
	    	  stmt.getConnection().commit();
	      }
	}
}
