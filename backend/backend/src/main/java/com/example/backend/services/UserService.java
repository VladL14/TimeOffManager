package com.example.backend.services;

import com.example.backend.entities.User;
import com.example.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers()
    {
        return userRepository.findAll();
    }

    public User getUserById(@PathVariable Long id)
    {
        return userRepository.findById(id).get();
    }
}
