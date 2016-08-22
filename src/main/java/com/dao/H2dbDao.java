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
			sql.appendLine("  owner_id text,");
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
	 * ログイン情報を取得する TODO 作成途中
	 * @param sql
	 * @return ログイン情報クラス
	 */
	public LoginInfoModel select_login_info(StringBuilderPlus sql)
	{
		System.out.println("---------aaaaaa-----");
		LoginInfoModel login_info = new LoginInfoModel();
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
}
