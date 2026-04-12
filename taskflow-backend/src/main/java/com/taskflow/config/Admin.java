package com.taskflow.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.taskflow.entity.Role;
import com.taskflow.entity.User;
import com.taskflow.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Admin implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if(userRepository.findByEmail("vaibhav.admin@taskflow.com").isEmpty()){

            User admin = User.builder()
                    .name("Vaibhav")
                    .email("vaibhav.admin@taskflow.com")
                    .password(passwordEncoder.encode("Vaibhav@123"))
                    .role(Role.ADMIN)
                    .build();

            userRepository.save(admin);
        }
    }
}