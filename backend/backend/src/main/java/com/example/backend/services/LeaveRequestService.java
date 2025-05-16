package com.example.backend.services;

import com.example.backend.RequestStatus;
import com.example.backend.entities.LeaveRequest;
import com.example.backend.repositories.LeaveRequestRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Service
public class LeaveRequestService {
    private final LeaveRequestRepository leaveRequestRepository;

    public LeaveRequestService(LeaveRequestRepository leaveRequestRepository)
    {
        this.leaveRequestRepository = leaveRequestRepository;
    }

    public List<LeaveRequest> getAll()
    {
        return leaveRequestRepository.findAll();
    }

    public LeaveRequest createLeaveRequest(LeaveRequest leaveRequest)
    {
        leaveRequest.setId(null);
        leaveRequest.setApprovedBy(null);
        return leaveRequestRepository.save(leaveRequest);
    }

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

    public List<LeaveRequest> getLeaveRequestByUser(@PathVariable int userId)
    {
        return leaveRequestRepository.findByUserId(userId);
    }

    public ResponseEntity<?> approveLeaveRequest(@PathVariable long id, @RequestParam int managerId) {
        Optional<LeaveRequest> optionalLeaveRequest = leaveRequestRepository.findById(id);

        if (optionalLeaveRequest.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        LeaveRequest leaveRequest = optionalLeaveRequest.get();

        if (leaveRequest.getStatus() != RequestStatus.PENDING) {
            return ResponseEntity.badRequest().body("The request cannot be approved");
        }

        leaveRequest.setStatus(RequestStatus.APPROVED);
        leaveRequest.setApprovedBy(managerId);
        leaveRequestRepository.save(leaveRequest);

        return ResponseEntity.ok(leaveRequest);
    }

    public ResponseEntity<?> rejectLeaveRequest(@PathVariable long id, @RequestParam int managerId)
    {
        Optional<LeaveRequest> optionalLeaveRequest = leaveRequestRepository.findById(id);
        if (optionalLeaveRequest.isEmpty())
        {
            return ResponseEntity.notFound().build();
        }
        LeaveRequest leaveRequest = optionalLeaveRequest.get();
        if (leaveRequest.getStatus() != RequestStatus.PENDING)
        {
            return ResponseEntity.badRequest().body("The request cannot be rejected");
        }
        leaveRequest.setStatus(RequestStatus.REJECTED);
        leaveRequest.setApprovedBy(managerId);
        leaveRequestRepository.save(leaveRequest);

        return ResponseEntity.ok(leaveRequest);
    }
    public boolean deleteLeaveRequest(@PathVariable long id)
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
}