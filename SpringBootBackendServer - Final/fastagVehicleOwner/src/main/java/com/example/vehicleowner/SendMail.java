package com.example.vehicleowner;

import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import java.util.Properties;


public class SendMail
{

    // In this method if some error occurs while sending mail then the program will not terminate
    // It will just print the error message and the program will continue
    // As if we are sending transaction details to the vehicle owner and if the mail is not sent due to some reasons
    // then the the TollPlaza should not terminate it should only display the error message and then continue with the next vehicle
    public static boolean sendEmail(String to, String from, String subject, String text) 
    {
        boolean flag = false;
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        final String username = "hardik23555";
        final String password = "wnetpfvovgnsmqjw";

        Session session = Session.getInstance(props, new Authenticator() 
        {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() 
            {
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
    // public static boolean sendEmail_(String to, String from, String subject, String text) 
    // {
    //     System.out.println("Hello3");
    //     boolean flag = false;
    //     Properties props = new Properties();
    //     props.put("mail.smtp.host", "smtp.gmail.com");
    //     props.put("mail.smtp.port", "587");
    //     props.put("mail.smtp.auth", "true");
    //     props.put("mail.smtp.starttls.enable", "true");

    //     System.out.println("Hello4");
    //     final String username = "hardik23555";
    //     final String password = "wnetpfvovgnsmqjw";

    //     Session session = Session.getInstance(props, new Authenticator() 
    //     {
    //         @Override
    //         protected PasswordAuthentication getPasswordAuthentication() 
    //         {
    //             return new PasswordAuthentication(username, password);
    //         }
    //     });
    //     System.out.println("Hello5");
        
    //     try 
    //     {
    //         jakarta.mail.Message message = new MimeMessage(session);
    //         System.out.println("Hello6");
    //         message.setFrom(new InternetAddress(from));
    //         System.out.println("Hello7");
    //         System.out.println(to);
    //         System.out.println(from);
    //         System.out.println(subject);
    //         System.out.println(text);
    //         message.setRecipients(jakarta.mail.Message.RecipientType.TO, InternetAddress.parse(to));
    //         System.out.println("Hello8");
    //         message.setSubject(subject);
    //         System.out.println("Hello9");
    //         message.setText(text);
    //         System.out.println("Hello10");
    //         Transport.send(message);
    //         System.out.println("Hello11");
    //         flag = true;
    //         System.out.println("Hello12");
    //         System.out.println("OTP sent successfully! ");
    //     } 
    //         catch (Exception e) 
    //     {
    //         System.out.println("Hello13");
    //         System.err.println("An error occurred: " + e.getMessage());

    //     }

    //     return flag;
    // }


}
