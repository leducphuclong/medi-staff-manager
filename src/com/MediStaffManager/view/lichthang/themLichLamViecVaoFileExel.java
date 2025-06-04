package com.MediStaffManager.view.lichthang;

import javax.swing.*;
import java.awt.*;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class themLichLamViecVaoFileExel {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Excel Reader Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        JButton button = new JButton("Select Excel File");
        button.setFont(new Font("Arial", Font.PLAIN, 16));

        button.addActionListener(_ -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Choose Excel File");
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Files", "xls", "xlsx"));

            int returnValue = fileChooser.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                readAndUpdateExcelFile(selectedFile);  // Modify this to read and update the Excel file
            }
        });

        frame.setLayout(new FlowLayout());
        frame.add(button);
        frame.setVisible(true);
    }

    private static void readAndUpdateExcelFile(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            // Open the Excel file
            Workbook workbook = new XSSFWorkbook(fis);  // For .xlsx files
            Sheet sheet = workbook.getSheetAt(0);  // Reading the first sheet

            // Print existing data from the Excel file to the console
            System.out.println("Existing Data in Excel:");
            for (Row row : sheet) {
                for (Cell cell : row) {
                    System.out.print(cell.toString() + "\t");
                }
                System.out.println();  // Move to the next row
            }

            // Add new data to the Excel sheet (example with employee shift data)
            addNewData(sheet);

            // Write the updated Excel file
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
                workbook.close();
                System.out.println("Excel file updated successfully!");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error reading the Excel file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void addNewData(Sheet sheet) {
        // Define new data to add (example with employee shift data)
    	Object[][] newData = {
    		    { 1, 2, "2025-05-06", "Ca 1", "Sáng", "2025-05-06 08:00:00", "2025-05-06 17:00:00", "2025-05-06 12:00:00", "2025-05-06 13:00:00", "Phòng Kỹ Thuật", "Nghỉ trưa", 0 },
    		    { 2, 2, "2025-05-06", "Ca 1", "Chiều", "2025-05-06 13:00:00", "2025-05-06 17:00:00", "2025-05-06 15:00:00", "2025-05-06 16:00:00", "Phòng Hành Chính", "Nghỉ chiều", 0 },
    		    { 3, 3, "2025-05-09", "Ca 2", "Sáng", "2025-05-09 08:00:00", "2025-05-09 17:00:00", "2025-05-09 12:00:00", "2025-05-09 13:00:00", "Phòng Hành Chính", "Nghỉ trưa", 0 }  		    
    		};


        // Find the next empty row in the Excel sheet
        int rowNum = sheet.getPhysicalNumberOfRows();

        // Loop through and add new rows with the data
        for (Object[] rowData : newData) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < rowData.length; i++) {
                row.createCell(i).setCellValue(rowData[i].toString());
            }
        }
    }
}
