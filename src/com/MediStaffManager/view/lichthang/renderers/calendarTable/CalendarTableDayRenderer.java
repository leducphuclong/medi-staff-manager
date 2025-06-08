package com.MediStaffManager.view.lichthang.renderers.calendarTable;

import com.MediStaffManager.bean.CaLamViecBEAN;
import com.MediStaffManager.controller.CaLamViecController;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CalendarTableDayRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 1L;
    private LocalDate calendarCurrentDate;
    private CaLamViecController caLamViecController;
    
    public CalendarTableDayRenderer(LocalDate calendarCurrentDate, CaLamViecController caLamViecController) {
        this.calendarCurrentDate = calendarCurrentDate;
        
        this.caLamViecController = caLamViecController;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        Color borderColor = new Color(200, 200, 200);
        int borderSize = 2;
        label.setBorder(new LineBorder(borderColor, borderSize));

        label.setHorizontalAlignment(JLabel.CENTER);  
        label.setVerticalAlignment(JLabel.TOP);    

        Object cellValue = table.getValueAt(row, column);
        String schedule = "Không có ca nào"; 

        if (cellValue != null && !cellValue.toString().isEmpty()) {
            String formattedDay = "";
            try {
                formattedDay = String.format("%02d", Integer.parseInt(cellValue.toString()));
            } catch (NumberFormatException e) {
                formattedDay = cellValue.toString();
            }

            String dateLamViec = calendarCurrentDate.getYear() + "-" + 
                                  String.format("%02d", calendarCurrentDate.getMonthValue()) + "-" + 
                                  formattedDay;
            
            List<CaLamViecBEAN> caLamViecList = caLamViecController.layCaLamViecTheoNgayLamViec(dateLamViec);

            StringBuilder donViList = new StringBuilder();  
            Set<String> displayedDepartments = new HashSet<>();  

            for (CaLamViecBEAN caLamViec : caLamViecList) {
                String department = caLamViec.getDonVi(); 
                
                if (!displayedDepartments.contains(department)) {
                    donViList.append("• ")
                            .append(department) 
                            .append("<br>");
                    displayedDepartments.add(department);  
                }
            }

            if (donViList.length() > 0) {
                schedule = donViList.toString();
            }
        }

        String cellContent = 
            "<html>"
            + "<div style='text-align:center;'>"
                + "<div style='font-size:25pt; font-weight:bold;'>"
                + getText()
                + "</div>"
                + "<div style='font-size:17pt;'>"
                + schedule  
                + "</div>"
            + "</div>"
            + "</html>";

        if (table.getSelectedRow() == row && table.getSelectedColumn() == column) {
            Color selectedDayBackGroundColor = new Color(255, 255, 150);
            label.setBackground(selectedDayBackGroundColor); 
        } else {
            label.setBackground(Color.WHITE); 
        }

        String dayText = getText();
        System.out.println("dayText: " + dayText);
        if (dayText != null && !dayText.isEmpty()) {
            try {
                int displayedMonth = calendarCurrentDate.getMonthValue();
                int displayedYear = calendarCurrentDate.getYear();

                int displayedDay = Integer.parseInt(dayText);

                if (displayedDay == LocalDate.now().getDayOfMonth() && displayedMonth == LocalDate.now().getMonthValue() && displayedYear == LocalDate.now().getYear()) {
                    Color toDayBackGroundColor = new Color(211, 211, 211);
                    label.setBackground(toDayBackGroundColor);
                }

            } catch (NumberFormatException e) {
            }
        }

        if (cellValue != null && !cellValue.toString().isEmpty()) 
        	label.setText(cellContent);
        return label;
    }
}
