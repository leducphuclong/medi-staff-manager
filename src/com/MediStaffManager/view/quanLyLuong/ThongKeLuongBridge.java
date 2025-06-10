package com.MediStaffManager.view.quanLyLuong;

// JavaFX imports
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

// File and data type imports
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Type;

// Database imports
import com.MediStaffManager.utils.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// JSON imports (Gson library is required)
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ThongKeLuongBridge {
	private final WebEngine webEngine;
	private final Gson gson = new Gson(); // Create a single Gson instance for reuse

	public ThongKeLuongBridge(WebEngine webEngine) {
		this.webEngine = webEngine;
	}

	/**
	 * Tải file HTML vào WebView và hiển thị Stage.
	 * @param primaryStage Stage chính của ứng dụng.
	 * @param webView      Đối tượng WebView để tải nội dung.
	 */
	public void taiTrang(Stage primaryStage, WebView webView) {
		final String basePath = "./src/com/MediStaffManager/view/quanLyLuong/html/";
		final String fileName = "ThongKe.html";
		String filePath = basePath + fileName;
		File htmlFile = new File(filePath);

		if (htmlFile.exists() && htmlFile.isFile()) {
			String url = htmlFile.toURI().toString();
			webView.getEngine().load(url);
		} else {
			String errorMessage = "<html><body><h1>Lỗi khởi tạo</h1><p>Không thể tìm thấy file: " + filePath + "</p></body></html>";
			webView.getEngine().loadContent(errorMessage);
			System.err.println("Error: Cannot find HTML file at " + htmlFile.getAbsolutePath());
		}

		StackPane root = new StackPane();
		root.getChildren().add(webView);

		Scene scene = new Scene(root, 1400, 800);
		primaryStage.setTitle("Medi Staff Manager - Trang Thống Kê Lương");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	// --- Bridge Methods Callable from JavaScript ---

	/**
	 * Nhận và in một thông điệp từ JavaScript ra console của Java.
	 * Rất hữu ích cho việc gỡ lỗi.
	 * @param message Thông điệp cần log.
	 */
	public void log(String message) {
		System.out.println("[JS LOG] " + message);
	}

	/**
	 * Hàm chung để lấy dữ liệu thống kê dựa trên các tham số từ JavaScript.
	 * @param jsonParams Một chuỗi JSON chứa các tham số, ví dụ:
	 *                   '{"type":"tat_ca_thang"}'
	 *                   '{"type":"quy", "nam":"2025", "quy":"2"}'
	 *                   '{"type":"nam", "nam":"2025"}'
	 * @return Một chuỗi JSON chứa danh sách kết quả thống kê.
	 */
	public String getThongKeLuong(String jsonParams) {
		log("Java: Received request for statistics with params: " + jsonParams);

		// Phân tích chuỗi JSON thành một Map
		Type type = new TypeToken<Map<String, String>>(){}.getType();
		Map<String, String> params = gson.fromJson(jsonParams, type);

		String statsType = params.get("type");
		String sql = "";
		List<Object> queryParams = new ArrayList<>();

		// Xây dựng câu lệnh SQL và danh sách tham số dựa trên loại thống kê
		switch (statsType) {
			case "tat_ca_thang":
				// Thống kê theo tất cả các tháng có trong CSDL, sắp xếp theo thời gian
				sql = "SELECT lnv.ThangNam AS label, AVG(lnv.TongLuong) AS luongTrungBinh, SUM(lnv.TongLuong) AS tongLuong " +
					  "FROM luong_nhan_vien lnv " +
					  "GROUP BY lnv.ThangNam ORDER BY lnv.ThangNam ASC";
				// Không cần tham số cho câu lệnh này
				break;

			case "quy":
				// Thống kê theo từng phòng ban trong một quý cụ thể
				int yearForQuarter = Integer.parseInt(params.get("nam"));
				int quarter = Integer.parseInt(params.get("quy"));
				// Xác định tháng bắt đầu và kết thúc của quý
				int startMonth = (quarter - 1) * 3 + 1;
				int endMonth = startMonth + 2;
				String startDate = String.format("%d-%02d", yearForQuarter, startMonth);
				String endDate = String.format("%d-%02d", yearForQuarter, endMonth);

				sql = "SELECT pb.TenPhongBan AS label, AVG(lnv.TongLuong) AS luongTrungBinh, SUM(lnv.TongLuong) AS tongLuong " +
					  "FROM luong_nhan_vien lnv " +
					  "JOIN nhan_vien nv ON lnv.IDNhanVien = nv.IDNhanVien " +
					  "JOIN phong_ban pb ON nv.IDPhongBan = pb.IDPhongBan " +
					  "WHERE lnv.ThangNam BETWEEN ? AND ? " +
					  "GROUP BY pb.IDPhongBan, pb.TenPhongBan ORDER BY tongLuong DESC";
				queryParams.add(startDate);
				queryParams.add(endDate);
				break;

			case "nam":
				// Thống kê theo từng tháng trong một năm cụ thể
				sql = "SELECT lnv.ThangNam AS label, AVG(lnv.TongLuong) AS luongTrungBinh, SUM(lnv.TongLuong) AS tongLuong " +
					  "FROM luong_nhan_vien lnv " +
					  "WHERE lnv.ThangNam LIKE ? " +
					  "GROUP BY lnv.ThangNam ORDER BY lnv.ThangNam ASC";
				queryParams.add(params.get("nam") + "-%"); // Sử dụng LIKE '2025-%'
				break;
				
			default:
				log("Error: Invalid statistics type received: " + statsType);
				return "[]"; // Trả về mảng rỗng nếu loại không hợp lệ
		}
		
		List<ThongKeItem> results = new ArrayList<>();
		// Sử dụng try-with-resources để đảm bảo tài nguyên (Connection, PreparedStatement) được đóng
		try (Connection conn = DBConnection.connect();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			
			// Gán các tham số vào câu lệnh SQL
			for (int i = 0; i < queryParams.size(); i++) {
				pstmt.setObject(i + 1, queryParams.get(i));
			}
			log("Executing SQL: " + pstmt.toString());

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					// Tạo đối tượng DTO và điền dữ liệu
					ThongKeItem item = new ThongKeItem();
					item.label = rs.getString("label");
					item.luongTrungBinh = rs.getBigDecimal("luongTrungBinh");
					item.tongLuong = rs.getBigDecimal("tongLuong");
					results.add(item);
				}
			}
		} catch (SQLException e) {
			System.err.println("ERROR executing stats query: " + e.getMessage());
			e.printStackTrace();
			return "[]"; // Trả về mảng rỗng nếu có lỗi SQL
		}

		// Chuyển danh sách kết quả thành chuỗi JSON và trả về cho JavaScript
		String jsonResult = gson.toJson(results);
		log("Java: Sending JSON response: " + jsonResult);
		return jsonResult;
	}

	/**
	 * Lớp nội bộ (Data Transfer Object - DTO) để chứa kết quả từ truy vấn SQL.
	 * Tên các trường (label, luongTrungBinh, tongLuong) khớp với những gì JavaScript mong đợi.
	 */
	private static class ThongKeItem {
		String label;
		BigDecimal luongTrungBinh;
		BigDecimal tongLuong;
	}
}