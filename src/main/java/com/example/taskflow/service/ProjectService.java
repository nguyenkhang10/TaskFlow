package com.example.taskflow.service;
import com.example.taskflow.dto.ProjectRequest;
import com.example.taskflow.dto.ProjectResponse;
import com.example.taskflow.domain.ProjectStatus;
import java.util.List;

public interface ProjectService {
    ProjectResponse create(ProjectRequest request);

    ProjectResponse getById(Long id);

    List<ProjectResponse> search(ProjectStatus status, String keyword);

    ProjectResponse update(Long id, ProjectRequest request);

    void delete(Long id);
}
