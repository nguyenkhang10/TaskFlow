package com.example.taskflow.controller;

import com.example.taskflow.domain.TaskPriority;
import com.example.taskflow.domain.TaskStatus;
import com.example.taskflow.dto.TaskRequest;
import com.example.taskflow.dto.TaskResponse;
import com.example.taskflow.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Tầng Controller: điểm vào của mọi HTTP request cho tài nguyên "task".
 */
@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * POST /api/projects/{projectId}/tasks — Tạo 1 task mới gắn liền với project.
     */
    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskResponse> create(
            @PathVariable Long projectId,
            @Valid @RequestBody TaskRequest request,
            UriComponentsBuilder uriBuilder) {

        TaskResponse created = taskService.create(projectId, request);

        URI location = uriBuilder
                .path("/api/tasks/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    /**
     * POST /api/tasks — Tạo 1 task mới (projectId được chỉ định trong body).
     */
    @PostMapping("/tasks")
    public ResponseEntity<TaskResponse> createDirect(
            @Valid @RequestBody TaskRequest request,
            UriComponentsBuilder uriBuilder) {

        TaskResponse created = taskService.create(request.projectId(), request);

        URI location = uriBuilder
                .path("/api/tasks/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    /**
     * GET /api/tasks — Lấy danh sách task, hỗ trợ lọc theo status, priority, projectId, creatorId, handlerId, và keyword.
     * Ví dụ: /api/tasks?status=IN_PROGRESS&priority=HIGH&projectId=1&creatorId=2&handlerId=3&keyword=feature
     */
    @GetMapping("/tasks")
    public List<TaskResponse> search(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long creatorId,
            @RequestParam(required = false) Long handlerId,
            @RequestParam(required = false) String keyword) {

        return taskService.search(status, priority, projectId, creatorId, handlerId, keyword);
    }

    /** GET /api/tasks/{id} — Lấy chi tiết 1 task. Trả về 404 nếu không tìm thấy. */
    @GetMapping("/tasks/{id}")
    public TaskResponse getById(@PathVariable Long id) {
        return taskService.getById(id);
    }

    /** PUT /api/tasks/{id} — Cập nhật 1 task theo id. Trả về 404 nếu task không tồn tại. */
    @PutMapping("/tasks/{id}")
    public TaskResponse update(@PathVariable Long id,
                               @Valid @RequestBody TaskRequest request) {
        return taskService.update(id, request);
    }

    /** DELETE /api/tasks/{id} — Xóa 1 task. Trả về 204 No Content. */
    @DeleteMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        taskService.delete(id);
    }
}
