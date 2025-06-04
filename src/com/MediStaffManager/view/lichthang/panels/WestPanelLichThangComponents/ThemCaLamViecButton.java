package com.MediStaffManager.view.lichthang.panels.WestPanelLichThangComponents;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JOptionPane;

import com.MediStaffManager.view.lichthang.panels.NorthPanelLichThang;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ThemCaLamViecButton extends JButton {
    private static final long serialVersionUID = 1L;

    public ThemCaLamViecButton(NorthPanelLichThang northPanel) {
        super("Thêm ca làm việc từ file Excel");
        setFont(getFont().deriveFont(Font.BOLD | Font.ITALIC, 16f));

        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Choose Excel File");
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Excel Files", "xls", "xlsx"));

                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    
                    ThemCaLamViecBangExcel.readAndDisplayExcelFile(selectedFile);
                    ThemCaLamViecBangExcel.readAndInsertDataToDatabase(selectedFile);

                    northPanel.getCenterPanelLichThang().fillCalendarTable();
                    northPanel.getCenterPanelLichThang().setTableCellRenderers();

                    JOptionPane.showMessageDialog(
                            null,
                            "Cập nhật ca làm việc cho ngày này thành công",
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        });
    }
}
