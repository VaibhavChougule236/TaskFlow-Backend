package com.taskflow.dto;

import java.time.LocalDateTime;

import com.taskflow.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileDto {
	Long id;
	String name;
	String email;
	Role role;
	String imagePath;
	LocalDateTime createdAt;
			
}
