package com.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
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
	 * update系のsqlを１件発行する
	 * @param sql
	 */
	public void update(String db_name, StringBuilderPlus sql) 
	{
		loadDriver();
		
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

	      //1行ずつコミットしない
	      stmt.getConnection().setAutoCommit(false);
	      
	      /**
	       *  SQL実行
	       */
	      transaction(stmt, sql);
	    }
	    catch(Exception ex)
	    {
			Log log = new Log();
			log.insert_error_log("ERROR", ex.getStackTrace().toString());
		    System.err.println(ex.getMessage());
	    }
	    finally
	    {
	      close(connection);
	    }	    
	}

	public void update_log_db(StringBuilderPlus sql) 
	{
		loadDriver();

		String now = Util.getNow("yyyy_MM");
		String db_name = "system_log_" + now + ".db";
		String db_save_path = Constant.SQLITE_LOG_FOLDER_PATH;
		
	    Connection connection = null;
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
	      transaction(stmt, sql);
	    }
	    catch(Exception ex)
	    {
			Log log = new Log();
			log.insert_error_log("ERROR", ex.getStackTrace().toString());
		    System.err.println(ex.getMessage());
	    }
	    finally
	    {
	      close(connection);
	    }	    
	}
	
	
	public void loadDriver() {
		// load the sqlite-JDBC driver using the current class loader
		try 
		{
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException ex) 
		{
			// TODO log output
			ex.printStackTrace();
		}
	}
	
	/**
	 * 1ユーザーにつき1個、会員登録のタイミングでSQliteのDBを作成する
	 * @return データベース名
	 */
	public String createOwnerDB(String user_id) {
		loadDriver();

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
	      // QA入力エリアのHTML
	      sql.appendLine("  qa_html text,");
	      // 読むだけ問題フラグ
	      sql.appendLine("	yomudake_flg integer default 0,");
	      // 問題と正答を入れ替えた結果生成された問題かどうか
	      sql.appendLine("  is_reversible integer default 0,");
	      // 広告問題フラグ
	      sql.appendLine("  koukoku_flg integer default 0,");	      
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
	      // 無料配布した数
	      sql.appendLine("  free_sold_num integer,");
	      // 有料販売フラグ
	      sql.appendLine("  charge_flg integer,");
	      // 有料で売った数
	      sql.appendLine("  charge_sold_num integer,");
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
	      // テキスト読み上げデータ
	      sql.appendLine("  yomiage blob default null,");
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
	      // 正答が文字であるかのフラグ
	      sql.appendLine("  is_text_flg integer default 1,");
	      // 正答がバイナリであるかのフラグ
	      sql.appendLine("  is_binary_flg integer default 0,");
	      // 正答
	      sql.appendLine("	seitou text,");
	      // 正解フラグ
	      sql.appendLine("	seikai_flg integer,");
	      // 正答が画像などのバイナリである場合に格納する
	      sql.appendLine("  seitou_binary blob default null,");
	      // 重要度（５段階）
	      sql.appendLine("	juyoudo integer default 3,");
	      // 難易度（５段階）
	      sql.appendLine("	nanido integer default 3,");
	      // 言語
	      sql.appendLine("  language text,");
	      // テキスト読み上げデータ
	      sql.appendLine("  yomiage blob default null,");
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
	      // 正解フラグ
	      sql.appendLine("   seikai_flg integer,");
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
	       * 問題集テーブル
	       */
	      sql.appendLine("create table mondaishu (");
	      // 行番号
	      sql.appendLine("  row_no integer auto increment unique not null,");
	      // 問題集ID
	      sql.appendLine("  qa_set_id text primary key unique not null,");
	      // 問題集の名前
	      sql.appendLine("  qa_set_name text,");
	      // 公開範囲
	      sql.appendLine("  koukai_level integer,");
	      // 無料販売フラグ
	      sql.appendLine("  free_flg integer,");
	      // 無料配布した数
	      sql.appendLine("  free_sold_num integer,");
	      // 有料販売フラグ
	      sql.appendLine("  charge_flg integer,");
	      // 有料で売った数
	      sql.appendLine("  charge_sold_num integer,");
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
	      sql.appendLine("create unique index mondaishu_idx on mondaishu (qa_set_id asc);");
	      
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
	      // デザイン種別
	      sql.appendLine("	design_type integer,");
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
	      // タグ内でのQAの順番
	      sql.appendLine("  junban integer,");
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
	       * 問題集とタグの関連テーブル　TODO 作成途中
	       */
	      sql.appendLine("create table mondaishu_tag_relation (");
	      // 行番号
	      sql.appendLine("  row_no integer auto increment unique not null,");
	      // 問題集ID
	      sql.appendLine("  qa_set_id not null,");
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
	      sql.appendLine("	unique (qa_set_id, tag_id, create_owner, update_owner)");
	      sql.appendLine(");");
	      // INDEX
	      sql.appendLine("create unique index mondaishu_tag_relation_idx on mondaishu_tag_relation (");
	      sql.appendLine(" qa_set_id asc,");
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
			Log log = new Log();
			log.insert_error_log("ERROR", e.getStackTrace().toString());
	      System.err.println(e.getMessage());
	    }
	    finally
	    {
	      close(connection);
	    }
	    return db_name;
	}
	
	/**
	 * 月次ログDBを作成する
	 */
	public void create_log_db()
	{
		loadDriver();

	    Connection connection = null;
		String now = Util.getNow("yyyy_MM");
		String db_name = "system_log_" + now + ".db";
		String db_save_path = Constant.SQLITE_LOG_FOLDER_PATH;
		
		// 月次ログDBがすでに存在すれば、ログDBの作成処理は行わない
		File monthly_db = new File(db_save_path + db_name);
		if(monthly_db.exists() && !monthly_db.isDirectory()) { 
		    return;
		}
		
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
	      
	      StringBuilderPlus sql = new StringBuilderPlus();
	      
	      /**
	       *  パフォーマンスチューニング　
	       *  参考URL:http://arbitrage.jpn.org/it/2015-07-07-2/
	       */
	      // TODO 記載位置が合っているか要確認　TODO DB BROWSER FOR SQLITEで開いたら、以下は反映されていなかった模様。
		  sql.appendLine("PRAGMA default_synchronous = OFF;");
		  sql.appendLine("PRAGMA journal_mode = PESIST;");
	      
	      /**
	       *  アクセスログテーブル
	       */
	      sql.appendLine("create table access_log (");
	      // タイムスタンプ
	      sql.appendLine("timestamp text,");
	      // オーナーID
	      sql.appendLine("owner_id text,");
	      // リクエストURI
	      sql.appendLine("request_uri text,");
	      // メソッド名
	      sql.appendLine("method_name text,");
	      // IPアドレス
	      sql.appendLine("client_ip text,");
	      // OS
	      sql.appendLine("client_os text,");
	      // ブラウザ
	      sql.appendLine("client_browser text");
	      sql.appendLine(");");
	      
	      /**
	       *  エラーログテーブル
	       */
	      sql.appendLine("create table error_log (");
	      // タイムスタンプ
	      sql.appendLine("timestamp text,");
	      // エラー種別
	      sql.appendLine("error_type text,");
	      // スタックトレース
	      sql.appendLine("stack_trace text");
	      sql.appendLine(");");	      

	      transaction(stmt, sql);
	    }
	    catch(SQLException e)
	    {
			Log log = new Log();
			log.insert_error_log("ERROR", e.getStackTrace().toString());
	      System.err.println(e.getMessage());
	    }
	    finally
	    {
	      close(connection);
	    }		
	}	

	/**
	 * コネクションをクローズする
	 * @param connection
	 */
	public void close(Connection connection) {
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
