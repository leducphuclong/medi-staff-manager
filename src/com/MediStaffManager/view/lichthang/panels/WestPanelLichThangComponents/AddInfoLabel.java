package com.MediStaffManager.view.lichthang.panels.WestPanelLichThangComponents;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.util.List;

import com.MediStaffManager.bean.CaLamViecBEAN;
import com.MediStaffManager.controller.CaLamViecController;
import com.MediStaffManager.view.lichthang.panels.CenterPanelLichThang;
import com.MediStaffManager.view.lichthang.panels.WestPanelLichThang;

public class AddInfoLabel {
    private static final int NUMBER_OF_INFORMATIONS = 8;
    private CaLamViecController caLamViecController;
    private List<CaLamViecBEAN> caLamViecList;
    
    private WestPanelLichThang panel;
    private CenterPanelLichThang centerPanelLichThang;

    public AddInfoLabel(WestPanelLichThang panel, CenterPanelLichThang centerPanelLichThang) {
        this.caLamViecController = new CaLamViecController();
        this.centerPanelLichThang = centerPanelLichThang;
        this.panel = panel;
    }

    public void addInfoLabelToPanel(String ngayLamViecDangChon, int width, int height) {
        caLamViecList = caLamViecController.layCaLamViecTheoNgayLamViec(ngayLamViecDangChon);

        if (caLamViecList.isEmpty()) {
            String text = "Không có ca làm việc nào\ntrong ngày này"; 
            JTextArea noShiftsLabel = new JTextArea(text);
            noShiftsLabel.setFont(new Font("Arial", Font.BOLD, 18));
            noShiftsLabel.setEditable(false);
            noShiftsLabel.setWrapStyleWord(true);
            noShiftsLabel.setLineWrap(true);
            noShiftsLabel.setBackground(panel.getBackground());
            noShiftsLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  // Optional padding
            noShiftsLabel.setFocusable(false); 

            // Use a JPanel to align the JTextArea
            JPanel textPanel = new JPanel(new BorderLayout());
            textPanel.add(noShiftsLabel, BorderLayout.CENTER); // Add the JTextArea to the panel

            // Center the text panel both horizontally and vertically
            textPanel.setAlignmentX(Component.CENTER_ALIGNMENT);  // Center the panel in the parent

            panel.removeAll(); // Clear existing components
            panel.add(textPanel, BorderLayout.CENTER); // Add the text panel to the center of the main panel

            SwingUtilities.invokeLater(() -> {
                panel.revalidate(); // Revalidate to refresh layout
                panel.repaint(); // Repaint to reflect changes
            });
        } else {
            String[] columnNames = {"Thông tin", "Chi tiết"};
            Object[][] rowData = createRowData(caLamViecList, columnNames);

            panel.removeAll();

            DefaultTableModel tableModel = new DefaultTableModel(rowData, columnNames);
            JTable scheduleTable = createScheduleTable(tableModel);

            addEditButtonToPanel(scheduleTable);

            JScrollPane scrollPane = createScrollPane(scheduleTable, width, height);

            TitledBorder titledBorder = createTitledBorder("Lịch làm việc ngày: " + ngayLamViecDangChon);
            scrollPane.setBorder(titledBorder);

            panel.add(scrollPane, BorderLayout.CENTER);

            SwingUtilities.invokeLater(() -> {
                panel.revalidate();
                panel.repaint();
            });
        }
    }

    
    public void addEditButtonToPanel(JTable scheduleTable) {
    	panel.add(createEditButton(scheduleTable), BorderLayout.SOUTH);
    	
    }

    private Object[][] createRowData(List<CaLamViecBEAN> caLamViecList, String[] columnNames) {
        int rowCount = caLamViecList.size() * NUMBER_OF_INFORMATIONS;
        Object[][] rowData = new Object[rowCount][columnNames.length];
        int rowIndex = 0;

        for (CaLamViecBEAN caLamViecBEAN : caLamViecList) {
            rowData[rowIndex++] = new Object[] {"<html><b> Tên ca</b></html>", caLamViecBEAN.getTenCa()};
            rowData[rowIndex++] = new Object[] {"<html><b>Tên người trực</b></html>", caLamViecBEAN.getTenNhanVien()};
            rowData[rowIndex++] = new Object[] {"<html><b>ID người trực</b></html>", caLamViecBEAN.getIdNhanVien()};
            rowData[rowIndex++] = new Object[] {"<html><b>Giờ bắt đầu</b></html>", takeOutTime(caLamViecBEAN.getGioBatDauThucTe())};
            rowData[rowIndex++] = new Object[] {"<html><b>Giờ kết thúc</b></html>", takeOutTime(caLamViecBEAN.getGioKetThucThucTe())};
            rowData[rowIndex++] = new Object[] {"<html><b>Đơn vị</b></html>", caLamViecBEAN.getDonVi()};
            rowData[rowIndex++] = new Object[] {"<html><b>Ghi chú</b></html>", caLamViecBEAN.getGhiChu()};
            rowData[rowIndex++] = new Object[] {"Xóa ca này", ""};
        }

        return rowData;
    }

