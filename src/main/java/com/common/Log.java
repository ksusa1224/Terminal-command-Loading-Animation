package com.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;

import com.dao.SQliteDAO;

import lombok.Data;

public @Data class Log {

	// タイムスタンプ
	private String timestamp = Util.getNow(Constant.LOG_DB_DATE_FORMAT);
	
	// オーナーID
	private String owner_id;
	
	// リクエストURI
	private String request_uri;
	
    // メソッド名
    private String method_name;

	// IPアドレス
	private String client_ip;
	
	// OS
	private String client_os;
	
	// ブラウザ
	private String client_browser;

	// エラー種別
    private String error_type;
    
    // スタックトレース
    private String stack_trace;
		
    /**
     * 
     * @param owner_id
     * @param request_uri
     * @param client_ip
     * @param client_os
     * @param cliente_browser
     */
//	public void insert_access_log(
//			String owner_id,
//			String request_uri,
//			String method_name,
//			String client_ip, 
//			String client_os, 
//			String client_browser,
//			String client_location)
//	{
//		String now = Util.getNow("yyyy_MM");
//		String db_name = "system_log_" + now + ".db";
//		String db_save_path = Constant.SQLITE_LOG_FOLDER_PATH;
//		File monthly_db = new File(db_save_path + db_name);
//		if(monthly_db.exists() == false) { 
//		    SQliteDAO dao = new SQliteDAO();
//		    dao.create_log_db();
//		}		
//		
//		StringBuilderPlus sql = new StringBuilderPlus();
//		sql.appendLine("insert into access_log (");
//		sql.appendLine("timestamp,");
//		sql.appendLine("owner_id,");
//		sql.appendLine("request_uri,");
//	    sql.appendLine("method_name,");
//		sql.appendLine("client_ip,");
//		sql.appendLine("client_os,");
//		sql.appendLine("client_browser, ");
//		sql.appendLine("client_location) ");
//		sql.appendLine("values (");
//		sql.appendLine("'" + timestamp + "',");
//		sql.appendLine("'" + owner_id + "',");
//		sql.appendLine("'" + request_uri + "',");
//		sql.appendLine("'" + method_name + "',");
//		sql.appendLine("'" + client_ip + "',");
//		sql.appendLine("'" + client_os + "',");
//		sql.appendLine("'" + client_browser + "',");
//		sql.appendLine("'" + client_location + "')");
//		
//		SQliteDAO dao = new SQliteDAO();
//		dao.update_log_db(sql);
//	}
	
	public void insert_access_log(
			HttpServletRequest request,
			String owner_id,
			String method_name)
	{
		String now = Util.getNow("yyyy_MM");
		String db_name = "system_log_" + now + ".db";
		String db_save_path = Constant.SQLITE_LOG_FOLDER_PATH;
		File monthly_db = new File(db_save_path + db_name);
		if(monthly_db.exists() == false) { 
		    SQliteDAO dao = new SQliteDAO();
		    dao.create_log_db();
		}		
		
		String request_uri = request.getRequestURI();
		String client_ip = Log.getClientIpAddress(request);
		String client_os = Log.getClientOS(request);
		String client_browser = Log.getClientBrowser(request);
		String client_location = "";
		
		try {
			client_location = Log.getClientLocation(request);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String access_from = request.getHeader("Referer");
		InetAddress addr = null;
		try {
			addr = InetAddress.getByName(client_ip);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String host_name = addr.getHostName();
		
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("insert into access_log (");
		sql.appendLine("timestamp,");
		sql.appendLine("owner_id,");
		sql.appendLine("request_uri,");
	    sql.appendLine("method_name,");
		sql.appendLine("client_ip,");
		sql.appendLine("client_os,");
		sql.appendLine("client_browser, ");
		sql.appendLine("client_location, ");
		sql.appendLine("host_name, ");
		sql.appendLine("access_from) ");
		sql.appendLine("values (");
		sql.appendLine("'" + timestamp + "',");
		sql.appendLine("'" + owner_id + "',");
		sql.appendLine("'" + request_uri + "',");
		sql.appendLine("'" + method_name + "',");
		sql.appendLine("'" + client_ip + "',");
		sql.appendLine("'" + client_os + "',");
		sql.appendLine("'" + client_browser + "',");
		sql.appendLine("'" + client_location + "',");
		sql.appendLine("'" + host_name + "',");
		sql.appendLine("'" + access_from + "')");
		
		SQliteDAO dao = new SQliteDAO();
		dao.update_log_db(sql);
	}
	
	/**
	 * 
	 * @param error_type
	 * @param stack_trace
	 */
	public void insert_error_log(
			String error_type,
			String stack_trace)
	{
		String now = Util.getNow("yyyy_MM");
		String db_name = "system_log_" + now + ".db";
		String db_save_path = Constant.SQLITE_LOG_FOLDER_PATH;
		File monthly_db = new File(db_save_path + db_name);
		if(monthly_db.exists() == false) { 
		    SQliteDAO dao = new SQliteDAO();
		    dao.create_log_db();
		}		

		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("insert into error_log (");
		sql.appendLine("timestamp,");
		sql.appendLine("error_type,");
		sql.appendLine("stack_trace) ");
		sql.appendLine("values (");
		sql.appendLine("'" + timestamp + "',");
		sql.appendLine("'" + error_type + "',");
		sql.appendLine("'" + stack_trace + "')");
		
		SQliteDAO dao = new SQliteDAO();
		dao.update_log_db(sql);
	}
	
	private static final String[] HEADERS_TO_TRY = { 
		    "X-Forwarded-For",
		    "Proxy-Client-IP",
		    "WL-Proxy-Client-IP",
		    "HTTP_X_FORWARDED_FOR",
		    "HTTP_X_FORWARDED",
		    "HTTP_X_CLUSTER_CLIENT_IP",
		    "HTTP_CLIENT_IP",
		    "HTTP_FORWARDED_FOR",
		    "HTTP_FORWARDED",
		    "HTTP_VIA",
		    "REMOTE_ADDR" };	
	
	public static String getClientIpAddress(HttpServletRequest request) {
	    for (String header : HEADERS_TO_TRY) {
	        String ip = request.getHeader(header);
	        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
	            return ip;
	        }
	    }
	    return request.getRemoteAddr();
	}
	
	/**
	 * IPアドレスからおおよその住所を割り出す
	 * @param request
	 * @return
	 * @throws IOException 
	 */
	public static String getClientLocation(HttpServletRequest request) throws IOException 
	{
		String ip = getClientIpAddress(request);
		URL url = new URL("http://freegeoip.net/csv/" + ip);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.connect();

		InputStream is = connection.getInputStream();

		int status = connection.getResponseCode();
		if (status != 200) {
		    return null;
		}

		String location = "";
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		for (String line; (line = reader.readLine()) != null;) 
		{
			location = location + line;
		    //this API call will return something like:
//		    "2.51.255.200","AE","United Arab Emirates","03","Dubai","Dubai","","x-coord","y-coord","","";
		    // you can extract whatever you want from it
		}	
		return location;
	}
	
	public static String getClientOS(HttpServletRequest request)
	{
	    String  browserDetails  =   request.getHeader("User-Agent");
	    String  userAgent       =   browserDetails;

	    String os = "";

	     if (userAgent.toLowerCase().indexOf("windows") >= 0 )
	     {
	 	    if (userAgent.indexOf("Win16") >= 0)
	 	    {
	 	    	os = "Windows 3.11";
	 	    }
	 	    else if (userAgent.indexOf("Windows 95") >= 0 ||
	 	    		userAgent.indexOf("Win95") >= 0 ||
	 	    		userAgent.indexOf("Windows_95") >= 0) 
	 	    {
	 	    	os = "Windows 95";	 	    	
	 	    }
	 	    else if (userAgent.indexOf("Windows 98") >= 0 ||
	 	    		userAgent.indexOf("Win98") >= 0)
	 	    {
	 	    	os = "Windows 98";	 	    	
	 	    }
	 	    else if (userAgent.indexOf("Windows NT 5.0") >= 0 ||
	 	    		userAgent.indexOf("Windows 2000") >= 0)
	 	    {
	 	    	os = "Windows 2000";	 	    	
	 	    }
	 	    else if (userAgent.indexOf("Windows NT 5.1") >= 0 ||
	 	    		userAgent.indexOf("Windows XP") >= 0)
	 	    {
	 	    	os = "Windows XP";	 	    	
	 	    }
	 	    else if (userAgent.indexOf("Windows NT 5.2") >= 0) 
	 	    {
	 	    	os = "Windows Server 2003";	 	    	
	 	    }
	 	    else if (userAgent.indexOf("Windows NT 6.0") >= 0) 
	 	    {
	 	    	os = "Windows Vista";	 	    	
	 	    }
	 	    else if (userAgent.indexOf("Windows NT 6.1") >= 0) 
	 	    {
	 	    	os = "Windows 7";	 	    	
	 	    }
	 	    else if (userAgent.indexOf("Windows NT 6.2") >= 0) 
	 	    {
	 	    	os = "Windows 8";	 	    	
	 	    }
	 	    else if (userAgent.indexOf("Windows NT 10.0") >= 0) 
	 	    {
	 	    	os = "Windows 10";	 	    	
	 	    }
	 	    else if (userAgent.indexOf("Windows NT 4.0") >= 0 ||
	 	    		userAgent.indexOf("WinNT4.0") >= 0 ||
	 	    		userAgent.indexOf("WinNT") >= 0 ||
	 	    		userAgent.indexOf("Windows NT") >= 0) 
	 	    {
	 	    	os = "Windows NT 4.0";	 	    	
	 	    }
	 	    else if (userAgent.indexOf("Windows ME") >= 0) 
	 	    {
	 	    	os = "Windows ME";	 	    	
	 	    }
	     } else if(userAgent.indexOf("Mac_PowerPC") >= 0 ||
	    		 userAgent.indexOf("Macintosh") >= 0)
	     {
	         os = "Mac";
	     } else if(userAgent.indexOf("OpenBSD") >= 0)
	     {
	         os = "Open BSD";
	     } else if(userAgent.indexOf("SunOS") >= 0)
	     {
	         os = "Sun OS";
	     } else if(userAgent.indexOf("QNX") >= 0)
	     {
	         os = "QNX";
	     } else if(userAgent.indexOf("BeOS") >= 0)
	     {
	         os = "BeOS";
	     } else if(userAgent.indexOf("OS/2") >= 0)
	     {
	         os = "OS/2";
	     } else if(userAgent.toLowerCase().indexOf("x11") >= 0 ||
	    		 userAgent.toLowerCase().indexOf("Linux") >= 0)
	     {
	         os = "Unix";
	     } else if(userAgent.toLowerCase().indexOf("android") >= 0)
	     {
	         os = "Android";
	     } else if(userAgent.toLowerCase().indexOf("iphone") >= 0)
	     {
	         os = "IPhone";
	     }else{
	         os = "UnKnown, More-Info: "+userAgent;
	     }
	     return os;
	}
	
	public static String getClientBrowser(HttpServletRequest request)
	{
	    String  browserDetails  =   request.getHeader("User-Agent");
	    String  userAgent       =   browserDetails;
	    String  browser = "";
	    String  user            =   userAgent.toLowerCase();

	    if (user.contains("msie"))
	    {
	        String substring=userAgent.substring(userAgent.indexOf("MSIE")).split(";")[0];
	        browser=substring.split(" ")[0].replace("MSIE", "IE")+"-"+substring.split(" ")[1];
	    } else if (user.contains("safari") && user.contains("version"))
	    {
	        browser=(userAgent.substring(userAgent.indexOf("Safari")).split(" ")[0]).split("/")[0]+"-"+(userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
	    } else if ( user.contains("opr") || user.contains("opera"))
	    {
	        if(user.contains("opera"))
	            browser=(userAgent.substring(userAgent.indexOf("Opera")).split(" ")[0]).split("/")[0]+"-"+(userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
	        else if(user.contains("opr"))
	            browser=((userAgent.substring(userAgent.indexOf("OPR")).split(" ")[0]).replace("/", "-")).replace("OPR", "Opera");
	    } else if (user.contains("chrome"))
	    {
	        browser=(userAgent.substring(userAgent.indexOf("Chrome")).split(" ")[0]).replace("/", "-");
	    } else if ((user.indexOf("mozilla/7.0") > -1) || (user.indexOf("netscape6") != -1)  || (user.indexOf("mozilla/4.7") != -1) || (user.indexOf("mozilla/4.78") != -1) || (user.indexOf("mozilla/4.08") != -1) || (user.indexOf("mozilla/3") != -1) )
	    {
	        //browser=(userAgent.substring(userAgent.indexOf("MSIE")).split(" ")[0]).replace("/", "-");
	        browser = "Netscape-?";

	    } else if (user.contains("firefox"))
	    {
	        browser=(userAgent.substring(userAgent.indexOf("Firefox")).split(" ")[0]).replace("/", "-");
	    } else if(user.contains("rv"))
	    {
	        browser="IE" + user.substring(user.indexOf("rv") + 3, user.indexOf(")"));
	    } else
	    {
	        browser = "UnKnown, More-Info: "+userAgent;
	    }
	    return browser;
		
	}
	
	/**
	 * 
	 * @param ex
	 * @return
	 */
	public String ex_to_string (Exception ex)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		pw.flush();
		String str = sw.toString();
		return str;
	}
}
