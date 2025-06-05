package com.MediStaffManager.bean;

import java.math.BigDecimal; // Sử dụng BigDecimal cho heSoLuong

public class Employee {
    private int idNhanVien;
    private String sdt;
    private String cccd;
    private String hoTen;
    private String email;
    private String gioiTinh;
    private String ngaySinh;
    private int idChucVu;
    private int idPhongBan;
    private String tenChucVu;
    private String tenPhongBan;
    private BigDecimal heSoLuong; // Thêm trường hệ số lương

    public Employee() {}

    public Employee(int idNhanVien, String cccd, String hoTen, String sdt, String email,
                    String gioiTinh, String ngaySinh, int idChucVu, int idPhongBan,
                    String tenChucVu, String tenPhongBan, BigDecimal heSoLuong) {
        this.idNhanVien = idNhanVien;
        this.cccd = cccd;
        this.hoTen = hoTen;
        this.sdt = sdt;
        this.email = email;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.idChucVu = idChucVu;
        this.idPhongBan = idPhongBan;
        this.tenChucVu = tenChucVu;
        this.tenPhongBan = tenPhongBan;
        this.heSoLuong = heSoLuong;
    }

    // Getters and Setters
    public int getIdNhanVien() { return idNhanVien; }
    public void setIdNhanVien(int idNhanVien) { this.idNhanVien = idNhanVien; }
    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }
    public String getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(String ngaySinh) { this.ngaySinh = ngaySinh; }
    public int getIdChucVu() { return idChucVu; }
    public void setIdChucVu(int idChucVu) { this.idChucVu = idChucVu; }
    public int getIdPhongBan() { return idPhongBan; }
    public void setIdPhongBan(int idPhongBan) { this.idPhongBan = idPhongBan; }
    public String getTenChucVu() { return tenChucVu; }
    public void setTenChucVu(String tenChucVu) { this.tenChucVu = tenChucVu; }
    public String getTenPhongBan() { return tenPhongBan; }
    public void setTenPhongBan(String tenPhongBan) { this.tenPhongBan = tenPhongBan; }
    public BigDecimal getHeSoLuong() { return heSoLuong; }
    public void setHeSoLuong(BigDecimal heSoLuong) { this.heSoLuong = heSoLuong; }

    @Override
    public String toString() {
        return "Employee{" +
                "idNhanVien=" + idNhanVien +
                ", sdt='" + sdt + '\'' +
                ", cccd='" + cccd + '\'' +
                ", hoTen='" + hoTen + '\'' +
                ", email='" + email + '\'' +
                ", gioiTinh='" + gioiTinh + '\'' +
                ", ngaySinh='" + ngaySinh + '\'' +
                ", idChucVu=" + idChucVu +
                ", idPhongBan=" + idPhongBan +
                ", tenChucVu='" + tenChucVu + '\'' +
                ", tenPhongBan='" + tenPhongBan + '\'' +
                ", heSoLuong=" + heSoLuong +
                '}';
    }
}