package com.MediStaffManager.view.lichthang.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.MediStaffManager.controller.CaLamViecController;
import com.MediStaffManager.view.lichthang.panels.WestPanelLichThangComponents.ThemCaLamViecButton;
import com.MediStaffManager.view.lichthang.panels.WestPanelLichThangComponents.XoaCaLamViecThangButton;

public class NorthPanelLichThang extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color BACK_GROUND_COLOR_LEFT_PANEL = new Color(70, 130, 180);
    private static final Color BACK_GROUND_COLOR_RIGHT_PANEL = new Color(30, 144, 255);

    private int heightLeftPanel;
    private int widthLeftPanel;

    private int heightRightPanel;
    private int widthRightPanel;

    private int heightParentPanel;
    private int widthParentPanel;

    private CenterPanelLichThang centerPanelLichThang;

    private JPanel leftPanel;
    private JPanel rightPanel;
    
    private CaLamViecController caLamViecController;

    public NorthPanelLichThang(int heightLeftPanel,
            int widthLeftPanel,
            int heightRightPanel,
            int widthRightPanel,
            int heightParentPanel,
            int widthParentPanel) {
        this.heightLeftPanel = heightLeftPanel;
        this.widthLeftPanel = widthLeftPanel;
        this.heightRightPanel = heightRightPanel;
        this.widthRightPanel = widthRightPanel;
        this.heightParentPanel = heightParentPanel;
        this.widthParentPanel = widthParentPanel;
        
        this.caLamViecController = new CaLamViecController();
        initializeUI();
    }

    private void initializeUI() {
        configureParentPanel();

        setLeftPanel(createLeftPanel());
        setRightPanel(createRightPanel());

        add(getLeftPanel(), BorderLayout.WEST);
        add(getRightPanel(), BorderLayout.CENTER);
    }

    private void configureParentPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(widthParentPanel, heightParentPanel));
    }

    private JPanel createLeftPanel() {
        leftPanel = new JPanel(new GridLayout(2, 1));
        leftPanel.setPreferredSize(new Dimension(widthLeftPanel, heightLeftPanel));
        leftPanel.setBackground(BACK_GROUND_COLOR_LEFT_PANEL);
        return leftPanel;
    }

    private JPanel createRightPanel() {
        rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setPreferredSize(new Dimension(widthRightPanel, heightRightPanel));
        rightPanel.setBackground(BACK_GROUND_COLOR_RIGHT_PANEL);

        addNavigationControls(rightPanel);

        return rightPanel;
    }
    
    private String getCalendarMonthYearFormattedString() {
    	LocalDate calendarCurrentDate = getCenterPanelLichThang().getCalendarCurrentDate();

    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("vi"));
    	String formattedDate = calendarCurrentDate.format(formatter);
    	
    	int month = calendarCurrentDate.getMonthValue();
    	
    	formattedDate = "Tháng " + month + ", " + calendarCurrentDate.getYear();
    	
    	return formattedDate;
    }
    
    public void addMonthYearLabel(JPanel panel) {
    	String formattedDate = getCalendarMonthYearFormattedString();

        JLabel monthLabel = new JLabel(formattedDate, SwingConstants.CENTER);
        monthLabel.setFont(new Font("Arial", Font.BOLD, 36));
        monthLabel.setForeground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        panel.add(monthLabel, gbc);
    }
    
    private void updateMonthYearLabel() {
        for (Component component : rightPanel.getComponents())
            if (component instanceof JLabel)
                rightPanel.remove(component); 

        String monthYearString = getCalendarMonthYearFormattedString();
        JLabel monthLabel = new JLabel(monthYearString, SwingConstants.CENTER);
        monthLabel.setFont(new Font("Arial", Font.BOLD, 36));
        monthLabel.setForeground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        rightPanel.add(monthLabel, gbc);
        rightPanel.revalidate(); 
        rightPanel.repaint(); 
    }


    private void addNavigationControls(JPanel panel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        // add prevButton
        gbc.gridx = 0; // ◀ button at column 0
        gbc.weightx = 0.1;
        JButton prevButton = createNavButton("◀");
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getCenterPanelLichThang().setCalendarCurrentDate(getCenterPanelLichThang().getCalendarCurrentDate().minusMonths(1));
                updateMonthYearLabel();
                getCenterPanelLichThang().fillCalendarTable();
                getCenterPanelLichThang().setTableCellRenderers();
            }
        });
        panel.add(prevButton, gbc);

        // add configure prevButton
        gbc.gridx = 2; // ▶ button at column 2
        gbc.weightx = 0.1;
        JButton nextButton = createNavButton("▶");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getCenterPanelLichThang().setCalendarCurrentDate(getCenterPanelLichThang().getCalendarCurrentDate().plusMonths(1));
                updateMonthYearLabel();
                getCenterPanelLichThang().fillCalendarTable();
                getCenterPanelLichThang().setTableCellRenderers();
            }
        });
        panel.add(nextButton, gbc);
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        String textButtonFont = "Dialog";
        int textButtonStyle = Font.BOLD;
        int textButtonSize = 40;
        button.setFont(new Font(textButtonFont, textButtonStyle, textButtonSize));

        return button;
    }

    public CenterPanelLichThang getCenterPanelLichThang() {
        return centerPanelLichThang;
    }

    public void setCenterPanelLichThang(CenterPanelLichThang centerPanelLichThang) {
        this.centerPanelLichThang = centerPanelLichThang;
    }

    public JPanel getLeftPanel() {
        return leftPanel;
    }

    public void setLeftPanel(JPanel leftPanel) {
        this.leftPanel = leftPanel;
        
        leftPanel.add(new XoaCaLamViecThangButton(this, caLamViecController));
     	
        leftPanel.add(new ThemCaLamViecButton(this));
    }

    public JPanel getRightPanel() {
        return rightPanel;
    }

    public void setRightPanel(JPanel rightPanel) {
        this.rightPanel = rightPanel;
    }
}
