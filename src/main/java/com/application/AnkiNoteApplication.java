package com.application;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.SQLException;

import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import com.common.Log;
import com.common.SystemOutToLog;
import com.dao.SQliteDAO;
import com.slime.SlimeSerif;

@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class AnkiNoteApplication {

	//public static String LOG = "/usr/local/anki_note/application/spring_log/console.log";
	
	public static void main(String[] args) throws SQLException {
		// H2のサーバーを起動
		Server server = Server.createTcpServer(args).start();
		
		// 月次ログDBがなければ作成
		SQliteDAO dao = new SQliteDAO();
		dao.create_log_db();
		
        SpringApplication.run(AnkiNoteApplication.class, args);
	}
}
