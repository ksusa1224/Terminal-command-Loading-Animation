package com.application.controller;

import java.security.SecureRandom;
import java.time.Instant;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.application.controller.dao.KaitouDao;
import com.application.controller.dao.SeitouDao;
import com.application.controller.dao.SystemDao;
import com.application.controller.dao.TagDao;
import com.application.model.LoginInfoModel;
import com.application.model.dao.SeitouModel;
import com.application.model.dao.SystemModel;
import com.application.model.dao.TagModel;
import com.common.AES;
import com.common.Constant;
import com.common.Log;
import com.common.StringBuilderPlus;
import com.common.Util;
import com.dao.H2dbDao;
import com.dao.SQliteDAO;
import com.email.MailSend;

import net.arnx.jsonic.JSON;

@Controller
public class TopPageController {
	  
	  /**
	   * 仮登録から本登録へ
	   * @param session
	   * @param request
	   * @param token
	   * @return
	   */
	  @RequestMapping(value="/register.html", method=RequestMethod.GET)
	  public String register(
			  HttpSession session,
			  HttpServletRequest request,
			  @RequestParam("token") String token)
	  {
		  H2dbDao dao = new H2dbDao();
		  dao.temp_to_register(token);
		  return "register";
	  }

	  @RequestMapping(value="/remind.html", method=RequestMethod.GET)
	  public String remind(
			  HttpSession session,
			  HttpServletRequest request)
	  {
		  return "remind";
	  }
	  
	  @RequestMapping(value="/remind_mail.html", method=RequestMethod.POST)
	  public String remind_mail(
			  HttpSession session,
			  HttpServletRequest request,
			  @RequestParam("email") String email)
	  {
		  AES aes = new AES();
		  H2dbDao dao = new H2dbDao();
		  LoginInfoModel login_info = dao.select_login_info(email);
		  MailSend mail = new MailSend();
		  mail.send_remind_mail(
				  login_info.getOwner_id(), email, login_info.getOwner_name(), 
				  aes.decrypt(login_info.getEncrypted_password()));
		  
		  return "redirect:remind.html?sended=true";
	  }

	  /**
	   * プレミアム会員登録前処理（fromトップページ）
	   * @param session
	   * @param request
	   * @param email
	   * @param owner_id
	   * @param owner_name
	   * @param password
	   * @return
	   */
	  @RequestMapping(value="/regist_premium.html", method=RequestMethod.GET)
	  public @ResponseBody String before_regist_premium(
			  HttpSession session,
			  HttpServletRequest request,
			  HttpServletResponse response,
			  @RequestParam(value="email", required=false) String email,
			  @RequestParam(value="owner_id", required=false) String owner_id,
			  @RequestParam(value="owner_name", required=false) String owner_name,
			  @RequestParam(value="password", required=false) String password)
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
		  // TODO メールアドレスとオーナーIDが紐付いてて重複してる場合は、プラン変更のロジックにする。
		  H2dbDao h2dao = new H2dbDao();
		  Boolean is_email_deplicate = h2dao.is_email_deplicate(email);
		  if (is_email_deplicate == true)
		  {
			  return "email_depricate";
		  }
		  
		  // オーナーID重複チェック
		  Boolean is_owner_id_deplicate = h2dao.is_owner_id_deplicate(owner_id);
		  if (is_owner_id_deplicate == true)
		  {
			  return "owner_id_depricate";
		  }
		  
		  String encoded_email = java.util.Base64.getUrlEncoder()
				  				.encodeToString(email.getBytes());
		  String encoded_owner_id = java.util.Base64.getUrlEncoder()
	  				.encodeToString(owner_id.getBytes());
		  String encoded_owner_name = java.util.Base64.getUrlEncoder()
	  				.encodeToString(owner_name.getBytes());
		  String encoded_password = java.util.Base64.getUrlEncoder()
	  				.encodeToString(password.getBytes());
	        	     
