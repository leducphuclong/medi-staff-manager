package com.MediStaffManager.controller;
 
import com.MediStaffManager.bean.TaiKhoan;
import com.MediStaffManager.bo.TaiKhoanBO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
 
import java.util.Collections;
import java.util.List;
 
public class TaiKhoanController {
 
    private TaiKhoanBO taiKhoanBO;
    private Gson gson;
 
    public TaiKhoanController() {
        this.taiKhoanBO = new TaiKhoanBO();
        this.gson = new Gson();
    }
 
    public List<TaiKhoan> getAllTaiKhoan() {
        try {
            return taiKhoanBO.getAllTaiKhoan();
        } catch (RuntimeException e) {
            System.err.println("Controller: Error getting all accounts: " + e.getMessage());
            return Collections.emptyList();
        }
    }
    
    public String getTaiKhoanByTenDangNhap(String tenDangNhap) {
        try {
            TaiKhoan tk = taiKhoanBO.getTaiKhoanByTenDangNhap(tenDangNhap);
            return gson.toJson(tk); // tk can be null if not found, Gson handles this
        } catch (RuntimeException e) {
            System.err.println("Controller: Error getting account by username: " + e.getMessage());
            return gson.toJson(null);
        }
    }
 
    public String addTaiKhoan(String taiKhoanJson) {
        try {
            if (taiKhoanJson == null || taiKhoanJson.trim().isEmpty()) {
                return "Error: Dữ liệu tài khoản không được để trống.";
            }
            // Parse JSON to get matKhauNhapLai and passwordChanged flag if present
            JsonObject jsonObject = JsonParser.parseString(taiKhoanJson).getAsJsonObject();
            TaiKhoan newTaiKhoan = gson.fromJson(jsonObject, TaiKhoan.class);
            String matKhauNhapLai = jsonObject.has("matKhauNhapLai") ? jsonObject.get("matKhauNhapLai").getAsString() : null;
 
            taiKhoanBO.addTaiKhoan(newTaiKhoan, matKhauNhapLai);
            return "Success: Thêm tài khoản thành công!";
        } catch (JsonSyntaxException e) {
            System.err.println("Controller: Invalid JSON format for addTaiKhoan: " + e.getMessage());
            return "Error: Dữ liệu gửi lên không đúng định dạng.";
        } catch (IllegalArgumentException e) {
            return "Error: " + e.getMessage();
        } catch (RuntimeException e) {
            System.err.println("Controller: Runtime error in addTaiKhoan: " + e.getMessage());
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            System.err.println("Controller: Unexpected error in addTaiKhoan: " + e.getMessage());
            e.printStackTrace();
            return "Error: Lỗi không xác định khi thêm tài khoản.";
        }
    }
 
    public String updateTaiKhoan(String taiKhoanJson) {
        try {
            if (taiKhoanJson == null || taiKhoanJson.trim().isEmpty()) {
                return "Error: Dữ liệu tài khoản không được để trống.";
            }
            JsonObject jsonObject = JsonParser.parseString(taiKhoanJson).getAsJsonObject();
            TaiKhoan taiKhoanToUpdate = gson.fromJson(jsonObject, TaiKhoan.class);
            String matKhauNhapLai = jsonObject.has("matKhauNhapLai") ? jsonObject.get("matKhauNhapLai").getAsString() : null;
            boolean passwordChanged = jsonObject.has("passwordChanged") && jsonObject.get("passwordChanged").getAsBoolean();
 
 
            if (taiKhoanToUpdate.getTenDangNhap() == null || taiKhoanToUpdate.getTenDangNhap().trim().isEmpty()) {
                 return "Error: Tên đăng nhập không hợp lệ để cập nhật.";
            }
 
            taiKhoanBO.updateTaiKhoan(taiKhoanToUpdate, matKhauNhapLai, passwordChanged);
            return "Success: Cập nhật tài khoản thành công!";
        } catch (JsonSyntaxException e) {
            System.err.println("Controller: Invalid JSON format for updateTaiKhoan: " + e.getMessage());
            return "Error: Dữ liệu gửi lên không đúng định dạng.";
        } catch (IllegalArgumentException e) {
            return "Error: " + e.getMessage();
        } catch (RuntimeException e) {
            System.err.println("Controller: Runtime error in updateTaiKhoan: " + e.getMessage());
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            System.err.println("Controller: Unexpected error in updateTaiKhoan: " + e.getMessage());
            e.printStackTrace();
            return "Error: Lỗi không xác định khi cập nhật tài khoản.";
        }
    }
 
    public String deleteTaiKhoan(String tenDangNhap) {
        System.out.println("[Controller] Nhận được yêu cầu xóa tài khoản: " + tenDangNhap);
        try {
            if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
                System.err.println("[Controller] Lỗi: Tên đăng nhập trống.");
                return "Error: Tên đăng nhập không hợp lệ để xóa.";
            }
            taiKhoanBO.deleteTaiKhoan(tenDangNhap);
            System.out.println("[Controller] Gọi BO xóa thành công. Trả về success.");
            return "Success: Xóa tài khoản thành công!";
        } catch (IllegalArgumentException e) {
            System.err.println("[Controller] Lỗi IllegalArgumentException: " + e.getMessage());
            return "Error: " + e.getMessage();
        } catch (RuntimeException e) {
            System.err.println("[Controller] Lỗi RuntimeException: " + e.getMessage());
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            System.err.println("[Controller] Lỗi không xác định: " + e.getMessage());
            e.printStackTrace();
            return "Error: Lỗi không xác định khi xóa tài khoản.";
        }
    }
    
    public List<String> getAllVaiTro() {
        try {
            return taiKhoanBO.getValidRoles();
        } catch (RuntimeException e) {
            System.err.println("Controller: Error getting VaiTro list: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}