package com.MediStaffManager.view.calendar.panels;

import com.MediStaffManager.bo.CalendarBO;
import com.MediStaffManager.view.calendar.utils.CalendarUtils;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class CalendarTopPanel extends JPanel implements Serializable {
    private static final long serialVersionUID = 1L;

    private JLabel monthLabel;
    private CalendarBO calendarBO;
    private CalendarCenterPanel centerPanel; 

    public CalendarTopPanel(CalendarBO calendarBO, CalendarCenterPanel centerPanel) {
        this.calendarBO = calendarBO;
        this.centerPanel = centerPanel; 
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1600, 80));

        // Left placeholder
        JPanel leftPlaceholder = new JPanel();
        leftPlaceholder.setBackground(Color.BLUE);
        leftPlaceholder.setPreferredSize(new Dimension(350, 100));
        add(leftPlaceholder, BorderLayout.WEST);

        // Main panel with navigation
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(30, 144, 255));
        add(mainPanel, BorderLayout.CENTER);

        // Navigation buttons and month label
        JButton prevButton = createNavButton("◀");
        JButton nextButton = createNavButton("▶");
        // Add action listeners to navigate months
        prevButton.addActionListener(e -> {
        	centerPanel.currentDay = centerPanel.currentDay.minusMonths(1);
            updateMonthLabel();
            reloadCalendar();
        });

        nextButton.addActionListener(e -> {
        	centerPanel.currentDay = centerPanel.currentDay.minusMonths(1);
            updateMonthLabel();
            reloadCalendar();
        });
        
        // Title
        monthLabel = new JLabel(getCurrentMonthText(), SwingConstants.CENTER);
        monthLabel.setFont(new Font("Arial", Font.BOLD, 36));
        monthLabel.setForeground(Color.WHITE);

        // Layout configuration for buttons and label
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridx = 0;
        gbc.weightx = 0.1;
        mainPanel.add(prevButton, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        mainPanel.add(monthLabel, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.1;
        mainPanel.add(nextButton, gbc);
    }

    private String getCurrentMonthText() {
        // Get current month and year from currentDate
        Locale vietnamese = new Locale("vi", "VN");
        String monthName = centerPanel.currentDay.getMonth().getDisplayName(TextStyle.FULL, vietnamese);
        monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1);
        return monthName + ", " + centerPanel.currentDay.getYear();
    }

    private void updateMonthLabel() {
        // Update the month label after navigation
        monthLabel.setText(getCurrentMonthText());
    }

    private void reloadCalendar() {
        centerPanel.refreshCalendar(centerPanel.currentDay); // Pass the new date to the center panel
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Dialog", Font.BOLD, 40));
        return button;
    }
}
