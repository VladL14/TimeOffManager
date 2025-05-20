package com.example.backend.services;

import com.example.backend.entities.Project;
import com.example.backend.entities.ProjectAssignment;
import com.example.backend.entities.User;
import com.example.backend.repositories.ProjectAssignmentRepository;
import com.example.backend.repositories.ProjectRepository;
import com.example.backend.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProjectAssignmentService {
    private ProjectAssignmentRepository projectAssignmentRepository;
    private ProjectRepository projectRepository;
    private UserRepository userRepository;

    public ProjectAssignmentService(ProjectAssignmentRepository projectAssignmentRepository, ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectAssignmentRepository = projectAssignmentRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> createProjectAssignment(ProjectAssignment projectAssignment) {
        long userId = projectAssignment.getUser().getId();
        long projectId = projectAssignment.getProject().getId();

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found.");
        }

        Optional<Project> projectOptional = projectRepository.findById(projectId);
        if (projectOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Project not found.");
        }

        Optional<ProjectAssignment> existingAssignment = projectAssignmentRepository.findByUserIdAndProjectId((int) userId, (int) projectId);
        if (existingAssignment.isPresent()) {
            return ResponseEntity.badRequest().body("This user is already assigned to the project.");
        }

        ProjectAssignment savedAssignment = projectAssignmentRepository.save(projectAssignment);
        return ResponseEntity.ok(savedAssignment);
    }

}