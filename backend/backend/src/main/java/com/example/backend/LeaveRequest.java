package com.example.backend;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "leave_requests")
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String status;
    private String notes;
    private int userId;
    private int leaveTypeId;
    private Date startDate;
    private Date endDate;
    private Integer approvedBy;

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getNotes() {
        return notes;
    }
}
