package com.MediStaffManager.view;

import com.MediStaffManager.bean.TaiKhoan;
import com.MediStaffManager.controller.TaiKhoanController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog cho phép người dùng đăng ký tài khoản mới.
 * Sử dụng TaiKhoanController để gọi nghiệp vụ đăng ký.
 */
public class RegisterDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> roleComboBox; // Chọn vai trò: "KETOAN" hoặc "QLNHANSU"
    private JButton registerButton;
    private JButton cancelButton;

    private TaiKhoanController taiKhoanController;

    public RegisterDialog(Frame owner) {
        super(owner, "Đăng Ký Tài Khoản", true);
        taiKhoanController = new TaiKhoanController();

        setSize(450, 300);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(new Color(204, 229, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Tên đăng nhập
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);

        // Mật khẩu
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        // Xác nhận mật khẩu
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Xác nhận mật khẩu:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        confirmPasswordField = new JPasswordField(20);
        formPanel.add(confirmPasswordField, gbc);

        // Vai trò
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Vai trò:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        roleComboBox = new JComboBox<>(new String[]{"KETOAN", "QLNHANSU"});
        roleComboBox.setSelectedItem("KETOAN");
        formPanel.add(roleComboBox, gbc);

        // Panel chứa 2 nút: Đăng ký + Hủy
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(new Color(204, 229, 255));
        registerButton = new JButton("Đăng ký");
        cancelButton = new JButton("Hủy");
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        // Thêm buttonPanel vào formPanel
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(15, 5, 5, 5);
        formPanel.add(buttonPanel, gbc);

        add(formPanel);

        // Xử lý sự kiện Đăng ký
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });
        confirmPasswordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });

        // Xử lý sự kiện Hủy
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();

        // Kiểm tra dữ liệu đầu vào
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng điền đầy đủ thông tin.",
                    "Lỗi Đăng Ký", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                    "Mật khẩu phải có ít nhất 6 ký tự.",
                    "Lỗi Đăng Ký", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Mật khẩu và xác nhận mật khẩu không khớp.",
                    "Lỗi Đăng Ký", JOptionPane.ERROR_MESSAGE);
            confirmPasswordField.setText("");
            confirmPasswordField.requestFocusInWindow();
            return;
        }

        // Tạo đối tượng TaiKhoan mới (ID để null, sẽ do CSDL generate)
        TaiKhoan newAccount = new TaiKhoan(null, username, password, role);

        // Gọi bước đăng ký qua controller
        boolean success = taiKhoanController.register(newAccount);
        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Đăng ký thành công! Vui lòng đăng nhập.",
                    "Đăng Ký Thành Công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Đăng ký thất bại. Có thể tên đăng nhập đã tồn tại hoặc có lỗi hệ thống.",
                    "Lỗi Đăng Ký", JOptionPane.ERROR_MESSAGE);
        }
    }
}
