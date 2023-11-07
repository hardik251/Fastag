package com.example.fastag;

import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;

import java.io.File;
import java.util.Properties;

public class SendMail
{

    // In this method if some error occurs while sending mail then the program will not terminate
    // It will just print the error message and the program will continue
    // As if we are sending transaction details to the vehicle owner and if the mail is not sent due to some reasons
    // then the the TollPlaza should not terminate it should only display the error message and then continue with the next vehicle
    public boolean sendEmail(String to, String from, String subject, String text) 
    {
        boolean flag = false;
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        final String username = "hardik23555";
        final String password = "wnetpfvovgnsmqjw";

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try 
        {
            jakarta.mail.Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(jakarta.mail.Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);
            flag = true;
            System.out.println("Mail sent successfully");
        } 
            catch (Exception e) 
        {
            System.err.println("An error occurred: " + e.getMessage());

        }

        return flag;
    }

    // only difference is _ in the method name
    // and if their is an error in sending mail then the program will terminate
    // as if we are sending OTP and Email ID is invalid then the program should terminate
    public boolean sendEmail_(String to, String from, String subject, String text) 
    {
        boolean flag = false;
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        final String username = "hardik23555";
        final String password = "wnetpfvovgnsmqjw";

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try 
        {
            jakarta.mail.Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(jakarta.mail.Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);
            flag = true;
            System.out.println("OTP sent successfully! ");
        } 
            catch (Exception e) 
        {
            System.err.println("An error occurred: " + e.getMessage());
            System.exit(-1);

        }

        return flag;
    }


    public boolean sendEmailWithAttachment(String to, String from, String subject, String text,File file) 
    {
        boolean flag = false;
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        final String username = "hardik23555";
        final String password = "wnetpfvovgnsmqjw";

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try 
        {
            jakarta.mail.Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(jakarta.mail.Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);

            MimeBodyPart part1 = new MimeBodyPart();
            part1.setText(text);

            MimeBodyPart part2 = new MimeBodyPart();
            part2.attachFile(file);

            MimeMultipart mimeMultipart = new MimeMultipart();
            mimeMultipart.addBodyPart(part1);
            mimeMultipart.addBodyPart(part2);

            message.setContent(mimeMultipart);

            Transport.send(message);
            flag = true;
            System.out.println("Mail sent successfully");
        } 
            catch (Exception e) 
        {
            System.err.println("An error occurred: " + e.getMessage());

        }

        return flag;
    }




}