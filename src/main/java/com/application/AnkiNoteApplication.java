package com.application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.dao.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class AnkiNoteApplication {

	public static void main(String[] args) {

		SQliteDAO sqlite_dao = new SQliteDAO();
		sqlite_dao.createSQliteDB();

		SpringApplication.run(AnkiNoteApplication.class, args);
	}
}
