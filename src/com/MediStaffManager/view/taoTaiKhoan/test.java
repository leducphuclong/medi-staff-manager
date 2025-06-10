package com.MediStaffManager.view.taoTaiKhoan;
import java.sql.*;
import java.util.Scanner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.MediStaffManager.utils.DBConnection;

public class test {

    // Lớp PasswordEncryption để mã hóa mật khẩu với BCrypt
    static class PasswordEncryption {
        private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Mã hóa mật khẩu
        public static String encryptPassword(String rawPassword) {
            return passwordEncoder.encode(rawPassword);
        }

        // Kiểm tra mật khẩu đã mã hóa với mật khẩu người dùng nhập
        public static boolean checkPassword(String rawPassword, String encodedPassword) {
            return passwordEncoder.matches(rawPassword, encodedPassword);
        }
    }

    // Lớp DatabaseUtil để kết nối cơ sở dữ liệu
 

    // Lớp AccountManager để thực hiện tạo tài khoản
    static class AccountManager {

        // Hàm để tạo tài khoản mới
        public static void createAccount(String username, String password, String email) {
            String hashedPassword = PasswordEncryption.encryptPassword(password);  // Mã hóa mật khẩu

            try (Connection connection = DBConnection.connect()) {
                String query = "INSERT INTO tai_khoan (TenDangNhap, MatKhau, email, VaiTro) VALUES (?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, username);
                statement.setString(2, hashedPassword); // Lưu mật khẩu đã mã hóa
                statement.setString(3, email);  // Lưu email
                statement.setString(4, "QuanLyNhanSu");  // Lưu vai trò (có thể thay đổi tùy vào yêu cầu)

                int result = statement.executeUpdate();
                if (result > 0) {
                    System.out.println("Tạo tài khoản thành công!");
                } else {
                    System.out.println("Lỗi khi tạo tài khoản.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Nhập thông tin tài khoản
        System.out.print("Nhập tên người dùng: ");
        String username = scanner.nextLine();

        System.out.print("Nhập mật khẩu: ");
        String password = scanner.nextLine();

        System.out.print("Nhập email: ");
        String email = scanner.nextLine();

        // Gọi hàm tạo tài khoản với email
        AccountManager.createAccount(username, password, email);

        scanner.close();
    }
}
