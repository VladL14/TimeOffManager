package com.example.backend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "projects_assignments")
public class ProjectAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int userId;
    private int projectId;

    public void setUserId(int userId) {this.userId = userId;}
    public long getUserId() {return userId;}

    public void setProjectId(int projectId) {this.projectId = projectId;}
    public int getProjectId() {return projectId;}

}
