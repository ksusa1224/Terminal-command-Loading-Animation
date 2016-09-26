package com.application;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.SQLException;

import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import com.common.SystemOutToLog;
import com.slime.SlimeSerif;

@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class AnkiNoteApplication {

	public static String LOG = "/usr/local/anki_note/application/spring_log/console.log";
	
	public static void main(String[] args) throws SQLException {
		// H2のサーバーを起動
		Server server = Server.createTcpServer(args).start();
		
        try {
            // 上記 MyPrintStream を生成して setOut.
            System.setOut( new SystemOutToLog(LOG) );
        } catch (Exception e) {
            e.printStackTrace();
        }
        SpringApplication.run(AnkiNoteApplication.class, args);
	}
}
