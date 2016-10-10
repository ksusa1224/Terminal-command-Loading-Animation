package com.application.controller.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import com.application.model.dao.SeitouModel;
import com.common.Constant;
import com.common.Log;
import com.common.StopWatch;
import com.common.StringBuilderPlus;
import com.dao.SQliteDAO;

public class SeitouDao {

	/**
	 * 最大行を得る
	 * @param db_name
	 * @return
	 */
	public int get_seitou_max_row_no(String db_name)
	{	
		int max_row_no = 0;
		
		SQliteDAO dao = new SQliteDAO();
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("select max(row_no) as row_no from seitou limit 1;");
		dao.loadDriver();
		
		//System.out.println(db_name);

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
	    	  max_row_no = rs.getInt("row_no");
	    	  System.out.println(max_row_no);
	      }
	    }
	    catch(Exception ex)
	    {
			Log log = new Log();
			log.insert_error_log("ERROR", ex.getStackTrace().toString());
		    System.err.println(ex.getMessage());
	    }
	    finally
	    {
	      dao.close(connection);
	    }	    
		
		return max_row_no;
	}
	
	/**
	 * 正答の総数を返す
	 * @param db_name
	 * @return
	 */
	public int get_seitou_cnt(String db_name)
	{	
		int seitou_cnt = 0;
		
		SQliteDAO dao = new SQliteDAO();
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("select count(seitou) from seitou ");
		sql.appendLine(" where del_flg = 0 and seitou != '' and seitou is not null limit 1;");
		dao.loadDriver();
		
		//System.out.println(db_name);

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
	    	  seitou_cnt = rs.getInt(1);
	    	  System.out.println(seitou_cnt);
	      }
	    }
	    catch(Exception ex)
	    {
			Log log = new Log();
			log.insert_error_log("ERROR", ex.getStackTrace().toString());
		    System.err.println(ex.getMessage());
	    }
	    finally
	    {
	      dao.close(connection);
	    }	    
		
		return seitou_cnt;
	}	
	
	/**
	 * タグ名に紐づく正答総数を出す
	 * @param db_name
	 * @param tag_names
	 * @return
	 */
	public int get_seitou_cnt(String db_name, String tag_names)
	{	
		int seitou_cnt = 0;
		
		SQliteDAO dao = new SQliteDAO();
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("select count(seitou) from seitou ");
		if (!tag_names.equals(""))
        {
        	sql.appendLine(",qa, qa_tag_relation,tag");
        }
		sql.appendLine(" where seitou.del_flg = 0 and seitou != '' and seitou is not null ");
		if (Arrays.asList(tag_names.split(",")).contains("未正解"))
		{
        	sql.appendLine(" and qa.qa_id = seitou.qa_id");
        	sql.appendLine(" and (seitou.seikai_flg = 0 or seitou.seikai_flg is null)");
			sql.appendLine(" and qa.qa_id = qa_tag_relation.qa_id");
			sql.appendLine(" and tag.tag_id = qa_tag_relation.tag_id");
        	if (tag_names.split(",").length > 1)
        	{
				sql.appendLine(" and (");
				for (int i = 0; i < tag_names.split(",").length; i++)
				{
					if (tag_names.split(",")[i].equals("未正解"))
					{
						sql.appendLine("1 = 1");
						continue;
					}
					sql.appendLine("tag.tag_name = '" + tag_names.split(",")[i] + "'");
					if (i < tag_names.split(",").length - 1)
					{
						sql.appendLine(" or ");
					}
				}
		        sql.appendLine(")");			
        	}
		}
		else if (Arrays.asList(tag_names.split(",")).contains("正解"))
		{
        	sql.appendLine(" and qa.qa_id = seitou.qa_id");
        	sql.appendLine(" and seitou.seikai_flg = 1");
			sql.appendLine(" and qa.qa_id = qa_tag_relation.qa_id");
			sql.appendLine(" and tag.tag_id = qa_tag_relation.tag_id");
        	if (tag_names.split(",").length > 1)
        	{
				sql.appendLine(" and (");
				for (int i = 0; i < tag_names.split(",").length; i++)
				{
					if (tag_names.split(",")[i].equals("正解"))
					{
						sql.appendLine("1 = 1");
						continue;
					}
					sql.appendLine("tag.tag_name = '" + tag_names.split(",")[i] + "'");
					if (i < tag_names.split(",").length - 1)
					{
						sql.appendLine(" or ");
					}
				}
		        sql.appendLine(")");			
        	}
		}
		else if (!tag_names.equals(""))
        {
			sql.appendLine(" and qa.qa_id = qa_tag_relation.qa_id");
			sql.appendLine(" and qa.qa_id = seitou.qa_id");
			sql.appendLine(" and tag.tag_id = qa_tag_relation.tag_id");
			sql.appendLine(" and (");
			for (int i = 0; i < tag_names.split(",").length; i++)
			{
				sql.appendLine("tag.tag_name = '" + tag_names.split(",")[i] + "'");
				if (i < tag_names.split(",").length - 1)
				{
					sql.appendLine(" or ");
				}
			}
	        sql.appendLine(")");
        }
        if (Arrays.asList(tag_names.split(",")).contains("未正解") ||
        	Arrays.asList(tag_names.split(",")).contains("正解"))
        {
        	sql.appendLine(" group by seitou.s_id");
        }
	      System.out.println("正答数："+sql.toString());

		
		dao.loadDriver();
		
		//System.out.println(db_name);

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
	    	  if (tag_names.contains("未正解"))
	    	  {
	    		  seitou_cnt++;
	    	  }
	    	  else if (tag_names.contains("正解"))
	    	  {
	    		  seitou_cnt++;
	    	  }
	    	  else
	    	  {
	    		  seitou_cnt = rs.getInt(1);
	    	  }
	      }
	    }
	    catch(Exception ex)
	    {
			Log log = new Log();
			log.insert_error_log("ERROR", ex.getStackTrace().toString());
		    System.err.println(ex.getMessage());
	    }
	    finally
	    {
	      dao.close(connection);
	    }	    
		
		return seitou_cnt;
	}	
	

	/**
	 * 正解数を得る
	 * @param db_name
	 * @return
	 */
	public int get_seikai_cnt(String db_name)
	{	
		int seikai_cnt = 0;
		
		SQliteDAO dao = new SQliteDAO();
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("SELECT  count(seikai_flg) ");
		sql.appendLine("FROM    seitou ");
		sql.appendLine("WHERE seikai_flg = 1");

		dao.loadDriver();
		
		//System.out.println(db_name);

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
	    	  seikai_cnt = rs.getInt(1);
	      }
	    }
	    catch(Exception ex)
	    {
			Log log = new Log();
			log.insert_error_log("ERROR", ex.getStackTrace().toString());
		    System.err.println(ex.getMessage());
	    }
	    finally
	    {
	      dao.close(connection);
	    }	    
		
		return seikai_cnt;
	}	

	/**
	 * タグ名に紐づく正解総数を得る
	 * @param db_name
	 * @param tag_names
	 * @return
	 */
	public int get_seikai_cnt(String db_name, String tag_names)
	{	
		int seikai_cnt = 0;
		
		SQliteDAO dao = new SQliteDAO();
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("SELECT  count(seikai_flg) ");
		sql.appendLine("FROM    seitou ");
        if (!tag_names.equals(""))
        {
        	sql.appendLine(",qa, qa_tag_relation,tag");
        }
		sql.appendLine("WHERE seikai_flg = 1");
		System.out.println(tag_names);
		if (tag_names.equals("未正解") || tag_names.contains("未正解"))
		{
			sql.appendLine(" and qa.qa_id = qa_tag_relation.qa_id");
			sql.appendLine(" and tag.tag_id = qa_tag_relation.tag_id");
			sql.appendLine(" and (");
			for (int i = 0; i < tag_names.split(",").length; i++)
			{
				if (tag_names.split(",")[i].equals("未正解"))
				{
					sql.appendLine("1 = 1");
					continue;
				}
				sql.appendLine("tag.tag_name = '" + tag_names.split(",")[i] + "'");
				if (i < tag_names.split(",").length - 1)
				{
					sql.appendLine(" or ");
				}
			}
	        sql.appendLine(")");			
		}
		else if (Arrays.asList(tag_names.split(",")).contains("正解"))
		{
        	if (tag_names.split(",").length > 1)
        	{
				sql.appendLine(" and qa.qa_id = qa_tag_relation.qa_id");
				sql.appendLine(" and tag.tag_id = qa_tag_relation.tag_id");
				sql.appendLine(" and (");
				for (int i = 0; i < tag_names.split(",").length; i++)
				{
					if (tag_names.split(",")[i].equals("正解"))
					{
						sql.appendLine("1 = 1");
						continue;
					}
					sql.appendLine("tag.tag_name = '" + tag_names.split(",")[i] + "'");
					if (i < tag_names.split(",").length - 1)
					{
						sql.appendLine(" or ");
					}
				}
		        sql.appendLine(")");			
        	}
		}
		else if (!tag_names.equals(""))
        {
			sql.appendLine(" and qa.qa_id = qa_tag_relation.qa_id");
			sql.appendLine(" and qa.qa_id = seitou.qa_id");
			sql.appendLine(" and tag.tag_id = qa_tag_relation.tag_id");
			sql.appendLine(" and (");
			for (int i = 0; i < tag_names.split(",").length; i++)
			{
				sql.appendLine("tag.tag_name = '" + tag_names.split(",")[i] + "'");
				if (i < tag_names.split(",").length - 1)
				{
					sql.appendLine(" or ");
				}
			}
	        sql.appendLine(")");
        }
        if (tag_names.contains("未正解") || tag_names.contains("正解"))
        {
        	sql.appendLine(" group by seitou.s_id");
        }

		dao.loadDriver();
		
		System.out.println(sql.toString());

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
	    	  if (tag_names.contains("未正解"))
	    	  {
	    		  seikai_cnt++;
	    	  }
	    	  else if (tag_names.contains("正解"))
	    	  {
	    		  seikai_cnt++;
	    	  }
	    	  else
	    	  {
	    		  seikai_cnt = rs.getInt(1);
	    	  }
	      }
	    }
	    catch(Exception ex)
	    {
			Log log = new Log();
			log.insert_error_log("ERROR", ex.getStackTrace().toString());
		    System.err.println(ex.getMessage());
	    }
	    finally
	    {
	      dao.close(connection);
	    }	    
		
		return seikai_cnt;
	}		
	
	public List<SeitouModel> select_seitou_list(String db_name, List<SeitouModel> seitou_list)
	{		
		SQliteDAO dao = new SQliteDAO();
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("select ");
	    // 行番号
		sql.appendLine("  row_no,");
	    // 正答ID
		sql.appendLine("  s_id,");
	    // QA ID
		sql.appendLine("  qa_id,");
	    // QA内での正答の順番
		sql.appendLine("  junban,");
	    // 正答が文字であるかのフラグ
		sql.appendLine("  is_text_flg,");
	    // 正答がバイナリであるかのフラグ
		sql.appendLine("  is_binary_flg,");
	    // 正答
		sql.appendLine("  seitou,");
	    // 正解フラグ
		sql.appendLine("  seikai_flg,");
	    // 正答が画像などのバイナリである場合に格納する
		sql.appendLine("  seitou_binary,");
	    // 重要度（５段階）
		sql.appendLine("  juyoudo,");
	    // 難易度（５段階）
		sql.appendLine("  nanido,");
	    // 言語
		sql.appendLine("  language,");
	    // テキスト読み上げデータ
		sql.appendLine("  yomiage,");
	    // 削除フラグ
		sql.appendLine("  del_flg,");
	    // 作成者
		sql.appendLine("  create_owner,");
	    // 更新者
		sql.appendLine("  update_owner,");
	    // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("  create_timestamp,");
	    // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("  update_timestamp");
		sql.appendLine(" from seitou");
		sql.appendLine(" where ");
		sql.appendLine(" del_flg = 0");
		sql.appendLine(" order by qa_id,junban asc;");
		
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
	    	  SeitouModel seitou = new SeitouModel();
		      // 行番号
	    	  seitou.setRow_no(rs.getInt("row_no"));
		      // 正答ID
	    	  seitou.setS_id(rs.getString("s_id"));
		      // QA ID
	    	  seitou.setQa_id(rs.getString("qa_id"));
		      // QA内での正答の順番
	    	  seitou.setJunban(rs.getInt("junban"));
	    	  // 正答が文字であるかのフラグ
	    	  seitou.setIs_text_flg(rs.getInt("is_text_flg"));
	    	  // 正答がバイナリであるかのフラグ
	    	  seitou.setIs_binary_flg(rs.getInt("is_binary_flg"));
		      // 正答
	    	  seitou.setSeitou(rs.getString("seitou"));
	    	  // 正解フラグ
	    	  seitou.setSeikai_flg(rs.getInt("seikai_flg"));
	    	  // 正答が画像などのバイナリである場合に格納する
	    	  seitou.setSeitou_binary(rs.getBytes("seitou_binary"));
		      // 重要度（５段階）
	    	  seitou.setJuyoudo(rs.getInt("juyoudo"));
		      // 難易度（５段階）
	    	  seitou.setNanido(rs.getInt("nanido"));
	    	  // 言語
	    	  seitou.setLanguage(rs.getString("language"));
	    	  // テキスト読み上げデータ
	    	  seitou.setYomiage(rs.getBytes("yomiage"));
	    	  // 削除フラグ
		      seitou.setDel_flg(rs.getInt("del_flg"));
		      // 作成者
		      seitou.setCreate_owner(rs.getString("create_owner"));
		      // 更新者
		      seitou.setUpdate_owner(rs.getString("update_owner"));
		      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		      seitou.setUpdate_timestamp(rs.getString("create_timestamp"));
		      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		      seitou.setUpdate_timestamp(rs.getString("update_timestamp"));

		      seitou_list.add(seitou);
	      }
	    }
	    catch(Exception ex)
	    {
			Log log = new Log();
			log.insert_error_log("ERROR", ex.getStackTrace().toString());
		    System.err.println(ex.getMessage());
	    }
	    finally
	    {
	      dao.close(connection);
	    }	    
		
		return seitou_list;
	}
	
	/**
	 * 
	 * @param db_name
	 * @param s_id
	 * @return
	 */
	public int is_seikai(String db_name, String s_id)
	{
		int is_seikai = 0;
		
		SQliteDAO dao = new SQliteDAO();
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("select seikai_flg from seitou where s_id = '" + s_id + "' limit 1;");
		dao.loadDriver();
		
		//System.out.println(db_name);

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
	    	  is_seikai = rs.getInt(1);
	    	  //System.out.println("is_seikai"+is_seikai);
	      }
	    }
	    catch(Exception ex)
	    {
			Log log = new Log();
			log.insert_error_log("ERROR", ex.getStackTrace().toString());
		    System.err.println(ex.getMessage());
	    }
	    finally
	    {
	      dao.close(connection);
	    }	    
		
		return is_seikai;
	}
	
	/**
	 * 
	 * @param db_name
	 * @param s_id
	 * @param seikai_flg
	 */
	public void update_seikai_flg(String db_name, String s_id, int seikai_flg)
	{
		SQliteDAO dao = new SQliteDAO();
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("update seitou ");		
		sql.appendLine("set seikai_flg = " + seikai_flg);
		sql.appendLine(" where s_id = '" + s_id + "'");
		dao.update(db_name, sql);
	}
	
	/**
	 * 
	 * @param db_name
	 * @param tag_names
	 */
	public void to_huseikai_by_tag(String db_name,String tag_names)
	{
		StopWatch stopwatch = new StopWatch();

		SQliteDAO dao = new SQliteDAO();
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("update seitou");
        sql.appendLine(" set seikai_flg = 0");
        sql.appendLine(" where seitou.del_flg = 0");
        if (!tag_names.equals(""))
        {
            if (!tag_names.equals(""))
            {
            	sql.appendLine("and (select 0 from seitou,qa, qa_tag_relation,tag ");
            }
			sql.appendLine(" where qa.qa_id = qa_tag_relation.qa_id");
			sql.appendLine(" and qa.qa_id = seitou.qa_id");
			sql.appendLine(" and tag.tag_id = qa_tag_relation.tag_id");
			sql.appendLine(" and (");
			for (int i = 0; i < tag_names.split(",").length; i++)
			{
				sql.appendLine("tag.tag_name = '" + tag_names.split(",")[i] + "'");
				if (i < tag_names.split(",").length - 1)
				{
					sql.appendLine(" or ");
				}
			}
	        sql.appendLine(")");
	        sql.appendLine(")");
        }
		
		dao.update(db_name, sql);		
		stopwatch.stop(new Object(){}.getClass().getEnclosingMethod().getName());		
	}
	
	/**
	 * @param db_name
	 * @param seitou_list
	 * @return
	 */
	public List<SeitouModel> select_seitou_list(String db_name, List<SeitouModel> seitou_list, String qa_id)
	{		
		SQliteDAO dao = new SQliteDAO();
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("select ");
	    // 行番号
		sql.appendLine("  row_no,");
	    // 正答ID
		sql.appendLine("  s_id,");
	    // QA ID
		sql.appendLine("  qa_id,");
	    // QA内での正答の順番
		sql.appendLine("  junban,");
	    // 正答が文字であるかのフラグ
		sql.appendLine("  is_text_flg,");
	    // 正答がバイナリであるかのフラグ
		sql.appendLine("  is_binary_flg,");
	    // 正答
		sql.appendLine("  seitou,");
		// 正解フラグ
		sql.appendLine("  seikai_flg,");
	    // 正答が画像などのバイナリである場合に格納する
		sql.appendLine("  seitou_binary,");
	    // 重要度（５段階）
		sql.appendLine("  juyoudo,");
	    // 難易度（５段階）
		sql.appendLine("  nanido,");
	    // 言語
		sql.appendLine("  language,");
	    // テキスト読み上げデータ
		sql.appendLine("  yomiage,");
	    // 削除フラグ
		sql.appendLine("  del_flg,");
	    // 作成者
		sql.appendLine("  create_owner,");
	    // 更新者
		sql.appendLine("  update_owner,");
	    // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("  create_timestamp,");
	    // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("  update_timestamp");
		sql.appendLine(" from seitou");
		sql.appendLine(" where qa_id = '" + qa_id + "'");
		sql.appendLine(" and del_flg = 0");
		sql.appendLine(" order by junban asc;");
		
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
	    	  SeitouModel seitou = new SeitouModel();
		      // 行番号
	    	  seitou.setRow_no(rs.getInt("row_no"));
		      // 正答ID
	    	  seitou.setS_id(rs.getString("s_id"));
		      // QA ID
	    	  seitou.setQa_id(rs.getString("qa_id"));
		      // QA内での正答の順番
	    	  seitou.setJunban(rs.getInt("junban"));
	    	  // 正答が文字であるかのフラグ
	    	  seitou.setIs_text_flg(rs.getInt("is_text_flg"));
	    	  // 正答がバイナリであるかのフラグ
	    	  seitou.setIs_binary_flg(rs.getInt("is_binary_flg"));
		      // 正答
	    	  seitou.setSeitou(rs.getString("seitou"));
	    	  // 正解フラグ
	    	  seitou.setSeikai_flg(rs.getInt("seikai_flg"));	    	  
	    	  // 正答が画像などのバイナリである場合に格納する
	    	  seitou.setSeitou_binary(rs.getBytes("seitou_binary"));
		      // 重要度（５段階）
	    	  seitou.setJuyoudo(rs.getInt("juyoudo"));
		      // 難易度（５段階）
	    	  seitou.setNanido(rs.getInt("nanido"));
	    	  // 言語
	    	  seitou.setLanguage(rs.getString("language"));
	    	  // テキスト読み上げデータ
	    	  seitou.setYomiage(rs.getBytes("yomiage"));
	    	  // 削除フラグ
		      seitou.setDel_flg(rs.getInt("del_flg"));
		      // 作成者
		      seitou.setCreate_owner(rs.getString("create_owner"));
		      // 更新者
		      seitou.setUpdate_owner(rs.getString("update_owner"));
		      // レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		      seitou.setUpdate_timestamp(rs.getString("create_timestamp"));
		      // レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		      seitou.setUpdate_timestamp(rs.getString("update_timestamp"));

		      seitou_list.add(seitou);
	      }
	    }
	    catch(Exception ex)
	    {
			Log log = new Log();
			log.insert_error_log("ERROR", ex.getStackTrace().toString());
		    System.err.println(ex.getMessage());
	    }
	    finally
	    {
	      dao.close(connection);
	    }	    
		
		return seitou_list;
	}
	
	/**
	 * 正答テーブルに１件レコードを追加する
	 * @param seitou
	 * @return
	 */
	public void insert_seitou(String db_name, SeitouModel seitou)
	{
		SQliteDAO dao = new SQliteDAO();
	    Connection connection = null;
		String db_save_path = Constant.SQLITE_OWNER_DB_FOLDEDR_PATH + "/";
		String connection_str = "jdbc:sqlite:" 
				  				+ db_save_path
				  				+ db_name;
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("replace into seitou (");
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
		// 正解フラグ
		sql.appendLine("    seikai_flg,");
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
		sql.appendLine("'" + (seitou.getSeitou()).replace("'", "''") + "',");
		// 正解フラグ
		sql.appendLine("" + seitou.getSeikai_flg() + ",");
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
			Log log = new Log();
			log.insert_error_log("ERROR", ex.getStackTrace().toString());
		    System.err.println(ex.getMessage());
	    }
	    finally
	    {
	      dao.close(connection);
	    }	    
		
	}	
	
	/**
	 * QAテーブルを１件更新する
	 * @param seitou
	 * @return 
	 */
	public void update_seitou(String db_name, SeitouModel seitou)
	{
		SQliteDAO dao = new SQliteDAO();
	    Connection connection = null;
		String db_save_path = Constant.SQLITE_OWNER_DB_FOLDEDR_PATH + "/";
		String connection_str = "jdbc:sqlite:" 
				  				+ db_save_path
				  				+ db_name;
		
		StringBuilderPlus sql = new StringBuilderPlus();
				
		sql.appendLine("update seitou ");
		sql.appendLine("values (");
	    // 行番号
		sql.appendLine("  row_no = " + seitou.getRow_no() + ",");
	    // 正答ID
		sql.appendLine("  s_id = '" + seitou.getS_id() + "',");
	    // QA ID
		sql.appendLine("  qa_id = '" + seitou.getQa_id() + "',");
	    // QA内での正答の順番
		sql.appendLine("  junban = " + seitou.getJunban() + ",");
		// 正答が文字であるかのフラグ
		sql.appendLine("  is_text_flg = " + seitou.getIs_text_flg() + ",");
		// 正答がバイナリであるかのフラグ
		sql.appendLine("  is_binary_flg = " + seitou.getIs_binary_flg() + ",");
	    // 正答
		sql.appendLine("  seitou = '" + seitou.getSeitou() + "',");
		// 正解フラグ
		sql.appendLine("  seikai_flg = " + seitou.getSeikai_flg() + ",");
		// 正答が画像などのバイナリである場合に格納する
		sql.appendLine("  seitou_binary = " + seitou.getSeitou_binary() + ",");		
	    // 重要度（５段階）
		sql.appendLine("  juyoudo = " + seitou.getJuyoudo() + ",");
	    // 難易度（５段階）
		sql.appendLine("  nanido = " + seitou.getNanido() + ",");
	    // 言語
		sql.appendLine("  language = '" + seitou.getLanguage() + "',");
		// テキスト読み上げデータ
		sql.appendLine("  yomiage = " + seitou.getYomiage() + ",");
		// 削除フラグ
		sql.appendLine("  del_flg = " + seitou.getDel_flg() + ",");
		// 作成者
		sql.appendLine("  create_owner = '" + seitou.getCreate_owner() + "',");
		// 更新者
		sql.appendLine("  update_owner = '" + seitou.getUpdate_owner() + "',");
		// レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("  create_timestamp = '" + seitou.getCreate_timestamp() + "',");
		// レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		sql.appendLine("  update_timestamp = '" + seitou.getUpdate_timestamp() + "'");
		sql.appendLine(");");
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
			Log log = new Log();
			log.insert_error_log("ERROR", ex.getStackTrace().toString());
		    System.err.println(ex.getMessage());
	    }
	    finally
	    {
	      dao.close(connection);
	    }		
	}

}
