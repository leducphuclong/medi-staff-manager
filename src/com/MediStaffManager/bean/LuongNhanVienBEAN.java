package com.MediStaffManager.bean;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) for the Salary Management UI.
 * This class combines information from NhanVien, ChucVu, and LuongNhanVien
 * into a single object to be sent to the JavaScript frontend.
 */
public class LuongNhanVienBEAN {
    // === From NhanVien & ChucVu ===
    private int idNhanVien;
    private String hoTen;
    private int idChucVu;
    private String tenChucVu;
    private BigDecimal heSoLuong; // Needed to calculate base salary

    // === From LuongNhanVien (can be null if record doesn't exist) ===
    private Integer idLuong; // Use Integer to allow for null
    private String thangNam;
    private BigDecimal luongCoBanChuan; // The global standard base salary
    private BigDecimal thuong;
    private BigDecimal phuCap;
    private BigDecimal tangCa;
    private BigDecimal tongLuong;

    // === UI-specific calculated field ===
    private String trangThai; // e.g., "Đã có" or "Chưa có"

    // --- Constructors, Getters, and Setters ---
    public LuongNhanVienBEAN() {}

    // Getters and setters for all fields...
    public int getIdNhanVien() { return idNhanVien; }
    public void setIdNhanVien(int idNhanVien) { this.idNhanVien = idNhanVien; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public int getIdChucVu() { return idChucVu; }
    public void setIdChucVu(int idChucVu) { this.idChucVu = idChucVu; }
    public String getTenChucVu() { return tenChucVu; }
    public void setTenChucVu(String tenChucVu) { this.tenChucVu = tenChucVu; }
    public BigDecimal getHeSoLuong() { return heSoLuong; }
    public void setHeSoLuong(BigDecimal heSoLuong) { this.heSoLuong = heSoLuong; }
    public Integer getIdLuong() { return idLuong; }
    public void setIdLuong(Integer idLuong) { this.idLuong = idLuong; }
    public String getThangNam() { return thangNam; }
    public void setThangNam(String thangNam) { this.thangNam = thangNam; }
    public BigDecimal getLuongCoBanChuan() { return luongCoBanChuan; }
    public void setLuongCoBanChuan(BigDecimal luongCoBanChuan) { this.luongCoBanChuan = luongCoBanChuan; }
    public BigDecimal getThuong() { return thuong; }
    public void setThuong(BigDecimal thuong) { this.thuong = thuong; }
    public BigDecimal getPhuCap() { return phuCap; }
    public void setPhuCap(BigDecimal phuCap) { this.phuCap = phuCap; }
    public BigDecimal getTangCa() { return tangCa; }
    public void setTangCa(BigDecimal tangCa) { this.tangCa = tangCa; }
    public BigDecimal getTongLuong() { return tongLuong; }
    public void setTongLuong(BigDecimal tongLuong) { this.tongLuong = tongLuong; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}