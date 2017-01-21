package com.application.controller;

import java.security.SecureRandom;

import static org.mockito.Matchers.byteThat;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.application.controller.dao.KaitouDao;
import com.application.controller.dao.SeitouDao;
import com.application.controller.dao.TagDao;
import com.application.model.LoginInfoModel;
import com.application.model.dao.SeitouModel;
import com.application.model.dao.TagModel;
import com.common.AES;
import com.common.Constant;
import com.common.Log;
import com.common.StringBuilderPlus;
import com.common.Util;
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

		  // メールアドレス重複チェック
		  H2dbDao h2dao = new H2dbDao();
		  Boolean is_email_deplicate = h2dao.is_email_deplicate(email);
		  if (is_email_deplicate == true)
		  {
			  return "redirect:index.html";
		  }
		  // オーナーID重複チェック
		  Boolean is_owner_id_deplicate = h2dao.is_owner_id_deplicate(owner_id);
		  if (is_owner_id_deplicate == true)
		  {
			  return "redirect:index.html";
		  }				  
			
		  // 仮登録用のワンタイムパスワード
		  UUID uuid = UUID.randomUUID();
		  String token = uuid.toString();
		  MailSend mail_send = new MailSend();
		  Boolean sended = mail_send.send_register_mail(email, owner_name, token);

		  SQliteDAO sqlite_dao = new SQliteDAO();
		  String db_name = sqlite_dao.createOwnerDB(owner_id);
			TagDao tag_dao = new TagDao();
			tag_dao.add_system_tags(db_name, owner_id);

		  
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
			  @RequestParam("login_password") String input_password,
			  HttpServletResponse response) 
	  {
			H2dbDao h2db_dao = new H2dbDao();
			
			AES aes = new AES();
			byte[] encrypted_input_password = aes.encrypt(input_password);
			byte[] encrypted_db_name = null;
			
			LoginInfoModel login_info = new LoginInfoModel();
			
			H2dbDao dao = new H2dbDao();
			login_info = dao.select_login_info(owner_id_or_email);
			
			// DBパッチ　サーバに入れたのでコメントアウト
			//dao.alter_common_db_add_token();
			
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
			
//			TagDao tag_dao = new TagDao();
//			tag_dao.add_system_tags(owner_db, owner_id);
			
			if (input_password.equals(password_in_db))
			{
//				// ログインしているかどうか TODO セッションの値を書き換えられてしまわないように対策要
				session.setAttribute("is_authenticated", true);
				session.setAttribute("owner_id", login_info.getOwner_id());
//				session.setAttribute("password", login_info.getEncrypted_password());
				
				// セッションに暗号化されたオーナー専用DB名を格納
				session.setAttribute("owner_db", login_info.getEncrypted_db_name());

				// Cookieから自動ログインするためのトークンをCookieおよびH2に保存
				setAutoLoginToken(owner_id_or_email, session, request, response);
				
				return "redirect:" + response_url;
			}
			else
			{
				System.out.println("login_failed");
			    return "index";
			}	
	  }
	  
	/**
	 * Cookieから自動ログインするためのトークンをCookieおよびH2に保存
	 */
	public boolean setAutoLoginToken(
			String owner_id_or_email, 
			HttpSession session,
			HttpServletRequest request,
			HttpServletResponse response)
	{
		try{
			H2dbDao dao = new H2dbDao();
			Cookie[] cookies = request.getCookies();
			String last_token_cookie = null;
	
			// 前回トークンを取得
			if (cookies != null) {
			 for (Cookie cookie : cookies) {
			   if (cookie.getName().equals("ankinote")) {
				   last_token_cookie = cookie.getValue();
			    }
			  }
			}
			String last_token_db = dao.get_last_token(last_token_cookie);
			if (owner_id_or_email == null && last_token_cookie != last_token_db)
			{
				return false;
			}
			
			String new_token = getToken();
		    final String cookieName = "ankinote";
		    final String cookieValue = new_token;  // you could assign it some encoded value
		    final Boolean useSecureCookie = new Boolean(false);
			// TODO Cookieの有効期限を無期限にする
		    final int expiryTime = 10 * 365 * 24 * 60 * 60 ;  // 10年
		    final String cookiePath = "/";
	
		    Cookie new_cookie = new Cookie(cookieName, cookieValue);
			// CookieのHttpOnly 属性を有効にする
		    new_cookie.isHttpOnly();	    
			// TODO Cookieのsecure 属性を有効にする（HTTPSに対応するまでは不可能）
		    new_cookie.setSecure(useSecureCookie.booleanValue());  // determines whether the cookie should only be sent using a secure protocol, such as HTTPS or SSL
		    // A negative value means that the cookie is not stored persistently and will be deleted when the Web browser exits. A zero value causes the cookie to be deleted.
		    new_cookie.setMaxAge(expiryTime);  
		    // The cookie is visible to all the pages in the directory you specify, and all the pages in that directory's subdirectories
		    new_cookie.setPath(cookiePath);  
			// tokenをCookieに書き込む
		    response.addCookie(new_cookie);
	
			// tokenをH2に入れる
		    dao.update_token(owner_id_or_email, last_token_cookie, new_token);
		    LoginInfoModel login_info = dao.select_login_info(owner_id_or_email);
		    session.setAttribute("owner_db", login_info.getEncrypted_db_name());
		    session.setAttribute("owner_id", login_info.getOwner_id());
		    session.setAttribute("owner_name", login_info.getOwner_name());
		    session.setAttribute("is_authenticated", true);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return true;
	}
	
	/**
	 * トークンを作成する
	 * @return
	 */
	private String getToken() {
		SecureRandom secure_random = new SecureRandom();
		return new BigInteger(130, secure_random).toString(32);
	}

	/**
	 * DBパッチ
	 */
	public void add_is_seikai_to_seitou_tbl(String db_name) {
		try
		{
			SQliteDAO sqlite_dao = new SQliteDAO();
			
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
	
	/**
	 * メールアドレス重複登録チェック
	 * @param email
	 * @return
	 */
	@RequestMapping(value={"/is_email_deplicate.html"}, method=RequestMethod.GET)
	public @ResponseBody String check_email_deplicate(
			@RequestParam(value = "email", required=false) String email) {
		String is_deplicate = "not_deplicate";
		H2dbDao dao = new H2dbDao();
		if (dao.is_email_deplicate(email))
		{
			is_deplicate = "deplicate";
		}
		
		return is_deplicate;
	}

	/**
	 * オーナーID重複チェック
	 * @param owner_id
	 * @return
	 */
	@RequestMapping(value={"/is_owner_id_deplicate.html"}, method=RequestMethod.GET)
	public @ResponseBody String check_owner_id_deplicate(
			@RequestParam(value = "owner_id", required=false) String owner_id) {
		String is_deplicate = "not_deplicate";
		H2dbDao dao = new H2dbDao();
		if (dao.is_owner_id_deplicate(owner_id))
		{
			is_deplicate = "deplicate";
		}
		
		return is_deplicate;
	}		
}
