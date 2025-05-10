package com.MediStaffManager.view.calendar.panels;

import com.MediStaffManager.bo.CalendarBO;
import com.MediStaffManager.view.calendar.renderers.*;
import com.MediStaffManager.view.calendar.utils.CalendarUtils;

import java.awt.BorderLayout;
import java.time.LocalDate;
import java.time.YearMonth;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class CalendarCenterPanel extends JPanel {
    private JTable calendarTable;
    private CalendarBO calendarBO;
    public LocalDate currentDay;

    public CalendarCenterPanel(CalendarBO calendarBO) {
        this.calendarBO = calendarBO;
        this.currentDay = LocalDate.now(); 
        initializeCalendarTable();
        CalendarUtils.reloadCalendar(calendarTable, currentDay);  // Add this line to reload the calendar
    }

    private void initializeCalendarTable() {
        String[] columnNames = {"Tuần", "T2", "T3", "T4", "T5", "T6", "T7", "CN"};
        calendarTable = new JTable(new DefaultTableModel(new Object[6][8], columnNames));
        calendarTable.setRowHeight(150);
        calendarTable.setDefaultEditor(Object.class, null);

        JTableHeader header = calendarTable.getTableHeader();
        header.setDefaultRenderer(new CalendarHeaderRenderer());
        
        // Renderer for week collumn 
        calendarTable.getColumnModel().getColumn(0).setCellRenderer(new CalendarWeekRenderer());
        calendarTable.getColumnModel().getColumn(0).setMinWidth(50);
        calendarTable.getColumnModel().getColumn(0).setMaxWidth(50);
        calendarTable.getColumnModel().getColumn(0).setPreferredWidth(50);

        for (int i = 1; i < calendarTable.getColumnCount(); i++) {
            calendarTable.getColumnModel().getColumn(i)
                .setCellRenderer(new CalendarCellRenderer(calendarBO));
        }

        JScrollPane scrollPane = new JScrollPane(calendarTable);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshCalendar(LocalDate newDate) {
        this.currentDay = newDate;
        CalendarUtils.reloadCalendar(calendarTable, newDate);
    }
}
