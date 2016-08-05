package com.application;

import com.common.AES;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class AnkiNoteApplication {

	public static void main(String[] args) {
		AES aes = new AES();
		byte[] encrypted = aes.encrypt("てすと");
		String original = aes.decrypt(encrypted);
		System.out.print(original);
		SpringApplication.run(AnkiNoteApplication.class, args);
	}
}
