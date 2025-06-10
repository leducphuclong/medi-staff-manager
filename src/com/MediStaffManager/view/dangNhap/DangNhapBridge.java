package com.MediStaffManager.view.dangNhap;

import com.MediStaffManager.utils.DBConnection;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

// Giả định bạn có các lớp Bridge và view khác
import com.MediStaffManager.view.trangChu.KeToanBridge;
import com.MediStaffManager.view.trangChu.QuanLyNhanSuBridge;

public class DangNhapBridge {

    // Các biến này sẽ được khởi tạo khi taiTrang được gọi
    private Stage primaryStage;
    private WebView webView;
    
    private final WebEngine webEngine;
    private final Connection conn;
    private final Gson gson;

    // Giữ nguyên constructor gốc
    public DangNhapBridge(WebEngine webEngine) {
        this.webEngine = webEngine;
        this.conn = DBConnection.connect();
        this.gson = new Gson();
    }

    /**
     * Giữ nguyên phương thức taiTrang như yêu cầu.
     * Phương thức này sẽ được gọi từ lớp Application chính để thiết lập màn hình đăng nhập.
     * Nó cũng có nhiệm vụ quan trọng là lưu lại Stage và WebView để sử dụng sau này.
     */
    public void taiTrang(Stage primaryStage, WebView webView) {
        // Lưu lại Stage và WebView để phương thức dangNhap() có thể sử dụng
        this.primaryStage = primaryStage;
        this.webView = webView;

        final String basePath = "./src/com/MediStaffManager/view/dangNhap/";
        final String fileName = "dangNhap.html";
        File htmlFile = new File(basePath + fileName);

        if (htmlFile.exists() && htmlFile.isFile()) {
            this.webEngine.load(htmlFile.toURI().toString());
        } else {
            this.webEngine.loadContent("<html><body><h1>Lỗi khởi tạo</h1><p>Không thể tìm thấy Trang Đăng Nhập</p></body></html>");
        }

        StackPane root = new StackPane(this.webView);
        Scene scene = new Scene(root, 1400, 800);
        this.primaryStage.setTitle("Medi Staff Manager - Đăng Nhập");
        this.primaryStage.setScene(scene);
        this.primaryStage.show();
    }

    private boolean authDangNhap(String rawPassword, String encodedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * Xử lý toàn bộ quá trình đăng nhập.
     * Phương thức này được gọi từ JavaScript và không cần tham số Stage/WebView
     * vì nó sử dụng các biến instance đã được lưu bởi phương thức taiTrang().
     * @return true nếu xác thực thành công, false nếu thất bại.
     */
    public boolean dangNhap(String username, String password) {
        // Kiểm tra xem taiTrang đã được gọi và khởi tạo primaryStage/webView chưa
        if (this.primaryStage == null || this.webView == null) {
            System.err.println("Lỗi nghiêm trọng: Cần gọi taiTrang() trước khi thực hiện đăng nhập.");
            return false;
        }

        String query = "SELECT MatKhau, VaiTro FROM tai_khoan WHERE TenDangNhap = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String storedPassword = resultSet.getString("MatKhau");
                    String vaiTro = resultSet.getString("VaiTro");

                    if (authDangNhap(password, storedPassword)) {
                        System.out.println("Đăng nhập thành công với vai trò: " + vaiTro);
                        Platform.runLater(() -> dieuHuongTheoVaiTro(vaiTro));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        System.out.println("Đăng nhập thất bại cho người dùng: " + username);
        return false;
    }

    /**
     * Điều hướng dựa trên vai trò, sử dụng Stage và WebView đã được lưu.
     */
    private void dieuHuongTheoVaiTro(String vaiTro) {
        if (vaiTro == null) {
            System.err.println("Lỗi: Vai trò không được xác định.");
            return;
        }
        // Lưu ý quan trọng: bạn cần đảm bảo các lớp Bridge khác có constructor phù hợp
        // và phương thức taiTrang nhận đúng tham số (Stage, WebView).
        switch (vaiTro) {
            case "Quản lý Nhân sự":
                // Giả sử QuanLyNhanSuBridge có constructor nhận WebEngine
                new QuanLyNhanSuBridge(this.webEngine).taiTrang(this.primaryStage, this.webView);
                break;
            case "Kế toán":
                // Giả sử KeToanBridge có constructor nhận WebEngine
                new KeToanBridge(this.webEngine).taiTrang(this.primaryStage, this.webView);
                break;
            default:
                System.err.println("Cảnh báo: Không có trang nào được định nghĩa cho vai trò '" + vaiTro + "'");
                this.webEngine.loadContent("<html><body><h1>Lỗi Phân Quyền</h1><p>Vai trò của bạn không được hỗ trợ.</p></body></html>");
                break;
        }
    }

    public static void main(String[] args) {
        // main method không thể hoạt động đúng trong thiết lập này vì nó phụ thuộc vào
        // một ứng dụng JavaFX đang chạy để cung cấp Stage và WebView.
        System.out.println("Phương thức main chỉ để kiểm tra logic, không thể kiểm tra UI.");
    }
}