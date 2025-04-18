package com.MediStaffManager.view;

import com.MediStaffManager.controller.AddEmployeeController;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            AddEmployeeView view = new AddEmployeeView();
            new AddEmployeeController(view);
            view.setVisible(true);
        });
    }
}