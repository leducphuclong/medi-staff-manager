package com.MediStaffManager.bean;

import java.math.BigDecimal;

<<<<<<< HEAD
/**
 * Holds the result of statistical aggregation queries on salary data.
 */
public class ThongKeResult {
    private long soLuongNhanVien;
    private BigDecimal tongChiTra;
    private BigDecimal luongTrungBinh;
    private BigDecimal luongCaoNhat;
    private BigDecimal luongThapNhat;

    // Constructors
    public ThongKeResult() {
        this.soLuongNhanVien = 0;
        this.tongChiTra = BigDecimal.ZERO;
        this.luongTrungBinh = BigDecimal.ZERO;
        this.luongCaoNhat = BigDecimal.ZERO;
        this.luongThapNhat = BigDecimal.ZERO;
    }
    
    // Getters and Setters
    public long getSoLuongNhanVien() { return soLuongNhanVien; }
    public void setSoLuongNhanVien(long soLuongNhanVien) { this.soLuongNhanVien = soLuongNhanVien; }
    public BigDecimal getTongChiTra() { return tongChiTra; }
    public void setTongChiTra(BigDecimal tongChiTra) { this.tongChiTra = tongChiTra; }
    public BigDecimal getLuongTrungBinh() { return luongTrungBinh; }
    public void setLuongTrungBinh(BigDecimal luongTrungBinh) { this.luongTrungBinh = luongTrungBinh; }
    public BigDecimal getLuongCaoNhat() { return luongCaoNhat; }
    public void setLuongCaoNhat(BigDecimal luongCaoNhat) { this.luongCaoNhat = luongCaoNhat; }
    public BigDecimal getLuongThapNhat() { return luongThapNhat; }
    public void setLuongThapNhat(BigDecimal luongThapNhat) { this.luongThapNhat = luongThapNhat; }
}
=======
public class ThongKeResult {
    private int soNhanVien;
    private BigDecimal luongTrungBinh;
    private BigDecimal tongLuong;

    public ThongKeResult(int soNhanVien, BigDecimal luongTrungBinh, BigDecimal tongLuong) {
        this.soNhanVien = soNhanVien;
        this.luongTrungBinh = luongTrungBinh;
        this.tongLuong = tongLuong;
    }

    public int getSoNhanVien() {
        return soNhanVien;
    }

    public BigDecimal getLuongTrungBinh() {
        return luongTrungBinh;
    }

    public BigDecimal getTongLuong() {
        return tongLuong;
    }
}
>>>>>>> ThaoDuyen-QuanLyLuong-ThongKe
