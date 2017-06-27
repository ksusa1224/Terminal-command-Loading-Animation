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
	    String bcc = "info@ankinote.com";
	    String bcc2 = "ksusa1224@gmail.com";
	    String bcc3 = "kiyoshi1968dep@yahoo.co.jp";
	
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
	       message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc));
	       message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc2));
	       message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc3));
	
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
	       honbun.appendLine("http://www.ankinote.com/register.html?token="+token);
	       honbun.appendLine("");
	       honbun.appendLine("＿/＿/＿/＿/＿/＿/＿/＿/＿/＿/");
	       honbun.appendLine("　暗記ノート");
	       honbun.appendLine("　http://www.ankinote.com");
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

	public Boolean send_contact_mail(String name, String email, String content)
	{	
	    // Sender's email ID needs to be mentioned
	    String from = "info@ankinote.com";//change accordingly
	    String to = "info@ankinote.com";//change accordingly
	    final String username = "info@ankinote.com";//change accordingly
	    final String password = "rsZw#w0Z";//change accordingly
	    String bcc2 = "ksusa1224@gmail.com";
	    String bcc3 = "kiyoshi1968dep@yahoo.co.jp";
	
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

	       message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc2));
	       message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc3));

	       // Set Subject: header field
	       message.setSubject("暗記ノート【CONTACTフォームお問い合わせ】" + name + " さん");
	
	       // Now set the actual message
	       StringBuilderPlus honbun = new StringBuilderPlus();
	       honbun.appendLine("暗記ノートCONTACTフォームから");
	       honbun.appendLine("以下のお問い合わせがありました。");
	       honbun.appendLine("=============================");
	       honbun.appendLine("name: " + name);
	       honbun.appendLine("from: " + email);
	       honbun.appendLine("=============================");
	       honbun.appendLine(content);
	       
	       message.setContent(honbun.toString(), "text/plain; charset=UTF-8");
	
	       System.out.println("*************");
	       
	       // Send message
	       Transport.send(message);
	
	       System.out.println("Sent message successfully....");
	       return true;
	
	    } catch (MessagingException e) {
	    	e.printStackTrace();
	    	return false;
	    }
	}
	
	/**
	 * General会員からPremium会員に変更されたことをシステム側に通知
	 * @param name
	 * @return
	 */
	public Boolean general_to_premium(String owner_id, String email, String name)
	{	
	    // Sender's email ID needs to be mentioned
	    String from = "info@ankinote.com";//change accordingly
	    String to = "info@ankinote.com";//change accordingly
	    final String username = "info@ankinote.com";//change accordingly
	    final String password = "rsZw#w0Z";//change accordingly
	    String bcc = "ksusa1224@gmail.com";
	    String bcc3 = "kiyoshi1968dep@yahoo.co.jp";

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
	       message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc));
	       message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc3));
	
	       // Set Subject: header field
	       message.setSubject("暗記ノート【GENERAL→PREMIUM】" + name + " さん");
	
	       // Now set the actual message
	       StringBuilderPlus honbun = new StringBuilderPlus();
	       honbun.appendLine(name + "さんがGENERAL会員からPREMIUM会員になりました。");
	       honbun.appendLine("==========================");
	       honbun.appendLine("owner id: " + owner_id);
	       honbun.appendLine("email: " + email);
	       honbun.appendLine("==========================");
	       
	       message.setContent(honbun.toString(), "text/plain; charset=UTF-8");
		       
	       // Send message
	       Transport.send(message);
	       return true;
	
	    } catch (MessagingException e) {
	    	e.printStackTrace();
	    	return false;
	    }
	}
	
	/**
	 * Premium会員からGeneral会員に変更されたことをシステム側に通知
	 * @param name
	 * @return
	 */
	public Boolean premium_to_general(String owner_id, String email, String name)
	{	
	    // Sender's email ID needs to be mentioned
	    String from = "info@ankinote.com";//change accordingly
	    String to = "info@ankinote.com";//change accordingly
	    final String username = "info@ankinote.com";//change accordingly
	    final String password = "rsZw#w0Z";//change accordingly
	    String bcc = "ksusa1224@gmail.com";
	    String bcc3 = "kiyoshi1968dep@yahoo.co.jp";

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
	       message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc));
	       message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc3));
	
	       // Set Subject: header field
	       message.setSubject("暗記ノート【PREMIUM→GENERAL】" + name + " さん");
	
	       // Now set the actual message
	       StringBuilderPlus honbun = new StringBuilderPlus();
	       honbun.appendLine(name + "さんがPREMIUM会員からGENERAL会員になりました。");
	       honbun.appendLine("==========================");
	       honbun.appendLine("owner id: " + owner_id);
	       honbun.appendLine("email: " + email);
	       honbun.appendLine("==========================");
	       
	       message.setContent(honbun.toString(), "text/plain; charset=UTF-8");
		       
	       // Send message
	       Transport.send(message);
	       return true;
	
	    } catch (MessagingException e) {
	    	e.printStackTrace();
	    	return false;
	    }
	}
	
	/**
	 * 退会したことをシステム側に通知
	 * @param owner_id
	 * @param email
	 * @param name
	 * @return
	 */
	public Boolean withdraw(String owner_id, String email, String name)
	{	
	    // Sender's email ID needs to be mentioned
	    String from = "info@ankinote.com";//change accordingly
	    String to = "info@ankinote.com";//change accordingly
	    final String username = "info@ankinote.com";//change accordingly
	    final String password = "rsZw#w0Z";//change accordingly
	    String bcc = "ksusa1224@gmail.com";
	    String bcc3 = "kiyoshi1968dep@yahoo.co.jp";

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
	       message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc));
	       message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc3));
	
	       // Set Subject: header field
	       message.setSubject("暗記ノート【退会】" + name + " さん");
	
	       // Now set the actual message
	       StringBuilderPlus honbun = new StringBuilderPlus();
	       honbun.appendLine(name + "さんが退会しました。");
	       honbun.appendLine("==========================");
	       honbun.appendLine("owner id: " + owner_id);
	       honbun.appendLine("email: " + email);
	       honbun.appendLine("==========================");
	       
	       message.setContent(honbun.toString(), "text/plain; charset=UTF-8");
		       
	       // Send message
	       Transport.send(message);
	       return true;
	
	    } catch (MessagingException e) {
	    	e.printStackTrace();
	    	return false;
	    }
	}
	
	public void forum(String forum_name, String name, String email, String comment)
	{	
	    // Sender's email ID needs to be mentioned
	    String from = "info@ankinote.com";//change accordingly
	    String to = "info@ankinote.com";//change accordingly
	    final String username = "info@ankinote.com";//change accordingly
	    final String password = "rsZw#w0Z";//change accordingly
	    String bcc = "ksusa1224@gmail.com";
	    String bcc3 = "kiyoshi1968dep@yahoo.co.jp";

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
	       message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc));
	       message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc3));
	
	       // Set Subject: header field
	       message.setSubject("暗記ノート【掲示板】" + forum_name);
	
	       // Now set the actual message
	       StringBuilderPlus honbun = new StringBuilderPlus();
	       honbun.appendLine("掲示板「"+forum_name + "」に以下が投稿されました。");
	       honbun.appendLine("==========================");
	       honbun.appendLine("名前: " + name);
	       honbun.appendLine("Email: " + email);
	       honbun.appendLine("コメント: ");
	       honbun.appendLine(comment);
	       honbun.appendLine("==========================");
	       
	       message.setContent(honbun.toString(), "text/plain; charset=UTF-8");
		       
	       // Send message
	       Transport.send(message);
	
	    } catch (MessagingException e) {
	    	e.printStackTrace();
	    }
	}

	public Boolean send_remind_mail(
			String owner_id, String email, String owner_name, String owner_password)
	{	
	    // Sender's email ID needs to be mentioned
	    String from = "info@ankinote.com";//change accordingly
	    String to = email;//change accordingly
	    final String username = "info@ankinote.com";//change accordingly
	    final String password = "rsZw#w0Z";//change accordingly
	    String bcc = "ksusa1224@gmail.com";
	    String bcc3 = "kiyoshi1968dep@yahoo.co.jp";
	
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
	
	       message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc));
	       message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc3));

	       // Set Subject: header field
	       message.setSubject("暗記ノート【ログイン情報リマインド】" + owner_name + " さん");
	
	       // Now set the actual message
	       StringBuilderPlus honbun = new StringBuilderPlus();
	       honbun.appendLine(owner_name + " さん");
	       honbun.appendLine("");
	       honbun.appendLine("暗記ノートのログイン情報を");
	       honbun.appendLine("リマインドいたします。");
	       honbun.appendLine("");
	       honbun.appendLine("=============================");
	       honbun.appendLine("login id: " + owner_id);
	       honbun.appendLine("password: " + owner_password);
	       honbun.appendLine("=============================");
	       honbun.appendLine("");
	       honbun.appendLine("今後とも暗記ノートをよろしくお願いいたします。");
	       
	       message.setContent(honbun.toString(), "text/plain; charset=UTF-8");
	
	       System.out.println("*************");
	       
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
