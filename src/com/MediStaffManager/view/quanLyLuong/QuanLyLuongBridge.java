package com.MediStaffManager.view.quanLyLuong;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.File;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.MediStaffManager.bean.LuongNhanVien;
import com.MediStaffManager.bean.ThongKeResult;
import com.MediStaffManager.controller.LuongNhanVienController;

public class QuanLyLuongBridge {
	private WebEngine webEngine;
	private LuongNhanVienController luongNhanVienController;

	public QuanLyLuongBridge(WebEngine webEngine) {
		this.webEngine = webEngine;
		this.luongNhanVienController = new LuongNhanVienController();
	}

	public void taiTrang(Stage primaryStage, WebView webView) {
		final String basePath = "./src/com/MediStaffManager/view/quanLyLuong/html/";
		final String fileName = "QuanLyLuong.html";
		String filePath = basePath + fileName;
		File htmlFile = new File(filePath);

		if (htmlFile.exists() && htmlFile.isFile()) {
			String url = htmlFile.toURI().toString();
			webView.getEngine().load(url);
		} else {
			webView.getEngine().loadContent(
					"<html><body><h1>Lỗi khởi tạo</h1><p>Không thể tìm thấy Trang Quản Lý Lương</p></body></html>");
		}

		StackPane root = new StackPane();
		root.getChildren().add(webView);

		Scene scene = new Scene(root, 1400, 800);
		primaryStage.setTitle("Medi Staff Manager - Trang Quản Lý Lương");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	private String escapeJsonString(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    private String bigDecimalToString(BigDecimal bd) {
        return bd == null ? "null" : bd.toPlainString();
    }
    
    private BigDecimal parseBigDecimal(String valueStr, String fieldName) {
        if (valueStr == null || valueStr.trim().isEmpty() || "null".equalsIgnoreCase(valueStr.trim())) {
            log("LuongBridge: " + fieldName + " is null or empty string, returning null for BigDecimal.");
            return null; // Return null for optional fields if they are empty
        }
        try {
            return new BigDecimal(valueStr);
        } catch (NumberFormatException e) {
            System.err.println("LuongBridge: Invalid format for BigDecimal " + fieldName + ": \"" + valueStr + "\". Error: " + e.getMessage());
            return null; // Or throw an error / return BigDecimal.ZERO if appropriate
        }
    }

    // --- Methods called from JavaScript, which then callback to JavaScript ---

    public String layLuongTheoThang(String thangNam) {
        log("(Bridge)layLuongTheoThang called with: " + thangNam);
        List<LuongNhanVien> list = luongNhanVienController.layLuongTheoThang(thangNam);
        StringBuilder sb = new StringBuilder("[");
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                LuongNhanVien l = list.get(i);
                sb.append("{")
                  .append("\"idLuong\":").append(l.getIdLuong()).append(",")
                  .append("\"idChucVu\":").append(l.getIdChucVu()).append(",")
                  .append("\"idNhanVien\":").append(l.getIdNhanVien()).append(",")
                  .append("\"thangNam\":\"").append(escapeJsonString(l.getThangNam())).append("\",")
                  .append("\"luongThuNhap\":").append(l.getLuongThuNhap()).append(",")
                  .append("\"thuong\":").append(l.getThuong()).append(",")
                  .append("\"phuCap\":").append(l.getPhuCap()).append(",")
                  .append("\"tangCa\":").append(l.getTangCa()).append(",")
                  .append("\"hoTen\":\"").append(escapeJsonString(luongNhanVienController.layHoTenTheoIDNhanVien(l.getIdNhanVien()))).append("\",")
                  .append("\"tongLuong\":").append(l.getTongLuong())
                  .append("}");
                if (i < list.size() - 1) sb.append(",");
            }
        }
        sb.append("]");
        String jsonResult = sb.toString();
        // Print JSON to console for debugging:
        System.out.println("[Bridge] JSON result for layLuongTheoThang: " + jsonResult);

        return sb.toString();
    }

    public String timKiemLuongTheoIDNhanVien(int idNhanVien) {
        log("JavaBridge: timKiemLuongTheoIDNhanVien called for idNhanVien: " + idNhanVien);
        try {
            List<LuongNhanVien> danhSach = luongNhanVienController.timKiemLuongTheoIDNhanVien(idNhanVien);
            String jsonData = convertLuongNhanVienListToJson(danhSach);
            return jsonData;
        } catch (Exception e) {
            System.err.println("Error in timKiemLuongTheoIDNhanVien: " + e.getMessage());
            e.printStackTrace();
            return "[]";  // Trả mảng rỗng nếu lỗi
        }
    }
    
