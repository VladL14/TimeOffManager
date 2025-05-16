package com.example.backend.controllers;

import com.example.backend.entities.User;
import com.example.backend.repositories.UserRepository;
import com.example.backend.services.UserService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {

        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id)
    {
        return userService.getUserById(id);
    }
}
