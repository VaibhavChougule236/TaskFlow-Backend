package com.taskflow.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.taskflow.dto.ApiResponse;
import com.taskflow.dto.TaskRequest;
import com.taskflow.dto.TaskResponse;
import com.taskflow.service.AdminTaskService;
import com.taskflow.service.UserTaskService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/my-tasks")
@RequiredArgsConstructor
@CrossOrigin
@PreAuthorize("hasRole('USER')")
public class UserTaskController {

    private final UserTaskService taskService;

    // Create task for logged-in user
    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @Valid @RequestBody TaskRequest request) {

        TaskResponse task = taskService.createTask(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Task created successfully", task));
    }

    // Get only logged-in user's tasks
    @GetMapping
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getMyTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {

        Page<TaskResponse> tasks = taskService.getMyTasks(
                page, size, status, category, keyword, sortBy, direction
        );

        return ResponseEntity.ok(
                new ApiResponse<>(true, "User tasks fetched successfully", tasks)
        );
    }

    // Update own task
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {

        TaskResponse task = taskService.updateTask(id, request);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Task updated successfully", task)
        );
    }

    // Mark own task done/undone
    @PatchMapping("/{id}/done")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTaskStatus(
            @PathVariable Long id) {

        TaskResponse task = taskService.updateTaskStatus(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Task status updated", task)
        );
    }

    // Delete own task
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @PathVariable Long id) {

        taskService.deleteTask(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Task deleted successfully", null)
        );
    }

}