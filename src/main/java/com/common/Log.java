package com.common;

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
	public void insert_access_log(
			String owner_id,
			String request_uri,
			String method_name,
			String client_ip, 
			String client_os, 
			String client_browser)
	{
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("insert into access_log (");
		sql.appendLine("timestamp,");
		sql.appendLine("owner_id,");
		sql.appendLine("request_uri,");
	    sql.appendLine("method_name,");
		sql.appendLine("client_ip,");
		sql.appendLine("client_os,");
		sql.appendLine("client_browser) ");
		sql.appendLine("values (");
		sql.appendLine("'" + timestamp + "',");
		sql.appendLine("'" + owner_id + "',");
		sql.appendLine("'" + request_uri + "',");
		sql.appendLine("'" + method_name + "',");
		sql.appendLine("'" + client_ip + "',");
		sql.appendLine("'" + client_os + "',");
		sql.appendLine("'" + client_browser + "')");
		
		String now = Util.getNow("yyyy_MM");
		String db_name = "system_log" + now + ".db";
		String db_save_path = Constant.SQLITE_LOG_FOLDER_PATH;

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
		StringBuilderPlus sql = new StringBuilderPlus();
		sql.appendLine("insert into error_log (");
		sql.appendLine("timestamp,");
		sql.appendLine("error_type,");
		sql.appendLine("stack_trace) ");
		sql.appendLine("values (");
		sql.appendLine("'" + timestamp + "',");
		sql.appendLine("'" + error_type + "',");
		sql.appendLine("'" + stack_trace + "')");
		
		String now = Util.getNow("yyyy_MM");
		String db_name = "system_log" + now + ".db";
		String db_save_path = Constant.SQLITE_LOG_FOLDER_PATH;

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
	
	public static String getClientOS(HttpServletRequest request)
	{
	    String  browserDetails  =   request.getHeader("User-Agent");
	    String  userAgent       =   browserDetails;

	    String os = "";

	    //=================OS=======================
	     if (userAgent.toLowerCase().indexOf("windows") >= 0 )
	     {
	         os = "Windows";
	     } else if(userAgent.toLowerCase().indexOf("mac") >= 0)
	     {
	         os = "Mac";
	     } else if(userAgent.toLowerCase().indexOf("x11") >= 0)
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
	        browser="IE";
	    } else
	    {
	        browser = "UnKnown, More-Info: "+userAgent;
	    }
	    return browser;
		
	}
}
