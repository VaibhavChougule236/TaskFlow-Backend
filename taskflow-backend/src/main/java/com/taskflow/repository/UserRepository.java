package com.taskflow.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.taskflow.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

	Page<User> findAll(Pageable pageable);

	long count();

	Optional<User> findByVerificationOtp(String verificationOtp);

	Boolean existsByEmail(String email);

	Optional<User> findByResetToken(String token);

}
