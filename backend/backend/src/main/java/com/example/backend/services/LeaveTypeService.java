package com.example.backend.services;


import com.example.backend.entities.LeaveType;
import com.example.backend.repositories.LeaveTypeRepository;
import org.springframework.stereotype.Service;

@Service
public class LeaveTypeService {
    private final LeaveTypeRepository leaveTypeRepository;

    public LeaveTypeService(LeaveTypeRepository leaveTypeRepository) {
        this.leaveTypeRepository = leaveTypeRepository;
    }

    public int getVacationBalance(int userId) {
        LeaveType leaveType = leaveTypeRepository.findByUserIdAndName(userId, "Vacation").get();
        return leaveType.getBalanceDays();
    }
}
