package com.MediStaffManager.bean;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class CalendarBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private LocalDate currentDate;
    private Map<LocalDate, String> events;

    public CalendarBean() {
        this.currentDate = LocalDate.now();
        this.events = new HashMap<>();
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }

    public Map<LocalDate, String> getEvents() {
        return events;
    }

    public void addEvent(LocalDate date, String event) {
        events.put(date, event);
    }
}