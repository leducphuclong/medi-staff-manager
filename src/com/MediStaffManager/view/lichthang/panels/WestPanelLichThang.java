package com.MediStaffManager.view.lichthang.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

public class WestPanelLichThang extends JPanel{
	private static final long serialVersionUID = 1L;
	
	int numberOfInformations = 8;
	
	private int width;
	private int height;
	
	private CenterPanelLichThang centerPanelLichThang;
	
	
    public WestPanelLichThang(int width, int height) {
    	this.width = width;
    	this.height = height;
        initializeUI();
    }
	
	private void initializeUI() {
		configurePanel();
    }
	
	private void configurePanel() {
        setPreferredSize(new Dimension(width, height));
        setLayout(new BorderLayout());
	}
	
	public CenterPanelLichThang getCenterPanelLichThang() {
        return centerPanelLichThang;
    }

    public void setCenterPanelLichThang(CenterPanelLichThang centerPanelLichThang) {
        this.centerPanelLichThang = centerPanelLichThang;
    }
}