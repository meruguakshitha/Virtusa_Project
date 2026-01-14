package com.bank.loan.service;

import com.bank.loan.dto.UserCreateRequest;
import com.bank.loan.dto.UserResponseDto;
import com.bank.loan.dto.UserStatusUpdateRequest;
import com.bank.loan.model.Role;
import com.bank.loan.model.User;
import com.bank.loan.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCurrentUser_returnsUserDto() {
        User user = User.builder()
                .id("1")
                .email("rm@bank.com")
                .role(Role.USER)
                .active(true)
                .build();
        when(userRepository.findByEmail("rm@bank.com")).thenReturn(Optional.of(user));

        UserResponseDto dto = userService.getCurrentUser("rm@bank.com");

        assertEquals("rm@bank.com", dto.getEmail());
        assertEquals(Role.USER, dto.getRole());
    }

    @Test
    void getCurrentUser_throwsWhenNotFound() {
        when(userRepository.findByEmail("x@bank.com")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> userService.getCurrentUser("x@bank.com"));
    }

    @Test
    void getAllUsers_mapsToDtos() {
        User u1 = User.builder().id("1").email("a@bank.com").role(Role.USER).active(true).build();
        User u2 = User.builder().id("2").email("b@bank.com").role(Role.ADMIN).active(false).build();
        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        List<UserResponseDto> list = userService.getAllUsers();

        assertEquals(2, list.size());
        assertEquals("a@bank.com", list.get(0).getEmail());
        assertEquals(Role.ADMIN, list.get(1).getRole());
    }

    @Test
    void createUser_success() {
        UserCreateRequest req = new UserCreateRequest();
        req.setEmail("rm@bank.com");
        req.setPassword("pass");
        req.setRole(Role.USER);

        when(userRepository.existsByEmail("rm@bank.com")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("ENC_PASS");
        when(userRepository.save(any())).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId("123");
            return u;
        });

        UserResponseDto dto = userService.createUser(req);

        assertEquals("rm@bank.com", dto.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_throwsWhenEmailExists() {
        UserCreateRequest req = new UserCreateRequest();
        req.setEmail("rm@bank.com");
        req.setPassword("pass");
        req.setRole(Role.USER);

        when(userRepository.existsByEmail("rm@bank.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserStatus_updatesActiveFlag() {
        User user = User.builder()
                .id("1")
                .email("rm@bank.com")
                .role(Role.USER)
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        UserStatusUpdateRequest req = new UserStatusUpdateRequest();
        req.setActive(false);

        UserResponseDto dto = userService.updateUserStatus("1", req);

        assertFalse(dto.isActive());
        verify(userRepository).save(any(User.class));
    }


    @Test
    void updateUserStatus_throwsWhenUserNotFound() {
        when(userRepository.findById("99")).thenReturn(Optional.empty());

        UserStatusUpdateRequest req = new UserStatusUpdateRequest();
        req.setActive(false);

        assertThrows(IllegalArgumentException.class,
                () -> userService.updateUserStatus("99", req));
    }
}
