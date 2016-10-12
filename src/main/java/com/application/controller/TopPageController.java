package com.application.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.application.controller.dao.KaitouDao;
import com.application.controller.dao.SeitouDao;
import com.application.model.LoginInfoModel;
import com.application.model.dao.SeitouModel;
import com.common.AES;
import com.common.Constant;
import com.common.Log;
import com.common.StringBuilderPlus;
import com.dao.H2dbDao;
import com.dao.SQliteDAO;
import com.email.MailSend;

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
	  // registerボタン押下の場合
	  @RequestMapping(value="/", method=RequestMethod.POST, params={"register"})
	  public String createOwner(
			  HttpSession session,
			  HttpServletRequest request,
			  @RequestParam("owner_id") String owner_id,
			  @RequestParam("login_password") String login_password,
			  @RequestParam("owner_name") String owner_name,
			  @RequestParam("email") String email) 
	  {
			/**
			 * アクセスログ記録
			 */
			String request_uri = request.getRequestURI();
			String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
			String client_ip = Log.getClientIpAddress(request);
			String client_os = Log.getClientOS(request);
			String client_browser = Log.getClientBrowser(request);
			Log log = new Log();
			log.insert_access_log(owner_id, request_uri, method_name, client_ip, client_os, client_browser);

		  // 仮登録用のワンタイムパスワード
		  UUID uuid = UUID.randomUUID();
		  String token = uuid.toString();
		  MailSend mail_send = new MailSend();
		  Boolean sended = mail_send.send_register_mail(email, owner_name, token);

		  SQliteDAO sqlite_dao = new SQliteDAO();
		  String db_name = sqlite_dao.createOwnerDB(owner_id);

		  if (sended = true)
		  {
			  try
			  {				
				// 暗号化ユーティリティ
				AES aes = new AES();
				byte[] encrypted_password = aes.encrypt(login_password);
				byte[] encrypted_db_name = aes.encrypt(db_name);
	
				// SQLに渡すパラメーター（バイナリのみ対象）
				List<byte[]> params = new ArrayList<byte[]>();
				params.add(encrypted_password);
				  			
				H2dbDao h2db_dao2 = new H2dbDao();
				//h2db_dao2.create_common_db();
			
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
				  	
			}
			catch(Exception ex)
			{
				String trace = ex.toString() + "\n";                     
	
				for (StackTraceElement e1 : ex.getStackTrace()) {
				    trace += "\t at " + e1.toString() + "\n";
				} 
				log.insert_error_log(ex.toString(), trace);
				
				ex.printStackTrace();
			}
		  }
		  else if (sended == false)
		  {
			  return "error";
		  }
		  
		  
	      return "index";
	  }
	  
	  // loginボタン押下の場合
	  @RequestMapping(value="/", method=RequestMethod.POST, params={"login"})
	  public String login(
			  HttpSession session,
			  HttpServletRequest request,
			  // 同じフォームにowner_id入れてもemail入れてもログインできるようにするため
			  @RequestParam("owner_id_or_email") String owner_id_or_email,
			  @RequestParam("login_password") String input_password) 
	  {
			H2dbDao h2db_dao = new H2dbDao();
			
			AES aes = new AES();
			byte[] encrypted_input_password = aes.encrypt(input_password);
			byte[] encrypted_db_name = null;
			
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
			
			LoginInfoModel login_info = new LoginInfoModel();
			
			H2dbDao dao = new H2dbDao();
			login_info = dao.select_login_info(sql);
			
//			System.out.println(login_info.getEncrypted_db_name());
//			System.out.println(aes.decrypt(login_info.getEncrypted_db_name()));
//			System.out.println(login_info.getEncrypted_password());
//			System.out.println(aes.decrypt(login_info.getEncrypted_password()));
//			System.out.println(encrypted_input_password);
//			System.out.println(aes.decrypt(encrypted_input_password));
//			System.out.println(login_info.getOwner_id());
			
			input_password = aes.decrypt(encrypted_input_password);
			String password_in_db = aes.decrypt(login_info.getEncrypted_password());
			encrypted_db_name = login_info.getEncrypted_db_name();
			
			
			String response_url = "/"+ login_info.getOwner_id() + "/main.html";
			
			String owner_db = aes.decrypt(encrypted_db_name);
			//add_is_seikai_to_seitou_tbl(owner_db);
			
			
			/**
			 * アクセスログ記録
			 */
			String owner_id = (String)session.getAttribute("owner_id");
			String request_uri = request.getRequestURI();
			String method_name = new Object(){}.getClass().getEnclosingMethod().getName();
			String client_ip = Log.getClientIpAddress(request);
			String client_os = Log.getClientOS(request);
			String client_browser = Log.getClientBrowser(request);
			Log log = new Log();
			log.insert_access_log(owner_id, request_uri, method_name, client_ip, client_os, client_browser);
			
			if (input_password.equals(password_in_db))
			{
//				// ログインしているかどうか TODO セッションの値を書き換えられてしまわないように対策要
				session.setAttribute("is_authenticated", true);
				session.setAttribute("owner_id", login_info.getOwner_id());
				session.setAttribute("password", login_info.getEncrypted_password());
				
				// セッションに暗号化されたオーナー専用DB名を格納
				session.setAttribute("owner_db", login_info.getEncrypted_db_name());

				return "redirect:" + response_url;
			}
			else
			{
				System.out.println("login_failed");
			    return "index";
			}	
	  }

	public void add_is_seikai_to_seitou_tbl(String db_name) {
		/**
		 * DBパッチ
		 */
		try
		{
			SQliteDAO sqlite_dao = new SQliteDAO();
//			sql.appendLine("alter table seitou ");
//			sql.appendLine("add column seikai_flg integer;");
//			sqlite_dao.update(db_name, sql);
			
			List<SeitouModel> seitou_list = new ArrayList<SeitouModel>();
			SeitouDao seitou_dao = new SeitouDao();
			seitou_list = seitou_dao.select_seitou_list(db_name, seitou_list);
			for (SeitouModel seitou : seitou_list)
			{
				StringBuilderPlus sql = new StringBuilderPlus();
				KaitouDao kaitou_dao = new KaitouDao();
				int seikai_flg = kaitou_dao.is_seikai(db_name, seitou.getS_id());
				sql.appendLine("update seitou set seikai_flg = " + seikai_flg);
				sql.appendLine(" where s_id = '" + seitou.getS_id() + "'");
				sqlite_dao.update(db_name, sql);
				System.out.println(sql.toString());
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	  
}
