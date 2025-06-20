package com.MediStaffManager.view.lichthang.renderers.calendarTable;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CalendarTableWeekRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    // Constants to avoid magic numbers
    private static final String FONT_NAME = "Arial";
    private static final int FONT_STYLE = Font.BOLD;
    private static final int FONT_SIZE = 18;
    private static final int BORDER_THICKNESS = 1;
    private static final int SHADOW_BORDER_THICKNESS = 1;
    
    // Color constants
    private static final Color BORDER_COLOR = new Color(200, 200, 200);  // Lighter blue
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 30); // Shadow color

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        label.setOpaque(true);
        label.setFont(new Font(FONT_NAME, FONT_STYLE, FONT_SIZE));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);

        label.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, BORDER_THICKNESS),  // Top border
                new LineBorder(BORDER_COLOR, 0)   // Bottom border (No bottom border here)
        ));

        label.setBorder(BorderFactory.createCompoundBorder(
                label.getBorder(),
                new LineBorder(SHADOW_COLOR, SHADOW_BORDER_THICKNESS)
        ));

        return label;
    }
}
