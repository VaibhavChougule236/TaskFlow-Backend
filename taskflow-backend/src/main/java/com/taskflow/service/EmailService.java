package com.taskflow.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String to, String otp) {
        try {
            log.info("📩 Sending OTP to: " + to);
            log.info("🔑 OTP (DEV): " + otp);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("TaskFlow - Verify Your Email");
            message.setText(
                "Welcome to TaskFlow!\n\n" +
                "Your verification code is: " + otp + "\n\n" +
                "This code will expire in 10 minutes."
            );

            message.setFrom("developervaibhav124@gmail.com");

            mailSender.send(message);

            log.info("✅ Email sent successfully");

        } catch (Exception e) {
            log.info("❌ EMAIL FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("developervaibhav124@gmail.com");
        
        mailSender.send(message);
    }
}