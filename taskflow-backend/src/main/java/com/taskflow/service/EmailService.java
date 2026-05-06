//package com.taskflow.service;
//
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class EmailService {
//
//    private final JavaMailSender mailSender;
//
//    public void sendOtpEmail(String to, String otp) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject("TaskFlow - Verify Your Email");
//        message.setText("Welcome to TaskFlow!\n\n" +
//                        "Your verification code is: " + otp + "\n\n" +
//                        "This code will expire in 10 minutes. Do not share it with anyone.");
//        
//        mailSender.send(message);
//    }
//
//    public void sendEmail(String to, String subject, String body) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject(subject);
//        message.setText(body);
//        message.setFrom("TaskFlow <noreply@taskflow.com>");
//        
//        mailSender.send(message);
//    }
//}


package com.taskflow.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    private static final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";

    public boolean sendOtpEmail(String to, String otp) {
        try {
            log.info("📩 Sending OTP via Brevo API to: {}", to);
            log.info("🔑 OTP (DEV): {}", otp);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("sender", Map.of(
                    "name", senderName,
                    "email", senderEmail
            ));
            body.put("to", List.of(Map.of("email", to)));
            body.put("subject", "TaskFlow - Verify Your Email");
            body.put("textContent",
                    "Welcome to TaskFlow!\n\n" +
                    "Your OTP is: " + otp + "\n\n" +
                    "Valid for 10 minutes."
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(BREVO_URL, request, String.class);

            log.info("✅ Email sent successfully. Status: {}", response.getStatusCode());

            return true;

        } catch (Exception e) {
            log.error("❌ EMAIL FAILED: {}", e.getMessage(), e);
            log.warn("⚠️ OTP (DEV FALLBACK): {}", otp);
            return false;
        }
    }

    public boolean sendEmail(String to, String subject, String bodyText) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("sender", Map.of(
                    "name", senderName,
                    "email", senderEmail
            ));
            body.put("to", List.of(Map.of("email", to)));
            body.put("subject", subject);
            body.put("textContent", bodyText);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            restTemplate.postForEntity(BREVO_URL, request, String.class);

            log.info("✅ General email sent to {}", to);
            return true;

        } catch (Exception e) {
            log.error("❌ EMAIL FAILED: {}", e.getMessage(), e);
            return false;
        }
    }
}