    public String timKiemLuongTheoChucVu(int idChucVu) {
        log("JavaBridge: timKiemLuongTheoChucVu called for idChucVu: " + idChucVu);
        try {
            // Gọi BO/Controller để lấy danh sách
            List<LuongNhanVien> danhSach = luongNhanVienController.timKiemLuongTheoChucVu(idChucVu);
            // Chuyển về JSON
            String jsonData = convertLuongNhanVienListToJson(danhSach);
            return jsonData;
        } catch (Exception e) {
            System.err.println("Error in timKiemLuongTheoChucVu: " + e.getMessage());
            e.printStackTrace();
            // Trả về mảng rỗng khi có lỗi
            return "[]";
        }
    }

    public void themLuong(int idChucVu, int idNhanVien, String thangNam,
                          String luongThuNhapStr, String thuongStr, String phuCapStr,
                          String tangCaStr, WebView webView) {
        log("JavaBridge: themLuong called for NV ID: " + idNhanVien + " for ThangNam: " + thangNam);
        try {
            BigDecimal luongThuNhap = parseBigDecimal(luongThuNhapStr, "luongThuNhap");
            BigDecimal thuong = parseBigDecimal(thuongStr, "thuong"); // Can be null
            BigDecimal phuCap = parseBigDecimal(phuCapStr, "phuCap"); // Can be null
            BigDecimal tangCa = parseBigDecimal(tangCaStr, "tangCa"); // Can be null

            if (luongThuNhap == null) {
                 Platform.runLater(() -> 
                 webView.getEngine().executeScript("alert('Lỗi: Lương cơ bản không được để trống hoặc không hợp lệ.');")
                );
                return;
            }

            // idLuong is auto-generated by DB, so pass 0 or rely on constructor default
            LuongNhanVien lnv = new LuongNhanVien(0, idChucVu, idNhanVien, thangNam,
                                                  luongThuNhap, thuong, phuCap, tangCa, null); // Tổng lương sẽ được tính trong DB
         // Tính tổng lương
            BigDecimal tongLuong = luongNhanVienController.tinhTongLuong(lnv);
            lnv.setTongLuong(tongLuong);
            boolean success = luongNhanVienController.themLuong(lnv);
            if (success) {
                // gọi callback JS nếu nó tồn tại, ngược lại dùng alert và reload trực tiếp
                String script =
                    "if (typeof window.onThemCapNhatThanhCong === 'function') {" +
                    "  window.onThemCapNhatThanhCong('Thêm lương thành công!');" +
                    "} else {" +
                    "  alert('Thêm lương thành công!');" +
                    "  closeModal();" +
                    "  loadLuongTheoThang();" +
                    "}";
                Platform.runLater(() ->
                webView.getEngine().executeScript(script)
                );
            } else {
                Platform.runLater(() -> {
                	webView.getEngine()
                            .executeScript("alert('Lỗi: bản ghi lương cho tháng này đã tồn tại!');");
                });
            }

          
        } catch (Exception e) {
            System.err.println("Error in themLuong: " + e.getMessage());
            e.printStackTrace();
             Platform.runLater(() -> 
             webView.getEngine().executeScript("window.onThemCapNhatThatBai('Lỗi hệ thống khi thêm lương: " + escapeJsStringForScript(e.getMessage()) + "');")
            );
        }
    }

