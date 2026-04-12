package com.taskflow.dto;

import com.taskflow.entity.User;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@RequiredArgsConstructor
public class AuthResponse {

    private String token;
    private Long id;
    private String name;
    private String email;
    private String role;
    
    public AuthResponse(String token, User user) {
        this.token = token;
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole().name();
    }
}