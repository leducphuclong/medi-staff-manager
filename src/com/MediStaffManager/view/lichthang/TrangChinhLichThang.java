package com.MediStaffManager.view.lichthang;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.MediStaffManager.view.lichthang.panels.CenterPanelLichThang;
import com.MediStaffManager.view.lichthang.panels.NorthPanelLichThang;
import com.MediStaffManager.view.lichthang.panels.WestPanelLichThang;
import com.MediStaffManager.view.lichthang.panels.WestPanelLichThangComponents.AddInfoLabel;

public class TrangChinhLichThang {
    static final String WINDOW_TITLE   = "Lịch Tháng";
    static final int    PARENT_WIDTH   = 1600, PARENT_HEIGHT = 900;
    static final int    WEST_WIDTH     = 350,  WEST_HEIGHT   = 900;
    static final int    NORTH_HEIGHT   = 80;

    public static void launch() {
        JFrame frame = configureFrame();
        
        // Create component Labels
        WestPanelLichThang westPanelLichThang = createWestPanel();
        frame.add(westPanelLichThang, BorderLayout.WEST);
        
        NorthPanelLichThang northPanelLichThang = createNorthPanel();
        frame.add(northPanelLichThang, BorderLayout.NORTH);
        
        CenterPanelLichThang centerPanelLichThang = createCenterPanel();
        frame.add(centerPanelLichThang, BorderLayout.CENTER);
        
        // Link those Panel
        northPanelLichThang.setCenterPanelLichThang(centerPanelLichThang);
        centerPanelLichThang.setNorthPanelLichThang(northPanelLichThang);
        centerPanelLichThang.setWestPanelLichThang(westPanelLichThang);
        westPanelLichThang.setCenterPanelLichThang(centerPanelLichThang);
        
        // add month and year from calendar of center to the North panel
        northPanelLichThang.addMonthYearLabel(northPanelLichThang.getRightPanel());
        
        // add Infor label to the West panel
        AddInfoLabel addInfoLabel = new AddInfoLabel(westPanelLichThang, centerPanelLichThang);
        addInfoLabel.addInfoLabelToPanel(centerPanelLichThang.getCalendarCurrentDate().toString(), westPanelLichThang.getWidth(), westPanelLichThang.getHeight());
    }

    private static JFrame configureFrame() {
        JFrame frame = new JFrame(WINDOW_TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(PARENT_WIDTH, PARENT_HEIGHT);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        return frame;
    }

    private static CenterPanelLichThang createCenterPanel() {
    	return new CenterPanelLichThang();
    }
    
    private static WestPanelLichThang createWestPanel() {
        return new WestPanelLichThang(WEST_WIDTH, WEST_HEIGHT);
    }

    private static NorthPanelLichThang createNorthPanel() {
        return new NorthPanelLichThang(
            NORTH_HEIGHT,            // heightLeftPanel
            WEST_WIDTH,              // widthLeftPanel
            NORTH_HEIGHT,            // heightRightPanel
            PARENT_WIDTH - WEST_WIDTH	, // widthRightPanel
            NORTH_HEIGHT,            // heightParentPanel
            PARENT_WIDTH             // widthParentPanel
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TrangChinhLichThang::launch);
    }
}
