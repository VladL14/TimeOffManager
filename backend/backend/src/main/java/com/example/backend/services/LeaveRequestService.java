package com.example.backend.services;

import com.example.backend.RequestStatus;
import com.example.backend.entities.LeaveRequest;
import com.example.backend.entities.LeaveType;
import com.example.backend.repositories.LeaveRequestRepository;
import com.example.backend.repositories.LeaveTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Service
public class LeaveRequestService {
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveTypeRepository leaveTypeRepository;

    @Autowired
    public LeaveRequestService(LeaveRequestRepository leaveRequestRepository, UserService userService, LeaveTypeRepository leaveTypeRepository, LeaveTypeService leaveTypeService)
    {
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveTypeRepository = leaveTypeRepository;
    }

    public List<LeaveRequest> getAll()
    {
        return leaveRequestRepository.findAll();
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
        long leaveTypeId = leaveRequest.getLeaveTypeId();

        // Cautam tipul de concediu in baza de date
        Optional<LeaveType> optionalLeaveType = leaveTypeRepository.findById(leaveTypeId);
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

        Date startDate = leaveRequest.getStartDate();
        Date endDate = leaveRequest.getEndDate();
        long diffInMillis = endDate.getTime() - startDate.getTime();
        long daysRequested = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS) + 1;

        int userId = leaveRequest.getUserId();

        Optional<LeaveType> optionalLeaveType = leaveTypeRepository.findByUserIdAndName(userId, "Vacation");

        if (optionalLeaveType.isEmpty()) {
            return ResponseEntity.badRequest().body("User does not have a vacation leave type configured.");
        }

        LeaveType leaveType = optionalLeaveType.get();

        if (leaveType.getBalanceDays() < daysRequested) {
            return ResponseEntity.badRequest().body("Not enough vacation days available.");
        }

        leaveType.setBalanceDays(leaveType.getBalanceDays() - (int) daysRequested);
        leaveTypeRepository.save(leaveType);

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