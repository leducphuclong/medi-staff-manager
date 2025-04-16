package com.MediStaffManager.dao;

import com.MediStaffManager.bean.CalendarBean;
import com.MediStaffManager.bean.WorkScheduleBean;
import java.util.List;

public interface CalendarDAO {
    CalendarBean getCalendarData();
    List<WorkScheduleBean> getWorkSchedulesForDate(String date);
    void saveWorkSchedule(WorkScheduleBean schedule);
    void updateCalendar(CalendarBean calendar);
}