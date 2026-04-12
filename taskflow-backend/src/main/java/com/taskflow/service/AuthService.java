package com.taskflow.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.taskflow.dto.AuthResponse;
import com.taskflow.dto.LoginRequest;
import com.taskflow.dto.RegisterRequest;
import com.taskflow.entity.Role;
import com.taskflow.entity.User;
import com.taskflow.repository.UserRepository;
import com.taskflow.security.JwtService;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    public void sendOtp(String email) {
        String otp = String.format("%06d", new java.util.Random().nextInt(999999));
        
        User user = userRepository.findByEmail(email).orElse(new User());
        user.setEmail(email);
        user.setVerificationOtp(otp);
        user.setEnabled(false); 
        
        if (user.getId() == null) {
            user.setName("Pending");
            user.setPassword("temp_pass");
            user.setRole(Role.USER);
        }
        
        userRepository.save(user);

        emailService.sendOtpEmail(email, otp);
    }

    public boolean verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (otp.equals(user.getVerificationOtp())) {
            user.setEnabled(true);
            user.setVerificationOtp(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public void register(RegisterRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Please verify email first"));

        if (!user.isEnabled()) {
            throw new RuntimeException("Email not verified");
        }

        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (user.getRole()!=Role.ADMIN && !user.isEnabled()) {
            throw new RuntimeException("Please verify your email before logging in.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token, user);
    }
    
    public void processForgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        
        String token = UUID.randomUUID().toString();
        user.setResetToken(token); 
        userRepository.save(user);

        String resetLink = "https://task-flow-smart-workflow.vercel.app/reset-password?token=" + token;
        
        String emailBody = "Click the link below to reset your TaskFlow password:\n\n" + resetLink;
        
        emailService.sendEmail(email, "Reset Your Password", emailBody);
    }

    public void updatePasswordWithToken(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null); 
        userRepository.save(user);
    }
}