package com.MediStaffManager.view.lichthang.panels.WestPanelLichThangComponents;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.MediStaffManager.bean.CaLamViecBEAN;
import com.MediStaffManager.controller.CaLamViecController;
import com.MediStaffManager.view.lichthang.panels.CenterPanelLichThang;
import com.MediStaffManager.view.lichthang.renderers.caLamViec.CaLamViecTableCellRenderer;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class CreateScheduleTable extends JTable {
    private static final long serialVersionUID = 1L;

    private static final int FONT_SIZE = 16;
    private static final int ROW_HEIGHT = 30;
    private static final int MIN_COLUMN_WIDTH = 130;
    private static final int MAX_COLUMN_WIDTH = 140;
    private static final Color HIGHLIGHT_COLOR = new Color(255, 165, 85);
    private static final int XOA_CA_TEXT_FONT_STYLE = Font.BOLD | Font.ITALIC;
    private static final int XOA_CA_TEXT_FONT_SIZE = 14;
    
    
    private int lastRowCondition;
    private int numberOfInformations;
    private List<CaLamViecBEAN> caLamViecList;
    
    private CenterPanelLichThang centerPanelLichThang;
    
    public CreateScheduleTable(DefaultTableModel model, int numberOfInformations, List<CaLamViecBEAN> caLamViecList, CenterPanelLichThang centerPanelLichThang) {
        super(model);
        this.lastRowCondition = numberOfInformations - 1;
        this.numberOfInformations = numberOfInformations;
        this.caLamViecList = caLamViecList;
        this.centerPanelLichThang = centerPanelLichThang;
        setupTable();
    }

    private void setupTable() {
        this.setTableHeader(null);
        this.setFont(new Font("Arial", Font.PLAIN, FONT_SIZE));
        this.setFillsViewportHeight(true);
        this.setShowGrid(false);
        this.setRowHeight(ROW_HEIGHT);
        this.getColumnModel().getColumn(0).setMinWidth(MIN_COLUMN_WIDTH);
        this.getColumnModel().getColumn(0).setMaxWidth(MAX_COLUMN_WIDTH);
        this.setDefaultRenderer(Object.class, new CaLamViecTableCellRenderer());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = rowAtPoint(e.getPoint());
                int column = columnAtPoint(e.getPoint());
                
                CaLamViecController controller = new CaLamViecController();
                controller.xoaCaLamViecTheoIdCaLam(caLamViecList.get(row/numberOfInformations).getIdCaLam());
                
                centerPanelLichThang.fillCalendarTable();
                centerPanelLichThang.setTableCellRenderers();
                
                if (row % numberOfInformations == lastRowCondition && column == 0) {
                	JOptionPane.showMessageDialog(
                            null, 
                            "Xóa ca làm việc thành công", 
                            "Thông báo", 
                            JOptionPane.INFORMATION_MESSAGE
                        );
                }
            }
        });
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (row % numberOfInformations == 1 || row % numberOfInformations == lastRowCondition) {
            return false;
        }
        return super.isCellEditable(row, column);
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);

        if (row % numberOfInformations == lastRowCondition && column == 0) {
            c.setBackground(HIGHLIGHT_COLOR);
            c.setFont(new Font("Arial", XOA_CA_TEXT_FONT_STYLE, XOA_CA_TEXT_FONT_SIZE));
            ((JComponent) c).setBorder(BorderFactory.createRaisedBevelBorder());
        }

        return c;
    }
}
