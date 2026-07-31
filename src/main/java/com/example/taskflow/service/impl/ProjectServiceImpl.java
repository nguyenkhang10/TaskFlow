package com.example.taskflow.service.impl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.taskflow.domain.Project;
import com.example.taskflow.domain.ProjectStatus;
import com.example.taskflow.dto.ProjectRequest;
import com.example.taskflow.dto.ProjectResponse;
import com.example.taskflow.exception.ResourceNotFoundException;
import com.example.taskflow.mapper.ProjectMapper;
import com.example.taskflow.service.ProjectService;

import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectMapper projectMapper;

    public ProjectServiceImpl(ProjectMapper projectMapper) {
        this.projectMapper = projectMapper;
    }

    @Override
    @Transactional
    public ProjectResponse create(ProjectRequest request) {
        Project project = request.toDomain();
        projectMapper.insert(project);              // sau khi insert, project.id được gán
        // Đọc lại để lấy created_at/updated_at do DB set → trả về bản ghi đầy đủ
        return getById(project.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponse getById(Long id) {
        Project project = projectMapper.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy project id=" + id));
        return ProjectResponse.from(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> search(ProjectStatus status, String keyword) {
        return projectMapper.search(status, keyword)
                .stream()
                .map(ProjectResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public ProjectResponse update(Long id, ProjectRequest request) {
        Project project = request.toDomain();
        project.setId(id);
        int affected = projectMapper.update(project);
        if (affected == 0) {
            // update trả 0 dòng nghĩa là id không tồn tại
            throw new ResourceNotFoundException("Không tìm thấy project id=" + id);
        }
        return getById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        int affected = projectMapper.deleteById(id);
        if (affected == 0) {
            throw new ResourceNotFoundException("Không tìm thấy project id=" + id);
        }
    }
}
