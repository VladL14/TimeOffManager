package com.example.backend.controllers;

import com.example.backend.entities.LeaveType;
import com.example.backend.repositories.LeaveTypeRepository;
import com.example.backend.services.LeaveTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @GetMapping("/user/{userId}/leave_types")
    public ResponseEntity<?> getAllLeaveTypesForUser(@PathVariable int userId) {
        Map<String, Integer> leaveTypes = leaveTypeService.getAllLeaveTypesForUser(userId);
        if (leaveTypes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No leave types found or user is inactive");
        }
        return ResponseEntity.ok(leaveTypes);
    }

    @GetMapping("/user/{userId}/vacation")
    public ResponseEntity<?> getVacationBalance(@PathVariable int userId) {
        int balance = leaveTypeService.getVacationBalance(userId);
        if(balance != -1)
            return ResponseEntity.ok(balance);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not active.");
    }

    @GetMapping("/user/{userId}/sick_leave")
    public ResponseEntity<?> getSickLeaveBalance(@PathVariable int userId) {
        int balance = leaveTypeService.getSickLeaveBalance(userId);
        if(balance != -1)
            return ResponseEntity.ok(balance);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not active.");
    }

    @GetMapping("/user/{userId}/unpaid")
    public ResponseEntity<?> getUnpaidLeaveBalance(@PathVariable int userId) {
        int balance = leaveTypeService.getUnpaidLeaveBalance(userId);
        if (balance != -1)
            return ResponseEntity.ok(balance);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not active.");
    }

    @PutMapping("/user/{userId}/vacation/balance")
    public ResponseEntity<?> updateVacationBalance(@PathVariable int userId, @RequestParam int newBalance) {
        return leaveTypeService.setVacationBalance(userId, newBalance);
    }

    @PutMapping("/user/{userId}/sick_leave/balance")
    public ResponseEntity<?> updateSickLeaveBalance(@PathVariable int userId, @RequestParam int newBalance) {
        return leaveTypeService.setSickLeaveBalance(userId, newBalance);
    }

    @PutMapping("/user/{userId}/unpaid/balance")
    public ResponseEntity<?> updateUnpaidBalance(@PathVariable int userId, @RequestParam int newBalance) {
        return leaveTypeService.setUnpaidLeaveBalance(userId, newBalance);
    }
}
