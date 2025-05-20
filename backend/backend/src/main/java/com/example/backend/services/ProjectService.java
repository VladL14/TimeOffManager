package com.example.backend.services;

import com.example.backend.entities.Project;
import com.example.backend.entities.User;
import com.example.backend.repositories.ProjectAssignmentRepository;
import com.example.backend.repositories.ProjectRepository;
import com.example.backend.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectAssignmentRepository projectAssignmentRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository, ProjectAssignmentRepository projectAssignmentRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectAssignmentRepository = projectAssignmentRepository;
    }

    boolean isUserManager(int userId)
    {
        Optional<User> optionalUser = userRepository.findById((long) userId);
        return optionalUser.filter(user -> "Manager".equalsIgnoreCase(user.getRole())).isPresent();
    }

    public ResponseEntity<?> createProject(Project project) {
        String name = project.getName();
        String description = project.getDescription();
        int managerId = project.getManagerId();

        if(name == null || description == null) {
            return ResponseEntity.badRequest().body("Name and Description are required.");
        }

        if(!isUserManager(managerId)) {
            return ResponseEntity.badRequest().body("Only Managers can do this action.");
        }
        projectRepository.save(project);
        return ResponseEntity.ok().body(project);
    }

    public List<Project> getProjectsByUserId(int userId) {
        return projectRepository.findProjectsByUserId(userId);
    }

    public List<User> getUsersForProject(int projectId) {
        return projectAssignmentRepository.findUsersByProjectId(projectId);
    }
}