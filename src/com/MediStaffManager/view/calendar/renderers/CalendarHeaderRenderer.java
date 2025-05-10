package com.MediStaffManager.view.calendar.renderers;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.Serializable;

public class CalendarHeaderRenderer extends DefaultTableCellRenderer implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        setFont(new Font("Arial", Font.BOLD, 16));
        setHorizontalAlignment(SwingConstants.CENTER);
        setBackground(new Color(135, 206, 250));  
        setForeground(Color.BLACK);  
        return c;
    }
}