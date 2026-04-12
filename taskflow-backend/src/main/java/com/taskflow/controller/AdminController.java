package com.taskflow.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.taskflow.dto.ApiResponse;
import com.taskflow.dto.UserResponse;
import com.taskflow.dto.DashboardResponse;
import com.taskflow.service.AdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboardStats() {

        DashboardResponse stats = adminService.getDashboardStats();

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Dashboard data fetched successfully", stats)
        );
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {

        Page<UserResponse> users =
        		adminService.getAllUsers(page, size, sortBy, direction);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Users fetched successfully", users)
        );
    }
    
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {

        adminService.deleteUser(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "User deleted successfully", null)
        );
    }
}


