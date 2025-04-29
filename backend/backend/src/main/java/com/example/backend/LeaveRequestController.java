package com.example.backend;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
        leaveRequest.setStatus("PENDING");
        leaveRequest.setApprovedBy(null);
        return leaveRequestRepository.save(leaveRequest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLeaveRequest(@PathVariable Long id, @RequestBody LeaveRequest updatedRequest) {
        LeaveRequest existingRequest = leaveRequestRepository.findById(id)
                .orElse(null);

        if (existingRequest == null) {
            return ResponseEntity.notFound().build();
        }

        if (!"PENDING".equalsIgnoreCase(existingRequest.getStatus())) {
            return ResponseEntity.badRequest().body("Cererea nu poate fi modificată deoarece a fost deja aprobată sau respinsă.");
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

}