    public void capNhatLuong(int idLuong, int idChucVu, int idNhanVien, String thangNam,
            String luongThuNhapStr, String thuongStr, String phuCapStr,
            String tangCaStr, WebView webView) {

            log("JavaBridge: capnhatLuong called for Luong ID: " + idLuong + " for ThangNam: " + thangNam);
			BigDecimal luongCoBan = parseBigDecimal(luongThuNhapStr, "luongThuNhap");
			BigDecimal thuong     = parseBigDecimal(thuongStr, "thuong");
			BigDecimal phuCap     = parseBigDecimal(phuCapStr, "phuCap");
			BigDecimal tangCa     = parseBigDecimal(tangCaStr, "tangCa");

			if (luongCoBan == null) {
			Platform.runLater(() ->
			webView.getEngine().executeScript("alert('Lương cơ bản không được để trống hoặc không hợp lệ.');"));
			return;
			}

			// Tạo object và tính lại tổng lương
			LuongNhanVien luong = new LuongNhanVien(idLuong, idChucVu, idNhanVien, thangNam,
			                               luongCoBan, thuong, phuCap, tangCa, null);
			BigDecimal tongLuong = luongNhanVienController.tinhTongLuong(luong);
			luong.setTongLuong(tongLuong);
			
			boolean success = luongNhanVienController.capNhatLuong(luong);
			if (success) {
			    String script = ""
			      + "if (typeof window.onCapNhatThanhCong === 'function') {"
			      + "  window.onCapNhatThanhCong('Cập nhật lương thành công!');"
			      + "} else {"
			      + "  alert('Cập nhật lương thành công!');"
			      + "  closeModal();"
			      + "  loadLuongTheoThang();"
			      + "}";
			    Platform.runLater(() ->
			    webView.getEngine().executeScript(script)
			    );
			} else {
			    String script = ""
			      + "if (typeof window.onCapNhatThatBai === 'function') {"
			      + "  window.onCapNhatThatBai('Cập nhật lương thất bại.');"
			      + "} else {"
			      + "  alert('Cập nhật lương thất bại.');"
			      + "}";
			    Platform.runLater(() ->
			    webView.getEngine().executeScript(script)
			    );
			}

		}


    public void xoaLuong(int idLuong, int idNhanVien, WebView webView) {
        log("JavaBridge: xoaLuong called for idLuong: " + idLuong + ", idNhanVien: " + idNhanVien);
        try {
            String hoTen = luongNhanVienController.layHoTenTheoIDNhanVien(idNhanVien);
            boolean success = luongNhanVienController.xoaLuong(idLuong);
            String script;
            if (success) {
                script = ""
                  + "alert('Đã xóa bảng lương của nhân viên \""
                  + hoTen.replace("'", "\\'")
                  + "\" - ID Nhân viên \""
                  + idNhanVien
                  + "\"');"
                  + "window.loadLuongTheoThang();";
            } else {
                script = "alert('Xóa lương thất bại. Có thể do ràng buộc dữ liệu.');";
            }
            Platform.runLater(() ->
            webView.getEngine().executeScript(script)
            );

        } catch (Exception e) {
            String safeMsg = escapeJsStringForScript(e.getMessage());
            String script = "alert('Lỗi hệ thống khi xóa lương: " + safeMsg + "');";
            Platform.runLater(() ->
            webView.getEngine().executeScript(script)
            );
        }
    }

    //Các chức năng thống kê
    /**
     * Thống kê lương theo Tháng.
     * @param thangNam Chuỗi "YYYY-MM", ví dụ "2025-06"
     * @return JSON string:
     *   [
     *     { "soNhanVien":12, "luongTrungBinh":8000000, "tongLuong":96000000 }
     *   ]
     */
    public String thongKeTheoThang(String thangNam) {
        try {
            ThongKeResult tk = luongNhanVienController.thongKeTheoThang(thangNam);

            StringBuilder sb = new StringBuilder();
            sb.append("[");
            sb.append("{");
            sb.append("\"soNhanVien\":").append(tk.getSoNhanVien()).append(",");
            sb.append("\"luongTrungBinh\":").append(bigDecimalToString(tk.getLuongTrungBinh())).append(",");
            sb.append("\"tongLuong\":").append(bigDecimalToString(tk.getTongLuong()));
            sb.append("}");
            sb.append("]");

            return sb.toString();
        } catch (Exception e) {
            System.err.println("Error in thongKeTheoThang: " + e.getMessage());
            e.printStackTrace();
            return "[]";
        }
    }

