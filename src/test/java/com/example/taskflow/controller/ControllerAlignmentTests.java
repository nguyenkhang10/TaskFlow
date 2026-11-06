package com.example.taskflow.controller;

import com.example.taskflow.domain.UserRole;
import com.example.taskflow.domain.UserStatus;
import com.example.taskflow.dto.*;
import com.example.taskflow.security.JwtFilter;
import com.example.taskflow.security.JwtUtil;
import com.example.taskflow.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({
        AppealController.class,
        AdminUserController.class,
        AuthController.class,
        TaskController.class,
        UserController.class
})
@AutoConfigureMockMvc(addFilters = false)
class ControllerAlignmentTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AppealService appealService;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthService authService;

    @MockBean
    private TaskService taskService;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private JwtUtil jwtUtil;

    // --- AppealController Tests ---

    @Test
    @DisplayName("POST /api/appeals returns 201 Created and Location header")
    void testCreateAppeal() throws Exception {
        AppealRequest request = new AppealRequest("john_doe", "Account locked", "Please unlock my account");
        AppealResponse response = new AppealResponse(1L, "john_doe", "Account locked", "Please unlock my account",
                OffsetDateTime.now(), OffsetDateTime.now());

        given(appealService.create(any(AppealRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/appeals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/appeals/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("john_doe"));
    }

    @Test
    @DisplayName("GET /api/appeals searches appeals")
    void testSearchAppeals() throws Exception {
        AppealResponse response = new AppealResponse(1L, "john_doe", "Account locked", "Please unlock",
                OffsetDateTime.now(), OffsetDateTime.now());
        given(appealService.search(eq("john_doe"), eq("locked"))).willReturn(List.of(response));

        mockMvc.perform(get("/api/appeals")
                        .param("username", "john_doe")
                        .param("keyword", "locked"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("DELETE /api/appeals/{id} returns 204 No Content")
    void testDeleteAppeal() throws Exception {
        mockMvc.perform(delete("/api/appeals/1"))
                .andExpect(status().isNoContent());

        verify(appealService).delete(1L);
    }

    // --- AdminUserController Tests ---

    @Test
    @DisplayName("PUT and PATCH /api/admin/users/{id} call updateAdmin")
    void testAdminUserUpdatePutAndPatch() throws Exception {
        UserRequest request = new UserRequest("john", "john@example.com", "John Doe", "password123", UserRole.MEMBER, UserStatus.ACTIVE);
        UserResponse response = new UserResponse(1L, "john", "john@example.com", "John Doe",
                OffsetDateTime.now(), OffsetDateTime.now(), UserRole.MEMBER, UserStatus.ACTIVE, null);

        given(userService.updateAdmin(eq(1L), any(UserRequest.class))).willReturn(response);

        // Test PUT
        mockMvc.perform(put("/api/admin/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"));

        // Test PATCH
        mockMvc.perform(patch("/api/admin/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"));

        verify(userService, org.mockito.Mockito.times(2)).updateAdmin(eq(1L), any(UserRequest.class));
    }

    // --- AuthController Tests ---

    @Test
    @DisplayName("POST /api/auth/signup returns 201 Created and LoginResponse")
    void testAuthSignup() throws Exception {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setEmail("newuser@example.com");
        request.setFullName("New User");

        LoginResponse response = new LoginResponse("mock-jwt-token");
        given(authService.signup(any(SignUpRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));
    }

    @Test
    @DisplayName("POST /api/auth/password checks user password")
    void testCheckPassword() throws Exception {
        CheckPasswordRequest request = new CheckPasswordRequest();
        request.setPassword("currentPassword");

        mockMvc.perform(post("/api/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // --- TaskController Tests ---

    @Test
    @DisplayName("GET /api/projects/{projectId}/tasks and /api/project/{projectId}/tasks return tasks")
    void testGetTasksByProjectId() throws Exception {
        TaskResponse task = new TaskResponse(1L, 10L, "Task 1", "Desc", null, null, "assignee", 1L, 2L, null, OffsetDateTime.now(), OffsetDateTime.now());
        given(taskService.getByProjectId(10L)).willReturn(List.of(task));

        mockMvc.perform(get("/api/projects/10/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        mockMvc.perform(get("/api/project/10/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("PATCH /api/tasks/{id} updates task")
    void testPatchTask() throws Exception {
        TaskRequest request = new TaskRequest(10L, "Updated Title", "Desc", null, null, "assignee", 1L, 2L, null);
        TaskResponse response = new TaskResponse(1L, 10L, "Updated Title", "Desc", null, null, "assignee", 1L, 2L, null, OffsetDateTime.now(), OffsetDateTime.now());

        given(taskService.update(eq(1L), any(TaskRequest.class))).willReturn(response);

        mockMvc.perform(patch("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    // --- UserController Tests ---

    @Test
    @DisplayName("GET /api/users search with projectId, role, keyword")
    void testSearchUsers() throws Exception {
        UserResponse user = new UserResponse(1L, "alice", "alice@example.com", "Alice Smith",
                OffsetDateTime.now(), OffsetDateTime.now(), UserRole.MEMBER, UserStatus.ACTIVE, null);
        given(userService.search(eq(10L), eq(UserRole.MEMBER), eq("alice"))).willReturn(List.of(user));

        mockMvc.perform(get("/api/users")
                        .param("projectId", "10")
                        .param("role", "MEMBER")
                        .param("keyword", "alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("alice"));
    }

    @Test
    @DisplayName("PUT and PATCH /api/users/{id} update user profile")
    void testUpdateUserProfile() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest("alice", "alice_new@example.com", "Alice New", "newpass123");
        UserResponse response = new UserResponse(1L, "alice", "alice_new@example.com", "Alice New",
                OffsetDateTime.now(), OffsetDateTime.now(), UserRole.MEMBER, UserStatus.ACTIVE, null);

        given(userService.update(eq(1L), any(), any(UserUpdateRequest.class))).willReturn(response);

        mockMvc.perform(patch("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Alice New"));
    }

    @Test
    @DisplayName("GET /api/appeals/{id} returns single appeal")
    void testGetAppealById() throws Exception {
        AppealResponse response = new AppealResponse(1L, "john_doe", "Account locked", "Please unlock",
                OffsetDateTime.now(), OffsetDateTime.now());
        given(appealService.getById(1L)).willReturn(response);

        mockMvc.perform(get("/api/appeals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Account locked"));
    }
}
