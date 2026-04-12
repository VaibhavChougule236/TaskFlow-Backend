package com.taskflow.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("TaskFlow - Verify Your Email");
        message.setText("Welcome to TaskFlow!\n\n" +
                        "Your verification code is: " + otp + "\n\n" +
                        "This code will expire in 10 minutes. Do not share it with anyone.");
        
        mailSender.send(message);
    }

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("TaskFlow <noreply@taskflow.com>");
        
        mailSender.send(message);
    }
}