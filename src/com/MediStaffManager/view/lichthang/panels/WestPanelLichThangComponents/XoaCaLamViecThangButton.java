package com.MediStaffManager.view.lichthang.panels.WestPanelLichThangComponents;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import com.MediStaffManager.controller.CaLamViecController;
import com.MediStaffManager.view.lichthang.panels.NorthPanelLichThang;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class XoaCaLamViecThangButton extends JButton {
	private static final long serialVersionUID = 1L;
	
	public XoaCaLamViecThangButton(
            NorthPanelLichThang northPanel,
            CaLamViecController controller
    ) {
        super("Xóa tất cả ca làm việc trong tháng này");
        setFont(getFont().deriveFont(Font.BOLD | Font.ITALIC, 16f));
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                        null,
                        "Bạn có chắc chắn muốn xóa tất cả ca làm việc trong tháng này không?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (choice != JOptionPane.YES_OPTION) {
                    return;
                }
                String currentMonthYear = northPanel.getCenterPanelLichThang()
                        .getCalendarCurrentDate().getYear()
                    + "-"
                    + String.format("%02d", northPanel.getCenterPanelLichThang()
                        .getCalendarCurrentDate().getMonthValue());
                controller.xoaCaLamViecTheoThangNam(currentMonthYear);
                northPanel.getCenterPanelLichThang().fillCalendarTable();
                northPanel.getCenterPanelLichThang().setTableCellRenderers();
                JOptionPane.showMessageDialog(
                        null,
                        "Xóa tất cả ca làm việc trong tháng này thành công",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
    }
}
