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
	 */
	public void createSQliteDB() {
		// load the sqlite-JDBC driver using the current class loader
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException ex) {
			// TODO log output
			ex.printStackTrace();
		}

	    Connection connection = null;
	    try
	    {
		  String now = Util.getNow("yyyy_MM_dd_HH_mm_ss");
		  String user_id = "vsky";
		  String db_name = now + "_" + user_id + ".db";
		  String db_save_path = "sqlite_databases/ver1/";
		  String connection_str = "jdbc:sqlite:" 
				  				+ db_save_path
				  				+ db_name;
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
	      // TODO 記載位置が合っているか要確認
		  sql.appendLine("PRAGMA syncmode = OFF;");
		  sql.appendLine("PRAGMA journal_mode = PESIST;");
	      
	      /**
	       *  問題テーブル
	       */
	      sql.appendLine("create table mondai (");
	      // 問題ID
	      sql.appendLine("	q_id integer primary key autoincrement unique not null,");
	      // 問題文
	      sql.appendLine("	mondaibun text not null,");
	      // 問題タイプ
	      sql.appendLine("	mondai_type string,");
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
	      // 削除フラグ
	      sql.appendLine("	del_flg integer default 0,");
	      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	create_timestamp string,");
	      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	update_timestamp string");
	      sql.appendLine(");");
	      // INDEX
	      sql.appendLine("create unique index mondai_idx on mondai (q_id asc);");
	      
	      /**
	       *  正答テーブル
	       */
	      sql.appendLine("create table seitou (");
	      // 正答ID
	      sql.appendLine("	s_id integer primary key autoincrement unique not null,");
	      // 問題ID
	      sql.appendLine("	q_id integer not null,");
	      // 問題内での正答の順番
	      sql.appendLine("	junban integer default 1,");
	      // 正答
	      sql.appendLine("	seitou string,");
	      // 重要度（５段階）
	      sql.appendLine("	juyoudo integer default 3,");
	      // 難易度（５段階）
	      sql.appendLine("	nanido integer default 3,");
	      // 削除フラグ
	      sql.appendLine("	del_flg integer default 0,");
	      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	create_timestamp string,");
	      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	update_timestamp string,");
	      // 正答ID,問題ID,問題内での順番でuniqueにする
	      sql.appendLine("	unique (s_id,q_id,junban)");
	      sql.appendLine(");");
	      // INDEX
	      sql.appendLine("create unique index seitou_idx on seitou (");
	      sql.appendLine(" s_id asc,");
	      sql.appendLine(" q_id asc,");
	      sql.appendLine(" junban asc");
	      sql.appendLine(");");
	      
	      /**
	       *  回答テーブル
	       */
	      sql.appendLine("create table kaitou (");
	      // 回答ID
	      sql.appendLine("	k_id integer primary key autoincrement unique not null,");
	      // 問題ID
	      sql.appendLine("	q_id integer not null,");
	      // 正答ID
	      sql.appendLine("	s_id integer,");
	      // アクション・・・チェックを入れた、外した、解いて正解した、解いて不正解、等
	      sql.appendLine("	action string,");
	      // アクション日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	action_timestamp,");
	      // ユーザーが入力した回答
	      sql.appendLine("	kaitou string,");
	      // 削除フラグ
	      sql.appendLine("	del_flg integer default 0,");
	      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	create_timestamp string,");
	      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	update_timestamp string");
	      sql.appendLine(");");
	      // INDEX
	      sql.appendLine("create unique index kaitou_idx on kaitou (");
	      sql.appendLine("	k_id asc,");
	      sql.appendLine("	q_id asc,");
	      sql.appendLine("	s_id asc");
	      sql.appendLine("");
	      sql.appendLine(");");
	      
	      /**
	       *  タグテーブル
	       */
	      sql.appendLine("create table tag (");
	      // タグID
	      sql.appendLine("	tag_id integer primary key autoincrement unique not null,");
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
	      sql.appendLine("	tag_type string,");
	      // 削除フラグ
	      sql.appendLine("	del_flg integer default 0,");
	      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	create_timestamp string,");
	      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	update_timestamp string");
	      sql.appendLine(");");
	      // INDEX
	      sql.appendLine("create unique index tag_idx on tag (");
	      sql.appendLine(" tag_id asc,");
	      sql.appendLine(" tag_name");
	      sql.appendLine(");");
	      
	      /**
	       *  問題とタグの紐付けテーブル
	       */
	      sql.appendLine("create table q_tag_relation (");
	      // 問題ID
	      sql.appendLine("	q_id integer,");
	      // タグID
	      sql.appendLine("	tag_id integer,");
	      sql.appendLine("	unique (q_id, tag_id)");
	      sql.appendLine(");");
	      // INDEX
	      sql.appendLine("create unique index q_tag_relation_idx on q_tag_relation (");
	      sql.appendLine(" q_id asc,");
	      sql.appendLine(" tag_id asc");
	      sql.appendLine(");");
	      
	      /**
	       *  タグ同士の関連テーブル
	       */
	      sql.appendLine("create table tags_relation (");
	      // 親タグID
	      sql.appendLine("	parent_tag_id integer,");
	      // 子タグID
	      sql.appendLine("	child_tag_id integer,");
	      sql.appendLine("	unique (parent_tag_id, child_tag_id)");
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
	      // 状態ID
	      sql.appendLine("	state_id integer primary key autoincrement unique not null,");
	      // 問題ID
	      sql.appendLine("	q_id integer,");
	      // 正答ID
	      sql.appendLine("	s_id integer,");
	      // 暗記状態
	      sql.appendLine("	anki_state integer,");
	      // 削除フラグ
	      sql.appendLine("	del_flg integer default 0,");
	      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	create_timestamp string,");
	      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	update_timestamp string");
	      sql.appendLine(");");
	      // INDEX
	      sql.appendLine("create unique index anki_state_idx on anki_state (state_id asc);");
	            
	      /**
	       *  システム設定テーブル
	       */
	      sql.appendLine("create table system (");
	      // 設定ID
	      sql.appendLine("	sys_id integer primary key autoincrement unique not null,");
	      // 項目グループID
	      sql.appendLine("	sys_group_id integer,");
	      // 項目グループ名
	      sql.appendLine("	sys_group_name string,");
	      // キー
	      sql.appendLine("	key string,");
	      // 値
	      sql.appendLine("	value string,");
	      // 削除フラグ
	      sql.appendLine("	del_flg integer default 0,");
	      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	create_timestamp string,");
	      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	update_timestamp string");
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
	}
	
	/**
	 * 作成、更新、削除等、トランザクション管理が必要なSQLを発行する
	 * @param stmt
	 * @param sql
	 * @throws SQLException
	 */
	public static void transaction(Statement stmt, StringBuilderPlus sql) 
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
