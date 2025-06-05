package com.MediStaffManager.view.quanLyPhongBan; // Assuming this view is in a 'nhanVien' subpackage

import com.MediStaffManager.controller.NhanVienController;
import com.MediStaffManager.bean.NhanVien; // Ensure NhanVien can be serialized if needed, or convert to JSON

import java.util.List;
import java.util.ArrayList; // For parsing JSON array from JS if needed

// For JSON conversion (recommended for complex objects)
// You'll need to add a JSON library like Gson or Jackson to your project
// For example, using Gson:
// import com.google.gson.Gson;
// import com.google.gson.reflect.TypeToken;
// import java.lang.reflect.Type;

public class QuanLyPhongBanBridge2 {
    private NhanVienController nhanVienController;
    // private Gson gson = new Gson(); // Uncomment if using Gson

    public QuanLyPhongBanBridge2(NhanVienController controller) {
        this.nhanVienController = controller;
    }

    // --- NhanVienBO related methods ---

    /**
     * Retrieves all employees.
     * It's best to return complex data like lists of objects as JSON strings.
     * JavaScript can then parse this JSON.
     * @return A JSON string representing a list of NhanVien objects, or List<NhanVien> directly (less robust).
     */
    public String layToanBoNhanVien() {
        List<NhanVien> danhSach = nhanVienController.layToanBoNhanVien();
        // For direct return (simpler, but JS handling might be complex):
        // return danhSach; 
        // For JSON return (recommended - uncomment Gson lines and add Gson library):
        // return gson.toJson(danhSach);
        System.out.println("Bridge: layToanBoNhanVien called, found " + (danhSach != null ? danhSach.size() : 0) + " employees.");
        // If not using JSON library, you might need a simpler representation or ensure NhanVien is simple enough.
        // For now, returning the list directly, but be aware of potential JS interop issues.
        // A better approach without a full JSON lib for simple cases might be to convert to List<Map<String, Object>>
        // or ensure NhanVien has simple public getters.
        return convertNhanVienListToJson(danhSach); // Helper for manual JSON or use a library
    }

    public boolean xoaNhanVienTrongPhongBan(int idNhanVien, int idPhongBan) {
        System.out.println("Bridge: xoaNhanVienTrongPhongBan called with idNhanVien=" + idNhanVien + ", idPhongBan=" + idPhongBan);
        return nhanVienController.xoaNhanVienTrongPhongBan(idNhanVien, idPhongBan);
    }

    public boolean xoaNhanVien(int idNhanVien) {
        System.out.println("Bridge: xoaNhanVien called with idNhanVien=" + idNhanVien);
        return nhanVienController.xoaNhanVien(idNhanVien);
    }
    
    public boolean xoaTatCaNhanVienTrongPhongBan(String tenPhongBan) {
        System.out.println("Bridge: xoaTatCaNhanVienTrongPhongBan called for phongBan=" + tenPhongBan);
        return nhanVienController.xoaTatCaNhanVienTrongPhongBan(tenPhongBan);
    }

