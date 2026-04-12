package com.taskflow.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.taskflow.dto.ApiResponse;
import com.taskflow.dto.AuthResponse;
import com.taskflow.dto.LoginRequest;
import com.taskflow.dto.RegisterRequest;
import com.taskflow.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<String>> sendOtp(@RequestBody Map<String, String> request) {
        authService.sendOtp(request.get("email"));
        return ResponseEntity.ok(new ApiResponse<>(true, "OTP sent successfully", null));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Boolean>> verifyOtp(@RequestBody Map<String, String> request) {
        boolean isValid = authService.verifyOtp(request.get("email"), request.get("otp"));
        if (isValid) {
            return ResponseEntity.ok(new ApiResponse<>(true, "Email verified successfully", true));
        }
        return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Invalid or expired OTP", false));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "User registered successfully", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        ApiResponse<AuthResponse> apiResponse = new ApiResponse<>(true, "Login successful", response);
        return ResponseEntity.ok(apiResponse);
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        authService.processForgotPassword(email);
        return ResponseEntity.ok(new ApiResponse<>(true, "Reset link sent to your email", null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");
        authService.updatePasswordWithToken(token, newPassword);
        return ResponseEntity.ok(new ApiResponse<>(true, "Password updated successfully", null));
    }
}