    /**
     * Thống kê lương theo Quý.
     * Quý 1: tháng 01–03
     * Quý 2: tháng 04–06
     * Quý 3: tháng 07–09
     * Quý 4: tháng 10–12
     * @param nam Năm (ví dụ 2025)
     * @param quy Quý (1–4)
     * @return JSON string:
     *   [
     *     { "soNhanVien":30, "luongTrungBinh":9000000, "tongLuong":270000000 }
     *   ]
     */
    public String thongKeTheoQuy(int nam, int quy) {
        try {
            ThongKeResult tk = luongNhanVienController.thongKeTheoQuy(nam, quy);

            StringBuilder sb = new StringBuilder();
            sb.append("[");
            sb.append("{");
            sb.append("\"soNhanVien\":").append(tk.getSoNhanVien()).append(",");
            sb.append("\"luongTrungBinh\":").append(bigDecimalToString(tk.getLuongTrungBinh())).append(",");
            sb.append("\"tongLuong\":").append(bigDecimalToString(tk.getTongLuong()));
            sb.append("}");
            sb.append("]");

            return sb.toString();
        } catch (Exception e) {
            System.err.println("Error in thongKeTheoQuy: " + e.getMessage());
            e.printStackTrace();
            return "[]";
        }
    }

    /**
     * Thống kê lương theo Năm.
     * @param nam Năm (ví dụ 2025)
     * @return JSON string:
     *   [
     *     { "soNhanVien":120, "luongTrungBinh":8500000, "tongLuong":1020000000 }
     *   ]
     */
    public String thongKeTheoNam(int nam) {
        try {
            ThongKeResult tk = luongNhanVienController.thongKeTheoNam(nam);

            StringBuilder sb = new StringBuilder();
            sb.append("[");
            sb.append("{");
            sb.append("\"soNhanVien\":").append(tk.getSoNhanVien()).append(",");
            sb.append("\"luongTrungBinh\":").append(bigDecimalToString(tk.getLuongTrungBinh())).append(",");
            sb.append("\"tongLuong\":").append(bigDecimalToString(tk.getTongLuong()));
            sb.append("}");
            sb.append("]");

            return sb.toString();
        } catch (Exception e) {
            System.err.println("Error in thongKeTheoNam: " + e.getMessage());
            e.printStackTrace();
            return "[]";
        }
    }

    // Helper method to escape strings for JavaScript execution
    private String escapeJsStringForScript(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                .replace("\b", "\\b")
                .replace("\f", "\\f");
    }


    // Helper to convert List<LuongNhanVien> to JSON String, including hoTen
    private String convertLuongNhanVienListToJson(List<LuongNhanVien> danhSach) {
        if (danhSach == null) {
            return "[]";
        }
        StringBuilder jsonBuilder = new StringBuilder("[");
        for (int i = 0; i < danhSach.size(); i++) {
            LuongNhanVien lnv = danhSach.get(i);
            String hoTen = "";
            try {
                hoTen = luongNhanVienController.layHoTenTheoIDNhanVien(lnv.getIdNhanVien());
            } catch (Exception e) {
                System.err.println("Error fetching hoTen for idNhanVien " + lnv.getIdNhanVien() + ": " + e.getMessage());
                // hoTen remains empty or you can set a default like "Không tìm thấy tên"
            }

            jsonBuilder.append("{")
                .append("\"idLuong\":").append(lnv.getIdLuong()).append(",")
                .append("\"idChucVu\":").append(lnv.getIdChucVu()).append(",")
                .append("\"idNhanVien\":").append(lnv.getIdNhanVien()).append(",")
                .append("\"hoTen\":\"").append(escapeJsonString(hoTen)).append("\",")
                .append("\"thangNam\":\"").append(escapeJsonString(lnv.getThangNam())).append("\",")
                .append("\"luongThuNhap\":").append(bigDecimalToString(lnv.getLuongThuNhap())).append(",")
                .append("\"thuong\":").append(bigDecimalToString(lnv.getThuong())).append(",")
                .append("\"phuCap\":").append(bigDecimalToString(lnv.getPhuCap())).append(",")
                .append("\"tangCa\":").append(bigDecimalToString(lnv.getTangCa())).append(",")
                .append("\"tongLuong\":").append(bigDecimalToString(lnv.getTongLuong()))
                .append("}");
            if (i < danhSach.size() - 1) {
                jsonBuilder.append(",");
            }
        }
        jsonBuilder.append("]");
        log("JavaBridge: Generated JSON: " + jsonBuilder.toString().substring(0, Math.min(jsonBuilder.toString().length(), 200)) + "..."); // Log snippet
        return jsonBuilder.toString();
    }
    public void log(String message) {
        System.out.println("Luong JS Console: " + message);
    }
	
	public void hello() {
		System.out.println("hello");
	}
}
