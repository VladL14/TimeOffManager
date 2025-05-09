package com.example.backend.entities;

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

    public void setDescription(String description) { this.description = description; }

    public String getDescription() { return description; }

    public void setManagerId(int managerId) { this.managerId = managerId; }
    public int getManagerId() { return managerId; }
}
