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
public class H2dbDao {

	/**
	 * 共通DBを作成（基本的には呼び出さないが、壊れた際などに再作成するのに使う）
	 */
	public void create_common_db()
	{
		try 
		{
			Class.forName("org.h2.Driver");
		}
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		}
		
		Connection conn;
		try 
		{
			conn = DriverManager.
			    getConnection(
			    "jdbc:h2:tcp://localhost/" + Constant.H2DB_FOLDER_PATH +"common",
			    "common_admin",
			    "DSFRsD0eSD2Sh5#8");
			
			Statement stmt = conn.createStatement();
			
			StringBuilderPlus sql = new StringBuilderPlus();
			
			// 管理者アカウントの作成
			sql.appendLine("CREATE USER IF NOT EXISTS common_admin PASSWORD 'DSFRsD0eSD2Sh5#8';");
			sql.appendLine("ALTER USER common_admin ADMIN TRUE;");
			
			// デフォルトユーザ（ID・パスともに空文字）の削除
			sql.appendLine("DROP USER IF EXISTS \"\";");
			
			// オーナー情報テーブル作成
			sql.appendLine("create table if not exists owner(");
			sql.appendLine("  id integer primary key auto_increment,");
			sql.appendLine("  owner_id varchar(100) unique not null,");
			sql.appendLine("  owner_name varchar(100),");
			sql.appendLine("  email varchar(100),");
			sql.appendLine("  password binary,");
			sql.appendLine("  kakin_type char(2),");
			sql.appendLine("  del_flg char(1),");
			sql.appendLine("  insert_date timestamp default current_timestamp(),");
			sql.appendLine("  update_date timestamp");
			sql.appendLine(");");
			
			// オーナーDB情報テーブル作成
			sql.appendLine("create table user_db(");
			sql.appendLine("  id integer primary key auto_increment,");
			sql.appendLine("  owner_id varchar(100) not null,");
			sql.appendLine("  db_name binary,");
			sql.appendLine("  db_version char(5),");
			sql.appendLine("  is_current_db char(1),");
			sql.appendLine("  insert_date timestamp default current_timestamp(),");
			sql.appendLine("  update_date timestamp");
			sql.appendLine(");");
			
			transaction(stmt, sql);
			
			conn.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
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
