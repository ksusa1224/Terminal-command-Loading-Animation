package com.example.demo;

import java.util.HashSet;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		
		HashSet<String> links = new HashSet<String>();		    		
		registFileTmp(links, "http://www.juntendo.ac.jp/hospital/streaming/", "http://www.juntendo.ac.jp/hospital/streaming/");					

//        Document document = null;
//        Elements linksOnPage = null;
//        	try
//        	{
//        		String url = "http://www.juntendo.ac.jp/hospital/streaming/hp.html";
//        		document = Jsoup.connect(url).get();
//        		linksOnPage = document.select("a[href]");
//        	}
//        	catch(Exception ex)
//        	{
//        		ex.printStackTrace();
//        	}		    

		SpringApplication.run(DemoApplication.class, args);
	}
	
	private static void registFileTmp(
			HashSet<String> links, 
			String domain, 
			String url)
	{
		try
		{
	        System.out.println(url);

	        String extension = "html";
	        
	        if (extension.equals("html"))
	        {
		        Document document = null;
		        Elements linksOnPage = null;
		        	try
		        	{
		        		document = Jsoup.connect(url).get();
		        		linksOnPage = document.select("a[href]");
		        	}
		        	catch(Exception ex)
		        	{
		        		ex.printStackTrace();
		        	}		    
		        	if (linksOnPage != null)
		        	{
			        for (Element page : linksOnPage) 
			        {
			        		try 
			        		{
			        			url = page.attr("abs:href");
//			        			before_crawl(links, domain, page.attr("abs:href"), exclude_domains);
		        				if (url != null && !links.contains(url)) {
		        					links.add(url);
		        					System.out.println(url);
		        					String regex = "^" + domain + ".*";
		        					outerIf:
		        					if (url.matches(regex))
		        					{
		        								        						
		        						try
		        						{
		        							registFileTmp(links, domain, url);					
		        						}
		        						catch(Exception ex)
		        						{
		        							ex.printStackTrace();
		        						}
		        					}
		        				}
			        		}
			        		catch(Exception ex)
			        		{
			        			ex.printStackTrace();
			        		}
			        }
		        	}
	        }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
}
