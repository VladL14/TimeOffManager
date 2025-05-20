package com.example.backend.entities;

import com.example.backend.RequestStatus;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "leave_requests")
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private RequestStatus status;
    private String notes;
    private int userId;
    private String leaveTypeName;
    private Date startDate;
    private Date endDate;
    private Integer approvedBy;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }



    public void setStatus(RequestStatus status) {this.status = status;}
    public RequestStatus getStatus() {return this.status;}

    public void setUserId(int userId) {
        this.userId = userId;
    }
    public int getUserId() {return this.userId;}

    public void setLeaveTypeName(String leaveTypeName) {this.leaveTypeName = leaveTypeName;}
    public String getLeaveTypeName() {return this.leaveTypeName;}

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
