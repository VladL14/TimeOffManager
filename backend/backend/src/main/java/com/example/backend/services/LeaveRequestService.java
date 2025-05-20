package com.example.backend.services;

import com.example.backend.RequestStatus;
import com.example.backend.entities.*;
import com.example.backend.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class LeaveRequestService {
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectAssignmentRepository projectAssignmentRepository;

    @Autowired
    public LeaveRequestService(LeaveRequestRepository leaveRequestRepository, UserService userService, LeaveTypeRepository leaveTypeRepository, LeaveTypeService leaveTypeService, UserRepository userRepository, ProjectRepository projectRepository, ProjectAssignmentRepository projectAssignmentRepository)
    {
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.projectAssignmentRepository = projectAssignmentRepository;
    }

    public List<LeaveRequest> getAll()
    {
        return leaveRequestRepository.findAll();
    }

    public boolean isUserManager(int userId)
    {
        Optional<User> optionalUser = userRepository.findById((long) userId);
        if(optionalUser.isEmpty())
        {
            return false;
        }
        User user = optionalUser.get();
        return "Manager".equalsIgnoreCase(user.getRole());
    }

    public boolean isUserAdmin(int userId)
    {
        Optional<User> optionalUser = userRepository.findById((long) userId);
        if(optionalUser.isEmpty())
        {
            return false;
        }
        User user = optionalUser.get();
        return "Admin".equalsIgnoreCase(user.getRole());
    }


    public boolean isUserActive(int userId)
    {
        Optional<User> optionalUser = userRepository.findById((long) userId);
        if(optionalUser.isEmpty())
        {
            return false;
        }
        User user = optionalUser.get();
        return user.getIsActive();
    }


    public ResponseEntity<?> createLeaveRequest(LeaveRequest leaveRequest)
    {
        // Setarile initiale
        leaveRequest.setId(null);
        leaveRequest.setApprovedBy(null);
        leaveRequest.setStatus(RequestStatus.PENDING);

        // Verificam daca startDate-ul si endDate-ul sunt valide
        Date startDate = leaveRequest.getStartDate();
        Date endDate = leaveRequest.getEndDate();
        if(startDate == null || endDate == null || endDate.before(startDate))
        {
            return ResponseEntity.badRequest().body("Invalid start or end date.");
        }

        // Calculam zilele cerute de concediu
        long diffInMillies = endDate.getTime() - startDate.getTime();
        long daysRequested = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) + 1;

        int userId = leaveRequest.getUserId();
        String leaveTypeName = leaveRequest.getLeaveTypeName();

        // Cautam tipul de concediu in baza de date
        Optional<LeaveType> optionalLeaveType = leaveTypeRepository.findByUserIdAndName(userId,leaveTypeName);
        if(optionalLeaveType.isEmpty())
        {
            return ResponseEntity.badRequest().body("Leave type does not exist.");
        }

        LeaveType leaveType = optionalLeaveType.get();
        // Verificam daca user-ul are destule zile de concediu
        if(leaveType.getBalanceDays() < daysRequested)
        {
            return ResponseEntity.badRequest().body("Not enough leave balance.");
        }

        leaveTypeRepository.save(leaveType);
        LeaveRequest savedRequest = leaveRequestRepository.save(leaveRequest);

        return ResponseEntity.ok(savedRequest);
    }

    public ResponseEntity<?> updateLeaveRequest(Long id,LeaveRequest updatedRequest) {
        Optional<LeaveRequest> optionalLeaveRequest = leaveRequestRepository.findById(id);
        if(optionalLeaveRequest.isEmpty())
        {
            return ResponseEntity.notFound().build();
        }
        LeaveRequest existingRequest = optionalLeaveRequest.get();

        if (existingRequest.getStatus() != RequestStatus.PENDING) {
            return ResponseEntity.badRequest().body("The request cannot be modified because it has already been approved or rejected.");
        }


        existingRequest.setStartDate(updatedRequest.getStartDate());
        existingRequest.setEndDate(updatedRequest.getEndDate());
        existingRequest.setNotes(updatedRequest.getNotes());

        leaveRequestRepository.save(existingRequest);

        return ResponseEntity.ok(existingRequest);
    }

    public List<LeaveRequest> getLeaveRequestByUser(int userId)
    {
        return leaveRequestRepository.findByUserId(userId);
    }

    public ResponseEntity<?> approveLeaveRequest(long id,int givenId) {
        if(isUserManager(givenId) || isUserAdmin(givenId)) {
            if(isUserActive(givenId)) {
                Optional<LeaveRequest> optionalLeaveRequest = leaveRequestRepository.findById(id);

                if (optionalLeaveRequest.isEmpty()) {
                    return ResponseEntity.notFound().build();
                }

                LeaveRequest leaveRequest = optionalLeaveRequest.get();

                if (leaveRequest.getStatus() != RequestStatus.PENDING) {
                    return ResponseEntity.badRequest().body("The request cannot be approved");
                }

                Date startDate = leaveRequest.getStartDate();
                Date endDate = leaveRequest.getEndDate();
                long diffInMillis = endDate.getTime() - startDate.getTime();
                long daysRequested = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS) + 1;

                int userId = leaveRequest.getUserId();

                Optional<LeaveType> optionalLeaveType = leaveTypeRepository.findByUserIdAndName(userId, leaveRequest.getLeaveTypeName());
                if (optionalLeaveType.isEmpty()) {
                    return ResponseEntity.badRequest().body("User does not have leave type configured.");
                }

                LeaveType leaveType = optionalLeaveType.get();
                String leaveName = leaveType.getName();

                if (leaveName.equalsIgnoreCase("Vacation") || leaveName.equalsIgnoreCase("Sick Leave")
                        || leaveName.equalsIgnoreCase("Unpaid")) {
                    if (leaveType.getBalanceDays() < daysRequested) {
                        return ResponseEntity.badRequest().body("Not enough " + leaveName.toLowerCase() + " days available.");
                    }
                    leaveType.setBalanceDays(leaveType.getBalanceDays() - (int) daysRequested);
                    leaveTypeRepository.save(leaveType);
                } else {
                    return ResponseEntity.badRequest().body("Unsupported leave type.");
                }

                leaveRequest.setStatus(RequestStatus.APPROVED);
                leaveRequest.setApprovedBy(givenId);
                leaveRequestRepository.save(leaveRequest);

                return ResponseEntity.ok(leaveRequest);
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Manager or Admin account is inactive.");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.Only managers and admins can approve requests.");
    }

    public ResponseEntity<?> rejectLeaveRequest(long id, int givenId)
    {
        if(isUserManager(givenId) || isUserAdmin(givenId)) {
            if(isUserActive(givenId)) {
                Optional<LeaveRequest> optionalLeaveRequest = leaveRequestRepository.findById(id);
                if (optionalLeaveRequest.isEmpty()) {
                    return ResponseEntity.notFound().build();
                }
                LeaveRequest leaveRequest = optionalLeaveRequest.get();
                if (leaveRequest.getStatus() != RequestStatus.PENDING) {
                    return ResponseEntity.badRequest().body("The request cannot be rejected");
                }
                leaveRequest.setStatus(RequestStatus.REJECTED);
                leaveRequest.setApprovedBy(givenId);
                leaveRequestRepository.save(leaveRequest);

                return ResponseEntity.ok(leaveRequest);
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Manager or Admin account is inactive.");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.Only managers and admins can reject requests.");
    }

    public boolean deleteLeaveRequest(long id)
    {
        Optional<LeaveRequest> optionalLeaveRequest = leaveRequestRepository.findById(id);
        if(optionalLeaveRequest.isEmpty())
            return false;
        LeaveRequest leaveRequest = optionalLeaveRequest.get();
        if(leaveRequest.getStatus() != RequestStatus.PENDING)
        {
            throw new IllegalStateException("The request cannot be deleted");
        }
        leaveRequestRepository.delete(leaveRequest);
        return true;
    }

    public ResponseEntity<?> getSubordinatesLeaveRequests(long managerId)
    {
        if(isUserManager((int) managerId))
        {
            List<Project> managerProjects = projectRepository.findByManagerId(managerId);

            List<Integer> projectIds = new ArrayList<>();
            for (Project project : managerProjects) {
                projectIds.add(project.getId());
            }

            List<ProjectAssignment> assignments = projectAssignmentRepository.findByProjectIdIn(projectIds);

            Set<Long> userIds = new HashSet<>();
            for (ProjectAssignment assignment : assignments) {
                userIds.add(assignment.getUserId());
            }

            List<LeaveRequest> leaveRequests = leaveRequestRepository.findAllByUserIdIn(userIds);
            return ResponseEntity.ok(leaveRequests);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.User is not authorized.");
    }
}