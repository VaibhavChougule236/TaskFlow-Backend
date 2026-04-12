package com.taskflow.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private String category;
    private String priority;
    private LocalDate dueDate;
    private boolean isDone;
    private String createdByName;
    private String createdByEmail;
}