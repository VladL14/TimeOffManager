package com.example.backend.controllers;

import com.example.backend.entities.LeaveRequest;
import com.example.backend.RequestStatus;
import com.example.backend.repositories.LeaveRequestRepository;
import com.example.backend.services.LeaveRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/leaverequests")
@CrossOrigin(origins = "http://localhost:4200")
public class LeaveRequestController {
    private final LeaveRequestService leaveRequestService;

    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    @GetMapping
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestService.getAll();
    }

    @PostMapping
    public ResponseEntity<?> createLeaveRequest(@RequestBody LeaveRequest leaveRequest)
    {
        return leaveRequestService.createLeaveRequest(leaveRequest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLeaveRequest(@PathVariable long id, @RequestBody LeaveRequest leaveRequest) {
        return leaveRequestService.updateLeaveRequest(id, leaveRequest);
    }

    @GetMapping("/user/{userId}")
    public List<LeaveRequest> getLeaveRequestsByUser(@PathVariable int userId) {
        return leaveRequestService.getLeaveRequestByUser(userId);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveLeaveRequest(@PathVariable long id, @RequestParam int managerId) {
        return leaveRequestService.approveLeaveRequest(id, managerId);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectLeaveRequest(@PathVariable long id, @RequestParam int managerId) {
        return leaveRequestService.rejectLeaveRequest(id, managerId);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteLeaveRequest(@PathVariable long id) {
        boolean deleted = leaveRequestService.deleteLeaveRequest(id);
        if(deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/viewSubordinatesLeaveRequests/{managerId}")
    public ResponseEntity<?> viewSubordinatesLeaveRequests(@PathVariable int managerId) {
        return leaveRequestService.getSubordinatesLeaveRequests(managerId);
    }
}