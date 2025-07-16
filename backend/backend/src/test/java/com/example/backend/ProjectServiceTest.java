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
    }

    @Test
    void testCreateProject_missingFields(){
        Project project = new Project();
        project.setManagerId(1);
        ResponseEntity<?> response = projectService.createProject(project);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Name and Description are required.", response.getBody());
    }

    @Test
    void testCreateProject_userNotManager(){
        Project project = new Project();
        project.setManagerId(2);
        project.setDescription("descriere");
        project.setName("nume");

        User user = new User();
        user.setId(2);
        user.setRole("User");
        // simulam existenta user

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        ResponseEntity<?> response = projectService.createProject(project);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Only Managers can do this action.", response.getBody());

    }

    @Test
    void testCreateProject_success() {
        Project project = new Project();
        project.setName("nume");
        project.setDescription("descriere");
        project.setManagerId(1);
        User manager = new User();
        manager.setId(1);
        manager.setRole("Manager");
        when(userRepository.findById(1L)).thenReturn(Optional.of(manager));
        ResponseEntity<?> response = projectService.createProject(project);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(project, response.getBody());
    }


    @Test
    void testGetProjectsByUserId() {
        Project p1 = new Project();
        p1.setName("p1");
        Project p2 = new Project();
        p2.setName("p2");
        List<Project> projects = Arrays.asList(p1,p2);
        when(projectRepository.findProjectsByUserId(2)).thenReturn(projects);
        List<Project> result = projectService.getProjectsByUserId(2);
        assertEquals(2, result.size());
        assertEquals(p1, result.get(0));
        assertEquals(p2, result.get(1));

    }

    @Test
    void testGetUsersForProject() {
        User u1 = new User();
        u1.setName("u1");
        User u2 = new User();
        u2.setName("u2");
        User u3 = new User();
        u3.setName("u3");
        List<User> users = Arrays.asList(u1,u2,u3);
        when(projectAssignmentRepository.findUsersByProjectId(3)).thenReturn(users);
        List<User> result = projectService.getUsersForProject(3);
        assertEquals(3, result.size());
        assertEquals(u1, result.get(0));
        assertEquals(u2, result.get(1));
        assertEquals(u3, result.get(2));
    }
}
