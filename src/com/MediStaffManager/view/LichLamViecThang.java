package com.MediStaffManager.view;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LichLamViecThang implements Serializable {
    private static final long serialVersionUID = 1L;  // Add serialVersionUID for the main class
    static JLabel leftLabel; 
    static JLabel titleLabel;
    static LocalDate currentMonth = LocalDate.now();
    static JTable calendarTable;
    static Map<LocalDate, String> customEvents = new HashMap<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LichLamViecThang::LichLamViecThangGUI);
    }

    private static void leftPanel(JFrame frame) {
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(70, 130, 180)); 
        leftPanel.setPreferredSize(new Dimension(350, frame.getHeight()));
        leftPanel.setLayout(new BorderLayout());
        
        leftLabel = new JLabel("<html><div style='text-align:center;'>Select a day</div></html>", JLabel.CENTER);
        leftLabel.setFont(new Font("Arial", Font.BOLD, 20));
        leftLabel.setForeground(Color.WHITE);
        
        leftPanel.add(leftLabel, BorderLayout.CENTER);
        frame.add(leftPanel, BorderLayout.WEST);
    }

    private static void topPanel(JFrame frame) {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(frame.getWidth(), 80));

        // Left placeholder panel
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.BLUE);
        leftPanel.setPreferredSize(new Dimension(350, 100));
        topPanel.add(leftPanel, BorderLayout.WEST);

        // Right panel for title and buttons
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(new Color(30, 144, 255));

        JButton prevButton = new JButton("◀");
        JButton nextButton = new JButton("▶");
        
        prevButton.setContentAreaFilled(false);
        prevButton.setBorderPainted(false);
        prevButton.setFocusPainted(false);

        nextButton.setContentAreaFilled(false);
        nextButton.setBorderPainted(false);
        nextButton.setFocusPainted(false);

        // Dialog is good corss-platfom Unicode font in Java
        prevButton.setFont(new Font("Dialog", Font.BOLD, 40));
        nextButton.setFont(new Font("Dialog", Font.BOLD, 40));

        titleLabel = new JLabel("", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        updateTitle();

        prevButton.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            updateTitle();
            reloadCalendar();
        });

        nextButton.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            updateTitle();
            reloadCalendar();
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridx = 0;
        gbc.weightx = 0.1;
        rightPanel.add(prevButton, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        rightPanel.add(titleLabel, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.1;
        rightPanel.add(nextButton, gbc);

        topPanel.add(rightPanel, BorderLayout.CENTER);
        frame.add(topPanel, BorderLayout.NORTH);
    }

    
    private static void updateTitle() {
        Locale vietnamese = new Locale("vi", "VN");
        String monthName = currentMonth.getMonth().getDisplayName(TextStyle.FULL, vietnamese);
        monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1);
        String monthYear = monthName + ", " + currentMonth.getYear();
        titleLabel.setText(monthYear);
    }

    private static void centerPanel(JFrame frame) {
    	// Create main Panel for center of Frame
        JPanel centerPanel = new JPanel();
        
        // Columns Name of calendar
        String[] columnNames = {"Tuần", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        calendarTable = new JTable(new DefaultTableModel(new Object[6][8], columnNames));
//        // Data - days
//        Object[][] data = {
//                {"w1", "31", "1", "2", "3", "4", "5", "6"},
//                {"w2", "7",  "8", "9", "10", "11", "12", "13"},
//                {"w3", "14", "15", "16", "17", "18", "19", "20"},
//                {"w4", "21", "22", "23", "24", "25", "26", "27"},
//                {"w5", "28", "29", "30", "", "", "", ""}
//        };
        // Create a JTable to contain the data
//        JTable calendarTable = new JTable(data, columnNames);
        calendarTable.setRowHeight(150);
        calendarTable.setDefaultEditor(Object.class, null); // disable editing

        // Create a header instance to contain the header of calendar
        JTableHeader header = calendarTable.getTableHeader();
        header.setDefaultRenderer(new HeaderRenderer());
        
        // Custom renderer for "No. Week" column
        calendarTable.getColumnModel().getColumn(0).setCellRenderer(new CustomWeekRenderer());
        calendarTable.getColumnModel().getColumn(0).setMinWidth(50);
        calendarTable.getColumnModel().getColumn(0).setMaxWidth(50);
        calendarTable.getColumnModel().getColumn(0).setPreferredWidth(50);


        // set render with custom renderer for "days" cells
        for (int i = 1; i < calendarTable.getColumnCount(); i++)
            calendarTable.getColumnModel().getColumn(i).setCellRenderer(new CustomTableCellRenderer());
        
        // 	Listen for row selection changes
        calendarTable.getSelectionModel().addListSelectionListener(e -> updateLeftPanel(calendarTable));

        // Listen for column selection changes
        calendarTable.getColumnModel().getSelectionModel().addListSelectionListener(e -> updateLeftPanel(calendarTable));

        // Others Listener 
        

        // Add scroll panel to take all space in the center
        JScrollPane scrollPane = new JScrollPane(calendarTable);
        
        // Add the scroll panel to Center of main panel
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add main panel to Frame in Center
        frame.add(centerPanel, BorderLayout.CENTER);
        
        // reload 
        reloadCalendar();
    }
    
    private static void reloadCalendar() {
        DefaultTableModel model = (DefaultTableModel) calendarTable.getModel();
        for (int row = 0; row < model.getRowCount(); row++)
            for (int col = 0; col < model.getColumnCount(); col++)
                model.setValueAt("", row, col);

        YearMonth ym = YearMonth.of(currentMonth.getYear(), currentMonth.getMonth());
        LocalDate firstDay = ym.atDay(1);
        int dayOfWeekOffset = ((firstDay.getDayOfWeek().getValue()));

        int totalDays = ym.lengthOfMonth();
        int dayCounter = 1;

        for (int i = 0; i < 6; i++) {
        	LocalDate dateInWeek = LocalDate.of(currentMonth.getYear(), currentMonth.getMonthValue(), 1).plusWeeks(i);
            int weekOfYear = dateInWeek.get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            model.setValueAt(weekOfYear, i, 0);
//            model.setValueAt((i + 1), i, 0);
            for (int j = 1; j <= 7; j++) {
                if (i == 0 && j < dayOfWeekOffset) continue;
                if (dayCounter > totalDays) return;
                model.setValueAt(String.valueOf(dayCounter), i, j);
                dayCounter++;
            }
        }
    }

    private static void LichLamViecThangGUI() {
        JFrame frame = new JFrame("Lịch làm việc trong tháng của phòng ban");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1600, 900);  
        frame.setLayout(new BorderLayout());

        leftPanel(frame);
        topPanel(frame);
        centerPanel(frame);

        frame.setVisible(true);
    }

    private static void updateLeftPanel(JTable table) {
        int row = table.getSelectedRow();
        int col = table.getSelectedColumn();

        if (row >= 0 && col >= 1 && col <= 7) {
            Object dayValue = table.getValueAt(row, col);
            if (dayValue != null && !dayValue.toString().isEmpty()) {
                String day = dayValue.toString();
                String exampleInfo = "<html><div style='text-align:center;'>"
                        + "<h2 style='color:white;'>Schedule for " + day + "</h2>"
                        + "<p>• 08:00 - Staff A<br>"
                        + "• 10:00 - Staff B<br>"
                        + "• 14:00 - Staff C</p></div></html>";
                leftLabel.setText(exampleInfo);
            }
        }
    }

    
    static class CustomTableCellRenderer extends DefaultTableCellRenderer implements Serializable {
        private static final long serialVersionUID = 1L;  // Add serialVersionUID for inner class
        private final LocalDate today = LocalDate.now(); 

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Background color handling
            Color defaultBg = Color.WHITE;
            Color focusBg = new Color(255, 228, 181);
            c.setBackground(hasFocus ? focusBg : defaultBg);
            
            // Cell style
            setHorizontalAlignment(SwingConstants.CENTER);
            setVerticalAlignment(SwingConstants.CENTER);
            Color borderColor = new Color(200, 200, 200);
            setBorder(new LineBorder(borderColor, 2));
            
            // Content
            String day = value != null ? value.toString() : "";
            String[] employeeIds = {"ID: 123", "ID: 456", "ID: 789", "ID: abc", "ID: def"};
            String idsHtml = String.join("<br>", employeeIds);
            
            setText("<html><div style='text-align:center'>"
                    + "<span style='font-size:25pt; font-weight:bold;'>" + day + "</span><br>"
                    + "<span style='font-size:15pt;'>" + idsHtml + "</span>"
                    + "</div></html>");
            
            // Highlight today
            try {
                int dayInt = Integer.parseInt(day);
                if (today.getMonthValue() == currentMonth.getMonthValue() && today.getDayOfMonth() == dayInt) {
                    c.setBackground(Color.ORANGE);
                    setFont(new Font("Arial", Font.BOLD, 28));
                }
            } catch (NumberFormatException ignored) {
                // Not a valid day number — ignore
            }
            
            return c;
        }
    }

    static class HeaderRenderer extends DefaultTableCellRenderer implements Serializable {
        private static final long serialVersionUID = 1L;  // Add serialVersionUID for inner class
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setFont(new Font("Arial", Font.BOLD, 16));
            setHorizontalAlignment(SwingConstants.CENTER);
            setBackground(new Color(135, 206, 250));  
            setForeground(Color.BLACK);  
            return c;
        }
    }
    
    static class CustomWeekRenderer extends DefaultTableCellRenderer implements Serializable {
    	private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            // Set different background color and bold font for "No. Week" column
//            setBackground(new Color(173, 216, 230));  // Light blue background
            setFont(new Font("Arial", Font.BOLD, 18));  // Bold font
            setHorizontalAlignment(SwingConstants.CENTER);
            setVerticalAlignment(SwingConstants.CENTER);
            Color borderColor = new Color(200, 200, 200);
            setBorder(new LineBorder(borderColor, 2));
            
            return c;
        }
    }
}
