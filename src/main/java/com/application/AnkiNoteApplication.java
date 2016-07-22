package com.application;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
//import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.ViewResolver;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class AnkiNoteApplication {
//	public class AnkiNoteApplication implements CommandLineRunner {

	public static void main(String[] args) {
//		SpringApplication app = new SpringApplication(AnkiNoteApplication.class);
//	    System.out.print("Starting app with System Args: [" );
//	    for (String s : args) {
//	      System.out.print(s + " ");
//	    }
//	    System.out.println("]");
//	    app.run(args);
		SpringApplication.run(AnkiNoteApplication.class, args);
	}

//	@Bean
//	public ViewResolver viewResolver() {
//	    ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
//	    templateResolver.setTemplateMode("XHTML");
//	    templateResolver.setPrefix("views/");
//	    templateResolver.setSuffix(".html");
//
//	    SpringTemplateEngine engine = new SpringTemplateEngine();
//	    engine.setTemplateResolver(templateResolver);
//
//	    ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
//	    viewResolver.setTemplateEngine(engine);
//	    return viewResolver;	
//	}
	
//	@Autowired JdbcTemplate jdbc;
//	
//	// アプリ起動時に実行される。
//    @Override public void run(String... args) throws Exception {
//        jdbc.execute("create table task (id serial, name varchar(255))");
//        jdbc.update("insert into task (name) values (?)", "First Task.");
//    }	
}
