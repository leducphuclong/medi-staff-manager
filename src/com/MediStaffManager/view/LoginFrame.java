package com.MediStaffManager.view;

import com.MediStaffManager.controller.TaiKhoanController;
import com.MediStaffManager.bean.TaiKhoan;
import com.MediStaffManager.utils.DBConnection;
import com.MediStaffManager.view.quanLyLuong.QuanLyLuongView;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * LoginFrame sử dụng Swing, sau đó khi người dùng có vai trò "KETOAN", sẽ mở cửa sổ JavaFX QuanLyLuongView.
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    private TaiKhoanController userController;
    public static TaiKhoan currentUser = null;

    public LoginFrame() {
        userController = new TaiKhoanController();

        setTitle("Đăng nhập - MediStaff Manager");
        setSize(420, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Thiết lập font chung
        Font mainFont = new Font("Segoe UI", Font.PLAIN, 16);
        UIManager.put("Label.font", mainFont);
        UIManager.put("Button.font", mainFont);
        UIManager.put("TextField.font", mainFont);
        UIManager.put("PasswordField.font", mainFont);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(204, 229, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Label: Tên đăng nhập
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(new JLabel("Tên đăng nhập:"), gbc);

        // TextField: username
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        usernameField = new JTextField(20);
        mainPanel.add(usernameField, gbc);

        // Label: Mật khẩu
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Mật khẩu:"), gbc);

        // PasswordField: password
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        mainPanel.add(passwordField, gbc);

        // Panel chứa 2 nút: Đăng nhập + Đăng ký
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(new Color(204, 229, 255));
        loginButton = new JButton("Đăng nhập");
        loginButton.setBackground(new Color(244, 67, 54));
        loginButton.setForeground(Color.WHITE);
        registerButton = new JButton("Đăng ký");
        registerButton.setBackground(new Color(76, 175, 80));
        registerButton.setForeground(Color.WHITE);
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        // Thêm buttonPanel vào mainPanel
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 5, 5, 5);
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);

        // Xử lý sự kiện Đăng nhập
        loginButton.addActionListener(e -> handleLogin());
        passwordField.addActionListener(e -> handleLogin());
        usernameField.addActionListener(e -> passwordField.requestFocusInWindow());

        // Xử lý sự kiện Đăng ký (giả sử bạn có RegisterDialog)
        registerButton.addActionListener(e -> {
            RegisterDialog registerDialog = new RegisterDialog(LoginFrame.this);
            registerDialog.setVisible(true);
        });

        // Khi đóng ứng dụng, giải phóng kết nối CSDL
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DBConnection.closeConnection();
                System.out.println("Ứng dụng đã đóng và kết nối CSDL đã được giải phóng.");
            }
        });
    }

    /**
     * Xử lý logic Đăng nhập (gọi TaiKhoanController.login).
     * Nếu role = "ADMIN1", khởi tạo JavaFX và mở QuanLyLuongView.
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập tên đăng nhập và mật khẩu.",
                    "Lỗi Đăng Nhập", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Trường hợp đặc biệt (ví dụ cứng): tên đăng nhập = "ketoan", mật khẩu = "ketoan123"
        if ("ketoan".equals(username) && "Ketoan123".equals(password)) {
            this.dispose();
            openJavaFXQuanLyLuong();
            return;
        }

        // Xác thực qua CSDL
        currentUser = userController.login(username, password);
        if (currentUser != null) {
            JOptionPane.showMessageDialog(this,
                    "Đăng nhập thành công! Chào mừng " + currentUser.getTenDangNhap() + ".",
                    "Đăng Nhập Thành Công", JOptionPane.INFORMATION_MESSAGE);

            this.dispose();

            String role = currentUser.getVaiTro();
            if ("ADMIN1".equalsIgnoreCase(role)) {
                openJavaFXQuanLyLuong();
            }
            else if ("ADMIN2".equalsIgnoreCase(role)) {
                // Viết vào đây tương tự như ADMIN1 với view quản lý nhân sự
               
            }
            else {
                JOptionPane.showMessageDialog(this,
                        "Vai trò không hợp lệ: " + role,
                        "Lỗi Vai Trò", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Tên đăng nhập hoặc mật khẩu không đúng.",
                    "Lỗi Đăng Nhập", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            passwordField.requestFocusInWindow();
        }
    }

    /**
     * Phương thức này khởi tạo JavaFX Toolkit rồi mở QuanLyLuongView.
     * - Tạo một JFXPanel để initialize JavaFX.
     * - Sau đó gọi Platform.runLater(...) để hiển thị Stage.
     */
    private void openJavaFXQuanLyLuong() {
        // Bước 1: Khởi JavaFX Toolkit (nếu chưa khởi)
        new JFXPanel();  // tạo JFXPanel ⇒ JavaFX runtime được khởi

        // Bước 2: Trên JavaFX Thread, gọi start() để hiển thị Stage
        Platform.runLater(() -> {
            try {
                QuanLyLuongView view = new QuanLyLuongView();
                Stage stage = new Stage();
                view.start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }


   //  (Optional) main để chạy thử riêng LoginFrame
     public static void main(String[] args) {
         SwingUtilities.invokeLater(() -> {
             try {
                 UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
             } catch (Exception ex) {
                 ex.printStackTrace();
             }
             new LoginFrame().setVisible(true);
         });
     }
}
