package com.example.taskflow.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import com.example.taskflow.domain.ProjectStatus;
import com.example.taskflow.dto.ProjectRequest;
import com.example.taskflow.dto.ProjectResponse;
import com.example.taskflow.service.ProjectService;

import java.net.URI;
import java.util.List;

/**
 * Tầng Controller: điểm vào của mọi HTTP request cho tài nguyên "project".
 * <p>
 * Nhiệm vụ: nhận request, validate ({@code @Valid}), gọi Service, và trả
 * {@code ResponseEntity} với đúng status code. KHÔNG chứa business logic.
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }
    @PostMapping
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectRequest request,
                                                  UriComponentsBuilder uriBuilder) {
        ProjectResponse created = projectService.create(request);
        URI location = uriBuilder.path("/api/projects/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * GET /api/projects — danh sách, có thể lọc theo status và keyword.
     * Ví dụ: /api/projects?status=ACTIVE&keyword=portal
     */
    @GetMapping
    public List<ProjectResponse> search(@RequestParam(required = false) ProjectStatus status,
                                        @RequestParam(value = "keyword", required = false) String keyword) {
        return projectService.search(status, keyword);
    }

    /** GET /api/projects/{id} — lấy 1 project. Trả 404 nếu không có. */
    @GetMapping("/{id}")
    public ProjectResponse getById(@PathVariable Long id) {
        return projectService.getById(id);
    }

    /** PUT /api/projects/{id} — cập nhật. Trả 404 nếu id không tồn tại. */
    @PutMapping("/{id}")
    public ProjectResponse update(@PathVariable Long id,
                                  @Valid @RequestBody ProjectRequest request) {
        return projectService.update(id, request);
    }

    /** DELETE /api/projects/{id} — xóa. Trả 204 No Content. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        projectService.delete(id);
    }
}