package com.MediStaffManager.view.quanLyPhongBan;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import com.google.gson.Gson;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.MediStaffManager.bean.Employee;
import com.MediStaffManager.bean.NhanVienBEAN;
import com.MediStaffManager.controller.NhanVienController;

public class QuanLyPhongBanBridge {
    private WebEngine webEngine;
    private Gson gson;
    private NhanVienController nhanVienController;
    final String htmlFileBasePath = "src/com/MediStaffManager/view/quanLyPhongBan/";

    public QuanLyPhongBanBridge(WebEngine webEngine) {
        this.webEngine = webEngine;
        this.nhanVienController = new NhanVienController();
        this.gson = new Gson();
    }
    
    public void taiTrang(Stage primaryStage, WebView webView) {
    	final String basePath = "./src/com/MediStaffManager/view/quanLyPhongBan/html/";
        final String fileName = "trang_quan_ly_phong_ban.html";
        String filePath = basePath + fileName;
        File htmlFile = new File(filePath);

        if (htmlFile.exists() && htmlFile.isFile()) {
            String url = htmlFile.toURI().toString();
            webView.getEngine().load(url);
        } else {
        	webView.getEngine().loadContent("<html><body><h1>Lỗi khởi tạo</h1><p>Không thể tìm thấy Quản Lý Phòng Ban</p></body></html>");
        }
        
        StackPane root = new StackPane();
        root.getChildren().add(webView);

        Scene scene = new Scene(root, 1400, 800);
        primaryStage.setTitle("Medi Staff Manager - Quản Lý Phòng Ban");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public String getAllEmployees() {
    	System.out.println("da goi!");
        List<Employee> employees = nhanVienController.getAllEmployees();
        return gson.toJson(employees);
    }
    
    public String addEmployee(String employeeJson) {
        return nhanVienController.addEmployee(employeeJson);
    }

    public String updateEmployee(String employeeJson) {
        return nhanVienController.updateEmployee(employeeJson);
    }

    public String deleteEmployee(int idNhanVien) {
        return nhanVienController.deleteEmployee(idNhanVien);
    }

    public String searchEmployees(String keyword, String criteria) {
        List<Employee> employees = nhanVienController.searchEmployees(keyword, criteria);
        return gson.toJson(employees);
    }

    public String getAllTenChucVu() {
        List<String> chucVuList = nhanVienController.getAllTenChucVu();
        return gson.toJson(chucVuList);
    }

    public String getAllTenPhongBan() {
        List<String> phongBanList = nhanVienController.getAllTenPhongBan();
        return gson.toJson(phongBanList);
    }

    public String getHeSoLuongByTenChucVu(String tenChucVu) {
        return nhanVienController.getHeSoLuongByTenChucVu(tenChucVu);
    }


	public void log(String message) {
		System.out.println("Console Log: " + message);
	}

	private String escapeJsonString(String str) {
		if (str == null)
			return "";
		return str.replace("\\", "\\\\").replace("\"", "\\\"").replace("\b", "\\b").replace("\f", "\\f")
				.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
	}

	private String escapeJson(String input) {
		if (input == null)
			return "";
		return input.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "").replace("\r", "");
	}

	public String layNhanVienTheoIdPhongBan(int idPhongBan) {
		System.out.println("Dang lay nahnvien theo id phong bannnnnnnnnnnnnnnnnnnnnnn");
		List<NhanVienBEAN> danhSach = nhanVienController.layNhanVienTheoIdPhongBan(idPhongBan);
		StringBuilder jsonBuilder = new StringBuilder("[");

		if (danhSach != null && !danhSach.isEmpty()) {
			for (int i = 0; i < danhSach.size(); i++) {
				NhanVienBEAN nv = danhSach.get(i);
				if (nv != null) {
					jsonBuilder.append("{").append("\"idNhanVien\":").append(nv.getIdNhanVien()).append(",")
							.append("\"cccd\":\"").append(escapeJson(nv.getCccd())).append("\",").append("\"hoTen\":\"")
							.append(escapeJson(nv.getHoTen())).append("\",").append("\"sdt\":\"")
							.append(escapeJson(nv.getSdt())).append("\",").append("\"email\":\"")
							.append(escapeJson(nv.getEmail())).append("\",").append("\"gioiTinh\":\"")
							.append(escapeJson(nv.getGioiTinh())).append("\",").append("\"ngaySinh\":\"")
							.append(escapeJson(nv.getNgaySinh())).append("\",").append("\"idChucVu\":")
							.append(nv.getIdChucVu()).append(",").append("\"idPhongBan\":").append(nv.getIdPhongBan())
							.append(",").append("\"tenChucVu\":\"").append(escapeJson(nv.getTenChucVu())).append("\",")
							.append("\"tenPhongBan\":\"").append(escapeJson(nv.getTenPhongBan())).append("\"")
							.append("}");
					if (i < danhSach.size() - 1) {
						jsonBuilder.append(",");
					}
				}
			}
		}

		jsonBuilder.append("]");
		return jsonBuilder.toString();
	}

