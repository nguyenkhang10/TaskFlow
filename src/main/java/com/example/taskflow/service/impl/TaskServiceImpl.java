package com.example.taskflow.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.taskflow.domain.Task;
import com.example.taskflow.domain.TaskPriority;
import com.example.taskflow.domain.TaskStatus;
import com.example.taskflow.dto.TaskRequest;
import com.example.taskflow.dto.TaskResponse;
import com.example.taskflow.exception.ResourceNotFoundException;
import com.example.taskflow.mapper.ProjectMapper;
import com.example.taskflow.mapper.TaskMapper;
import com.example.taskflow.mapper.UserMapper;
import com.example.taskflow.service.TaskService;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskMapper taskMapper;
    private final ProjectMapper projectMapper;
    private final UserMapper userMapper;

    public TaskServiceImpl(TaskMapper taskMapper, ProjectMapper projectMapper, UserMapper userMapper) {
        this.taskMapper = taskMapper;
        this.projectMapper = projectMapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public TaskResponse create(Long projectId, TaskRequest request) {
        Long targetProjectId = projectId != null ? projectId : request.projectId();
        if (targetProjectId != null) {
            projectMapper.findById(targetProjectId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy project id=" + targetProjectId));
        }

        validateUsersExist(request.creatorId(), request.handlerId());

        Task task = request.toDomain(targetProjectId);
        taskMapper.insert(task);
        return getById(task.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getById(Long id) {
        Task task = taskMapper.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy task id=" + id));
        return TaskResponse.from(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> search(TaskStatus status, TaskPriority priority, Long projectId, Long creatorId, Long handlerId, String keyword) {
        return taskMapper.search(status, priority, projectId, creatorId, handlerId, keyword)
                .stream()
                .map(TaskResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public TaskResponse update(Long id, TaskRequest request) {
        Task existing = taskMapper.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy task id=" + id));

        validateUsersExist(request.creatorId(), request.handlerId());

        Task task = request.toDomain(existing.getProjectId());
        task.setId(id);

        int affected = taskMapper.update(task);
        if (affected == 0) {
            throw new ResourceNotFoundException("Không tìm thấy task id=" + id);
        }
        return getById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        int affected = taskMapper.deleteById(id);
        if (affected == 0) {
            throw new ResourceNotFoundException("Không tìm thấy task id=" + id);
        }
    }

    private void validateUsersExist(Long creatorId, Long handlerId) {
        if (creatorId != null) {
            userMapper.findById(creatorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy creator user id=" + creatorId));
        }
        if (handlerId != null) {
            userMapper.findById(handlerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy handler user id=" + handlerId));
        }
    }
}
