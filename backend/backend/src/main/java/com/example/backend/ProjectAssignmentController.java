package com.example.backend;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@CrossOrigin(origins = "http://localhost:4200")
public class ProjectAssignmentController {
    private final ProjectAssignmentRepository projectAssignmentRepository;

    public ProjectAssignmentController(ProjectAssignmentRepository projectAssignmentRepository) {
        this.projectAssignmentRepository = projectAssignmentRepository;
    }

    @GetMapping
    public List<ProjectAssignment> getAllAssignments() {
        return projectAssignmentRepository.findAll();
    }
}