	public boolean suaPhongBan(int idPhongBanCu, int idPhongBanMoi, String tenPhongBanMoi) {
		return nhanVienController.suaPhongBan(idPhongBanCu, idPhongBanMoi, tenPhongBanMoi);
	}

	public boolean themPhongBan(int idPhongBan, String tenPhongBan) {
		return nhanVienController.themPhongBan(idPhongBan, tenPhongBan);
	}
	
	public void loadPage(String pageFileName, WebView webView) {
		String filePath = pageFileName;
		File htmlFile = new File(filePath);
		System.out.println("Bridge: Attempting to load file: " + htmlFile.getAbsolutePath());

		if (htmlFile.exists() && htmlFile.isFile()) {
			try {
				URL fileUrl = htmlFile.toURI().toURL();
				String urlToLoad = fileUrl.toExternalForm();
				Platform.runLater(() -> webView.getEngine().load(urlToLoad));
				System.out.println("Bridge: Loading page from file system: " + urlToLoad);
			} catch (Exception e) {
				System.err.println("Bridge: Error loading page: " + e.getMessage());
				e.printStackTrace();
			}
		} else {
			System.err.println("Bridge: FATAL - HTML không tìm thấy tại đường dẫn : " + htmlFile.getAbsolutePath());
			System.err.println("Bridge: đường dẫn hiện tại : " + new File(".").getAbsolutePath());
		}
	}
	
	public void navigateToDepartmentDetail(int idPhongBan, String tenPhongBan, WebView webView) {
		System.out.println("JavaBridge: Navigating to department detail for ID: " + idPhongBan);
		Platform.runLater(() -> {
			String basePageFileName = "html/trang_chi_tiet_phong_dieu_hanh.html";
			try {
				String baseFilePath = htmlFileBasePath + basePageFileName;
				File baseHtmlFile = new File(baseFilePath);

				if (baseHtmlFile.exists() && baseHtmlFile.isFile()) {
					URL baseFileUrl = baseHtmlFile.toURI().toURL();
					String baseUrlString = baseFileUrl.toExternalForm();
					// Truyền idPhongBan qua query string
					String finalUrl = baseUrlString + "?idPhongBan=" + idPhongBan + "&tenPhongBan=" + tenPhongBan;

					System.out.println("JavaBridge: Loading detail page with full URL: " + finalUrl);
					webView.getEngine().load(finalUrl);
				} else {
					System.err.println("JavaBridge: CRITICAL - Base file '" + basePageFileName + "' not found at: "
							+ baseHtmlFile.getAbsolutePath());
					loadPage(baseFilePath, webView);
				}
			} catch (Exception e) {
				System.err
						.println("Error in navigateToDepartmentDetail constructing URL or loading: " + e.getMessage());
				e.printStackTrace();
				loadPage(htmlFileBasePath + basePageFileName, webView);
			}
		});
	}

	public String layDanhSachPhongBan() {
		List<Object[]> danhSach = nhanVienController.layDanhSachPhongBan(); // Assuming this returns List<[idPhongBan, tenPhongBan]>
		StringBuilder jsonBuilder = new StringBuilder("[");
		if (danhSach != null && !danhSach.isEmpty()) {
			for (int i = 0; i < danhSach.size(); i++) {
				Object[] phongBanData = danhSach.get(i);
				if (phongBanData != null && phongBanData.length >= 2 && phongBanData[0] instanceof Integer && phongBanData[1] instanceof String) {
					jsonBuilder.append("{");
					jsonBuilder.append("\"idPhongBan\":").append(phongBanData[0]);
					jsonBuilder.append(",");
					jsonBuilder.append("\"tenPhongBan\":\"").append(escapeJsonString((String) phongBanData[1])).append("\"");
					jsonBuilder.append("}");
					if (i < danhSach.size() - 1) {
						jsonBuilder.append(",");
					}
				} else {
					log("Bridge: Invalid data format for phongBan: " + (phongBanData != null ? java.util.Arrays.toString(phongBanData) : "null"));
				}
			}
		}
		jsonBuilder.append("]");
		log("Bridge: layDanhSachPhongBan JSON: " + jsonBuilder.toString());
		return jsonBuilder.toString();
	}

