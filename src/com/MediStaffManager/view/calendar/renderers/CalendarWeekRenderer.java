package com.MediStaffManager.view.calendar.renderers;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.Serializable;

public class CalendarWeekRenderer extends DefaultTableCellRenderer implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        setFont(new Font("Arial", Font.BOLD, 18));
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
        Color borderColor = new Color(200, 200, 200);
        setBorder(new LineBorder(borderColor, 2));
        
        return c;
    }
}