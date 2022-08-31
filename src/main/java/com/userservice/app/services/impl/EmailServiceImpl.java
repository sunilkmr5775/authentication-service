package com.userservice.app.services.impl;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.userservice.app.constant.ConstantVariables;
import com.userservice.app.constant.StatusConstant;
import com.userservice.app.models.Users;
import com.userservice.app.repository.UserRepository;
import com.userservice.app.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

	@Value("${spring.mail.username}")
	private String SENDER_EMAIL;

	@Value("${spring.mail.password}")
	private String PASSWORD;

	@Value("${spring.mail.subject}")
	private String SUBJECT;

	@Value("${spring.mail.host}")
	private String HOST;

	@Value("${spring.mail.port}")
	private String PORT;

	@Value("${spring.mail.properties.mail.smtp.starttls.enable}")
	private String TLS_ENABLE;

	@Value("${spring.mail.properties.mail.smtp.auth}")
	private String AUTH_ENABLE;

	@Value("${fileLocation}")
	private String PATH;

	@Autowired
	UserRepository userRepository;

	@Override
	public boolean sendVerificationEmail(Users user, String siteURL) {
		boolean foo = false; // Set the false, default variable "foo", we will allow it after sending code
								// process email

		Properties properties = new Properties();

//      Setup host and mail server
		properties.put("mail.smtp.auth", AUTH_ENABLE);
		properties.put("mail.smtp.starttls.enable", TLS_ENABLE);
		properties.put("mail.smtp.host", HOST);
		properties.put("mail.smtp.port", PORT);
		
		

//	     get the session object and pass username and password
		Session session = Session.getDefaultInstance(properties, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(SENDER_EMAIL, PASSWORD);
			}
		});

		try {
			String content = ConstantVariables.EMAIL_CONTENT;
			content = content.replace("[[name]]",
					user.getFirstName() +" "+ user.getMiddleName() + " " + user.getLastName());
			
			String verifyURL = siteURL + "/api/auth/verify?code=" + user.getVerificationCode();
			
			content = content.replace("[[URL]]", verifyURL.trim().toString());
			
			MimeBodyPart mimeBodyPart = new MimeBodyPart();
			mimeBodyPart.setContent(content, "text/html");
			MimeMultipart multipart = new MimeMultipart();
			multipart.addBodyPart(mimeBodyPart);

			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(SENDER_EMAIL));
			message.setContent(multipart);
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
			message.setSubject(user.getUsername()+" - "+SUBJECT);
			message.setText(content, "UTF-8", "html");
			Transport.send(message);

			System.out.println("Email Sent With Inline Image Successfully to " + user.getEmail());

			foo = true;

		} catch (Exception e) {

			System.out.println("EmailService File Error" + e);
		}

		return foo;
	}

	@Override
	public boolean verify(String verificationCode) {

		Users user = userRepository.findByVerificationCode(verificationCode);

		if (user == null || user.isEmailVerified()) {
			return false;
		} else {
			user.setVerificationCode(null);
			user.setStatus(StatusConstant.STATUS_ACTIVE);
			user.setEmailVerified(true);
			userRepository.save(user);
			return true;
		}
	}

}
