package com.MediStaffManager.view.lichthang.renderers.calendarTable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class CalendarTableHeaderRenderer extends DefaultTableCellRenderer{
	private static final long serialVersionUID = 1L;
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, 
	        boolean isSelected, boolean hasFocus, int row, int column) {
	    
	    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	    
	    setHorizontalAlignment(SwingConstants.CENTER);

	    String fontName = "Arial";
	    int fontStyle = Font.BOLD;
	    int fontSize = 16;
	    setFont(new Font(fontName, fontStyle, fontSize));

	    Color backgroundColor = new Color(255, 140, 140);
	    setBackground(backgroundColor);
	    
	    Color textColor = Color.BLACK;
	    setForeground(textColor);

	    return c;
	}
}

