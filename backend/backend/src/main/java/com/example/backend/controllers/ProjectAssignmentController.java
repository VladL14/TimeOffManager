package com.example.backend.controllers;

import com.example.backend.entities.ProjectAssignment;
import com.example.backend.repositories.ProjectAssignmentRepository;
import com.example.backend.services.ProjectAssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@CrossOrigin(origins = "http://localhost:4200")
public class ProjectAssignmentController {
    private final ProjectAssignmentRepository projectAssignmentRepository;
    private final ProjectAssignmentService projectAssignmentService;

    public ProjectAssignmentController(ProjectAssignmentRepository projectAssignmentRepository, ProjectAssignmentService projectAssignmentService) {
        this.projectAssignmentRepository = projectAssignmentRepository;
        this.projectAssignmentService = projectAssignmentService;
    }

    @GetMapping
    public List<ProjectAssignment> getAllAssignments() {
        return projectAssignmentRepository.findAll();
    }

    @PostMapping
    public ProjectAssignment addAssignment(@RequestBody ProjectAssignment projectAssignment) {
        return projectAssignmentRepository.save(projectAssignment);
    }

    @PostMapping("/direct")
    public ResponseEntity<?> assignUserToProject(@RequestBody ProjectAssignment projectAssignment) {
        return projectAssignmentService.createProjectAssignment(projectAssignment);
    }
}