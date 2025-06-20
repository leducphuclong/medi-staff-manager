package com.MediStaffManager.bean;

import java.math.BigDecimal;

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