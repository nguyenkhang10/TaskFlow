package com.example.taskflow.dto;

import com.example.domain.Project;
import com.example.domain.ProjectStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Dữ liệu client GỬI LÊN khi tạo/cập nhật project (input DTO).
 * <p>
 * Validate ngay ở biên bằng Bean Validation ({@code @NotBlank}, {@code @Size}...).
 * Không nhận {@code id}, {@code createdAt}, {@code updatedAt} từ client — những
 * field đó do hệ thống/DB quản lý.
*/
public class ProjectResponse (
    Long id,
    String name,
    String description,
    ProjectStatus status,
    String owner,
    LocalDate startDate,
    LocalDate dueDate,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
        public static ProjectResponse from(Project p) {
            return new ProjectResponse(
                    p.getId(),
                    p.getName(),
                    p.getDescription(),
                    p.getStatus(),
                    p.getOwner(),
                    p.getStartDate(),
                    p.getDueDate(),
                    p.getCreatedAt(),
                    p.getUpdatedAt()
            );
        }
}
