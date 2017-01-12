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
	private Connection connect() {
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
	private void disconnect(Connection conn) {
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
	private void transaction(Statement stmt, StringBuilderPlus sql) throws SQLException 
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
}
