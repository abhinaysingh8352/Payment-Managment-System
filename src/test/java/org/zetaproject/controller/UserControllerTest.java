package org.zetaproject.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zetaproject.dto.RegisterRequest;
import org.zetaproject.dto.UserResponse;
import org.zetaproject.model.entites.User;
import org.zetaproject.model.enums.UserRole;
import org.zetaproject.services.UserService;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User user1;
    private User user2;

    @BeforeEach
    public void setUp() {
        user1 = new User();
        user1.setId(1L);
        user1.setName("Alice");
        user1.setEmail("alice@example.com");
        user1.setPassword("password123");
        user1.setRole(UserRole.ADMIN);

        user2 = new User();
        user2.setId(2L);
        user2.setName("Bob");
        user2.setEmail("bob@example.com");
        user2.setPassword("secret456");
        user2.setRole(UserRole.ADMIN);
    }

    @Test
    public void testCreateUser() {
        RegisterRequest request = new RegisterRequest(
                "Charlie",
                "charlie@example.com",
                "pass789",
                UserRole.ADMIN
        );

        User createdUser = new User();
        createdUser.setId(3L);
        createdUser.setName(request.getName());
        createdUser.setEmail(request.getEmail());
        createdUser.setPassword(request.getPassword());
        createdUser.setRole(request.getRole());

        when(userService.createUser(any(User.class))).thenReturn(createdUser);

        UserResponse response = userController.createUser(request);

        assertThat(response.getId()).isEqualTo(3L);
        assertThat(response.getName()).isEqualTo("Charlie");
        assertThat(response.getEmail()).isEqualTo("charlie@example.com");
        assertThat(response.getRole()).isEqualTo(UserRole.ADMIN);

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    public void testGetAllUsers() {
        when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        List<UserResponse> responses = userController.getAllUsers();

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getName()).isEqualTo("Alice");
        assertThat(responses.get(1).getName()).isEqualTo("Bob");

        verify(userService, times(1)).getAllUsers();
    }
}
