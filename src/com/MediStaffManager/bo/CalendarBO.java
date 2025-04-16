package com.MediStaffManager.bo;

import com.MediStaffManager.bean.CalendarBean;
import com.MediStaffManager.bean.WorkScheduleBean;
import com.MediStaffManager.dao.CalendarDAO;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import javax.swing.table.DefaultTableModel;

public class CalendarBO {
    private CalendarDAO calendarDAO;

    public CalendarBO(CalendarDAO calendarDAO) {
        this.calendarDAO = calendarDAO;
    }

    public CalendarBean getCurrentCalendar() {
        return calendarDAO.getCalendarData();
    }

    public List<WorkScheduleBean> getDailySchedules(String date) {
        return calendarDAO.getWorkSchedulesForDate(date);
    }

    public void addWorkSchedule(WorkScheduleBean schedule) {
        calendarDAO.saveWorkSchedule(schedule);
    }

    public void updateCalendar(CalendarBean calendar) {
        calendarDAO.updateCalendar(calendar);
    }

    // Method to set the current date in CalendarBean
    public void setCurrentDay(LocalDate newDate) {
        CalendarBean currentCalendar = getCurrentCalendar(); // Get the current calendar from DAO
        currentCalendar.setCurrentDate(newDate);  // Update the current date in the CalendarBean
        updateCalendar(currentCalendar); // Save the updated calendar back to DAO
    }

    private static void reloadCalendar(DefaultTableModel model, YearMonth currentMonth) {
        for (int row = 0; row < model.getRowCount(); row++) {
            for (int col = 0; col < model.getColumnCount(); col++) {
                model.setValueAt("", row, col);
            }
        }

        YearMonth ym = YearMonth.of(currentMonth.getYear(), currentMonth.getMonth());
        LocalDate firstDay = ym.atDay(1);
        int dayOfWeekOffset = firstDay.getDayOfWeek().getValue(); // 1 (Monday) to 7 (Sunday)

        int totalDays = ym.lengthOfMonth();
        int dayCounter = 1;

        for (int i = 0; i < 6; i++) {
            LocalDate dateInWeek = LocalDate.of(currentMonth.getYear(), currentMonth.getMonthValue(), 1).plusWeeks(i);
            int weekOfYear = dateInWeek.get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            model.setValueAt(weekOfYear, i, 0); // set week number in first column
            for (int j = 1; j <= 7; j++) {
                if (i == 0 && j < dayOfWeekOffset) continue;
                if (dayCounter > totalDays) return;
                model.setValueAt(String.valueOf(dayCounter), i, j);
                dayCounter++;
            }
        }
    }
}
