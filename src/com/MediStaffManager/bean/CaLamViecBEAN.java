package com.MediStaffManager.bean;

public class CaLamViecBEAN {
    private int idCaLam;                // ID của ca làm việc
    private int idNhanVien;             // Mã nhân viên
    private String tenNhanVien;         // Tên nhân viên
    private String ngayLamViec;         // Ngày làm việc
    private String tenCa;               // Tên ca
    private String moTaCa;              // Mô tả ca
    private String gioBatDauThucTe;     // Giờ bắt đầu thực tế
    private String gioKetThucThucTe;   // Giờ kết thúc thực tế
    private String gioNghiBatDau;      // Giờ bắt đầu nghỉ
    private String gioNghiKetThuc;     // Giờ kết thúc nghỉ
    private String donVi;              // Đơn vị công tác
    private String ghiChu;             // Ghi chú về ca làm việc
    private boolean laTrucOnCall;      // Trực OnCall hay không

    // Constructor
    public CaLamViecBEAN(int idCaLam, int idNhanVien, String tenNhanVien, String ngayLamViec, 
                      String tenCa, String moTaCa, String gioBatDauThucTe, String gioKetThucThucTe, 
                      String gioNghiBatDau, String gioNghiKetThuc, String donVi, 
                      String ghiChu, boolean laTrucOnCall) {
        this.idCaLam = idCaLam;
        this.idNhanVien = idNhanVien;
        this.tenNhanVien = tenNhanVien;  // Initialize tenNhanVien
        this.ngayLamViec = ngayLamViec;
        this.tenCa = tenCa;
        this.moTaCa = moTaCa;
        this.gioBatDauThucTe = gioBatDauThucTe;
        this.gioKetThucThucTe = gioKetThucThucTe;
        this.gioNghiBatDau = gioNghiBatDau;
        this.gioNghiKetThuc = gioNghiKetThuc;
        this.donVi = donVi;
        this.ghiChu = ghiChu;
        this.laTrucOnCall = laTrucOnCall;
    }

    // Getters and Setters
    public int getIdCaLam() {
        return idCaLam;
    }

    public void setIdCaLam(int idCaLam) {
        this.idCaLam = idCaLam;
    }

    public int getIdNhanVien() {
        return idNhanVien;
    }

    public void setIdNhanVien(int idNhanVien) {
        this.idNhanVien = idNhanVien;
    }

    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public void setTenNhanVien(String tenNhanVien) {
        this.tenNhanVien = tenNhanVien;
    }

    public String getNgayLamViec() {
        return ngayLamViec;
    }

    public void setNgayLamViec(String ngayLamViec) {
        this.ngayLamViec = ngayLamViec;
    }

    public String getTenCa() {
        return tenCa;
    }

    public void setTenCa(String tenCa) {
        this.tenCa = tenCa;
    }

    public String getMoTaCa() {
        return moTaCa;
    }

    public void setMoTaCa(String moTaCa) {
        this.moTaCa = moTaCa;
    }

    public String getGioBatDauThucTe() {
        return gioBatDauThucTe;
    }

    public void setGioBatDauThucTe(String gioBatDauThucTe) {
        this.gioBatDauThucTe = gioBatDauThucTe;
    }

    public String getGioKetThucThucTe() {
        return gioKetThucThucTe;
    }

    public void setGioKetThucThucTe(String gioKetThucThucTe) {
        this.gioKetThucThucTe = gioKetThucThucTe;
    }

    public String getGioNghiBatDau() {
        return gioNghiBatDau;
    }

    public void setGioNghiBatDau(String gioNghiBatDau) {
        this.gioNghiBatDau = gioNghiBatDau;
    }

    public String getGioNghiKetThuc() {
        return gioNghiKetThuc;
    }

    public void setGioNghiKetThuc(String gioNghiKetThuc) {
        this.gioNghiKetThuc = gioNghiKetThuc;
    }

    public String getDonVi() {
        return donVi;
    }

    public void setDonVi(String donVi) {
        this.donVi = donVi;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public boolean isLaTrucOnCall() {
        return laTrucOnCall;
    }

    public void setLaTrucOnCall(boolean laTrucOnCall) {
        this.laTrucOnCall = laTrucOnCall;
    }

    // Override toString() if needed for printing
    @Override
    public String toString() {
        return "CaLamViecBEAN [idCaLam=" + idCaLam + ", idNhanVien=" + idNhanVien + ", tenNhanVien=" + tenNhanVien + 
               ", ngayLamViec=" + ngayLamViec + ", tenCa=" + tenCa + ", moTaCa=" + moTaCa + ", gioBatDauThucTe=" + 
               gioBatDauThucTe + ", gioKetThucThucTe=" + gioKetThucThucTe + ", gioNghiBatDau=" + gioNghiBatDau + 
               ", gioNghiKetThuc=" + gioNghiKetThuc + ", donVi=" + donVi + ", ghiChu=" + ghiChu + ", laTrucOnCall=" + 
               laTrucOnCall + "]";
    }
}
