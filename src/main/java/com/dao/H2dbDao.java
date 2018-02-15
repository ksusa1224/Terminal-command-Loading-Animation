package com.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.application.model.LoginInfoModel;
import com.common.*;

/**
 * 
 * @author ksusa
 *
 */
@Component
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
	 * PayPal用のテーブルを作成
	 */
	public void create_paypal_table()
	{
		Connection conn = connect();
		try
		{
			Statement stmt = conn.createStatement();
			
			StringBuilderPlus sql = new StringBuilderPlus();
			
			//sql.appendLine("drop table if exists paypal;");
			//Paypalテーブル作成
			sql.appendLine("create table if not exists paypal(");
			sql.appendLine("  owner_id varchar(20) unique,");
			sql.appendLine("  token text,");
			sql.appendLine("  profile_id text,");
			sql.appendLine("  last_name text,");
			sql.appendLine("  first_name text,");
			sql.appendLine("  email text,");
			sql.appendLine("  GetExpressCheckoutDetails text,");
			sql.appendLine("  del_flg integer,");
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
	 * Paypal情報の登録
	 */
	public void insert_paypal_info(
			String owner_id, 
			String token, 
			String profile_id,
			String last_name,
			String first_name,
			String email,
			String details)
	{
		Connection conn = connect();
		try
		{
			Statement stmt = conn.createStatement();
			
			StringBuilderPlus sql = new StringBuilderPlus();
			
			// オーナー情報テーブル作成
			sql.appendLine("insert into paypal (");
			sql.appendLine("  owner_id,");
			sql.appendLine("  token,");
			sql.appendLine("  profile_id,");
			sql.appendLine("  last_name,");
			sql.appendLine("  first_name,");
			sql.appendLine("  email,");
			sql.appendLine("  GetExpressCheckoutDetails,");
			sql.appendLine("  del_flg,");
			sql.appendLine("  update_date");
			sql.appendLine(") values (");
			sql.appendLine("'" + owner_id + "',");
			sql.appendLine("'" + token + "',");
			sql.appendLine("'" + profile_id + "',");
			sql.appendLine("'" + last_name + "',");
			sql.appendLine("'" + first_name + "',");
			sql.appendLine("'" + email + "',");
			sql.appendLine("'" + details + "',");
			sql.appendLine("0,");
			sql.appendLine("'" + Util.getNow(Constant.DB_DATE_FORMAT) + "'");
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
	 * Paypalのprofile_idを返却（定期支払停止に必要）
	 * @param owner_id
	 * @return
	 */
	public String get_paypal_profile_id(String owner_id)
	{
		String profile_id = null;
		
		Connection conn = connect();
		try
		{
			Statement stmt = conn.createStatement();
			
			// ログイン情報を取得
			StringBuilderPlus sql = new StringBuilderPlus();
			sql.appendLine("select");
			sql.appendLine("  profile_id ");
			sql.appendLine("from paypal "); 
			sql.appendLine("where "); 
			sql.appendLine("  owner_id = '" + owner_id + "'");
			sql.appendLine("  and del_flg = 0;");
			System.out.println(sql.toString());
			ResultSet rs = stmt.executeQuery(sql.toString());
			while (rs.next()) {
				profile_id = rs.getString("profile_id");
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
		
		return profile_id;
	}

	/**
	 * 
	 * @param token
	 */
	public void temp_to_register(String token)
	{
		Connection conn = connect();
		try
		{
			String sql = null;
			Statement stmt = conn.createStatement();
			
			sql="update owner_info set kakin_type = "
					+ " case when kakin_type = '" + Constant.KAKIN_TYPE_TEMPORARY + "'"
					+ " then '" + Constant.KAKIN_TYPE_FREE_PREIMIUM + "'"
					+ " when kakin_type = '" + Constant.KAKIN_TYPE_PREMIUM_TEMPORARY + "'"
					+ " then '" + Constant.KAKIN_TYPE_PREMIUM + "' end"
					+ " where token = '" + token + "';";
			
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
	 * db名変更（有料→無料ユーザになったとき用）
	 * @param db_name
	 */
	public void update_db_name(String owner_id, byte[] db_name)
	{
		Connection conn = connect();
		try
		{
			// SQLに渡すパラメーター（バイナリのみ対象）
			List<byte[]> params = new ArrayList<byte[]>();
			params = new ArrayList<byte[]>();
			params.add(db_name);
			
			StringBuilderPlus sql = new StringBuilderPlus();
			sql.appendLine("update owner_db ");
			sql.appendLine("  set db_name = ?,");
			sql.appendLine("  update_date = current_timestamp()");
			sql.appendLine("  where owner_id = '" + owner_id + "';");
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
	
	/**
	 * GENERAL OWNERからPREMIUM OWNERへ
	 * @param owner_id
	 */
	public void general_to_premium(String owner_id)
	{
		Connection conn = connect();
		try
		{
			String sql = null;
			Statement stmt = conn.createStatement();
			
			sql="update owner_info set kakin_type = "
					+ " '" + Constant.KAKIN_TYPE_PREMIUM + "'"
					+ " where owner_id = '" + owner_id + "';";
			
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
	 * PREMIUM OWNERからGENERAL OWNERへ
	 * @param owner_id
	 */
	public void premium_to_general(String owner_id)
	{
		Connection conn = connect();
		try
		{
			String sql = null;
			Statement stmt = conn.createStatement();
			
			sql="update owner_info set kakin_type = "
					+ " '" + Constant.KAKIN_TYPE_FREE + "'"
					+ " where owner_id = '" + owner_id + "';";
			
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
			sql.appendLine("  owner.token as token,");
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
			System.out.println(sql.toString());
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
				login_info.setToken(rs.getString("token"));
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

	public Integer get_kakin_type(String owner_id)
	{
		Integer kakin_type = null;
		
		Connection conn = connect();
		try
		{
			Statement stmt = conn.createStatement();
			
			// ログイン情報を取得
			StringBuilderPlus sql = new StringBuilderPlus();
			sql.appendLine("select");
			sql.appendLine("  owner.kakin_type as kakin_type ");
			sql.appendLine("from owner_info as owner "); 
			sql.appendLine("where "); 
			sql.appendLine("  owner.owner_id = '" + owner_id + "'");
			sql.appendLine("  and owner.del_flg = 0");
			System.out.println(sql.toString());
			ResultSet rs = stmt.executeQuery(sql.toString());
			while (rs.next()) {
				kakin_type = rs.getInt("kakin_type");
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
		
		return kakin_type;
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
	public String get_last_token(String owner_id_or_email)
	{
		Connection conn = connect();
		String last_token_db = null;
		try
		{
			String sql = "select token from owner_info where owner_id='" + owner_id_or_email + "' or email='" + owner_id_or_email + "'";
			PreparedStatement stmt = conn.prepareStatement(sql);
//			stmt.setString(1, owner_id_or_email);
//			stmt.setString(2, owner_id_or_email);
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

	public String get_owner_id_by_token(String token, String os, String browser)
	{
		Connection conn = connect();
		String last_token_db = null;
		try
		{
			StringBuilderPlus sql = new StringBuilderPlus();
			sql.appendLine("select owner_id from login_token where token='" + token + "' and os='" + os + "' and browser = '" + browser + "' limit 1;");
			PreparedStatement stmt = conn.prepareStatement(sql.toString());
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
	
	public String get_last_token(String owner_id, String os, String browser)
	{
		Connection conn = connect();
		String last_token_db = null;
		try
		{
			StringBuilderPlus sql = new StringBuilderPlus();
			sql.appendLine("select token from login_token where owner_id='" + owner_id + "' and os='" + os + "' and browser = '" + browser + "' limit 1;");
			PreparedStatement stmt = conn.prepareStatement(sql.toString());
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
	public void update_token_for_logout(String owner_id, String os, String browser)
	{
		Connection conn = connect();
		try
		{
			String sql = null;
			Statement stmt = conn.createStatement();
			
			sql="update login_token set token = '' where owner_id = '" + owner_id + "' and os = '" + os + "' and browser = '" + browser + "';";				
			
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
	 * emailとowner_idの組み合わせがすでに存在しているかチェック
	 * 存在していれば、オーナー名を返す
	 * @param email
	 * @param owner_id
	 * @return
	 */
	public String[] get_ownername_kakintype_from_email_owner_id(String email, String owner_id)
	{
		String[] owner_name_kakin_type = null;
		Connection conn = connect();
		try
		{
			Statement stmt = conn.createStatement();
			
			// ログイン情報を取得
			StringBuilderPlus sql = new StringBuilderPlus();
			sql.appendLine("select");
			sql.appendLine("  owner_name, kakin_type ");
			sql.appendLine("from owner_info "); 
			sql.appendLine("where "); 
			sql.appendLine("  email = '" + email + "'");
			sql.appendLine("  and owner_id = '" + owner_id + "'");
			sql.appendLine("  and del_flg = 0 limit 1;");
			
			ResultSet rs = stmt.executeQuery(sql.toString());
			if (rs.next()) {
				owner_name_kakin_type = new String[2];
				owner_name_kakin_type[0] = rs.getString("owner_name");
				owner_name_kakin_type[1] = String.valueOf(rs.getInt("kakin_type"));
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
		return owner_name_kakin_type;
	}	

	public boolean email_and_owner_id_exists(String email, String owner_id)
	{
		boolean is_exist = false;
		Connection conn = connect();
		try
		{
			Statement stmt = conn.createStatement();
			
			// ログイン情報を取得
			StringBuilderPlus sql = new StringBuilderPlus();
			sql.appendLine("select");
			sql.appendLine("  owner_id ");
			sql.appendLine("from owner_info "); 
			sql.appendLine("where "); 
			sql.appendLine("  email = '" + email + "'");
			sql.appendLine("  and owner_id = '" + owner_id + "'");
			sql.appendLine("  and del_flg = 0 limit 1;");
			
			ResultSet rs = stmt.executeQuery(sql.toString());
			if (rs.next()) {
				is_exist = true;
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
		return is_exist;
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
			sql.appendLine("  and del_flg = 0;");
			
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
			sql.appendLine("  and del_flg = 0;");
			
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
	 * ブラウザごとにオートログインできるようにする
	 */
	public void common_db_add_token_table()
	{
		Connection conn = connect();
		try
		{
			Statement stmt = conn.createStatement();
			
			StringBuilderPlus sql = new StringBuilderPlus();
						
			// オーナー情報テーブルにトークンを追加
			sql.appendLine("create table if not exists login_token (");
			sql.appendLine("owner_id varchar(20),");
			sql.appendLine("os varchar(255),");
			sql.appendLine("browser varchar(255),");
			sql.appendLine("token clob,");
			sql.appendLine("insert_date timestamp default current_timestamp(),");
			sql.appendLine("update_date timestamp");
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

	public void update_login_token(String owner_id, String os, String browser, String token)
	{
		Connection conn = connect();
		try
		{
			Statement stmt = conn.createStatement();
			
			StringBuilderPlus sql = new StringBuilderPlus();
						
			// ログイントークンテーブルにトークンを追加
			sql.appendLine("merge into login_token(");
			sql.appendLine("owner_id,");
			sql.appendLine("os,");
			sql.appendLine("browser,");
			sql.appendLine("token,");
			sql.appendLine("update_date)");
			sql.appendLine("key(owner_id, os, browser)");
			sql.appendLine("values(");
			sql.appendLine("'" + owner_id + "',");
			sql.appendLine("'" + os + "',");
			sql.appendLine("'" + browser + "',");
			sql.appendLine("'" + token + "',");
			sql.appendLine("'" + Util.getNow(Constant.DB_DATE_FORMAT) + "'");
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
	      // 問題と正答を入れ替えた結果生成された問題かどうか
	      sql.appendLine("  is_reversible integer default 0,");
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
	      // 問題と正答を入れ替えた結果生成された問題かどうか
	      sql.appendLine("  is_reversible integer default 0,");
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
	       * 問題集とQAの関連テーブル
	       */
	      sql.appendLine("create table if not exists mondaishu_qa_relation (");
	      // 行番号
	      sql.appendLine("  row_no integer auto_increment unique not null,");
	      // 問題集ID
	      sql.appendLine("  qa_set_id text not null,");
	      // タグID
	      sql.appendLine("	qa_id text,");
	      // 順番
	      sql.appendLine("  junban integer,");
	      // 作成者
	      sql.appendLine("  create_owner text,");
	      // 更新者
	      sql.appendLine("  update_owner text,");
	      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	create_timestamp text,");
	      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	update_timestamp text");
//	      sql.appendLine("	unique (qa_set_id, qa_id, create_owner, update_owner)");
	      sql.appendLine(");");
//	      // INDEX
//	      sql.appendLine("create unique index mondaishu_qa_relation_idx on mondaishu_qa_relation (");
//	      sql.appendLine(" qa_set_id asc,");
//	      sql.appendLine(" qa_id asc");
//	      sql.appendLine(");");
	      
	      /**
	       * 問題集とタグの関連テーブル
	       */
	      sql.appendLine("create table if not exists mondaishu_tag_relation (");
	      // 行番号
	      sql.appendLine("  row_no integer auto_increment,");
	      // 問題集ID
	      sql.appendLine("  qa_set_id text not null,");
	      // タグID
	      sql.appendLine("	tag_id text,");
	      // 問題集用タグ名（デフォルトはタグテーブル内の名前のコピー）
	      sql.appendLine("  tag_name_replacement text,");
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
	       * グループテーブル
	       */
	      sql.appendLine("create table if not exists groups (");
	      // 行番号
	      sql.appendLine("  row_no integer auto_increment,");
	      // グループID
	      sql.appendLine("  group_id text not null,");
	      // グループ名
	      sql.appendLine("	group_name text,");
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
	      sql.appendLine(");");

	      /**
	       * グループ・オーナー関係テーブル
	       */
	      sql.appendLine("create table if not exists owner_group_relation (");
	      // 行番号
	      sql.appendLine("  row_no integer auto_increment,");
	      // グループID
	      sql.appendLine("  group_id text not null,");
	      // オーナーID
	      sql.appendLine("	owner_id text,");
	      // 権限
	      sql.appendLine("  kengen integer,");
	      // 作成者
	      sql.appendLine("  create_owner text,");
	      // 更新者
	      sql.appendLine("  update_owner text,");
	      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	create_timestamp text,");
	      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	update_timestamp text");
	      sql.appendLine(");");

	      /**
	       * グループ・問題集関係テーブル
	       */
	      sql.appendLine("create table if not exists group_mondaishu_relation (");
	      // 行番号
	      sql.appendLine("  row_no integer auto_increment,");
	      // グループID
	      sql.appendLine("  group_id text not null,");
	      // 問題集ID
	      sql.appendLine("	mondaishu_id text,");
	      // 作成者
	      sql.appendLine("  create_owner text,");
	      // 更新者
	      sql.appendLine("  update_owner text,");
	      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	create_timestamp text,");
	      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	update_timestamp text");
	      sql.appendLine(");");
	      
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
	
	/**
	 * 問題集関連とグループ関連のテーブル作成パッチ
	 */
	public void mondaishu_and_group_patch()
	{
		Connection conn = connect();
		
		StringBuilderPlus sql = new StringBuilderPlus();

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
	       * 問題集とQAの関連テーブル
	       */
	      sql.appendLine("create table if not exists mondaishu_qa_relation (");
	      // 行番号
	      sql.appendLine("  row_no integer auto_increment unique not null,");
	      // 問題集ID
	      sql.appendLine("  qa_set_id text not null,");
	      // タグID
	      sql.appendLine("	qa_id text,");
	      // 順番
	      sql.appendLine("  junban integer,");
	      // 作成者
	      sql.appendLine("  create_owner text,");
	      // 更新者
	      sql.appendLine("  update_owner text,");
	      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	create_timestamp text,");
	      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	update_timestamp text");
//	      sql.appendLine("	unique (qa_set_id, qa_id, create_owner, update_owner)");
	      sql.appendLine(");");
//	      // INDEX
//	      sql.appendLine("create unique index mondaishu_qa_relation_idx on mondaishu_qa_relation (");
//	      sql.appendLine(" qa_set_id asc,");
//	      sql.appendLine(" qa_id asc");
//	      sql.appendLine(");");
	      
	      /**
	       * 問題集とタグの関連テーブル
	       */
	      sql.appendLine("create table if not exists mondaishu_tag_relation (");
	      // 行番号
	      sql.appendLine("  row_no integer auto_increment,");
	      // 問題集ID
	      sql.appendLine("  qa_set_id text not null,");
	      // タグID
	      sql.appendLine("	tag_id text,");
	      // 問題集用タグ名（デフォルトはタグテーブル内の名前のコピー）
	      sql.appendLine("  tag_name_replacement text,");
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
	       * グループテーブル
	       */
	      sql.appendLine("create table if not exists groups (");
	      // 行番号
	      sql.appendLine("  row_no integer auto_increment,");
	      // グループID
	      sql.appendLine("  group_id text not null,");
	      // グループ名
	      sql.appendLine("	group_name text,");
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
	      sql.appendLine(");");

	      /**
	       * グループ・オーナー関係テーブル
	       */
	      sql.appendLine("create table if not exists owner_group_relation (");
	      // 行番号
	      sql.appendLine("  row_no integer auto_increment,");
	      // グループID
	      sql.appendLine("  group_id text not null,");
	      // オーナーID
	      sql.appendLine("	owner_id text,");
	      // 権限
	      sql.appendLine("  kengen integer,");
	      // 作成者
	      sql.appendLine("  create_owner text,");
	      // 更新者
	      sql.appendLine("  update_owner text,");
	      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	create_timestamp text,");
	      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	update_timestamp text");
	      sql.appendLine(");");

	      /**
	       * グループ・問題集関係テーブル
	       */
	      sql.appendLine("create table if not exists group_mondaishu_relation (");
	      // 行番号
	      sql.appendLine("  row_no integer auto_increment,");
	      // グループID
	      sql.appendLine("  group_id text not null,");
	      // 問題集ID
	      sql.appendLine("	mondaishu_id text,");
	      // 作成者
	      sql.appendLine("  create_owner text,");
	      // 更新者
	      sql.appendLine("  update_owner text,");
	      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	create_timestamp text,");
	      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
	      sql.appendLine("	update_timestamp text");
	      sql.appendLine(");");
		
		try
	    {	      
		  Statement stmt = conn.createStatement();
	      /**
	       *  SQL実行
	       */
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
	 * 問題TBLと正答TBLにリバーシブルフラグを追加するパッチ
	 * @param db_name
	 */
	public void is_reversible_patch()
	{
		Connection conn = connect();
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("alter table mondai add column if not exists is_reversible integer;");
		sql.appendLine("alter table seitou add column if not exists is_reversible integer;");
		
		try
	    {	      
		  Statement stmt = conn.createStatement();
	      /**
	       *  SQL実行
	       */
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

	public byte[] get_owner_db(String owner_id)
	{
		byte[] owner_db = null;
		Connection conn = connect();
		try
		{
			Statement stmt = conn.createStatement();
			
			// ログイン情報を取得
			StringBuilderPlus sql = new StringBuilderPlus();
			sql.appendLine("select");
			sql.appendLine("  db_name ");
			sql.appendLine("from owner_db "); 
			sql.appendLine("where "); 
			sql.appendLine("  owner_id = '" + owner_id + "'");
			sql.appendLine("  and del_flg = 0");
			
			ResultSet rs = stmt.executeQuery(sql.toString());
			if (rs.next()) {
				owner_db = rs.getBytes(1);
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
		return owner_db;
	}

	/**
	 * 退会する
	 * @param owner_id
	 */
	public void withdraw(String owner_id)
	{
		Connection conn = connect();
		try
		{
			String sql = null;
			Statement stmt = conn.createStatement();
			
			sql="update owner_info "
					+ "set email = "
					+ "concat(email," + "'." + Util.getNow(Constant.DB_DATE_FORMAT) + "'),"
					+ "owner_id = "
					+ "concat(owner_id," + "'" + Util.getNow("yyyyMMddhh") + "'),"
					+ "del_flg = 1"
					+ " where owner_id = '" + owner_id + "'";
			
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
}
