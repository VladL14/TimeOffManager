package com.example.backend.controllers;

import com.example.backend.entities.LeaveType;
import com.example.backend.repositories.LeaveTypeRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/leavetypes")
@CrossOrigin(origins = "http://localhost:4200")
public class LeaveTypeController {
    private final LeaveTypeRepository leaveTypeRepository;

    public LeaveTypeController(LeaveTypeRepository leaveTypeRepository) {
        this.leaveTypeRepository = leaveTypeRepository;
    }

    @GetMapping
    public List<LeaveType> getAllLeaveTypes() {
        return leaveTypeRepository.findAll();
    }
}
