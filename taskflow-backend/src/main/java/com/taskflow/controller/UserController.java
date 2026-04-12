package com.taskflow.controller;

import com.taskflow.dto.ApiResponse;
import com.taskflow.dto.UserProfileDto;
import com.taskflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileDto>> getCurrentUser(Authentication authentication) {
        UserProfileDto userDto = userService.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "User fetched successfully", userDto));
    }

    @PutMapping("/update-profile")
    public ResponseEntity<ApiResponse<UserProfileDto>> updateProfile(
            Authentication authentication,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) MultipartFile image) throws IOException {
        
        UserProfileDto updated = userService.updateProfile(authentication.getName(), name, image);
        return ResponseEntity.ok(new ApiResponse<>(true, "Profile updated successfully", updated));
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<ApiResponse<String>> deleteAccount(Authentication authentication) {
        userService.deleteAccount(authentication.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Account deleted successfully", "Success"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            Authentication authentication,
            @RequestBody Map<String, String> body) {
        
        userService.changePassword(
                authentication.getName(), 
                body.get("oldPassword"), 
                body.get("newPassword")
        );
        return ResponseEntity.ok(new ApiResponse<>(true, "Password changed successfully", "Success"));
    }
}