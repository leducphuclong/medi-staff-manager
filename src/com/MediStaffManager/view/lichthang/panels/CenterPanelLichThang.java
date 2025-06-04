package com.MediStaffManager.view.lichthang.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.time.LocalDate;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

import com.MediStaffManager.controller.CaLamViecController;
import com.MediStaffManager.view.lichthang.panels.WestPanelLichThangComponents.AddInfoLabel;
import com.MediStaffManager.view.lichthang.renderers.calendarTable.CalendarTableDayRenderer;
import com.MediStaffManager.view.lichthang.renderers.calendarTable.CalendarTableHeaderRenderer;
import com.MediStaffManager.view.lichthang.renderers.calendarTable.CalendarTableWeekRenderer;

public class CenterPanelLichThang extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JTable calendarTable;
	
	private WestPanelLichThang westPanelLichThang;
	private NorthPanelLichThang northPanelLichThang;
	
	private LocalDate calendarCurrentDate;
	
	private String ngayLamViecDangChon;
	
	private CaLamViecController caLamViecController;
	
	public CenterPanelLichThang() {
		this.setCalendarCurrentDate(LocalDate.now());
		System.out.println("Connecting to DB in Center Panel class");
		this.caLamViecController = new CaLamViecController();
		
		initializeUI();
	}
	
	private void initializeUI() {
		configurePanel();
		
		configureCalendarTable();
		
		fillCalendarTable();
		
		addCalendarTable();
	}
	
	private void addCalendarTable() {
		JScrollPane scrollPane = new JScrollPane(calendarTable);
		
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        
        // Add listener for select cell
        calendarTable.addMouseListener(new java.awt.event.MouseAdapter() {
	        @Override
	        public void mouseClicked(java.awt.event.MouseEvent e) {
	            int selectedRow = calendarTable.getSelectedRow();
	            int selectedCol = calendarTable.getSelectedColumn();

	            if (selectedRow != -1 && selectedCol != -1) {
	                Object cellValue = calendarTable.getValueAt(selectedRow, selectedCol);
	                ngayLamViecDangChon = cellValue.toString();
	                updateWestPanelLabelByCurrentDate(ngayLamViecDangChon);
	            }
	        }
	    });
        
        add(scrollPane, BorderLayout.CENTER);
	}
	
	private void updateWestPanelLabelByCurrentDate(String cellValue) {
		String monthYear = getCalendarCurrentDate().toString(); 
		String day = cellValue;

		String formattedDay = String.format("%02d", Integer.parseInt(day)); // Converts '6' to '06'

		String dayMonthYear = monthYear.substring(0, 8) + formattedDay; // e.g., "2025-05-06"

		AddInfoLabel addInfoLabel = new AddInfoLabel(getWestPanelLichThang(), this);
        addInfoLabel.addInfoLabelToPanel(dayMonthYear, getWestPanelLichThang().getWidth(), getWestPanelLichThang().getHeight());
	}
	
	public void fillCalendarTable() {
	    DefaultTableModel model = (DefaultTableModel) calendarTable.getModel();

	    LocalDate today = calendarCurrentDate;
	    int currentMonth = today.getMonthValue();
	    int currentYear = today.getYear();
	    
	    LocalDate firstDayOfMonth = LocalDate.of(currentYear, currentMonth, 1);
	    int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue();

	    int row = 0;
	    int col = firstDayOfWeek;
	    
	    for (int i = 0; i < model.getRowCount(); i++)
	    	for (int j = 0; j < model.getColumnCount(); j++)
	    		model.setValueAt("", i, j);

	    for (int i = 1; i <= today.lengthOfMonth(); i++) {
	    	int weekOfYear = firstDayOfMonth.plusDays(i - 1).get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR);
	        model.setValueAt(weekOfYear, row, 0); 
	        model.setValueAt(i, row, col);
	        col++;
	        if (col > 7) {
	            col = 1;
	            row++;
	        }
	    }
	    
	    calendarTable.revalidate();
	    calendarTable.setCellSelectionEnabled(true);
	    calendarTable.repaint();
	}
	
	private void configurePanel() {
		setLayout(new BorderLayout());

		setVisible(true); 
	}
	
	private void configureCalendarTable() {
	    String[] columnNames = {"Tuần", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "CN"};
	    int noRow = 6, noCol = 8;
	    
	    calendarTable = createTable(noRow, noCol, columnNames);
	    setTableProperties();
	    setTableHeader();
	    setTableCellRenderers();
	}

	private JTable createTable(int noRow, int noCol, String[] columnNames) {
	    return new JTable(new DefaultTableModel(new Object[noRow][noCol], columnNames));
	}

	private void setTableProperties() {
	    int rowHeight = 150;
	    calendarTable.setRowHeight(rowHeight);
	    calendarTable.setDefaultEditor(Object.class, null);
	    calendarTable.setShowGrid(false);
	}

	private void setTableHeader() {
	    calendarTable.getTableHeader().setDefaultRenderer(new CalendarTableHeaderRenderer());

	    int calendarTableHeaderHeight = 40;
	    calendarTable.getTableHeader().setPreferredSize(new Dimension(calendarTable.getTableHeader().getPreferredSize().width, calendarTableHeaderHeight));
	}

	public void setTableCellRenderers() {
		// refresh selected cell
		calendarTable.clearSelection();

		
	    // configure Calendar Table Day cell
	    for (int col = 1; col < calendarTable.getColumnCount(); col++) {
	        calendarTable.getColumnModel().getColumn(col)
	                .setCellRenderer(new CalendarTableDayRenderer(calendarCurrentDate, caLamViecController));
	    }

	    // configure Calendar Table Week column
	    calendarTable.getColumnModel().getColumn(0).setCellRenderer(new CalendarTableWeekRenderer());

	    int weekColumnMaxWidth = 50;
	    calendarTable.getColumnModel().getColumn(0).setMaxWidth(weekColumnMaxWidth);
	}



	public LocalDate getCalendarCurrentDate() {
		return calendarCurrentDate;
	}

	public void setCalendarCurrentDate(LocalDate currentDate) {
		this.calendarCurrentDate = currentDate;
	}
	
	public WestPanelLichThang getWestPanelLichThang() {
		return this.westPanelLichThang;
	}
	
	public void setWestPanelLichThang(WestPanelLichThang westPanelLichThang) {
		this.westPanelLichThang = westPanelLichThang;
	}

	public NorthPanelLichThang getNorthPanelLichThang() {
		return northPanelLichThang;
	}

	public void setNorthPanelLichThang(NorthPanelLichThang northPanelLichThang) {
		this.northPanelLichThang = northPanelLichThang;
	}
	
	public String getNgayLamViecDangChon() {
		return ngayLamViecDangChon;
	}
}
