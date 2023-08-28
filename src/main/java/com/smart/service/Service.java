package com.smart.service;

import java.util.Properties;



import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@org.springframework.stereotype.Service
public class Service {

	public boolean sendEmail(String to, String subject, String text) {

		boolean flag = false;
//logic

		Properties properties = new Properties();
		properties.put("mail.smtp.auth", true);
		properties.put("mail.smtp.starttls.enable", true);
		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.host", "smtp.gmail.com");

		String username = "pankajpk9731";
		String password = "ndgrqhpmkojdhffo";

//session get
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);

			message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
//			message.setFrom(new InternetAddress(form));
			message.setSubject(subject);
//			message.setText(text);
			message.setContent(text,"text/html");

			Transport.send(message);
			flag = true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return flag;

	}

}
