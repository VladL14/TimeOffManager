package com.example.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final ProjectAssignmentRepository projectAssignmentRepository;

    public DataLoader(UserRepository userRepository, ProjectRepository projectRepository, LeaveRequestRepository leaveRequestRepository, LeaveTypeRepository leaveTypeRepository, ProjectAssignmentRepository projectAssignmentRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.projectAssignmentRepository = projectAssignmentRepository;
    }


    @Override
    public void run(String... args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        InputStream usersStream = getClass().getResourceAsStream("/users.json");
        List<User> users = Arrays.asList(mapper.readValue(usersStream, User[].class));
        userRepository.saveAll(users);

        InputStream projectsStream = getClass().getResourceAsStream("/projects.json");
        List<Project> projects = Arrays.asList(mapper.readValue(projectsStream, Project[].class));
        projectRepository.saveAll(projects);

        InputStream leaveRequestsStream = getClass().getResourceAsStream("/leaveRequests.json");
        List<LeaveRequest> leaveRequests = Arrays.asList(mapper.readValue(leaveRequestsStream, LeaveRequest[].class));
        leaveRequestRepository.saveAll(leaveRequests);

        InputStream leaveTypesStream = getClass().getResourceAsStream("/leaveTypes.json");
        List<LeaveType> leaveTypes = Arrays.asList(mapper.readValue(leaveTypesStream, LeaveType[].class));
        leaveTypeRepository.saveAll(leaveTypes);

        InputStream projectAssignmentsStream = getClass().getResourceAsStream("/projectsAssignments.json");
        List<ProjectAssignment> projectAssignments = Arrays.asList(mapper.readValue(projectAssignmentsStream, ProjectAssignment[].class));
        projectAssignmentRepository.saveAll(projectAssignments);

    }

}
