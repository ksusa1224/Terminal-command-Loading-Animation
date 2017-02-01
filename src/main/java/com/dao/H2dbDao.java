package com.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.application.model.LoginInfoModel;
import com.common.*;

/**
 * 
 * @author ksusa
 *
 */
public class H2dbDao 
{
	/**
	 * 定数、変数
	 */
	private static final String COMMON_DB_URL = Constant.COMMON_DB_ROOT_URL + Constant.H2DB_FOLDER_PATH + Constant.COMMON_DB_NAME;
	
	/**
	 * ドライバに接続し、コネクションを張る
	 * @return Common DBに接続済のConnectionオブジェクトを返却
	 */
	public Connection connect() {
		try 
		{
			Class.forName("org.h2.Driver");
		}
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		}
		
		Connection conn = null;
		try 
		{
			conn = DriverManager.getConnection(
			    COMMON_DB_URL,
			    Constant.COMMON_DB_ADMIN_USER,
			    Constant.COMMON_DB_PASSWORD);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return conn;
	}	

	/**
	 * コネクションをクローズする
	 * @param conn
	 */
	public void disconnect(Connection conn) {
		if (conn != null)
		{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}	
	
	/**
	 * 共通DBを作成（基本的には呼び出さないが、壊れた際などに再作成するのに使う）
	 */
	public void create_common_db()
	{
		Connection conn = connect();
		try
		{
			Statement stmt = conn.createStatement();
			
			StringBuilderPlus sql = new StringBuilderPlus();
			
			// 管理者アカウントの作成
			sql.appendLine("CREATE USER IF NOT EXISTS common_admin PASSWORD 'DSFRsD0eSD2Sh5#8';");
			sql.appendLine("ALTER USER common_admin ADMIN TRUE;");
			
			// デフォルトユーザ（ID・パスともに空文字（""））の削除
			sql.appendLine("DROP USER IF EXISTS \"\";");
			
			// いったん削除
			//sql.appendLine("drop table if exists owner_info;");;
			// いったん削除
			//sql.appendLine("drop table if exists owner_db;");;
			
			// オーナー情報テーブル作成
			sql.appendLine("create table if not exists owner_info(");
			sql.appendLine("  id integer primary key auto_increment,");
			sql.appendLine("  owner_id varchar(20) unique,");
			sql.appendLine("  owner_name text,");
			sql.appendLine("  email text,");
			sql.appendLine("  password binary,");
			sql.appendLine("  kakin_type integer,");
			sql.appendLine("  del_flg integer,");
			sql.appendLine("  insert_date timestamp default current_timestamp(),");
			sql.appendLine("  update_date timestamp");
			sql.appendLine(");");
			
			// オーナーDB情報テーブル作成
			sql.appendLine("create table if not exists owner_db(");
			sql.appendLine("  id integer primary key auto_increment,");
			sql.appendLine("  owner_id text,");
			sql.appendLine("  db_name binary,");
			sql.appendLine("  db_version text,");
			sql.appendLine("  is_current_db integer,");
			sql.appendLine("  del_flg integer, ");
			sql.appendLine("  insert_date timestamp default current_timestamp(),");
			sql.appendLine("  update_date timestamp");
			sql.appendLine(");");
			
			transaction(stmt, sql);			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			disconnect(conn);
		}
	}
	
	/**
	 * ログイン情報を取得する
	 * @param sql
	 * @return ログイン情報クラス
	 */
	public LoginInfoModel select_login_info(String owner_id_or_email)
	{
		LoginInfoModel login_info = new LoginInfoModel();
		
		Connection conn = connect();
		try
		{
			Statement stmt = conn.createStatement();
			
			// ログイン情報を取得
			StringBuilderPlus sql = new StringBuilderPlus();
			sql.appendLine("select");
			sql.appendLine("  owner.owner_id as owner_id,");
			sql.appendLine("  owner.owner_name as owner_name,");
			sql.appendLine("  owner.email as email,");
			sql.appendLine("  owner.password as password,");
			sql.appendLine("  owner.kakin_type as kakin_type, ");
			sql.appendLine("  db.db_name as db_name,");
			sql.appendLine("  db.db_version as db_version ");
			sql.appendLine("from owner_info as owner "); 
			sql.appendLine("inner join ");
			sql.appendLine("  owner_db as db ");
			sql.appendLine("  on owner.owner_id = db.owner_id ");
			sql.appendLine("  and is_current_db = 1");
			sql.appendLine("where "); 
			sql.appendLine("  (owner.owner_id = '" + owner_id_or_email + "'");
			sql.appendLine("   or");
			sql.appendLine("  owner.email = '" + owner_id_or_email + "')");
			sql.appendLine("  and owner.del_flg = 0");
			sql.appendLine("  and db.del_flg = 0;");
			
			ResultSet rs = stmt.executeQuery(sql.toString());
			while (rs.next()) {
				login_info.setOwner_id(rs.getString("owner_id"));
				login_info.setEmail(rs.getString("email"));
				login_info.setEncrypted_password(rs.getBytes("password"));
				login_info.setOwner_name(rs.getString("owner_name"));
				login_info.setEncrypted_db_name(rs.getBytes("db_name"));
				login_info.setDb_version(rs.getString("db_version"));
				login_info.setKakin_type(rs.getInt("kakin_type"));
				login_info.setDecryptedDbName(rs.getBytes("db_name"));
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			disconnect(conn);
		}
		
		return login_info;
	}
	
	/**
	 * 引数で指定した作成、更新、削除のSQLを実行する（トランザクショナル)
	 * @param sql
	 */
	public void update(StringBuilderPlus sql)
	{
		Connection conn = connect();
		try
		{
			Statement stmt = conn.createStatement();
			transaction(stmt, sql);			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			disconnect(conn);
		}
	}
	
	/**
	 * バイナリデータの更新を含むSQLを発行する
	 * @param sql
	 * @param params　バイナリのSQLパラメーター
	 * @throws SQLException 
	 */
	public void update(StringBuilderPlus sql, List<byte[]> params)
	{
		Connection conn = connect();
		try
		{
			PreparedStatement prep = conn.prepareStatement(sql.toString());
			for (int i = 0; i < params.size(); i++)
			{
				prep.setBytes(1, params.get(i));						
			}
			prep.executeUpdate();
		}
		catch(Exception ex)
		{
			try {
				if (conn != null)
				{
					conn.rollback();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ex.printStackTrace();
		}
		finally
		{
			disconnect(conn);
		}
	}	
	
	/**
	 * 作成、更新、削除等、トランザクション管理が必要なSQLを発行する
	 * @param stmt
	 * @param sql
	 * @throws SQLException
	 */
	public void transaction(Statement stmt, StringBuilderPlus sql) throws SQLException 
	{
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

	/**
	 * 前回トークンを取得する
	 * @param last_encrypted_token
	 * @return
	 */
	public String get_last_token(String last_token_cookie)
	{
		Connection conn = connect();
		String last_token_db = null;
		try
		{
			String sql = "select token from owner_info where token=?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, last_token_cookie);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				last_token_db = rs.getString(1);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			disconnect(conn);
		}
		
		return last_token_db;
	}	
	
	/**
	 * オートログイン用のトークンを更新する
	 */
	public void update_token(String owner_id_or_email, String last_token, String new_token)
	{
		Connection conn = connect();
		try
		{
			String sql = null;
			Statement stmt = conn.createStatement();

			// SQLに渡すパラメーター（バイナリのみ対象）
//			List<String> params = new ArrayList<byte[]>();

			
			// オーナー情報テーブルにトークンを追加
			if (owner_id_or_email == null)
			{
				sql="update owner_info set token = '" + new_token +"' where token = '" + last_token + "';";
			}
			// 初回ログイン時はtokenが入っていないため、owner_idでレコードを識別
			else
			{
				sql="update owner_info set token = '" + new_token + "' where (owner_id = '" + owner_id_or_email + "' or email = '" + owner_id_or_email + "');";				
			}
			
			stmt.executeUpdate(sql);	
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			disconnect(conn);
		}
	}

	/**
	 * ログアウト用に、オートログイン用のトークンを空文字にする
	 * @param owner_id
	 */
	public void update_token_for_logout(String owner_id)
	{
		Connection conn = connect();
		try
		{
			String sql = null;
			Statement stmt = conn.createStatement();
			
			sql="update owner_info set token = '' where owner_id = '" + owner_id + "';";				
			
			stmt.executeUpdate(sql);	
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			disconnect(conn);
		}
	}	
	
	/**
	 * 会員登録時にE-mailがすでに登録されているものでないかをチェックする
	 * @param email_input
	 * @return
	 */
	public Boolean is_email_deplicate(String email_input)
	{
		Boolean is_deplicate = false;
		Connection conn = connect();
		try
		{
			Statement stmt = conn.createStatement();
			
			// ログイン情報を取得
			StringBuilderPlus sql = new StringBuilderPlus();
			sql.appendLine("select");
			sql.appendLine("  count(email) as count ");
			sql.appendLine("from owner_info "); 
			sql.appendLine("where "); 
			sql.appendLine("  email = '" + email_input + "'");
			sql.appendLine("  and del_flg = 0");
			
			ResultSet rs = stmt.executeQuery(sql.toString());
			if (rs.next()) {
				if (rs.getInt("count") > 0)
				{
					is_deplicate = true;
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			disconnect(conn);
		}
		return is_deplicate;
	}

	/**
	 * 会員登録時にオーナーIDがすでに登録されているものでないかをチェックする
	 * @param owner_id_input
	 * @return
	 */
	public Boolean is_owner_id_deplicate(String owner_id_input)
	{
		Boolean is_deplicate = false;
		Connection conn = connect();
		try
		{
			Statement stmt = conn.createStatement();
			
			// ログイン情報を取得
			StringBuilderPlus sql = new StringBuilderPlus();
			sql.appendLine("select");
			sql.appendLine("  count(owner_id) as count ");
			sql.appendLine("from owner_info "); 
			sql.appendLine("where "); 
			sql.appendLine("  owner_id = '" + owner_id_input + "'");
			sql.appendLine("  and del_flg = 0");
			
			ResultSet rs = stmt.executeQuery(sql.toString());
			if (rs.next()) {
				if (rs.getInt("count") > 0)
				{
					is_deplicate = true;
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			disconnect(conn);
		}
		return is_deplicate;
	}	
	
	/**
	 * Cookieオートログイン用のトークンカラムを追加
	 */
	public void alter_common_db_add_token()
	{
		Connection conn = connect();
		try
		{
			Statement stmt = conn.createStatement();
			
			StringBuilderPlus sql = new StringBuilderPlus();
						
			// オーナー情報テーブルにトークンを追加
			sql.appendLine("alter table owner_info add column token clob;");
			
			transaction(stmt, sql);			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			disconnect(conn);
		}
	}
	
	/**
	 * 
	 */
	public void createTablesforOwnerDB() {
		Connection conn = connect();
	    try
	    {
	      Statement stmt = conn.createStatement();

	      //1行ずつコミットしない
	      stmt.getConnection().setAutoCommit(false);
	      
	      StringBuilderPlus sql = new StringBuilderPlus();
	      	      
	      /**
	       *  QAテーブル
	       */
	      sql.appendLine("create table if not exists qa (");
	      // 行番号
	      sql.appendLine("  row_no integer auto_increment,");
	      // QA ID
	      sql.appendLine("	qa_id text,");
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
//	      sql.appendLine("create unique index qa_idx on qa (qa_id asc);");
	      
	      /**
	       * 問題テーブル
	       * QAに紐づく分割された問題のテキストまたはバイナリ（画像など）
	       */
	      sql.appendLine("create table if not exists mondai (");
	      // 行番号
	      sql.appendLine("  row_no integer auto_increment,");
	      // 問題ID
	      sql.appendLine("  q_id text,");
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
	      sql.appendLine("	update_timestamp text");
	      // 問題ID, QA ID, QA内での順番でuniqueにする
//	      sql.appendLine("	unique (q_id, qa_id, junban)");
	      sql.appendLine(");");
//	      // INDEX
//	      sql.appendLine("create unique index mondai_idx on mondai (");
//	      sql.appendLine(" q_id asc,");
//	      sql.appendLine(" qa_id asc,");
//	      sql.appendLine(" junban asc");
//	      sql.appendLine(");");
	      
	      /**
	       *  正答テーブル
	       */
	      sql.appendLine("create table if not exists seitou (");
	      // 行番号
	      sql.appendLine("  row_no integer auto_increment,");
	      // 正答ID
	      sql.appendLine("	s_id text,");
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
	      sql.appendLine("	update_timestamp text");
	      // 正答ID, QA ID, QA内での順番でuniqueにする
//	      sql.appendLine("	unique (s_id, qa_id, junban)");
	      sql.appendLine(");");
//	      // INDEX
//	      sql.appendLine("create unique index seitou_idx on seitou (");
//	      sql.appendLine(" s_id asc,");
//	      sql.appendLine(" qa_id asc,");
//	      sql.appendLine(" junban asc");
//	      sql.appendLine(");");	      
	      
	      /**
	       * 問題集テーブル
	       */
	      sql.appendLine("create table if not exists mondaishu (");
	      // 行番号
	      sql.appendLine("  row_no integer auto_increment,");
	      // 問題集ID
	      sql.appendLine("  qa_set_id text,");
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
//	      sql.appendLine("create unique index mondaishu_idx on mondaishu (qa_set_id asc);");
	      
	      /**
	       *  タグテーブル
	       */
	      sql.appendLine("create table if not exists tag (");
	      // 行番号
	      sql.appendLine("  row_no integer auto_increment,");
	      // タグID
	      sql.appendLine("	tag_id text,");
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
//	      // INDEX
//	      sql.appendLine("create unique index tag_idx on tag (");
//	      sql.appendLine(" tag_id asc,");
//	      sql.appendLine(" tag_name");
//	      sql.appendLine(");");
	      
	      /**
	       *  QAとタグの紐付けテーブル
	       */
	      sql.appendLine("create table if not exists qa_tag_relation (");
	      // 行番号
	      sql.appendLine("  row_no integer auto_increment,");
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
	      sql.appendLine("	update_timestamp text");
//	      sql.appendLine("	unique (qa_id, tag_id, create_owner, update_owner)");
	      sql.appendLine(");");
//	      // INDEX
//	      sql.appendLine("create unique index qa_tag_relation_idx on qa_tag_relation (");
//	      sql.appendLine(" qa_id asc,");
//	      sql.appendLine(" tag_id asc");
//	      sql.appendLine(");");
	      
	      /**
	       * 問題集とタグの関連テーブル　TODO 作成途中
	       */
	      sql.appendLine("create table if not exists mondaishu_tag_relation (");
	      // 行番号
	      sql.appendLine("  row_no integer auto_increment,");
	      // 問題集ID
	      sql.appendLine("  qa_set_id text not null,");
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
	      sql.appendLine("	update_timestamp text");
//	      sql.appendLine("	unique (qa_set_id, tag_id, create_owner, update_owner)");
	      sql.appendLine(");");
//	      // INDEX
//	      sql.appendLine("create unique index mondaishu_tag_relation_idx on mondaishu_tag_relation (");
//	      sql.appendLine(" qa_set_id asc,");
//	      sql.appendLine(" tag_id asc");
//	      sql.appendLine(");");
	      
	      /**
	       *  タグ同士の関連テーブル
	       */
	      sql.appendLine("create table if not exists tags_relation (");
	      // 行番号
	      sql.appendLine("  row_no integer auto_increment,");
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
	      sql.appendLine("	update_timestamp text");
//	      sql.appendLine("	unique (parent_tag_id, child_tag_id, create_owner, update_owner)");
	      sql.appendLine(");");
//	      // INDEX
//	      sql.appendLine("create unique index tags_relation_idx on tags_relation (");
//	      sql.appendLine(" parent_tag_id asc,");
//	      sql.appendLine(" child_tag_id asc");
//	      sql.appendLine(");");
	      	      
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
	    	disconnect(conn);
	    }
	}	

	/**
	 * 設定変更
	 * @param owner_id
	 */
	public void update_settings(String owner_id, String email, String owner_name, String new_password)
	{
		Connection conn = connect();
		try
		{
			List<byte[]> params = new ArrayList<byte[]>();
			
			StringBuilderPlus sql = new StringBuilderPlus();
			sql.appendLine("update owner_info");
			sql.appendLine(" set email = '" + email + "'");
			sql.appendLine(" ,owner_name = '" + owner_name + "'");
			if (new_password != null && new_password.equals("") == false)
			{
				AES aes = new AES();
				// SQLに渡すパラメーター（バイナリのみ対象）
				byte[] encrypted_password = aes.encrypt(new_password);
				params.add(encrypted_password);
				sql.appendLine(" ,password = ?");
			}
			sql.appendLine(" ,update_date = '" + Util.getNow(Constant.DB_DATE_FORMAT) + "'");
			sql.appendLine(" where owner_id = '" + owner_id + "'");
			update(sql, params);		
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			disconnect(conn);
		}
	}		
}
