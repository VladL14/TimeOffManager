package com.example.backend.controllers;

import com.example.backend.entities.Project;
import com.example.backend.entities.User;
import com.example.backend.repositories.ProjectRepository;
import com.example.backend.services.ProjectService;
import com.example.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "http://localhost:4200")
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;

    public ProjectController(ProjectService projectService, ProjectRepository projectRepository) {
        this.projectService = projectService;
        this.projectRepository = projectRepository;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Project>> getProjectsByUserId(@PathVariable int userId) {
        List<Project> projects = projectService.getProjectsByUserId(userId);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{projectId}/users")
    public ResponseEntity<List<User>> getUsersByProject(@PathVariable int projectId) {
        List<User> users = projectService.getUsersForProject(projectId);
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public Project addProject(@RequestBody Project project) {
        return projectRepository.save(project);
    }
}