package com.example.backend;

import com.example.backend.entities.Project;
import com.example.backend.entities.ProjectAssignment;
import com.example.backend.entities.User;
import com.example.backend.repositories.LeaveTypeRepository;
import com.example.backend.repositories.ProjectAssignmentRepository;
import com.example.backend.repositories.ProjectRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class UserServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private LeaveTypeRepository leaveTypeRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private ProjectAssignmentRepository projectAssignmentRepository;

    @InjectMocks
    private UserService userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1);
        sampleUser.setRole("Manager");
        sampleUser.setIsActive(true);
    }

    @Test
    void testGetAllUsers() {
        User user = new User();
        user.setId(1);
        user.setName("John Doe");

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> users = userService.getAllUsers();

        assertEquals(1, users.size());
    }

    @Test
    void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        User result = userService.getUserById(1L);
        assertEquals(sampleUser, result);
    }

    @Test
    void testIsManager_True() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        boolean result = userService.isManager(1L);
        assertTrue(result);
    }

    @Test
    void testIsManager_False_UserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        boolean result = userService.isManager(2L);
        assertFalse(result);
    }

    @Test
    void testIsManager_False_NotManagerRole(){
        sampleUser.setRole("User");
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        boolean result = userService.isManager(1L);
        assertFalse(result);
    }

    @Test
    void testCreateUser() {
        User newUser = new User();
        newUser.setRole("User");

        when(userRepository.save(ArgumentMatchers.<User>any())).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1);
            return user;
        });

        User result = userService.createUser(newUser);

        assertEquals(1, result.getId());
        assertTrue(result.getIsActive());
        verify(leaveTypeRepository).saveAll(anyList());
    }

    @Test
    void testDeleteUser_UserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        boolean result = userService.deleteUser(1L);
        assertFalse(sampleUser.getIsActive());
        assertTrue(result);
        verify(userRepository).save(sampleUser);
    }

    @Test
    void testDeleteUser_UserDoesNotExist() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        boolean result = userService.deleteUser(2L);
        assertFalse(result);
    }

    @Test
    void testGetSubordinates_Success() {
        Project project = new Project();
        project.setId(100);
        List<Project> projects = List.of(project);

        ProjectAssignment assignment = new ProjectAssignment();
        User subordinate = new User();
        subordinate.setId(99);
        assignment.setUser(subordinate);
        assignment.setProject(project);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(projectRepository.findByManagerId(1L)).thenReturn(projects);
        when(projectAssignmentRepository.findByProjectIdIn(List.of(100)))
                .thenReturn(List.of(assignment));
        when(userRepository.findAllById(Set.of(99L))).thenReturn(List.of(subordinate));

        ResponseEntity<?> response = userService.getSubordinates(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof List<?>);
        verify(userRepository).findAllById(Set.of(99L));
    }

    @Test
    void testGetSubordinates_NotManager() {
        sampleUser.setRole("Employee");
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        ResponseEntity<?> response = userService.getSubordinates(1L);

        assertEquals(403, response.getStatusCodeValue());
        assertEquals("Access Denied!User is not a Manager.", response.getBody());
    }

}