		  String token1 = null;
	        try {
		        String command = "curl -v https://api-3t.paypal.com/nvp -d "
		        		+ "USER=ksusa1224_api1.gmail.com"
		        		+ "&PWD=HQ7TUMJC7EYGRRPM"
		        		+ "&SIGNATURE=AFcWxV21C7fd0v3bYYYRCpSSRl31A3.TJ.pCOcROSkBlSwBUgPQQCVbK"
		        		+ "&METHOD=SetExpressCheckout"
		        		+ "&VERSION=124"
		        		+ "&cancelUrl="
		        		+ "https://ankinote.com/index.html"
		        		+ "&returnUrl="
		        		+ "https://ankinote.com/" + encoded_email + "/" + encoded_owner_id + "/"
		        		+ encoded_owner_name + "/" + encoded_password + "/" + "premium_register.html"
		        		+ "&L_BILLINGTYPE0=RecurringPayments"
		        		+ "&L_BILLINGAGREEMENTDESCRIPTION0=定期支払";

				Process proc = Runtime.getRuntime().exec(command);
	
				// Read the output
		        BufferedReader reader =  
		              new BufferedReader(new InputStreamReader(proc.getInputStream()));
	
		        StringBuilderPlus sb = new StringBuilderPlus();
				
		        String line = "";
		        while((line = reader.readLine()) != null) {
		        	sb.appendLine(line);
		        }
		        
		        proc.waitFor();  
			
		      token1 = java.net.URLDecoder.decode(sb.toString(), "UTF-8");
		      Cookie myCookie =
		    		  new Cookie("token1", token1);
		    		  response.addCookie(myCookie);
		  		//token1 = sb.toString();
		     return "https://www.paypal.com/cgi-bin/webscr?cmd=_express-checkout&useraction=commit&token="+token1;
			} catch (Exception e) {
				e.printStackTrace();
			}

		        
	        //TODO
		  return "";
	  }
	  
	  /**
	   * 新規会員登録（プレミアム会員）
	   * @param email
	   * @param owner_id
	   * @param owner_name
	   * @param owner_password
	   * @param request
	   * @param response
	   * @param session
	   * @param model
	   * @return
	   */
	  @RequestMapping(value="/{email}/{owner_id}/{owner_name}/{owner_password}/premium_register.html",
			  method=RequestMethod.GET)
		public String premium_regist(
				@PathVariable("email") String email,
				@PathVariable("owner_id") String owner_id,
				@PathVariable("owner_name") String owner_name,
				@PathVariable("owner_password") String owner_password,
				@RequestParam(value = "token") String token3, 
				HttpServletRequest request, 
				HttpServletResponse response, 
				HttpSession session,
				Model model) 
		{			
		  String token5 = null;
	        try {
		        String command = "curl -v https://api-3t.paypal.com/nvp -d "
		        		+ "USER=ksusa1224_api1.gmail.com"
		        		+ "&PWD=HQ7TUMJC7EYGRRPM"
		        		+ "&SIGNATURE=AFcWxV21C7fd0v3bYYYRCpSSRl31A3.TJ.pCOcROSkBlSwBUgPQQCVbK"
		        		+ "&METHOD=GetExpressCheckoutDetails"
		        		+ "&VERSION=124"
		        		+ "&TOKEN=" + token3;

				Process proc = Runtime.getRuntime().exec(command);
	
				// Read the output
		        BufferedReader reader =  
		              new BufferedReader(new InputStreamReader(proc.getInputStream()));
	
		        StringBuilderPlus sb = new StringBuilderPlus();
				
		        String line = "";
		        while((line = reader.readLine()) != null) {
		        	sb.appendLine(line);
		        }
		        
		        System.out.println(sb.toString());
		        
		        proc.waitFor();  
			
		      token5 = java.net.URLDecoder.decode(sb.toString(), "UTF-8");
//		        System.out.println(Util.getUtcNow() 
//		        		+ "T" + Util.getUtcNow() + "Z");
		      System.out.println(Instant.now().toString());

		      System.out.println("token5:    =="+token5);
		     // return "redirect:https://www.paypal.com/cgi-bin/webscr?cmd=_express-checkout&useraction=commit&token=" + token5;
		  				
			} catch (Exception e) {
				e.printStackTrace();
			}
	        
		  
		  String token2 = null;
	        try {
		  		//System.out.println(token1);
		        String command = "curl -v https://api-3t.paypal.com/nvp -d "
		        		+ "USER=ksusa1224_api1.gmail.com"
		        		+ "&PWD=HQ7TUMJC7EYGRRPM"
		        		+ "&SIGNATURE=AFcWxV21C7fd0v3bYYYRCpSSRl31A3.TJ.pCOcROSkBlSwBUgPQQCVbK"
		        		+ "&METHOD=CreateRecurringPaymentsProfile"
		        		+ "&VERSION=124"
		        		+ "&PROFILESTARTDATE=" + Instant.now() 
		        		+ "&AMT=500"
		        		+ "&CURRENCYCODE=JPY"
		        		+ "&BILLINGPERIOD=Month"
		        		+ "&BILLINGFREQUENCY=1"
		        		+ "&AUTOBILLOUTAMT=AddToNextBilling"
		        		+ "&DESC=定期支払"
		        		+ "&TOKEN=" + token5.replace("TOKEN=", "").split("&")[0];
		        //System.out.println(token1.split("&")[0]);

				Process proc = Runtime.getRuntime().exec(command);
	
				// Read the output
		        BufferedReader reader =  
		              new BufferedReader(new InputStreamReader(proc.getInputStream()));
	
		        StringBuilderPlus sb = new StringBuilderPlus();
				
		        String line = "";
		        while((line = reader.readLine()) != null) {
		        	sb.appendLine(line);
		        }
		        
		        System.out.println(sb.toString());
		        
		        proc.waitFor();  
			
		      token2 = java.net.URLDecoder.decode(sb.toString(), "UTF-8");
//		        System.out.println(Util.getUtcNow() 
//		        		+ "T" + Util.getUtcNow() + "Z");
		      System.out.println(Instant.now().toString());

		      System.out.println("token2:    =="+token2);
		      //return "redirect:https://www.paypal.com/cgi-bin/webscr?cmd=_express-checkout&useraction=commit&token=" + token2;
		  				
			} catch (Exception e) {
				e.printStackTrace();
			}
		  
	        String payer_id = token5.split("PAYERID=")[1].split("&")[0];
	        System.out.println("payerid:::"+payer_id);
		  String token6 = null;
	        try {
		  		//System.out.println(token1);
		        String command = "curl -v https://api-3t.paypal.com/nvp -d "
		        		+ "USER=ksusa1224_api1.gmail.com"
		        		+ "&PWD=HQ7TUMJC7EYGRRPM"
		        		+ "&SIGNATURE=AFcWxV21C7fd0v3bYYYRCpSSRl31A3.TJ.pCOcROSkBlSwBUgPQQCVbK"
		        		+ "&METHOD=DoExpressCheckoutPayment"
		        		+ "&VERSION=124"
		        		+ "&PAYMENTREQUEST_0_PAYMENTACTION=Sale"
		        		+ "&PAYMENTREQUEST_0_AMT=500"
		        		+ "&PAYMENTREQUEST_0_CURRENCYCODE=JPY"
		        		+ "&TOKEN=" + token3
		        		+ "&PAYERID=" + payer_id;
		        //System.out.println(token1.split("&")[0]);

				Process proc = Runtime.getRuntime().exec(command);
	
				// Read the output
		        BufferedReader reader =  
		              new BufferedReader(new InputStreamReader(proc.getInputStream()));
	
		        StringBuilderPlus sb = new StringBuilderPlus();
				
		        String line = "";
		        while((line = reader.readLine()) != null) {
		        	sb.appendLine(line);
		        }
		        
		        System.out.println(sb.toString());
		        
		        proc.waitFor();  
			
		      token6 = java.net.URLDecoder.decode(sb.toString(), "UTF-8");
//				        System.out.println(Util.getUtcNow() 
//				        		+ "T" + Util.getUtcNow() + "Z");

		      System.out.println("token6:    =="+token6);
//		      return "redirect:https://www.paypal.com/cgi-bin/webscr?cmd=_express-checkout&useraction=commit&token=" + token2;
		  				
			} catch (Exception e) {
				e.printStackTrace();
			}

		        
			email = new String(Base64.getUrlDecoder().decode(email));
			owner_id = new String(Base64.getUrlDecoder().decode(owner_id));
			owner_name = new String(Base64.getUrlDecoder().decode(owner_name));
			owner_password = new String(Base64.getUrlDecoder().decode(owner_password));
			
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
		  TagDao tag_dao = new TagDao();
		  tag_dao.add_system_tags(db_name, owner_id);
		  
		  if (sended = true)
		  {
			  try
			  {				
				// 暗号化ユーティリティ
				AES aes = new AES();
				byte[] encrypted_password = aes.encrypt(owner_password);
				byte[] encrypted_db_name = aes.encrypt(db_name);
	
				// SQLに渡すパラメーター（バイナリのみ対象）
				List<byte[]> params = new ArrayList<byte[]>();
				params.add(encrypted_password);
				  			
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
				sql.appendLine("  token,");
				sql.appendLine("  insert_date,");
				sql.appendLine("  update_date)");
				sql.appendLine("values(");
				sql.appendLine("  '" + owner_id + "',");
				sql.appendLine("  '" + owner_name + "',");
				sql.appendLine("  '" + email + "',");
				sql.appendLine("  ?,"); // Password
				sql.appendLine("  '" + Constant.KAKIN_TYPE_PREMIUM_TEMPORARY + "',");
				sql.appendLine("  0,");
				sql.appendLine("  '" + token + "',");
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
			  return "send_error";
		  }
		  
	      return "redirect:../../../../index.html?register_mail=sended";
	  }	  
	  
	  
	/**
	   * 新規会員登録（無料会員）
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
				sql.appendLine("  token,");
				sql.appendLine("  insert_date,");
				sql.appendLine("  update_date)");
				sql.appendLine("values(");
				sql.appendLine("  '" + owner_id + "',");
				sql.appendLine("  '" + owner_name + "',");
				sql.appendLine("  '" + email + "',");
				sql.appendLine("  ?,"); // Password
				sql.appendLine("  '" + Constant.KAKIN_TYPE_TEMPORARY + "',");
				sql.appendLine("  0,");
				sql.appendLine("  '" + token + "',");
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
		  
		  
	      return "redirect:index.html?register_mail=sended";
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
			
			//h2db_dao.createTablesforOwnerDB();
			
			AES aes = new AES();
			byte[] encrypted_input_password = aes.encrypt(input_password);
			byte[] encrypted_db_name = null;
			
			LoginInfoModel login_info = new LoginInfoModel();
			
			H2dbDao dao = new H2dbDao();
			login_info = dao.select_login_info(owner_id_or_email);
			
			// DBパッチ　サーバに入れたのでコメントアウト
			//dao.alter_common_db_add_token();
			
			// 仮登録の場合
			if (login_info.getKakin_type() == 
					Integer.valueOf(Constant.KAKIN_TYPE_TEMPORARY) ||
				login_info.getKakin_type() == 
					Integer.valueOf(Constant.KAKIN_TYPE_PREMIUM_TEMPORARY) ) 
			{
				return "redirect:index.html?type=temporary";
			}
			
			System.out.println(login_info.getOwner_id());
			
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
			
			// DBパッチ（システム付箋登録）
			TagDao tag_dao = new TagDao();
			tag_dao.add_system_tags(owner_db, owner_id);
			
			insert_system_initial_data(owner_db, owner_id);
			
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
	   * コンタクトフォーム押下の場合
	   * @param session
	   * @param request
	   * @param owner_id
	   * @param login_password
	   * @param owner_name
	   * @param email
	   * @return
	   */
	  @RequestMapping(value="/", method=RequestMethod.POST, params={"contact"})
	  public String contact(
			  HttpSession session,
			  HttpServletRequest request,
			  @RequestParam("name") String name,
			  @RequestParam("email") String email,
			  @RequestParam("content") String content)
	  {
		  MailSend mail_send = new MailSend();
		  mail_send.send_contact_mail(name, email, content);
		  return "index";
	  }

	  /**
	   * SSL証明書用テキストファイル
	   * @param response
	   * @return
	   */
	@RequestMapping(value = "/AC228EEA2E1AED85FDE07AF3F3DF3BB7.txt",method = RequestMethod.GET)
	@ResponseBody
	public String plaintext(HttpServletResponse response) {
	  response.setContentType("text/plain");
	  response.setCharacterEncoding("UTF-8");
	  StringBuilderPlus sb = new StringBuilderPlus();
	  sb.appendLine("35DEDB577526F01F69616B8AF55127DF7B904671");
	  sb.appendLine("comodoca.com");
	  return sb.toString();
	}	  
	  
	public void insert_system_initial_data(String owner_db, String owner_id) {
		SystemDao system_dao = new SystemDao();
		SystemModel system = new SystemModel();
		// 行番号
		system.setRow_no(system_dao.get_max_row_no(owner_db)+1);
		// 設定ID
		String sys_id = system.generate_sys_id(system_dao.get_max_row_no(owner_db) + 1, owner_id);
		system.setSys_id(sys_id);
		// 項目グループID
		system.setSys_group_id("0001");
		// 項目グループ名
		system.setSys_group_name("デフォルトソート順");
		// キー
		system.setKey("デフォルトソート順");
		// 値
		system.setValue(Constant.SORT_NEWEST);
		// 削除フラグ
		system.setDel_flg(0);
		// 作成者
		system.setCreate_owner(owner_id);
		// 更新者
		system.setUpdate_owner(owner_id);
		// レコード作成日時（H2DBのtimestampと同じフォーマットにする）
		system.setCreate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
		// レコード更新日時（H2DBのtimestampと同じフォーマットにする）
		system.setUpdate_timestamp(Util.getNow(Constant.DB_DATE_FORMAT));
//		if (system_dao.check_deplicate(
//				owner_db, 
//				system.getSys_group_id(), 
//				system.getKey()
//			) == false)
//		{
//			system_dao.insert_system(owner_db, system);
//		}
		system_dao.insert_system(owner_db, system);
	}
	  
	/**
	 * ログインしている状態かチェックする
	 * @return
	 */
    public boolean isLogin (HttpServletRequest request)
    {
		String last_token_cookie = null;
		String last_token_db = null;
		try{
			H2dbDao dao = new H2dbDao();
			Cookie[] cookies = request.getCookies();
	
			// 前回トークンを取得
			if (cookies != null) {
			 for (Cookie cookie : cookies) {
			   if (cookie.getName().equals("ankinote")) {
				   last_token_cookie = cookie.getValue();
			    }
			  }
			}
			last_token_db = dao.get_last_token(last_token_cookie);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		System.out.println("last_token_cookie:"+last_token_cookie);
		System.out.println("last_token_db"+last_token_db);
		
		if ((last_token_cookie != null && last_token_db != null &&
			last_token_cookie.equals(last_token_db)))
		{
			return true;
		}
		else
		{
	    	return false;			
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
		if (owner_id_or_email.equals("sample"))
		{
		    session.setAttribute("is_authenticated", true);
			return true;
		}
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
		    String owner_id = null;
//		    if (owner_id.equals("sample") == false)		    	
//		    {
		    	owner_id = (String)session.getAttribute("owner_id");
//		    }
		    LoginInfoModel login_info = dao.select_login_info(owner_id);
		    System.out.println(login_info.getOwner_id()+"login_info.getOwner_id()");
		    if (owner_id.equals("sample") == false)
		    {
		    	session.setAttribute("owner_db", login_info.getEncrypted_db_name());
		    }
		    session.setAttribute("owner_id", login_info.getOwner_id());
		    session.setAttribute("owner_name", login_info.getOwner_name());
		    System.out.println(login_info.getOwner_name()+"login_info.getOwner_name()");
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

	/**
	 * emailとowner_idの組み合わせがすでに存在するかチェック
	 * @param email
	 * @param owner_id
	 * @return
	 */
	@RequestMapping(value={"/is_email_and_owner_id_exists.html"}, method=RequestMethod.GET)
	public @ResponseBody String check_email_and_owner_id_exists(
			@RequestParam(value = "email", required=false) String email,
			@RequestParam(value = "owner_id", required=false) String owner_id) {
		H2dbDao dao = new H2dbDao();
		String owner_name = null;
		String kakin_type = null;
		String[] owner_name_kakin_type = dao.email_and_owner_id_exists(email, owner_id);
		if (owner_name_kakin_type != null)
		{
			owner_name = owner_name_kakin_type[0];
			kakin_type = owner_name_kakin_type[1];
		}
		
		String json = JSON.encode(
				new String[] 
				{owner_name, kakin_type});
				
		return json;
	}		
}
