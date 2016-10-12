package com.email;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.common.StringBuilderPlus;

public class MailSend {
	
	/**
	 * 
	 * @param to
	 * @param owner_name
	 * @param token
	 */
	public Boolean send_register_mail(String to, String owner_name, String token)
	{	
	    // Sender's email ID needs to be mentioned
	    String from = "info@ankinote.com";//change accordingly
	    final String username = "info@ankinote.com";//change accordingly
	    final String password = "rsZw#w0Z";//change accordingly
	
	    // Assuming you are sending email through relay.jangosmtp.net
	    String host = "smtp.ankinote.com";
	    
	    Properties props = new Properties();
	    
	    props.put("mail.smtp.auth","true");
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.smtp.host", host);
	    props.put("mail.smtp.port", "587");
	
	    // Get the Session object.
	    Session session = Session.getInstance(props,
	    new javax.mail.Authenticator() {
	       protected PasswordAuthentication getPasswordAuthentication() {
	          return new PasswordAuthentication(username, password);
	       }
	    });
	
	    try {
	       // Create a default MimeMessage object.
	       Message message = new MimeMessage(session);
	
	       // Set From: header field of the header.
	       message.setFrom(new InternetAddress(from));
	
	       // Set To: header field of the header.
	       message.setRecipients(Message.RecipientType.TO,
	       InternetAddress.parse(to));
	
	       // Set Subject: header field
	       message.setSubject("暗記ノート仮登録完了のお知らせ");
	
	       // Now set the actual message
	       StringBuilderPlus honbun = new StringBuilderPlus();
	       honbun.appendLine(owner_name + "様");
	       honbun.appendLine("");
	       honbun.appendLine("　この度は、暗記ノートの登録をお申し込みいただき、");
	       honbun.append("誠にありがとうございました。");
	       honbun.appendLine("");
	       honbun.appendLine("　以下のリンクをクリックし、登録手続きを完了させてください");
	       honbun.appendLine("http://localhost:8080/register.html?token="+token);
	       honbun.appendLine("");
	       honbun.appendLine("＿/＿/＿/＿/＿/＿/＿/＿/＿/＿/");
	       honbun.appendLine("　暗記ノート");
	       honbun.appendLine("　http://ankinote.com");
	       honbun.appendLine("＿/＿/＿/＿/＿/＿/＿/＿/＿/＿/");
	       
	       message.setContent(honbun.toString(), "text/plain; charset=UTF-8");
	
	       // Send message
	       Transport.send(message);
	
	       System.out.println("Sent message successfully....");
	       return true;
	
	    } catch (MessagingException e) {
	    	e.printStackTrace();
	    	return false;
	    }
	}
}
