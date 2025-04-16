package com.MediStaffManager.view.calendar.panels;

import com.MediStaffManager.bo.CalendarBO;
import javax.swing.*;
import java.awt.*;

public class CalendarLeftPanel extends JPanel {
    private JLabel infoLabel;
    private CalendarBO calendarBO;

    public CalendarLeftPanel(CalendarBO calendarBO) {
        this.calendarBO = calendarBO;
        initializeUI();
    }

    private void initializeUI() {
        setBackground(new Color(70, 130, 180));
        setPreferredSize(new Dimension(350, 900));
        setLayout(new BorderLayout());
        
        infoLabel = new JLabel("<html><div style='text-align:center;'>Select a day</div></html>", JLabel.CENTER);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        infoLabel.setForeground(Color.WHITE);
        
        add(infoLabel, BorderLayout.CENTER);
    }

    public void updateDailyInfo(String date) {
        // Get data from BO
        String scheduleInfo = "<html><div style='text-align:center;'>"
                + "<h2 style='color:white;'>Schedule for " + date + "</h2>";
        
        // Add schedule items from BO
        scheduleInfo += "</div></html>";
        
        infoLabel.setText(scheduleInfo);
    }
}