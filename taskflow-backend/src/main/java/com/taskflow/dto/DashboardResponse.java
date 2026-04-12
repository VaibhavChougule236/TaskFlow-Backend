package com.taskflow.dto;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardResponse {

    private long totalUsers;
    private long totalTasks;
    private long completedTasks;
    private long pendingTasks;
    private long overdueTasks;
    
    private Map<String, Long> priorityStats;
    private Map<String, Long> categoryStats;
}