    private String takeOutTime(String dateTime) {
        if (dateTime != null && dateTime.length() >= 16)
            return dateTime.substring(11, 16);
        return "N/A";
    }

    private JScrollPane createScrollPane(JTable scheduleTable, int width, int height) {
        int scrollPaneWidth = width - 20;
        int scrollPaneHeight = height - 60;

        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        scrollPane.setPreferredSize(new Dimension(scrollPaneWidth, scrollPaneHeight));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        return scrollPane;
    }

    private TitledBorder createTitledBorder(String title) {
        Color BORDER_COLOR = new Color(100, 149, 237);
        Color TITLE_COLOR = new Color(25, 25, 112);
        String FONT_NAME = "Arial";
        int FONT_STYLE_BOLD = Font.BOLD;
        int FONT_SIZE = 17;
        int BORDER_THICKNESS = 5;

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, BORDER_THICKNESS),
                title);

        titledBorder.setTitleFont(new Font(FONT_NAME, FONT_STYLE_BOLD, FONT_SIZE));
        titledBorder.setTitleColor(TITLE_COLOR);

        return titledBorder;
    }

    private JTable createScheduleTable(DefaultTableModel model) {
    	 return new CreateScheduleTable(model, NUMBER_OF_INFORMATIONS, caLamViecList, centerPanelLichThang);
    }


    private JButton createEditButton(JTable scheduleTable) {
        String editButtonText = "Cập nhật lịch ngày này";
        JButton editButton = new JButton(editButtonText);

        editButton.setFocusPainted(false);

        int editButtonHeight = 40;
        editButton.setPreferredSize(new Dimension(200, editButtonHeight));

        int editButtonTextSize = 16;
        editButton.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, editButtonTextSize));

        editButton.addActionListener((_) -> {
            updateScheduleData(scheduleTable);
            
            JOptionPane.showMessageDialog(
                    null, 
                    "Cập nhật ca làm việc cho ngày này thành công", 
                    "Thông báo", 
                    JOptionPane.INFORMATION_MESSAGE
                );
        });

        return editButton;
    }

    private void updateScheduleData(JTable scheduleTable) {
    	for (int i = 0; i < scheduleTable.getRowCount(); i += NUMBER_OF_INFORMATIONS) {
	        CaLamViecBEAN caLamViecBEAN = caLamViecList.get(i / NUMBER_OF_INFORMATIONS);
	        
	        String ngayLamViec = caLamViecBEAN.getNgayLamViec();
	        
	        caLamViecBEAN.setTenCa(parseString(scheduleTable.getValueAt(i, 1)));
	        caLamViecBEAN.setTenNhanVien(parseString(scheduleTable.getValueAt(i + 1, 1)));
	        caLamViecBEAN.setIdNhanVien(parseInteger(scheduleTable.getValueAt(i + 2, 1)));
	        
	        String gioBatDau = buildDatetime(ngayLamViec, scheduleTable.getValueAt(i + 3, 1));
	        String gioKetThuc = buildDatetime(ngayLamViec, scheduleTable.getValueAt(i + 4, 1));
	        
	        caLamViecBEAN.setGioBatDauThucTe(gioBatDau);
	        caLamViecBEAN.setGioKetThucThucTe(gioKetThuc);

	        caLamViecBEAN.setDonVi(parseString(scheduleTable.getValueAt(i + 5, 1)));
	        caLamViecBEAN.setGhiChu(parseString(scheduleTable.getValueAt(i + 6, 1)));

	        caLamViecController.capNhatCaLamViec(caLamViecBEAN);
	    }
    }
    
    private String buildDatetime(String ngayLamViec, Object time) {
	    return parseString(ngayLamViec + " " + time + ":00");
	}

	private String parseString(Object value) {
	    if (value instanceof String) {
	        return (String) value;
	    } else {
	        throw new IllegalArgumentException("Expected String, but got: " + value.getClass());
	    }
	}

	private int parseInteger(Object value) {
	    if (value instanceof Integer) {
	        return (Integer) value;
	    } else if (value instanceof String) {
	        try {
	            return Integer.parseInt((String) value);
	        } catch (NumberFormatException e) {
	            throw new IllegalArgumentException("Invalid integer value: " + value);
	        }
	    } else {
	        throw new IllegalArgumentException("Expected Integer or String, but got: " + value.getClass());
	    }
	}
}
