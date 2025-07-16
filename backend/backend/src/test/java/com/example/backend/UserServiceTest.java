package com.example.backend;

import com.example.backend.entities.Project;
import com.example.backend.entities.ProjectAssignment;
import com.example.backend.entities.User;
import com.example.backend.repositories.LeaveTypeRepository;
import com.example.backend.repositories.ProjectAssignmentRepository;
import com.example.backend.repositories.ProjectRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private LeaveTypeRepository leaveTypeRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectAssignmentRepository projectAssignmentRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testGetAllUsers() {
        User user = new User();
        user.setId(1);
        user.setName("Alex");

        when(userRepository.findAll()).thenReturn(List.of(user));
        List<User> users = userService.getAllUsers();
        assertEquals(1, users.size());
    }

    @Test
    void testGetUserById() {
        User user = new User();
        user.setId(1);
        user.setName("Alex");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User result = userService.getUserById(1L);
        assertEquals(user, result);
    }

    @Test
    void testIsManager_True() {
        User user = new User();
        user.setId(1);
        user.setName("Alex");
        user.setRole("Manager");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        boolean result = userService.isManager(1L);
        assertTrue(result);
    }

    @Test
    void testIsManager_False_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        boolean result = userService.isManager(1L);
        assertFalse(result);
    }

    @Test
    void testIsManager_False_NotManager() {
        User user = new User();
        user.setId(1);
        user.setName("Alex");
        user.setRole("User");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        boolean result = userService.isManager(1L);
        assertFalse(result);
    }

    @Test
    void testCreateUser() {
        User user = new User();
        user.setRole("User");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1);
            return savedUser;
        });

        User result = userService.createUser(user);
        assertEquals(1, result.getId());
        assertTrue(result.getIsActive());

        verify(leaveTypeRepository).saveAll(anyList());
    }

    @Test
    void testDeleteUser_UserExists() {
        User user = new User();
        user.setId(1);
        user.setIsActive(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        boolean result = userService.deleteUser(1L);

        assertFalse(user.getIsActive());
        assertTrue(result);
        verify(userRepository).save(user);
    }

    @Test
    void testDeleteUser_UserDoesNotExist() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        boolean result = userService.deleteUser(2L);
        assertFalse(result);
    }

    @Test
    void testGetSubordinates_Success() {
        User manager = new User();
        manager.setId(1);
        manager.setRole("Manager");

        Project project = new Project();
        project.setId(100);

        User subordinate = new User();
        subordinate.setId(99);

        ProjectAssignment projectAssignment = new ProjectAssignment();
        projectAssignment.setUser(subordinate);
        projectAssignment.setProject(project);

        when(userRepository.findById(1L)).thenReturn(Optional.of(manager));
        when(projectRepository.findByManagerId(1L)).thenReturn(List.of(project));
        when(projectAssignmentRepository.findByProjectIdIn(List.of(100))).thenReturn(List.of(projectAssignment));
        when(userRepository.findAllById(Set.of(99L))).thenReturn(List.of(subordinate));

        ResponseEntity<?> response = userService.getSubordinates(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof List<?>);

        verify(userRepository).findAllById(Set.of(99L));
    }

    @Test
    void testGetSubordinates_NotManager() {
        User user = new User();
        user.setId(1);
        user.setIsActive(true);
        user.setRole("User");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        ResponseEntity<?> response = userService.getSubordinates(1L);

        assertEquals(403, response.getStatusCodeValue());
        assertEquals("Access Denied!User is not a Manager.", response.getBody());
    }
}
