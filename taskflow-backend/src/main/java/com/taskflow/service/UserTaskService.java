package com.taskflow.service;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.taskflow.dto.TaskRequest;
import com.taskflow.dto.TaskResponse;
import com.taskflow.entity.Task;
import com.taskflow.entity.User;
import com.taskflow.exception.TaskException;
import com.taskflow.exception.TaskNotFoundException;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserTaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    // get logged-in user
    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new TaskException("User not found"));
    }

    // Create task
    public TaskResponse createTask(TaskRequest request) {

        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new TaskException("Task title cannot be empty");
        }

        User user = getCurrentUser();

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory().toLowerCase())
                .priority(request.getPriority().toLowerCase())
                .dueDate(request.getDueDate())
                .isDone(false)
                .user(user)
                .build();

        return mapToTaskResponse(taskRepository.save(task));
    }

    // Get only user's tasks
    public Page<TaskResponse> getMyTasks(
	        int page,
	        int size,
	        String status,
	        String category,
	        String keyword,
	        String sortBy,
	        String direction
	) {

	    User user = getCurrentUser();

	    Sort sort = direction.equalsIgnoreCase("desc")
	            ? Sort.by(sortBy).descending()
	            : Sort.by(sortBy).ascending();

	    Pageable pageable = PageRequest.of(page, size, sort);

	    if (keyword != null && !keyword.trim().isEmpty()) {
	        return taskRepository
	                .findByUserIdAndTitleContainingIgnoreCase(user.getId(), keyword, pageable)
	                .map(this::mapToTaskResponse);
	    }

	    if (status != null) {
	        switch (status.toLowerCase()) {

	            case "completed":
	                return taskRepository
	                        .findByUserIdAndIsDone(user.getId(), true, pageable)
	                        .map(this::mapToTaskResponse);

	            case "pending":
	                return taskRepository
	                        .findByUserIdAndIsDone(user.getId(), false, pageable)
	                        .map(this::mapToTaskResponse);

	            case "overdue":
	                return taskRepository
	                        .findByUserIdAndIsDoneFalseAndDueDateBefore(user.getId(), LocalDate.now(), pageable)
	                        .map(this::mapToTaskResponse);

	            default:
	                throw new TaskException("Invalid status filter");
	        }
	    }

	    if (category != null && !category.trim().isEmpty()) {
	        return taskRepository
	                .findByUserIdAndCategory(user.getId(), category.toLowerCase(), pageable)
	                .map(this::mapToTaskResponse);
	    }

	    return taskRepository
	            .findByUserId(user.getId(), pageable)
	            .map(this::mapToTaskResponse);
	}

    // Update task
    public TaskResponse updateTask(Long id, TaskRequest request) {

        User user = getCurrentUser();

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        if (!task.getUser().getId().equals(user.getId())) {
            throw new TaskException("You cannot update another user's task");
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setCategory(request.getCategory());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());

        return mapToTaskResponse(taskRepository.save(task));
    }

    // Toggle done status
    public TaskResponse updateTaskStatus(Long id) {

        User user = getCurrentUser();

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        if (!task.getUser().getId().equals(user.getId())) {
            throw new TaskException("You cannot modify another user's task");
        }

        task.setDone(!task.isDone());

        return mapToTaskResponse(taskRepository.save(task));
    }

    // Delete task
    public void deleteTask(Long id) {

        User user = getCurrentUser();

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        if (!task.getUser().getId().equals(user.getId())) {
            throw new TaskException("You cannot delete another user's task");
        }

        taskRepository.delete(task);
    }

    // DTO mapper
    private TaskResponse mapToTaskResponse(Task task) {

        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .category(task.getCategory())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .isDone(task.isDone())
                .createdByName(task.getUser() != null ? task.getUser().getName() : null)
                .createdByEmail(task.getUser() != null ? task.getUser().getEmail() : null)
                .build();
    }

}