package com.wedding.planner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetLink(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Vivaha - Password Reset Request");
        message.setText("Hello,\n\nClick the link below to reset your password:\n\n" + resetLink + "\n\nIf you did not request this, please ignore this email.\n\nVivaha Team");
        mailSender.send(message);
    }
}