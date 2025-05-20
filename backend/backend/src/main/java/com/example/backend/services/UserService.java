package com.example.backend.services;

import com.example.backend.entities.LeaveType;
import com.example.backend.entities.Project;
import com.example.backend.entities.ProjectAssignment;
import com.example.backend.entities.User;
import com.example.backend.repositories.LeaveTypeRepository;
import com.example.backend.repositories.ProjectAssignmentRepository;
import com.example.backend.repositories.ProjectRepository;
import com.example.backend.repositories.UserRepository;
import org.springframework.expression.spel.ast.OpAnd;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final ProjectRepository projectRepository;
    private final ProjectAssignmentRepository projectAssignmentRepository;

    public UserService(UserRepository userRepository, LeaveTypeRepository leaveTypeRepository, ProjectRepository projectRepository, ProjectAssignmentRepository projectAssignmentRepository) {
        this.userRepository = userRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.projectRepository = projectRepository;
        this.projectAssignmentRepository = projectAssignmentRepository;
    }

    public List<User> getAllUsers()
    {
        return userRepository.findAll();
    }

    public User getUserById(@PathVariable Long id)
    {
        return userRepository.findById(id).get();
    }

    public boolean isManager(Long id)
    {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return false;
        }
        User user = optionalUser.get();
        String role = user.getRole();
        return role.equals("Manager");
    }

    public User createUser(User user)
    {
        user.setId(0);
        user.setIsActive(true);
        User savedUser = userRepository.save(user);

        List<LeaveType> defaultTypes = List.of(
                new LeaveType("Vacation", 21, savedUser),
                new LeaveType("Sick Leave", 183, savedUser),
                new LeaveType("Unpaid", 90, savedUser)
        );
        leaveTypeRepository.saveAll(defaultTypes);

        return savedUser;
    }

    public boolean deleteUser(Long id)
    {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return false;
        }
        User user = optionalUser.get();
        user.setIsActive(false);
        userRepository.save(user);
        return true;
    }

    public ResponseEntity<?> getSubordinates(long managerId)
    {
       if(isManager(managerId)) {
           List<Project> managerProjects = projectRepository.findByManagerId(managerId);

           List<Integer> projectIds = new ArrayList<>();
           for (Project project : managerProjects) {
               projectIds.add(project.getId());
           }

           List<ProjectAssignment> assignments = projectAssignmentRepository.findByProjectIdIn(projectIds);

           Set<Long> userIds = new HashSet<>();
           for (ProjectAssignment assignment : assignments) {
               userIds.add((long) assignment.getUser().getId());
           }

           List<User> subordinates = userRepository.findAllById(userIds);

           return ResponseEntity.ok(subordinates);
       }
       return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied!User is not a Manager.");
    }
}
