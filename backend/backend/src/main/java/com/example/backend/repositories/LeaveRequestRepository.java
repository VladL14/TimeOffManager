package com.example.backend.repositories;

import com.example.backend.entities.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByUserId(int userId);
    List<LeaveRequest> findAllByUserIdIn(Set<Long> userIds);

}
