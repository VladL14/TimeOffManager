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

    public void setStatus(String status) {this.status = status;}
    public String getStatus() {return this.status;}

    public void setUserId(String userId) {this.userId = Integer.parseInt(userId);}
    public int getUserId() {return this.userId;}

    public void setLeaveTypeId(int leaveTypeId) {this.leaveTypeId = leaveTypeId;}
    public int getLeaveTypeId() {return this.leaveTypeId;}

    public void setStartDate(Date startDate) {this.startDate = startDate;}
    public Date getStartDate() {return this.startDate;}

    public void setEndDate(Date endDate) {this.endDate = endDate;}
    public Date getEndDate() {return this.endDate;}

    public void setApprovedBy(Integer approvedBy) {this.approvedBy = approvedBy;}
    public Integer getApprovedBy() {return this.approvedBy;}

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getNotes() {
        return notes;
    }
}
