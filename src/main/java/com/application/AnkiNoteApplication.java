package com.application;

import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;


import com.common.Log;
import com.dao.H2dbDao;
import com.dao.SQliteDAO;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class AnkiNoteApplication extends SpringBootServletInitializer {

	//public static String LOG = "/usr/local/anki_note/application/spring_log/console.log";
	
	public static void main(String[] args) {
		// 月次ログDBがなければ作成
		SQliteDAO dao = new SQliteDAO();
		dao.create_log_db();
		
		try
		{
			// H2のサーバーを起動
			Server server = Server.createTcpServer("-tcpAllowOthers").start();
			
			H2dbDao h2dao = new H2dbDao();
			h2dao.create_paypal_table();
			h2dao.mondaishu_and_group_patch();
			
		}
		catch(Exception ex)
		{
			Log log = new Log();
			log.insert_error_log(ex.toString(), ex.getMessage());
			ex.printStackTrace();
		}
		        
        SpringApplication.run(AnkiNoteApplication.class, args);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
	{
		return application.sources(AnkiNoteApplication.class);
	}
}
