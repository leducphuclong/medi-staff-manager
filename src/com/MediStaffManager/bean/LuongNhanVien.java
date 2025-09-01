package com.MediStaffManager.bean;

import java.math.BigDecimal;

public class LuongNhanVien {
    private int idLuong;
    private int idChucVu;
    private int idNhanVien;
    private String thangNam;
    private BigDecimal luongThuNhap;
    private BigDecimal thuong;
    private BigDecimal phuCap;
    private BigDecimal tangCa;
    private BigDecimal tongLuong;
    	//BigDecimal a = new Decimal("0.1"): Khi truyền "0.1" là chuỗi,
    	//BigDecimal phân tích từng chữ số một cách chính xác tuyệt đối, không liên quan đến nhị phân hay kiểu double.
    public LuongNhanVien() {}

    public LuongNhanVien(int idLuong, int idChucVu, int idNhanVien, String thangNam,
                         BigDecimal luongThuNhap,
                         BigDecimal thuong, BigDecimal phuCap,
                         BigDecimal tangCa, BigDecimal tongLuong) {
        this.idLuong = idLuong;
        this.idChucVu = idChucVu;
        this.idNhanVien = idNhanVien;
        this.thangNam = thangNam;
        this.luongThuNhap = luongThuNhap;
        this.thuong = thuong;
        this.phuCap = phuCap;
        this.tangCa = tangCa;
        this.tongLuong = tongLuong;
    }

    public int getIdLuong() { return idLuong; }
    public void setIdLuong(int idLuong) { this.idLuong = idLuong; }

    public	int getIdChucVu() { return idChucVu; }
    public void setIdChucVu(Integer idChucVu) { this.idChucVu = idChucVu; }

    public int getIdNhanVien() { return idNhanVien; }
    public void setIdNhanVien(int idNhanVien) { this.idNhanVien = idNhanVien; }

    public String getThangNam() { return thangNam; }
    public void setThangNam(String thangNam) { this.thangNam = thangNam; }

    public BigDecimal getLuongThuNhap() { return luongThuNhap; }
    public void setLuongThuNhap(BigDecimal luongThuNhap) { this.luongThuNhap = luongThuNhap; }

    public BigDecimal getThuong() { return thuong; }
    public void setThuong(BigDecimal thuong) { this.thuong = thuong; }

    public BigDecimal getPhuCap() { return phuCap; }
    public void setPhuCap(BigDecimal phuCap) { this.phuCap = phuCap; }

    public BigDecimal getTangCa() { return tangCa; }
    public void setTangCa(BigDecimal tangCa) { this.tangCa = tangCa; }

    public BigDecimal getTongLuong() { return tongLuong; }
    public void setTongLuong(BigDecimal tongLuong) { this.tongLuong = tongLuong; }


    @Override
    public String toString() {
        return "LuongNhanVien{" +
                "idLuong=" + idLuong +
                ", idChucVu=" + idChucVu +
                ", idNhanVien=" + idNhanVien +
                ", thangNam='" + thangNam + '\'' +
                ", luongThuNhap=" + luongThuNhap +
                ", thuong=" + thuong +
                ", phuCap=" + phuCap +
                ", tangCa=" + tangCa +
                ", tongLuong=" + tongLuong +
                '}';
    }
}
