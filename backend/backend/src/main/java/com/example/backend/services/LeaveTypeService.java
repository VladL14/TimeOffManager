package com.example.backend.services;


import com.example.backend.entities.LeaveType;
import com.example.backend.repositories.LeaveTypeRepository;
import com.example.backend.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.example.backend.entities.User;
@Service
public class LeaveTypeService {
    private final LeaveTypeRepository leaveTypeRepository;
    private final UserRepository userRepository;

    private final String VACATION = "Vacation";
    private final String SICKLEAVE= "Sick Leave";
    private final String UNPAID = "Unpaid";

    public LeaveTypeService(LeaveTypeRepository leaveTypeRepository, UserRepository userRepository) {
        this.leaveTypeRepository = leaveTypeRepository;
        this.userRepository = userRepository;
    }

    public Map<String, Integer> getAllLeaveTypesForUser(int userId) {
        Map<String, Integer> result = new HashMap<>();

        result.put(VACATION, 21);
        result.put(SICKLEAVE, 185);
        result.put(UNPAID, 90);

        if (isUserActive(userId)) {
            List<LeaveType> leaveTypeList = leaveTypeRepository.findByUserId(userId);

            for (LeaveType leaveType : leaveTypeList) {
                result.put(leaveType.getName(), leaveType.getBalanceDays());
            }
            return result;
        }

        return new HashMap<>();
    }


    public LeaveType getOrCreateLeaveType(int userId, String typeName, int defaultBalance) {
        Optional<LeaveType> optionalLeave = leaveTypeRepository.findByUserIdAndName(userId, typeName);
        if (optionalLeave.isPresent()) {
            return optionalLeave.get();
        }

        Optional<User> optionalUser = userRepository.findById((long) userId);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("User with id " + userId + " does not exist.");
        }

        User user = optionalUser.get();
        LeaveType newLeave = new LeaveType(typeName, defaultBalance, user);
        return leaveTypeRepository.save(newLeave);
    }

    public boolean isUserActive(int userId) {
        Optional<User> optionalUser = userRepository.findById((long) userId);
        if (optionalUser.isEmpty()) {
            return false;
        }
        User user = optionalUser.get();
        return user.getIsActive();
    }

    public int getVacationBalance(int userId) {
        if(isUserActive(userId)) {
            LeaveType leaveType = getOrCreateLeaveType(userId,VACATION,21);
            return leaveType.getBalanceDays();
        }
        return -1;
    }

    public int getSickLeaveBalance(int userId) {
        if(isUserActive(userId)) {
            LeaveType leaveType = getOrCreateLeaveType(userId,SICKLEAVE,183);
            return leaveType.getBalanceDays();
        }
        return -1;
    }

    public int getUnpaidLeaveBalance(int userId) {
        if(isUserActive(userId)) {
            LeaveType leaveType = getOrCreateLeaveType(userId,UNPAID,90);
            return leaveType.getBalanceDays();
        }
        return -1;
    }

    public ResponseEntity<?> setVacationBalance(int userId, int vacationBalance) {
        if (isUserActive(userId)) {
            Optional<LeaveType> optionalLeaveType = leaveTypeRepository.findByUserIdAndName(userId, VACATION);

            if (optionalLeaveType.isPresent()) {
                LeaveType leaveType = optionalLeaveType.get();
                leaveType.setBalanceDays(vacationBalance);
                leaveTypeRepository.save(leaveType);
                return ResponseEntity.ok().build();
            }

            return ResponseEntity.badRequest().body("Vacation balance does not exist");
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not active");
    }


    public ResponseEntity<?> setSickLeaveBalance(int userId, int sickLeaveBalance)
    {
        if(isUserActive(userId)) {
            Optional<LeaveType> optionalLeaveType = leaveTypeRepository.findByUserIdAndName(userId, SICKLEAVE);
            if(optionalLeaveType.isPresent()) {
                LeaveType leaveType = optionalLeaveType.get();
                leaveType.setBalanceDays(sickLeaveBalance);
                leaveTypeRepository.save(leaveType);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sick Leave balance does not exist");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not active");
    }

    public ResponseEntity<?> setUnpaidLeaveBalance(int userId, int unpaidLeaveBalance)
    {
        if(isUserActive(userId)) {
            Optional<LeaveType> optionalLeaveType = leaveTypeRepository.findByUserIdAndName(userId, UNPAID);
            if (optionalLeaveType.isPresent()) {
                LeaveType leaveType = optionalLeaveType.get();

                leaveType.setBalanceDays(unpaidLeaveBalance);
                leaveTypeRepository.save(leaveType);
                return ResponseEntity.ok().build();
            }

            return ResponseEntity.badRequest().body("Unpaid balance does not exist");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not active");
    }
}