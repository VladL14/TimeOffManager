package com.example.backend;

import com.example.backend.entities.*;
import com.example.backend.repositories.*;
import com.example.backend.services.LeaveRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class LeaveRequestServiceTest {

    @Mock
    private LeaveRequestRepository leaveRequestRepository;
    @Mock
    private LeaveTypeRepository leaveTypeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectAssignmentRepository projectAssignmentRepository;

    @InjectMocks
    private LeaveRequestService leaveRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateLeaveRequest_Success() {
        LeaveRequest request = new LeaveRequest();
        request.setUserId(1);
        request.setLeaveTypeName("Vacation");
        Date start = new Date();
        Date end = new Date(start.getTime() + TimeUnit.DAYS.toMillis(2));
        request.setStartDate(start);
        request.setEndDate(end);

        LeaveType leaveType = new LeaveType();
        leaveType.setBalanceDays(10);

        when(leaveTypeRepository.findByUserIdAndName(1, "Vacation"))
                .thenReturn(Optional.of(leaveType));
        when(leaveRequestRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ResponseEntity<?> response = leaveRequestService.createLeaveRequest(request);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testCreateLeaveRequest_InvalidLeaveType() {
        LeaveRequest request = new LeaveRequest();
        request.setUserId(1);
        request.setLeaveTypeName("Unknown");
        request.setStartDate(new Date());
        request.setEndDate(new Date());

        when(leaveTypeRepository.findByUserIdAndName(anyInt(), anyString()))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response = leaveRequestService.createLeaveRequest(request);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void testApproveLeaveRequest_Success() {
        int managerId = 10;
        int userId = 1;
        String leaveTypeName = "Vacation";

        User manager = new User();
        manager.setRole("Manager");
        manager.setIsActive(true);

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setId(1L);
        leaveRequest.setUserId(userId);
        leaveRequest.setLeaveTypeName(leaveTypeName);
        leaveRequest.setStatus(RequestStatus.PENDING);
        leaveRequest.setStartDate(new Date());
        leaveRequest.setEndDate(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(2)));

        LeaveType leaveType = new LeaveType();
        leaveType.setName(leaveTypeName);
        leaveType.setBalanceDays(10);

        when(userRepository.findById((long) managerId)).thenReturn(Optional.of(manager));
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));
        when(leaveTypeRepository.findByUserIdAndName(userId, leaveTypeName)).thenReturn(Optional.of(leaveType));
        when(leaveRequestRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ResponseEntity<?> response = leaveRequestService.approveLeaveRequest(1L, managerId);
        assertEquals(7, leaveType.getBalanceDays());
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(RequestStatus.APPROVED, ((LeaveRequest) response.getBody()).getStatus());
    }

    @Test
    void testRejectLeaveRequest_Success() {
        int managerId = 10;
        User manager = new User();
        manager.setRole("Manager");
        manager.setIsActive(true);

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setId(1L);
        leaveRequest.setStatus(RequestStatus.PENDING);

        when(userRepository.findById((long) managerId)).thenReturn(Optional.of(manager));
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));
        when(leaveRequestRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ResponseEntity<?> response = leaveRequestService.rejectLeaveRequest(1L, managerId);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(RequestStatus.REJECTED, ((LeaveRequest) response.getBody()).getStatus());
    }

    @Test
    void testDeleteLeaveRequest_Success() {
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setId(1L);
        leaveRequest.setStatus(RequestStatus.PENDING);

        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));

        boolean result = leaveRequestService.deleteLeaveRequest(1L);
        assertTrue(result);
        verify(leaveRequestRepository).delete(leaveRequest);
    }

    @Test
    void testUpdateLeaveRequest_Success() {
        LeaveRequest existing = new LeaveRequest();
        existing.setId(1L);
        existing.setStatus(RequestStatus.PENDING);

        LeaveRequest updated = new LeaveRequest();
        updated.setStartDate(new Date());
        updated.setEndDate(new Date());
        updated.setNotes("Updated notes");

        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(leaveRequestRepository.save(any())).thenReturn(existing);

        ResponseEntity<?> response = leaveRequestService.updateLeaveRequest(1L, updated);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testGetSubordinatesLeaveRequests_Success() {
        long managerId = 1L;
        User manager = new User();
        manager.setRole("Manager");
        when(userRepository.findById(managerId)).thenReturn(Optional.of(manager));

        Project project = new Project();
        project.setId(1);

        ProjectAssignment assignment = new ProjectAssignment();
        User user = new User();
        user.setId(2);
        assignment.setUser(user);

        LeaveRequest lr = new LeaveRequest();

        when(projectRepository.findByManagerId(managerId)).thenReturn(List.of(project));
        when(projectAssignmentRepository.findByProjectIdIn(List.of(1))).thenReturn(List.of(assignment));
        when(leaveRequestRepository.findAllByUserIdIn(any())).thenReturn(List.of(lr));

        ResponseEntity<?> response = leaveRequestService.getSubordinatesLeaveRequests(managerId);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, ((List<?>) response.getBody()).size());
    }
}