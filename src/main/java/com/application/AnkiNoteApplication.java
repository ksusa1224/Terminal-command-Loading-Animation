package com.application;

import java.sql.SQLException;

import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import com.slime.SlimeSerif;

@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class AnkiNoteApplication {

	public static void main(String[] args) throws SQLException {
		// H2のサーバーを起動
		Server server = Server.createTcpServer(args).start();
		SpringApplication.run(AnkiNoteApplication.class, args);
	}
}
