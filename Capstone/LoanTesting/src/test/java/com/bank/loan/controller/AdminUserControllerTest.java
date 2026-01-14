package com.bank.loan.controller;

import com.bank.loan.dto.UserCreateRequest;
import com.bank.loan.dto.UserResponseDto;
import com.bank.loan.dto.UserStatusUpdateRequest;
import com.bank.loan.exception.GlobalExceptionHandler;
import com.bank.loan.model.Role;
import com.bank.loan.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminUserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminUserController adminUserController;

    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(adminUserController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

    }

    @Test
    void listUsers_returnsList() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(
                UserResponseDto.builder().id("1").email("a@bank.com").role(Role.USER).active(true).build(),
                UserResponseDto.builder().id("2").email("b@bank.com").role(Role.ADMIN).active(false).build()
        ));

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void createUser_callsService() throws Exception {
        UserCreateRequest req = new UserCreateRequest();
        req.setEmail("a@bank.com");
        req.setPassword("pass");
        req.setRole(Role.USER);

        UserResponseDto dto = UserResponseDto.builder()
                .id("1").email("a@bank.com").role(Role.USER).active(true).build();
        when(userService.createUser(any())).thenReturn(dto);

        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("a@bank.com"));
    }

    @Test
    void updateStatus_callsService() throws Exception {
        UserStatusUpdateRequest req = new UserStatusUpdateRequest();
        req.setActive(false);
        UserResponseDto dto = UserResponseDto.builder()
                .id("1").email("a@bank.com").role(Role.USER).active(false).build();
        when(userService.updateUserStatus(eq("1"), any())).thenReturn(dto);

        mockMvc.perform(put("/api/admin/users/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }
}
