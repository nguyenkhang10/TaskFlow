package com.example.taskflow.service;

import com.example.taskflow.domain.TaskPriority;
import com.example.taskflow.domain.TaskStatus;
import com.example.taskflow.dto.TaskRequest;
import com.example.taskflow.dto.TaskResponse;

import java.util.List;

public interface TaskService {
    TaskResponse create(Long projectId, TaskRequest request);

    TaskResponse getById(Long id);

    List<TaskResponse> getByProjectId(Long projectId);

    List<TaskResponse> search(TaskStatus status, TaskPriority priority, Long projectId, Long creatorId, Long handlerId, String keyword);

    TaskResponse update(Long id, TaskRequest request);

    void delete(Long id);
}
