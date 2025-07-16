package com.example.backend;

import com.example.backend.entities.Project;
import com.example.backend.entities.ProjectAssignment;
import com.example.backend.entities.User;
import com.example.backend.repositories.ProjectAssignmentRepository;
import com.example.backend.repositories.ProjectRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.services.ProjectAssignmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProjectAssignmentServiceTest {

    @Mock
    private ProjectAssignmentRepository projectAssignmentRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectAssignmentService projectAssignmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProjectAssignementUserNotFound() {
        User user = new User();
        user.setId(1);
        Project project = new Project();
        project.setId(1);
        ProjectAssignment projectAssignment = new ProjectAssignment();
        projectAssignment.setUser(user);
        projectAssignment.setProject(project);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<?> response = projectAssignmentService.createProjectAssignment(projectAssignment);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("User not found.", response.getBody());
    }

    @Test
    void testCreateProjectAssignementProjectNotFound() {
        User user = new User();
        user.setId(1);
        Project project = new Project();
        project.setId(2);
        ProjectAssignment projectAssignment = new ProjectAssignment();
        projectAssignment.setUser(user);
        projectAssignment.setProject(project);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(projectRepository.findById(2L)).thenReturn(Optional.empty());
        ResponseEntity<?> response = projectAssignmentService.createProjectAssignment(projectAssignment);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Project not found.", response.getBody());
    }

    @Test
    void testCreateProjectAssignementUserAssigned() {
        User user = new User();
        user.setId(1);
        Project project = new Project();
        project.setId(3);
        ProjectAssignment projectAssignment = new ProjectAssignment();
        projectAssignment.setUser(user);
        projectAssignment.setProject(project);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(projectRepository.findById(3L)).thenReturn(Optional.of(project));
        when(projectAssignmentRepository.findByUserIdAndProjectId(1, 3)).thenReturn(Optional.of(projectAssignment));
        ResponseEntity<?> response = projectAssignmentService.createProjectAssignment(projectAssignment);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("This user is already assigned to the project.", response.getBody());

    }

    @Test
    void testCreateProjectAssignementDone() {
        User user = new User();
        user.setId(1);
        Project project = new Project();
        project.setId(4);
        ProjectAssignment projectAssignment = new ProjectAssignment();
        projectAssignment.setUser(user);
        projectAssignment.setProject(project);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(projectRepository.findById(4L)).thenReturn(Optional.of(project));
        ResponseEntity<?> response = projectAssignmentService.createProjectAssignment(projectAssignment);
        assertEquals(200, response.getStatusCodeValue());

    }
}
