package com.MediStaffManager.view.lichthang;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.MediStaffManager.view.lichthang.panels.CenterPanelLichThang;
import com.MediStaffManager.view.lichthang.panels.NorthPanelLichThang;
import com.MediStaffManager.view.lichthang.panels.WestPanelLichThang;
import com.MediStaffManager.view.lichthang.panels.WestPanelLichThangComponents.AddInfoLabel;

public class TrangChinhLichThang {
    static final String WINDOW_TITLE   = "Lịch Tháng";
    public static final int    PARENT_WIDTH   = 1900;
	public static final int PARENT_HEIGHT = 1000;
    static final int    WEST_WIDTH     = 350,  WEST_HEIGHT   = 900;
    static final int    NORTH_HEIGHT   = 80;
    
    public static JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(PARENT_WIDTH, PARENT_HEIGHT));

        WestPanelLichThang westPanel = createWestPanel();
        NorthPanelLichThang northPanel = createNorthPanel();
        CenterPanelLichThang centerPanel = createCenterPanel();

        mainPanel.add(westPanel, BorderLayout.WEST);
        mainPanel.add(northPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        northPanel.setCenterPanelLichThang(centerPanel);
        centerPanel.setNorthPanelLichThang(northPanel);
        centerPanel.setWestPanelLichThang(westPanel);
        westPanel.setCenterPanelLichThang(centerPanel);

        northPanel.addMonthYearLabel(northPanel.getRightPanel());
        AddInfoLabel addInfoLabel = new AddInfoLabel(westPanel, centerPanel);
        addInfoLabel.addInfoLabelToPanel(centerPanel.getCalendarCurrentDate().toString(), westPanel.getWidth(), westPanel.getHeight());

        return mainPanel;
    }

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
