package com.MediStaffManager.bean;

public class TaiKhoan {
<<<<<<< HEAD
    private String tenDangNhap;
    private String matKhau;
    private String vaiTro;
    private Integer idNhanVien; // Đã thêm

    public TaiKhoan() {}

    public TaiKhoan(String tenDangNhap, String matKhau, String vaiTro, Integer idNhanVien) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
        this.idNhanVien = idNhanVien;
    }

    // Getters and Setters
    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }
    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }
    public String getVaiTro() { return vaiTro; }
    public void setVaiTro(String vaiTro) { this.vaiTro = vaiTro; }
    public Integer getIdNhanVien() { return idNhanVien; }
    public void setIdNhanVien(Integer idNhanVien) { this.idNhanVien = idNhanVien; }

    @Override
    public String toString() {
        return "TaiKhoan{" +
                "tenDangNhap='" + tenDangNhap + '\'' +
                ", matKhau='[PROTECTED]'" +
                ", vaiTro='" + vaiTro + '\'' +
                ", idNhanVien=" + idNhanVien +
                '}';
    }
}
=======
    private Integer idNhanVien;
    private String tenDangNhap;
    private String matKhau;
    private String vaiTro;

    
    // Constructor không đối số
    public TaiKhoan() {
    }
    

    // Constructor đầy đủ các thuộc tính
    public TaiKhoan(Integer idNhanVien, String tenDangNhap, String matKhau, String vaiTro) {
        this.idNhanVien = idNhanVien;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
    }

    // Getter và setter cho idNhanVien
    public Integer getIdNhanVien() {
        return idNhanVien;
    }

    public void setIdNhanVien(Integer idNhanVien) {
        this.idNhanVien = idNhanVien;
    }

    // Getter và setter cho tenDangNhap
    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    // Getter và setter cho matKhau
    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    // Getter và setter cho vaiTro
    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    @Override
    public String toString() {
        return "User{" +
               "idNhanVien=" + idNhanVien +
               ", tenDangNhap='" + tenDangNhap + '\'' +
               ", matKhau='" + matKhau + '\'' +
               ", vaiTro='" + vaiTro + '\'' +
               '}';
    }
}
>>>>>>> ThaoDuyen-QuanLyLuong-ThongKe
