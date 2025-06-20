package com.MediStaffManager.view.lichthang.renderers.caLamViec;

import java.awt.Component;
import java.awt.Color;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CaLamViecTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	private static final Color DATA_ROW_COLOR = new Color(240, 240, 255);
    private static final Color SEPARATOR_ROW_COLOR = Color.WHITE;
    private static final Insets CELL_PADDING = new Insets(5, 5, 5, 5);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(
            table, value, isSelected, hasFocus, row, column);

        label.setHorizontalAlignment(JLabel.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(
            CELL_PADDING.top, CELL_PADDING.left, CELL_PADDING.bottom, CELL_PADDING.right));

        boolean isSeparator = (value == null || value.toString().isEmpty());
        label.setBackground(isSeparator ? SEPARATOR_ROW_COLOR : DATA_ROW_COLOR);

        return label;
    }
}
