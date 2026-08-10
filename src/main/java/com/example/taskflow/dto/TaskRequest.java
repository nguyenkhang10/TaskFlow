package com.example.taskflow.dto;

import com.example.taskflow.domain.Task;
import com.example.taskflow.domain.TaskPriority;
import com.example.taskflow.domain.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TaskRequest(
        Long projectId,

        @NotBlank(message = "Task title cannot be blank!")
        @Size(min = 1, max = 255, message = "Task title must be between 1 and 255 characters")
        String title,

        @Size(max = 2000, message = "Description max 2000 characters")
        String description,

        TaskStatus status,

        TaskPriority priority,

        @Size(max = 120, message = "Assignee name max 120 characters")
        String assignee,

        Long creatorId,

        Long handlerId,

        LocalDate dueDate
) {
    /** Chuyển DTO input → domain model để đẩy xuống tầng Service/Mapper. */
    public Task toDomain(Long defaultProjectId) {
        Task t = new Task();
        t.setProjectId(projectId != null ? projectId : defaultProjectId);
        t.setTitle(title);
        t.setDescription(description);
        t.setStatus(status != null ? status : TaskStatus.TODO);
        t.setPriority(priority != null ? priority : TaskPriority.MEDIUM);
        t.setAssignee(assignee);
        t.setCreatorId(creatorId);
        t.setHandlerId(handlerId);
        t.setDueDate(dueDate);
        return t;
    }
}
