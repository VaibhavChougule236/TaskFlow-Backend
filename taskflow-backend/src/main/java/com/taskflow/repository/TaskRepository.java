package com.taskflow.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.taskflow.entity.Task;

import java.time.LocalDate;

public interface TaskRepository extends JpaRepository<Task, Long> {
	
	//for Admin
	
	Page<Task> findByIsDone(boolean isDone, Pageable pageable);

	Page<Task> findByCategory(String category, Pageable pageable);

	Page<Task> findByIsDoneFalseAndDueDateBefore(LocalDate date, Pageable pageable);
	
	Page<Task> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
	
	//for Logged in user
	
	Page<Task> findByUserId(Long userId, Pageable pageable);
		
	Page<Task> findByUserIdAndTitleContainingIgnoreCase(Long userId, String keyword, Pageable pageable);

	Page<Task> findByUserIdAndIsDone(Long id, boolean isDone, Pageable pageable);

	Page<Task> findByUserIdAndIsDoneFalseAndDueDateBefore(Long id, LocalDate now, Pageable pageable);

	Page<Task> findByUserIdAndCategory(Long id, String lowerCase, Pageable pageable);
	
	//Admin

	long countByIsDone(boolean isDone);

	long countByIsDoneFalseAndDueDateBefore(LocalDate now);
	
	long countByPriority(String priority);

	long countByCategory(String category);


}
