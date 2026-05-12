package com.wedding.planner.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    // Automatically pulls your email from application.properties
    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendResetLink(String toEmail, String resetLink) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Vivaha | Password Reset Request");
            message.setText("Greetings from Vivaha Boutique,\n\n" +
                    "We received a request to reset your password. " +
                    "Please click the link below to securely update your credentials:\n\n" +
                    resetLink + "\n\n" +
                    "If you did not request this, please ignore this email.");

            mailSender.send(message);
            System.out.println("Email successfully dispatched to: " + toEmail);

        } catch (MailException e) {
            // Logs the error to your console if Gmail blocks the connection
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
}