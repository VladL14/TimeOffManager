package com.example.backend.controllers;

import com.example.backend.entities.LeaveType;
import com.example.backend.repositories.LeaveTypeRepository;
import com.example.backend.services.LeaveTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/leavetypes")
@CrossOrigin(origins = "http://localhost:4200")
public class LeaveTypeController {
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveTypeService leaveTypeService;

    public LeaveTypeController(LeaveTypeRepository leaveTypeRepository, LeaveTypeService leaveTypeService) {
        this.leaveTypeRepository = leaveTypeRepository;
        this.leaveTypeService = leaveTypeService;
    }

    @GetMapping
    public List<LeaveType> getAllLeaveTypes() {
        return leaveTypeRepository.findAll();
    }

    @GetMapping("/user/{userId}/vacation")
    public ResponseEntity<Integer> getVacationBalance(@PathVariable int userId) {
        int balance = leaveTypeService.getVacationBalance(userId);
        return ResponseEntity.ok(balance);
    }
}