    /**
     * Moves employees to a new department.
     * @param jsonDanhSachIdNhanVien A JSON string array of employee IDs, e.g., "[1, 2, 3]"
     * @param idPhongBanMoi The ID of the new department.
     * @return true if successful, false otherwise.
     */
    public boolean chuyenPhongBan(String jsonDanhSachIdNhanVien, int idPhongBanMoi) {
        System.out.println("Bridge: chuyenPhongBan called with IDs=" + jsonDanhSachIdNhanVien + ", newPhongBanId=" + idPhongBanMoi);
        List<Integer> danhSachIdNhanVien = new ArrayList<>();
        try {
            // Basic JSON array parsing (for numbers) - for robust parsing, use a JSON library
            String[] idsStr = jsonDanhSachIdNhanVien.replace("[", "").replace("]", "").split(",");
            for (String idStr : idsStr) {
                if (!idStr.trim().isEmpty()) {
                    danhSachIdNhanVien.add(Integer.parseInt(idStr.trim()));
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing danhSachIdNhanVien JSON: " + e.getMessage());
            return false;
        }
        // Uncomment and use if you add Gson:
        // Type listType = new TypeToken<ArrayList<Integer>>(){}.getType();
        // List<Integer> danhSachIdNhanVien = gson.fromJson(jsonDanhSachIdNhanVien, listType);
        return nhanVienController.chuyenPhongBan(danhSachIdNhanVien, idPhongBanMoi);
    }

    // --- phongBanBO related methods ---

    /**
     * Retrieves all departments.
     * Returns a JSON string representation of the list of Object arrays.
     * Each Object[] likely contains [idPhongBan, tenPhongBan].
     */
    public String layDanhSachPhongBan() {
        List<Object[]> danhSach = nhanVienController.layDanhSachPhongBan();
        System.out.println("Bridge: layDanhSachPhongBan called, found " + (danhSach != null ? danhSach.size() : 0) + " departments.");
        // Convert List<Object[]> to a JSON string.
        // Example: [{"id": 1, "ten": "IT"}, {"id": 2, "ten": "HR"}]
        // For now, using a simple manual conversion. A JSON library is much better.
        StringBuilder jsonBuilder = new StringBuilder("[");
        if (danhSach != null) {
            for (int i = 0; i < danhSach.size(); i++) {
                Object[] pb = danhSach.get(i);
                if (pb != null && pb.length >= 2) { // Assuming id is pb[0], ten is pb[1]
                    jsonBuilder.append(String.format("{\"idPhongBan\": %s, \"tenPhongBan\": \"%s\"}", pb[0], pb[1]));
                    if (i < danhSach.size() - 1) {
                        jsonBuilder.append(",");
                    }
                }
            }
        }
        jsonBuilder.append("]");
        return jsonBuilder.toString();
        // With Gson:
        // return gson.toJson(danhSach); // Gson handles List<Object[]> well.
    }
    
    public String layNhanVienTheoPhongBan(String tenPhongBan) {
        System.out.println("Bridge: layNhanVienTheoPhongBan called for phongBan=" + tenPhongBan);
        List<NhanVien> danhSach = nhanVienController.layNhanVienTheoPhongBan(tenPhongBan);
        return convertNhanVienListToJson(danhSach);
        // With Gson:
        // return gson.toJson(danhSach);
    }

    public boolean xoaPhongBan(String tenPhongBan) {
        System.out.println("Bridge: xoaPhongBan called for phongBan=" + tenPhongBan);
        return nhanVienController.xoaPhongBan(tenPhongBan);
    }

    public boolean themPhongBan(int idPhongBan, String tenPhongBan) {
        System.out.println("Bridge: themPhongBan called with id=" + idPhongBan + ", ten=" + tenPhongBan);
        return nhanVienController.themPhongBan(idPhongBan, tenPhongBan);
    }

    public boolean suaPhongBan(int idPhongBanCu, int idPhongBanMoi, String tenPhongBanMoi) {
        System.out.println("Bridge: suaPhongBan called with oldId=" + idPhongBanCu + ", newId=" + idPhongBanMoi + ", newTen=" + tenPhongBanMoi);
        return nhanVienController.suaPhongBan(idPhongBanCu, idPhongBanMoi, tenPhongBanMoi);
    }

    public int layIdPhongBanTheoTen(String tenPhongBan) {
        System.out.println("Bridge: layIdPhongBanTheoTen called for ten=" + tenPhongBan);
        return nhanVienController.layIdPhongBanTheoTen(tenPhongBan);
    }

    // Helper method to convert List<NhanVien> to JSON manually (basic example)
    // For production, use a proper JSON library like Gson or Jackson.
    private String convertNhanVienListToJson(List<NhanVien> nhanViens) {
        if (nhanViens == null || nhanViens.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < nhanViens.size(); i++) {
            NhanVien nv = nhanViens.get(i);
            // Assuming NhanVien has getIdNhanVien(), getHoTen(), getChucVu(), getIdPhongBan()
            // Adjust fields as per your NhanVien bean
            sb.append("{");
            sb.append("\"idNhanVien\":").append(nv.getIdNhanVien()).append(",");
            sb.append("\"hoTen\":\"").append(escapeJsonString(nv.getHoTen())).append("\",");
            sb.append("\"chucVu\":\"").append(escapeJsonString("Nhan vien")).append("\",");
            sb.append("\"idPhongBan\":").append(nv.getIdPhongBan());
            // Add other fields as needed
            sb.append("}");
            if (i < nhanViens.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
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

    // A method to log messages from JavaScript to the Java console
    public void log(String message) {
        System.out.println("JS Log: " + message);
    }
}