package com.MediStaffManager.view.calendar.renderers;

import com.MediStaffManager.bean.WorkScheduleBean;
import com.MediStaffManager.bo.CalendarBO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public class CalendarCellRenderer extends DefaultTableCellRenderer implements Serializable {
    private static final long serialVersionUID = 1L;
    private final LocalDate today = LocalDate.now();
    private final CalendarBO calendarBO;

    public CalendarCellRenderer(CalendarBO calendarBO) {
        this.calendarBO = calendarBO;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        Color defaultBg = Color.WHITE;
        Color focusBg = new Color(255, 228, 181);
        c.setBackground(hasFocus ? focusBg : defaultBg);
        
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
        Color borderColor = new Color(200, 200, 200);
        setBorder(new LineBorder(borderColor, 2));
        
        String day = value != null ? value.toString() : "";
        
        String dateStr = formatDateForLookup(day);
        List<WorkScheduleBean> schedules = calendarBO.getDailySchedules(dateStr);
        
        StringBuilder scheduleHtml = new StringBuilder();
        if (!schedules.isEmpty()) {
            for (WorkScheduleBean schedule : schedules) {
                scheduleHtml.append("• ")
                          .append(schedule.getTimeSlot())
                          .append(" - ")
                          .append(schedule.getStaffId())
                          .append("<br>");
            }
        } else {
            scheduleHtml.append("No schedules");
        }
        
        setText("<html><div style='text-align:center'>"
                + "<span style='font-size:25pt; font-weight:bold;'>" + day + "</span><br>"
                + "<span style='font-size:15pt;'>" + scheduleHtml.toString() + "</span>"
                + "</div></html>");
        
        try {
            int dayInt = Integer.parseInt(day);
            LocalDate currentDate = calendarBO.getCurrentCalendar().getCurrentDate();
            if (today.getMonthValue() == currentDate.getMonthValue() 
                    && today.getDayOfMonth() == dayInt) {
                c.setBackground(Color.ORANGE);
                setFont(new Font("Arial", Font.BOLD, 28));
            }
        } catch (NumberFormatException ignored) {
        }
        
        return c;
    }

    private String formatDateForLookup(String day) {
        try {
            int dayInt = Integer.parseInt(day);
            LocalDate currentDate = calendarBO.getCurrentCalendar().getCurrentDate();
            return LocalDate.of(currentDate.getYear(), currentDate.getMonthValue(), dayInt)
                          .toString();
        } catch (NumberFormatException e) {
            return "";
        }
    }
}