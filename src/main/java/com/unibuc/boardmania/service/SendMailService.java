package com.unibuc.boardmania.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

@Service
public class SendMailService {

    @Value("${mail.username}")
    private String username;

    @Value("${mail.password}")
    private String password;

    private final Properties prop;

    public SendMailService() {
        prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", 587);
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
    }

    @Scheduled(cron = "0 59 14 * * *", zone = "Europe/Bucharest")
    public void sendMail() throws Exception {

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("danynimara@gmail.com"));
        message.setSubject("Mail Subject");

        String msg = "This is my first email using JavaMailer";

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

        String msgStyled = "This is my <b style='color:red;'>bold-red email</b> using JavaMailer";
        MimeBodyPart mimeBodyPartWithStyledText = new MimeBodyPart();
        mimeBodyPartWithStyledText.setContent(msgStyled, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);
        multipart.addBodyPart(mimeBodyPartWithStyledText);

        message.setContent(multipart);

        Transport.send(message);
    }

}
