package com.example.backend;

import jakarta.persistence.*;

@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String description;
    private int managerId;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
