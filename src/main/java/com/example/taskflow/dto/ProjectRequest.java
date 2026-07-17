package com.example.taskflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.example.domain.Project;
import com.example.domain.ProjectStatus;

import java.time.LocalDate;

/**
 * Dữ liệu client GỬI LÊN khi tạo/cập nhật project (input DTO).
 * <p>
 * Validate ngay ở biên bằng Bean Validation ({@code @NotBlank}, {@code @Size}...).
 * Không nhận {@code id}, {@code createdAt}, {@code updatedAt} từ client — những
 * field đó do hệ thống/DB quản lý.
 */
public class ProjectRequest {
    @NotBlank(message = "không được để trống")
    @Size(min = 3, max = 367, message = "3 đến 367 ký tự")
    String name,

    @Size(max = 367, message = "tối đa 367 ký tự")
    String description,

    ProjectStatus status,   // có thể null → service sẽ mặc định PLANNING

    @NotBlank(message = "không được để trống")
    @Size(max = 367, message = "tối đa 367 kí tự")
    String owner,

    LocalDate startDate,
    LocalDate dueDate
) {
        /** Chuyển DTO input → domain model để đẩy xuống tầng Service/Mapper. */
        public Project toDomain() {
            Project p = new Project();
            p.setName(name);
            p.setDescription(description);
            p.setStatus(status != null ? status : ProjectStatus.PLANNING);
            p.setOwner(owner);
            p.setStartDate(startDate);
            p.setDueDate(dueDate);
            return p;
        }
    }
