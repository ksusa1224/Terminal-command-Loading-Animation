package com.application;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
//import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class AnkiNoteApplication {
//	public class AnkiNoteApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(AnkiNoteApplication.class, args);
	}
	
//	@Autowired JdbcTemplate jdbc;
//	
//	// アプリ起動時に実行される。
//    @Override public void run(String... args) throws Exception {
//        jdbc.execute("create table task (id serial, name varchar(255))");
//        jdbc.update("insert into task (name) values (?)", "First Task.");
//    }	
}
