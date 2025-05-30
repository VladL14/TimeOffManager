package com.example.backend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "leave_types")
public class LeaveType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private int balanceDays;
    private int userId;

    public LeaveType(String name, int balanceDays, User user){
        this.name = name;
        this.balanceDays = balanceDays;
        this.userId = user.getId();
    }

    public LeaveType() {}

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setBalanceDays(int balanceDays){this.balanceDays = balanceDays;}
    public int getBalanceDays(){return this.balanceDays;}

    public void setUserId(int userId){this.userId = userId;}
    public int getUserId(){return this.userId;}
}
