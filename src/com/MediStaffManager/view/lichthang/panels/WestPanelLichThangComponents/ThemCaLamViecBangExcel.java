package com.MediStaffManager.view.lichthang.panels.WestPanelLichThangComponents;

import javax.swing.*;
import java.awt.*;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.MediStaffManager.bean.CaLamViecBEAN;
import com.MediStaffManager.controller.CaLamViecController;

public class ThemCaLamViecBangExcel {
	
    public static void main(String[] args) {
        JFrame frame = new JFrame("Excel Reader Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

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
                readAndDisplayExcelFile(selectedFile);
                readAndInsertDataToDatabase(selectedFile);
            }
        });

        frame.setLayout(new FlowLayout());
        frame.add(button);
        frame.setVisible(true);
    }
    
    public static void readAndInsertDataToDatabase(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;  // Skip header row

                // Handle IDNhanVien as integer or string
                int idNhanVien = 0;
                if (row.getCell(0) != null) {  // Ensure that the cell is not null
                    if (row.getCell(0).getCellType() == CellType.NUMERIC) {
                        idNhanVien = (int) row.getCell(0).getNumericCellValue();  // Employee ID
                    } else if (row.getCell(0).getCellType() == CellType.STRING) {
                        try {
                            idNhanVien = Integer.parseInt(row.getCell(0).getStringCellValue());  // Employee ID
                        } catch (NumberFormatException e) {
                            System.out.println("Non-numeric ID found, skipping row or setting default ID.");
                        }
                    }
                }

                // Extract other fields, ensuring that each cell is not null before attempting to read
                String ngayLamViec = row.getCell(1) != null ? row.getCell(1).getStringCellValue() : "";
                String tenCa = row.getCell(2) != null ? row.getCell(2).getStringCellValue() : "";
                String moTaCa = row.getCell(3) != null ? row.getCell(3).getStringCellValue() : "";
                String gioBatDau = row.getCell(4) != null ? row.getCell(4).getStringCellValue() : "";
                String gioKetThuc = row.getCell(5) != null ? row.getCell(5).getStringCellValue() : "";
                String gioNghiBatDau = row.getCell(6) != null ? row.getCell(6).getStringCellValue() : "";
                String gioNghiKetThuc = row.getCell(7) != null ? row.getCell(7).getStringCellValue() : "";
                String donVi = row.getCell(8) != null ? row.getCell(8).getStringCellValue() : "";
                String ghiChu = row.getCell(9) != null ? row.getCell(9).getStringCellValue() : "";

                // If GioBatDau or GioKetThuc is empty, skip this row
                if (gioBatDau.isEmpty() || gioKetThuc.isEmpty()) {
                    continue;  // Skip row if either of the date fields is empty
                }

                // Handle laTrucOnCall as boolean
                boolean laTrucOnCall = false;
                if (row.getCell(10) != null) {  // Ensure that the cell is not null
                    if (row.getCell(10).getCellType() == CellType.STRING) {
                        String value = row.getCell(10).getStringCellValue();
                        laTrucOnCall = "1".equals(value); // Convert "1" to true, else false
                    } else if (row.getCell(10).getCellType() == CellType.NUMERIC) {
                        laTrucOnCall = row.getCell(10).getNumericCellValue() == 1;
                    }
                }

                // Create the CaLamViec object with validated data
                CaLamViecBEAN caLamViec = new CaLamViecBEAN(0, idNhanVien, "", ngayLamViec, tenCa, moTaCa,
                        gioBatDau, gioKetThuc, gioNghiBatDau, gioNghiKetThuc, donVi, ghiChu, laTrucOnCall);

                // Insert the caLamViec object into the database via CaLamViecController
                CaLamViecController controller = new CaLamViecController();
                controller.themCaLamViec(caLamViec);
            }

            workbook.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error reading the Excel file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void readAndDisplayExcelFile(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
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


            workbook.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error reading the Excel file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    

    @SuppressWarnings("unused")
	private static void writeUpdatedExcelFile(File file, Workbook workbook) {
        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
            workbook.close();
            System.out.println("Excel file updated successfully!");
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saving the Excel file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
