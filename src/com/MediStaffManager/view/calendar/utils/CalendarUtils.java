package com.MediStaffManager.view.calendar.utils;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.YearMonth;

public class CalendarUtils {

    public static void reloadCalendar(JTable calendarTable, LocalDate currentDate) {
        DefaultTableModel model = (DefaultTableModel) calendarTable.getModel();

        for (int row = 0; row < model.getRowCount(); row++) {
            for (int col = 0; col < model.getColumnCount(); col++) {
                model.setValueAt("", row, col);
            }
        }
        
        YearMonth ym = YearMonth.of(currentDate.getYear(), currentDate.getMonth());
        LocalDate firstDay = ym.atDay(1);
        int dayOfWeekOffset = firstDay.getDayOfWeek().getValue(); // 1 = Monday

        int totalDays = ym.lengthOfMonth();
        int dayCounter = 1;

        for (int i = 0; i < 6; i++) {
            LocalDate dateInWeek = currentDate.withDayOfMonth(1).plusWeeks(i);
            int weekOfYear = dateInWeek.get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            model.setValueAt(weekOfYear, i, 0);

            for (int j = 1; j <= 7; j++) {
                if (i == 0 && j < dayOfWeekOffset) continue;
                if (dayCounter > totalDays) return;
                model.setValueAt(String.valueOf(dayCounter), i, j);
                dayCounter++;
            }
        }
    }
}
