package com.MediStaffManager.view.lichthang.renderers.caLamViec;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

public class XoaCaLamViecButtonRenderer extends JLabel implements TableCellRenderer {
	private static final long serialVersionUID = 1L;

	public XoaCaLamViecButtonRenderer() {
        setOpaque(true);
        setHorizontalAlignment(SwingConstants.CENTER);
        setBackground(new Color(255, 165, 85)); // Light orange color
        setForeground(Color.WHITE);  // Set text color to white
        setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 14));  // Set font style
        setBorder(BorderFactory.createRaisedBevelBorder());  // Border to make it look like a button
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof String && value.equals("Xóa ca này")) {
            setText((String) value);  // Set the button text
            
            
            return this;
        }
        return this;
    }
}
