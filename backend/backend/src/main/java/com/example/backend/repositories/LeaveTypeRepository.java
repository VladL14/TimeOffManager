package com.example.backend.repositories;

import com.example.backend.entities.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {
    Optional<LeaveType> findByUserIdAndName(int userId, String name);
}
