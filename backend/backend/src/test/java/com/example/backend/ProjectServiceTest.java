package com.example.backend;

import com.example.backend.entities.Project;
import com.example.backend.entities.User;
import com.example.backend.repositories.ProjectAssignmentRepository;
import com.example.backend.repositories.ProjectRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.services.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectAssignmentRepository projectAssignmentRepository;

    @InjectMocks
    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        projectService = new ProjectService(projectRepository, userRepository, projectAssignmentRepository);
    }

    @Test
    void testCreateProject_Success() {
        Project project = new Project();
        project.setName("Project A");
        project.setDescription("Description A");
        project.setManagerId(1);

        User manager = new User();
        manager.setId(1);
        manager.setRole("Manager");

        when(userRepository.findById(1L)).thenReturn(Optional.of(manager));

        ResponseEntity<?> response = projectService.createProject(project);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(project, response.getBody());
        verify(projectRepository).save(project);
    }

    @Test
    void testCreateProject_MissingFields() {
        Project project = new Project();
        project.setManagerId(1);

        ResponseEntity<?> response = projectService.createProject(project);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Name and Description are required.", response.getBody());
        verify(projectRepository, never()).save(any());
    }

    @Test
    void testCreateProject_UserNotManager() {
        Project project = new Project();
        project.setName("Project A");
        project.setDescription("Description A");
        project.setManagerId(2);

        User nonManager = new User();
        nonManager.setId(2);
        nonManager.setRole("User");

        when(userRepository.findById(2L)).thenReturn(Optional.of(nonManager));

        ResponseEntity<?> response = projectService.createProject(project);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Only Managers can do this action.", response.getBody());
        verify(projectRepository, never()).save(any());
    }

    @Test
    void testGetProjectsByUserId() {
        List<Project> projects = Arrays.asList(new Project(), new Project());
        when(projectRepository.findProjectsByUserId(5)).thenReturn(projects);

        List<Project> result = projectService.getProjectsByUserId(5);

        assertEquals(2, result.size());
        verify(projectRepository).findProjectsByUserId(5);
    }

    @Test
    void testGetUsersForProject() {
        List<User> users = Arrays.asList(new User(), new User(), new User());
        when(projectAssignmentRepository.findUsersByProjectId(7)).thenReturn(users);

        List<User> result = projectService.getUsersForProject(7);

        assertEquals(3, result.size());
        verify(projectAssignmentRepository).findUsersByProjectId(7);
    }
}
