package com.example.backend.repositories;

import com.example.backend.entities.ProjectAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectAssignmentRepository extends JpaRepository<ProjectAssignment, Long> {
    List<ProjectAssignment> findByProjectIdIn(List<Integer> projectIds);

    Optional<ProjectAssignment> findByUserIdAndProjectId(int userId, int projectId);
}