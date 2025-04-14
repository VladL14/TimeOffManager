package com.example.backend;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String email;
    private String role;
    private Boolean isActive;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
