//package com.MediStaffManager;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//public class LoginUI extends JFrame {
//    private JTextField usernameField;
//    private JPasswordField passwordField;
//
//    public LoginUI() {
//        setTitle("MediStaff Manager - Login");
//        setSize(800, 500);  // Increased Window Size
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setLocationRelativeTo(null);
//        setResizable(false);
//        setLayout(null);
//
//        // Custom Colors
//        Color backgroundColor = new Color(34, 47, 62);  // Darker Theme
//        Color buttonColor = new Color(41, 128, 185);
//        Color textColor = Color.WHITE;
//
//        // Set Background
//        getContentPane().setBackground(backgroundColor);
//
//        // Title Label
//        JLabel titleLabel = new JLabel("MediStaff Manager");
//        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));  // Bigger Font
//        titleLabel.setForeground(textColor);
//        titleLabel.setBounds(250, 40, 400, 50);
//        add(titleLabel);
//
//        // Username Label
//        JLabel usernameLabel = new JLabel("Username:");
//        usernameLabel.setFont(new Font("Arial", Font.BOLD, 22));  // Bigger Font
//        usernameLabel.setForeground(textColor);
//        usernameLabel.setBounds(180, 150, 150, 35);
//        add(usernameLabel);
//
//        // Username Field
//        usernameField = new JTextField();
//        usernameField.setBounds(350, 150, 300, 45);  // Bigger Input Fields
//        usernameField.setFont(new Font("Arial", Font.PLAIN, 20));
//        usernameField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
//        add(usernameField);
//
//        // Password Label
//        JLabel passwordLabel = new JLabel("Password:");
//        passwordLabel.setFont(new Font("Arial", Font.BOLD, 22));
//        passwordLabel.setForeground(textColor);
//        passwordLabel.setBounds(180, 230, 150, 35);
//        add(passwordLabel);
//
//        // Password Field
//        passwordField = new JPasswordField();
//        passwordField.setBounds(350, 230, 300, 45);
//        passwordField.setFont(new Font("Arial", Font.PLAIN, 20));
//        passwordField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
//        add(passwordField);
//
//        // Login Button
//        JButton loginButton = new JButton("Login");
//        loginButton.setBounds(350, 320, 300, 50);
//        loginButton.setFont(new Font("Arial", Font.BOLD, 24));
//        loginButton.setForeground(Color.WHITE);
//        loginButton.setBackground(buttonColor);
//        loginButton.setFocusPainted(false);
//        loginButton.setBorder(BorderFactory.createEmptyBorder());
//        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        loginButton.addActionListener(new LoginHandler());
//        add(loginButton);
//
//        setVisible(true);
//    }
//
//    private class LoginHandler implements ActionListener {
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            String username = usernameField.getText();
//            String password = new String(passwordField.getPassword());
//
//            if ("admin".equals(username) && "password".equals(password)) {
//                JOptionPane.showMessageDialog(LoginUI.this, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
//                dispose();
//                new DashboardUI();
//            } else {
//                JOptionPane.showMessageDialog(LoginUI.this, "Invalid Credentials!", "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        }
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(LoginUI::new);
//    }
//}
//
//class DashboardUI extends JFrame {
//    public DashboardUI() {
//        setTitle("MediStaff Manager - Dashboard");
//        setSize(800, 500);  // Bigger Dashboard Window
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setLocationRelativeTo(null);
//        getContentPane().setBackground(new Color(236, 240, 241));
//
//        JLabel welcomeLabel = new JLabel("Welcome to MediStaff Manager!", SwingConstants.CENTER);
//        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));  // Bigger Welcome Text
//        welcomeLabel.setBounds(100, 200, 600, 50);
//        add(welcomeLabel);
//
//        setLayout(null);
//        setVisible(true);
//    }
//}
