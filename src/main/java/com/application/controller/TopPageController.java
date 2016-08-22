package com.application.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.common.AES;
import com.common.Constant;
import com.common.StringBuilderPlus;
import com.dao.H2dbDao;
import com.dao.SQliteDAO;

@Controller
public class TopPageController {

	/**
	   * 新規会員登録
	   * @param session
	   * @param owner_id
	   * @param login_password
	   * @param owner_name
	   * @param email
	   * @return
	   */
	  @RequestMapping(value="/", method=RequestMethod.POST, params={"register"})
	  public String createOwner(
			  HttpSession session,
			  @RequestParam("owner_id") String owner_id,
			  @RequestParam("login_password") String login_password,
			  @RequestParam("owner_name") String owner_name,
			  @RequestParam("email") String email) 
	  {
			SQliteDAO sqlite_dao = new SQliteDAO();
			String db_name = sqlite_dao.createOwnerDB(owner_id);
			
			// 暗号化ユーティリティ
			AES aes = new AES();
			byte[] encrypted_password = aes.encrypt(login_password);
			byte[] encrypted_db_name = aes.encrypt(db_name);

			// SQLに渡すパラメーター（バイナリのみ対象）
			List<byte[]> params = new ArrayList<byte[]>();
			params.add(encrypted_password);

//			H2dbDao h2db_dao2 = new H2dbDao();
//			h2db_dao2.create_common_db();
			
			// オーナー情報TBLに会員登録したユーザ情報を登録
			StringBuilderPlus sql = new StringBuilderPlus();
			sql.appendLine("insert into owner_info (");
			sql.appendLine("  owner_id,");
			sql.appendLine("  owner_name,");
			sql.appendLine("  email,");
			sql.appendLine("  password,");
			sql.appendLine("  kakin_type,");
			sql.appendLine("  del_flg,");
			sql.appendLine("  insert_date,");
			sql.appendLine("  update_date)");
			sql.appendLine("values(");
			sql.appendLine("  '" + owner_id + "',");
			sql.appendLine("  '" + owner_name + "',");
			sql.appendLine("  '" + email + "',");
			sql.appendLine("  ?,"); // Password
			sql.appendLine("  '" + Constant.KAKIN_TYPE_FREE_PREIMIUM + "',");
			sql.appendLine("  0,");
			sql.appendLine("  current_timestamp(),");
			sql.appendLine("  current_timestamp()");
			sql.appendLine(");");

			H2dbDao h2db_dao = new H2dbDao();
			h2db_dao.update(sql, params);

			// ユーザDB情報テーブルに会員登録したユーザのDB情報を格納
			params = new ArrayList<byte[]>();
			params.add(encrypted_db_name);
			
			StringBuilderPlus sql2 = new StringBuilderPlus();
			sql2.appendLine("insert into owner_db (");
			sql2.appendLine("  owner_id,");
			sql2.appendLine("  db_name,");
			sql2.appendLine("  db_version,");
			sql2.appendLine("  is_current_db,");
			sql2.appendLine("  del_flg,");
			sql2.appendLine("  insert_date,");
			sql2.appendLine("  update_date");
			sql2.appendLine(") ");
			sql2.appendLine("values (");
			sql2.appendLine("  '" + owner_id + "',");
			sql2.appendLine("  ?,"); // db_name
			sql2.appendLine("  '" + Constant.OWNER_DB_CURRENT_VERSION + "',");
			sql2.appendLine("  1,");
			sql2.appendLine("  0,");
			sql2.appendLine("  current_timestamp(),");
			sql2.appendLine("  current_timestamp()");
			sql2.appendLine(");");
			h2db_dao.update(sql2, params);
			
			// セッションに暗号化されたユーザ専用DB名を格納
			session.setAttribute("db", encrypted_db_name);
			byte[] a = (byte[])session.getAttribute("db");
			AES aes2 = new AES();
			String b = aes2.decrypt(a);
			System.out.println("a:"+a);
			System.out.println("b:"+b);
			String c = session.getId();
			System.out.println("c:"+c);
			
			String original = aes.decrypt(encrypted_db_name);
//			System.out.print(original);

	      return "index";
	  }
	  
	  @RequestMapping(value="/", method=RequestMethod.POST, params={"login"})
	  public String login(
			  HttpSession session,
			  @RequestParam("owner_id_or_email") String owner_id_or_email,
			  @RequestParam("login_password") String login_password) 
	  {
			H2dbDao h2db_dao = new H2dbDao();
			
			AES aes = new AES();
			byte[] encrypted_input_password = aes.encrypt(login_password);
			byte[] encrypted_db_name = null;
			
			// ログイン情報を取得
			StringBuilderPlus sql = new StringBuilderPlus();
			sql.appendLine("select");
			sql.appendLine("  owner.owner_id,");
			sql.appendLine("  owner.owner_name,");
			sql.appendLine("  owner.email,");
			sql.appendLine("  owner.kakin_type, ");
			sql.appendLine("  db.db_name");
			sql.appendLine("  db.db_version ");
			sql.appendLine("from owner "); 
			sql.appendLine("inner join ");
			sql.appendLine("  owner_db as db ");
			sql.appendLine("  on owner.owner_id = db.owner_id ");
			sql.appendLine("  and is_current_db = '1'");
			sql.appendLine("where "); 
			sql.appendLine("  (owner.owner_id = '" + owner_id_or_email + "',");
			sql.appendLine("   or");
			sql.appendLine("  owner.email = '" + owner_id_or_email + "')");
			sql.appendLine("  and");
			sql.appendLine("  owner.password = " + encrypted_input_password);
			

			
			
			// セッションに暗号化されたユーザ専用DB名を格納
			session.setAttribute("db", encrypted_db_name);
			byte[] a = (byte[])session.getAttribute("db");
			AES aes2 = new AES();
			String b = aes2.decrypt(a);
			System.out.println("a:"+a);
			System.out.println("b:"+b);
			String c = session.getId();
			System.out.println("c:"+c);
			
			String original = aes.decrypt(encrypted_db_name);
//			System.out.print(original);

	      return "index";
	  }
	  
}
