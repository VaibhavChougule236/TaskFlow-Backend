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
import com.taskflow.entity.Task;
import com.taskflow.service.AdminTaskService;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@CrossOrigin
@PreAuthorize("hasRole('ADMIN')")
public class AdminTaskController {

	private final AdminTaskService taskService;

	// Create Task
    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @Valid @RequestBody TaskRequest request) {

        TaskResponse task = taskService.createTask(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Task created successfully", task));
    }

    //(Pagination + Filter + Search + Sort)
    @GetMapping
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {

        Page<TaskResponse> tasks = taskService.getTasks(
                page, size, status, category, keyword, sortBy, direction
        );

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Tasks fetched successfully", tasks)
        );
    }

    // Update Task
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {

        TaskResponse task = taskService.updateTask(id, request);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Task updated successfully", task)
        );
    }

    // Mark As Done/Undone
    @PatchMapping("/{id}/done")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTaskStatus(
            @PathVariable Long id) {

        TaskResponse task = taskService.updateTaskStatus(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Task status updated", task)
        );
    }

    // Delete Task
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @PathVariable Long id) {

        taskService.deleteTask(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Task deleted successfully", null)
        );
    }
    
}