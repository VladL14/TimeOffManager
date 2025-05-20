package com.example.backend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "projects_assignments")
public class ProjectAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectId")
    private Project project;

    public User getUser() {
        return user;
    }

    public Project getProject() {
        return project;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
