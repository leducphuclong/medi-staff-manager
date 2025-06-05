package com.MediStaffManager.view.quanLyPhongBan;

import com.MediStaffManager.controller.NhanVienController;
import com.MediStaffManager.bean.NhanVien;
import javafx.application.Platform;

import java.io.File; // For constructing file paths
import java.net.URL;
import javafx.application.Platform;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;

public class QuanLyPhongBanBridge {
	private NhanVienController nhanVienController;
	private QuanLyPhongBanView mainView;
	final String htmlFileBasePath = "src/com/MediStaffManager/view/quanLyPhongBan/";

	public QuanLyPhongBanBridge(NhanVienController controller, QuanLyPhongBanView view) {
		this.nhanVienController = controller;
		this.mainView = view;
		

	}

	public void loadPage(String pageFileName) {
		String filePath = pageFileName;
		File htmlFile = new File(filePath);
		System.out.println("Bridge: Attempting to load file: " + htmlFile.getAbsolutePath());

		if (htmlFile.exists() && htmlFile.isFile()) {
			try {
				URL fileUrl = htmlFile.toURI().toURL();
				String urlToLoad = fileUrl.toExternalForm();
				Platform.runLater(() -> mainView.getWebEngine().load(urlToLoad));
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
		List<NhanVien> danhSach = nhanVienController.layNhanVienTheoIdPhongBan(idPhongBan);
		StringBuilder jsonBuilder = new StringBuilder("[");

		if (danhSach != null && !danhSach.isEmpty()) {
			for (int i = 0; i < danhSach.size(); i++) {
				NhanVien nv = danhSach.get(i);
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
	
	public void navigateToDepartmentDetail(int idPhongBan, String tenPhongBan) {
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
					mainView.getWebEngine().load(finalUrl);
				} else {
					System.err.println("JavaBridge: CRITICAL - Base file '" + basePageFileName + "' not found at: "
							+ baseHtmlFile.getAbsolutePath());
					loadPage(baseFilePath);
				}
			} catch (Exception e) {
				System.err
						.println("Error in navigateToDepartmentDetail constructing URL or loading: " + e.getMessage());
				e.printStackTrace();
				loadPage(htmlFileBasePath + basePageFileName);
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

}