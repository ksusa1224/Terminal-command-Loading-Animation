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
	  // TODO 入力された文字列でDBを作成する
	  @RequestMapping(value="/", method=RequestMethod.POST)
	  public String createUser(
			  HttpSession session,
			  @RequestParam("owner_id") String owner_id,
			  @RequestParam("login_password") String login_password,
			  @RequestParam("owner_name") String owner_name,
			  @RequestParam("email") String email) 
	  {
			SQliteDAO sqlite_dao = new SQliteDAO();
			String db_name = sqlite_dao.createSQliteDB(owner_id);
			
			// 暗号化ユーティリティ
			AES aes = new AES();
			byte[] encrypted_password = aes.encrypt(login_password);
			byte[] encrypted_db_name = aes.encrypt(db_name);

			// SQLに渡すパラメーター（バイナリのみ対象）
			List<byte[]> params = new ArrayList<byte[]>();
			params.add(encrypted_password);

			H2dbDao h2db_dao = new H2dbDao();
			
			// オーナー情報TBLに会員登録したユーザ情報を登録
			StringBuilderPlus sql = new StringBuilderPlus();
			sql.appendLine("insert into owner (");
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
			sql.appendLine("  (?),");
			sql.appendLine("  '" + Constant.KAKIN_TYPE_FREE_PREIMIUM + "',");
			sql.appendLine("  '0',");
			sql.appendLine("  current_timestamp(),");
			sql.appendLine("  current_timestamp()");
			sql.appendLine(");");
			h2db_dao.update(sql, params);

			// ユーザDB情報テーブルに会員登録したユーザのDB情報を格納
			params = new ArrayList<byte[]>();
			params.add(encrypted_db_name);
			
			sql = new StringBuilderPlus();
			sql.appendLine("insert into user_db (");
			sql.appendLine("  owner_id,");
			sql.appendLine("  db_name,");
			sql.appendLine("  db_version,");
			sql.appendLine("  is_current_db,");
			sql.appendLine("  insert_date,");
			sql.appendLine("  update_date");
			sql.appendLine(")");
			sql.appendLine("values(");
			sql.appendLine("  '" + owner_id + "',");
			sql.appendLine("  (?),");
			sql.appendLine("  '" + Constant.OWNER_DB_CURRENT_VERSION.replace("version", "00") + "',");
			sql.appendLine("  '1',");
			sql.appendLine("  current_timestamp(),");
			sql.appendLine("  current_timestamp()");
			sql.appendLine(");");
			h2db_dao.update(sql, params);
			
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
