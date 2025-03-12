package com.example.demo.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import org.springframework.stereotype.Service;

import com.example.demo.Util.EmailUtil;

@Service
public class EmailService {

    public boolean sendEmail(String toEmail, String subject, String body) {
        final String fromEmail = ""; //requires valid gmail id
        final String password = ""; // password
        
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.socketFactory.port", "465"); //SSL Port
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); //SSL Factory Class
        props.put("mail.smtp.auth", "true"); //Enabling SMTP Authentication
        props.put("mail.smtp.port", "465"); //SMTP Port
        
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };
        
        Session session = Session.getDefaultInstance(props, auth);

        try {
            EmailUtil.sendEmail(session, toEmail, subject, body);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
