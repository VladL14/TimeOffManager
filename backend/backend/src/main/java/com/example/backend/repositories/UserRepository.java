package com.example.backend.repositories;

import com.example.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllById(Iterable<Long> ids);
}
