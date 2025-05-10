package com.MediStaffManager.view.calendar;

import java.awt.*;
import javax.swing.*;
import com.MediStaffManager.bo.CalendarBO;
import com.MediStaffManager.dao.CalendarDAOImpl;
import com.MediStaffManager.view.calendar.panels.*;

public class CalendarFrame {
    private static CalendarBO calendarBO;

    public static void launch() {
        calendarBO = new CalendarBO(new CalendarDAOImpl());
        
        JFrame frame = new JFrame("Lịch làm việc trong tháng của phòng ban");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1600, 900);
        frame.setLayout(new BorderLayout());

        CalendarLeftPanel leftPanel = new CalendarLeftPanel(calendarBO);
        CalendarCenterPanel centerPanel = new CalendarCenterPanel(calendarBO);
        CalendarTopPanel topPanel = new CalendarTopPanel(calendarBO, centerPanel);
        

        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                launch();
            }
        });
    }
}
