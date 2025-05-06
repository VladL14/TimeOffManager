package com.example.backend.controllers;

import com.example.backend.entities.LeaveRequest;
import com.example.backend.RequestStatus;
import com.example.backend.repositories.LeaveRequestRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/leaverequests")
@CrossOrigin(origins = "http://localhost:4200")
public class LeaveRequestController {
    private final LeaveRequestRepository leaveRequestRepository;

    public LeaveRequestController(LeaveRequestRepository leaveRequestRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
    }

    @GetMapping
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestRepository.findAll();
    }

    @PostMapping
    public LeaveRequest createLeaveRequest(@RequestBody LeaveRequest leaveRequest) {
        leaveRequest.setId(null);
        leaveRequest.setApprovedBy(null);
        return leaveRequestRepository.save(leaveRequest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLeaveRequest(@PathVariable Long id, @RequestBody LeaveRequest updatedRequest) {
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
        existingRequest.setLeaveTypeId(updatedRequest.getLeaveTypeId());

        leaveRequestRepository.save(existingRequest);

        return ResponseEntity.ok(existingRequest);
    }

    @GetMapping("/user/{userId}")
    public List<LeaveRequest> getLeaveRequestsByUser(@PathVariable int userId) {
        return leaveRequestRepository.findByUserId(userId);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveLeaveRequest(@PathVariable long id, @RequestParam int managerId) {
        Optional<LeaveRequest> optionalLeaveRequest = leaveRequestRepository.findById(id);

        if (optionalLeaveRequest.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        LeaveRequest leaveRequest = optionalLeaveRequest.get();

        if (leaveRequest.getStatus() != RequestStatus.PENDING) {
            return ResponseEntity.badRequest().body("The request cannot be approved###");
        }

        leaveRequest.setStatus(RequestStatus.APPROVED);
        leaveRequest.setApprovedBy(managerId);
        leaveRequestRepository.save(leaveRequest);

        return ResponseEntity.ok(leaveRequest);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectLeaveRequest(@PathVariable long id, @RequestParam int managerId)
    {
        Optional<LeaveRequest> optionalLeaveRequest = leaveRequestRepository.findById(id);
        if (optionalLeaveRequest.isEmpty())
        {
            return ResponseEntity.notFound().build();
        }
        LeaveRequest leaveRequest = optionalLeaveRequest.get();
        if (leaveRequest.getStatus() != RequestStatus.PENDING )
        {
            return ResponseEntity.badRequest().body("The request cannot be rejected");
        }
        leaveRequest.setStatus(RequestStatus.REJECTED);
        leaveRequest.setApprovedBy(managerId);
        leaveRequestRepository.save(leaveRequest);

        return ResponseEntity.ok(leaveRequest);
    }
}