	public boolean xoaNhieuNhanVien(String csvIds) {
		log("Bridge: xoaNhieuNhanVien called with CSV IDs: " + csvIds);
		List<Integer> ids = parseCsvToIntList(csvIds);
		if (ids.isEmpty()) {
			log("Bridge: No valid employee IDs after parsing for deletion.");
			return false;
		}
		return nhanVienController.xoaNhieuNhanVien(ids);
	}
	
	public boolean chuyenPhongBan(String csvIds, int idPhongBanMoi) {
		log("Bridge: chuyenPhongBan called with CSV IDs: " + csvIds + ", New Dept ID: " + idPhongBanMoi);
		List<Integer> ids = parseCsvToIntList(csvIds);
		if (ids.isEmpty()) {
			log("Bridge: No valid employee IDs after parsing for transfer.");
			return false;
		}
		return nhanVienController.chuyenPhongBan(ids, idPhongBanMoi);
	}

	public boolean xoaPhongBan(String idPhongBan) { // Tham số này thực chất là ID dưới dạng chuỗi từ JS
        log("Bridge: xoaPhongBan called with ID: " + idPhongBan);
        return nhanVienController.xoaPhongBan(idPhongBan); // Gọi controller, controller sẽ parse String sang int
    }
	
	// Hàm tiện ích chuyển "1,2,3" thành List<Integer>
	private List<Integer> parseCsvToIntList(String csv) {
		List<Integer> list = new ArrayList<>();
		if (csv == null || csv.trim().isEmpty()) {
			return list;
		}
		String[] items = csv.split(",");
		for (String item : items) {
			try {
				list.add(Integer.parseInt(item.trim()));
			} catch (NumberFormatException e) {
				log("Bridge: Invalid number format in CSV: " + item);
				// Optionally, decide if an error in one item should stop the whole process or skip the item
			}
		}
		return list;
	}

	// Phương thức tìm kiếm phòng ban theo tên
    public String timKiemPhongBanTheoTen(String tenPhongBan) {
        log("Bridge: Tìm kiếm phòng ban theo tên: " + tenPhongBan);
        List<Object[]> danhSach = nhanVienController.timKiemPhongBanTheoTen(tenPhongBan);
        return chuyenDanhSachPhongBanThanhJson(danhSach);
    }
    
    // Phương thức tìm kiếm phòng ban theo ID
    public String timKiemPhongBanTheoId(String idPhongBanStr) {
        log("Bridge: Tìm kiếm phòng ban theo ID: " + idPhongBanStr);
        try {
            int idPhongBan = Integer.parseInt(idPhongBanStr);
            List<Object[]> danhSach = nhanVienController.timKiemPhongBanTheoId(idPhongBan);
            return chuyenDanhSachPhongBanThanhJson(danhSach);
        } catch (NumberFormatException e) {
            log("Bridge: ID phòng ban không hợp lệ: " + idPhongBanStr);
            return "[]"; // Trả về mảng rỗng nếu ID không hợp lệ
        }
    }
    
    // Phương thức hỗ trợ chuyển danh sách phòng ban thành chuỗi JSON
    private String chuyenDanhSachPhongBanThanhJson(List<Object[]> danhSach) {
        StringBuilder jsonBuilder = new StringBuilder("[");
        if (danhSach != null && !danhSach.isEmpty()) {
            for (int i = 0; i < danhSach.size(); i++) {
                Object[] phongBanData = danhSach.get(i);
                if (phongBanData != null && phongBanData.length >= 2 && phongBanData[0] instanceof Integer && phongBanData[1] instanceof String) {
                    jsonBuilder.append("{");
                    jsonBuilder.append("\"idPhongBan\":").append(phongBanData[0]);
                    jsonBuilder.append(",");
                    jsonBuilder.append("\"tenPhongBan\":\"").append(escapeJsonString((String) phongBanData[1])).append("\"");
                    jsonBuilder.append("}");
                    if (i < danhSach.size() - 1) {
                        jsonBuilder.append(",");
                    }
                } else {
                    log("Bridge: Invalid data format for phongBan: " + (phongBanData != null ? java.util.Arrays.toString(phongBanData) : "null"));
                }
            }
        }
        jsonBuilder.append("]");
        return jsonBuilder.toString();
    }
    
    public void hello() {
        System.out.println("hello");
    }
}
