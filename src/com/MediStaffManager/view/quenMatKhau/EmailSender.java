package com.MediStaffManager.view.quenMatKhau;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailSender {
    public static void sendEmail(String to, String subject, String messageContent) {
        String from = "MediStaffManager"; // Replace with your email address
        String host = "smtp.gmail.com"; // Gmail SMTP server

        final String username = "leducphuclong@gmail.com"; // Replace with your email
        final String appPassword = "ayew szdc aztf gnmt"; // Replace with your App Password

        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true"); // Use TLS

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, appPassword); // Use App Password
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(from));

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            message.setSubject(subject);

            message.setText(messageContent);

            Transport.send(message);
            System.out.println("Message sent successfully...");

        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Example usage of sendEmail function
        String to = "fixiva9019@gotemv.com"; // Recipient email address
        String subject = "Test Email from Java";
        String messageContent = "This is a test email sent from Java application.";

        // Call the function to send an email with recipient, subject, and message content
        sendEmail(to, subject, messageContent);
    }
}
