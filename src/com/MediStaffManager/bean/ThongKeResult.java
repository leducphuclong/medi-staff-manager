package com.MediStaffManager.bean;

import java.math.BigDecimal;

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
