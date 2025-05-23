package com.example.backend.repositories;

import com.example.backend.entities.ProjectAssignment;
import com.example.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectAssignmentRepository extends JpaRepository<ProjectAssignment, Long> {
    List<ProjectAssignment> findByProjectIdIn(List<Integer> projectIds);
    Optional<ProjectAssignment> findByUserIdAndProjectId(int userId, int projectId);

    @Query("SELECT pa.user FROM ProjectAssignment pa WHERE pa.project.id = :projectId")
    List<User> findUsersByProjectId(@Param("projectId") int projectId);
}

