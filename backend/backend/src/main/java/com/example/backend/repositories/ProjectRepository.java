package com.example.backend.repositories;

import com.example.backend.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByManagerId(long managerId);

    @Query("SELECT p FROM Project p JOIN ProjectAssignment pa ON p.id = pa.project.id WHERE pa.user.id = :userId")
    List<Project> findProjectsByUserId(@Param("userId") int userId);
}
