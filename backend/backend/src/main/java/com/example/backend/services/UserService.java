package com.example.backend.services;

import com.example.backend.entities.LeaveType;
import com.example.backend.entities.User;
import com.example.backend.repositories.LeaveTypeRepository;
import com.example.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final LeaveTypeRepository leaveTypeRepository;

    public UserService(UserRepository userRepository, LeaveTypeRepository leaveTypeRepository) {
        this.userRepository = userRepository;
        this.leaveTypeRepository = leaveTypeRepository;
    }

    public List<User> getAllUsers()
    {
        return userRepository.findAll();
    }

    public User getUserById(@PathVariable Long id)
    {
        return userRepository.findById(id).get();
    }

    public User createUser(User user)
    {
        user.setId(0);
        user.setIsActive(true);
        User savedUser = userRepository.save(user);

        List<LeaveType> defaultTypes = List.of(
                new LeaveType("Vacation", 21, savedUser),
                new LeaveType("Sick Leave", 183, savedUser),
                new LeaveType("Unpaid", 90, savedUser)
        );
        leaveTypeRepository.saveAll(defaultTypes);

        return savedUser;
    }
}
