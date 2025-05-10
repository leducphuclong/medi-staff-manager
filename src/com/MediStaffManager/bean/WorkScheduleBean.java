package com.MediStaffManager.bean;

import java.io.Serializable;

public class WorkScheduleBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String staffId;
    private String timeSlot;
    private String description;

    // Getters and setters
    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}