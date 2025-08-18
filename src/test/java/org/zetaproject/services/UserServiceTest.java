package org.zetaproject.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zetaproject.dao.UserDao;
import org.zetaproject.model.entites.User;
import org.zetaproject.model.enums.UserRole;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User sampleUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setEmail("test@example.com");
        sampleUser.setPassword("plainPassword");
        sampleUser.setRole(UserRole.ADMIN); // Assuming enum Role { ADMIN, CUSTOMER }
    }

    @Test
    public void testCreateUser() {
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userDao.create(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User createdUser = userService.createUser(sampleUser);

        assertNotNull(createdUser);
        assertEquals("encodedPassword", createdUser.getPassword());
        verify(passwordEncoder, times(1)).encode("plainPassword");
        verify(userDao, times(1)).create(createdUser);
    }

    @Test
    public void testGetAllUsers() {
        when(userDao.findAll()).thenReturn(List.of(sampleUser));

        List<User> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("test@example.com", users.get(0).getEmail());
        verify(userDao, times(1)).findAll();
    }

    @Test
    public void testFindByEmail() {
        when(userDao.findByEmail("test@example.com")).thenReturn(sampleUser);

        User foundUser = userService.findByEmail("test@example.com");

        assertNotNull(foundUser);
        assertEquals("test@example.com", foundUser.getEmail());
        verify(userDao, times(1)).findByEmail("test@example.com");
    }

    @Test
    public void testLoadUserByUsername_Success() {
        sampleUser.setPassword("encodedPassword");
        when(userDao.findByEmail("test@example.com")).thenReturn(sampleUser);

        UserDetails userDetails = userService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    public void testLoadUserByUsername_NotFound() {
        when(userDao.findByEmail("missing@example.com")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("missing@example.com"));

        verify(userDao, times(1)).findByEmail("missing@example.com");
    }
}
