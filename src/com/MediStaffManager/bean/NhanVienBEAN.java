package com.MediStaffManager.bean;
import javafx.beans.property.*;

public class NhanVienBEAN {
    private IntegerProperty idNhanVien;
    private StringProperty cccd;
    private StringProperty hoTen;
    private StringProperty sdt;
    private StringProperty email;
    private StringProperty gioiTinh;
    private StringProperty ngaySinh;
    private IntegerProperty idChucVu;
    private IntegerProperty idPhongBan;
    private StringProperty tenChucVu;
    private StringProperty tenPhongBan;
    private BooleanProperty selected; 

    public NhanVienBEAN() {
        this.idNhanVien = new SimpleIntegerProperty();
        this.cccd = new SimpleStringProperty();
        this.hoTen = new SimpleStringProperty();
        this.sdt = new SimpleStringProperty();
        this.email = new SimpleStringProperty();
        this.gioiTinh = new SimpleStringProperty();
        this.ngaySinh = new SimpleStringProperty();
        this.idChucVu = new SimpleIntegerProperty();
        this.idPhongBan = new SimpleIntegerProperty();
        this.tenChucVu = new SimpleStringProperty();
        this.tenPhongBan = new SimpleStringProperty();
        this.selected = new SimpleBooleanProperty(false); // Mặc định không được chọn
    }

    public NhanVienBEAN(int idNhanVien, String cccd, String hoTen, String sdt, String email,
                    String gioiTinh, String ngaySinh, int idChucVu, int idPhongBan,
                    String tenChucVu, String tenPhongBan) {
        this.idNhanVien = new SimpleIntegerProperty(idNhanVien);
        this.cccd = new SimpleStringProperty(cccd);
        this.hoTen = new SimpleStringProperty(hoTen);
        this.sdt = new SimpleStringProperty(sdt);
        this.email = new SimpleStringProperty(email);
        this.gioiTinh = new SimpleStringProperty(gioiTinh);
        this.ngaySinh = new SimpleStringProperty(ngaySinh);
        this.idChucVu = new SimpleIntegerProperty(idChucVu);
        this.idPhongBan = new SimpleIntegerProperty(idPhongBan);
        this.tenChucVu = new SimpleStringProperty(tenChucVu);
        this.tenPhongBan = new SimpleStringProperty(tenPhongBan);
        this.selected = new SimpleBooleanProperty(false); // Mặc định không được chọn
    }

    // Getter và Setter cho thuộc tính selected
    public BooleanProperty selectedProperty() {
        return selected;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    // Getters for properties
    public IntegerProperty idNhanVienProperty() {
        return idNhanVien;
    }

    public StringProperty cccdProperty() {
        return cccd;
    }

    public StringProperty hoTenProperty() {
        return hoTen;
    }

    public StringProperty sdtProperty() {
        return sdt;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public StringProperty gioiTinhProperty() {
        return gioiTinh;
    }

    public StringProperty ngaySinhProperty() {
        return ngaySinh;
    }

    public IntegerProperty idChucVuProperty() {
        return idChucVu;
    }

    public IntegerProperty idPhongBanProperty() {
        return idPhongBan;
    }

    public StringProperty tenChucVuProperty() {
        return tenChucVu;
    }

    public StringProperty tenPhongBanProperty() {
        return tenPhongBan;
    }

    // Getters for raw values
    public int getIdNhanVien() {
        return idNhanVien.get();
    }

    public String getCccd() {
        return cccd.get();
    }

    public String getHoTen() {
        return hoTen.get();
    }

    public String getSdt() {
        return sdt.get();
    }

    public String getEmail() {
        return email.get();
    }

    public String getGioiTinh() {
        return gioiTinh.get();
    }

    public String getNgaySinh() {
        return ngaySinh.get();
    }

    public int getIdChucVu() {
        return idChucVu.get();
    }

    public int getIdPhongBan() {
        return idPhongBan.get();
    }

    public String getTenChucVu() {
        return tenChucVu.get();
    }

    public String getTenPhongBan() {
        return tenPhongBan.get();
    }

    // Setters
    public void setIdNhanVien(int idNhanVien) {
        this.idNhanVien.set(idNhanVien);
    }

    public void setCccd(String cccd) {
        this.cccd.set(cccd);
    }

    public void setHoTen(String hoTen) {
        this.hoTen.set(hoTen);
    }

    public void setSdt(String sdt) {
        this.sdt.set(sdt);
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh.set(gioiTinh);
    }

    public void setNgaySinh(String ngaySinh) {
        this.ngaySinh.set(ngaySinh);
    }

    public void setIdChucVu(int idChucVu) {
        this.idChucVu.set(idChucVu);
    }

    public void setIdPhongBan(int idPhongBan) {
        this.idPhongBan.set(idPhongBan);
    }

    public void setTenChucVu(String tenChucVu) {
        this.tenChucVu.set(tenChucVu);
    }

    public void setTenPhongBan(String tenPhongBan) {
        this.tenPhongBan.set(tenPhongBan);
    }

    @Override
    public String toString() {
        return "NhanVien{" +
                "idNhanVien=" + idNhanVien.get() +
                ", cccd='" + cccd.get() + '\'' +
                ", hoTen='" + hoTen.get() + '\'' +
                ", sdt='" + sdt.get() + '\'' +
                ", email='" + email.get() + '\'' +
                ", gioiTinh='" + gioiTinh.get() + '\'' +
                ", ngaySinh='" + ngaySinh.get() + '\'' +
                ", idChucVu=" + idChucVu.get() +
                ", idPhongBan=" + idPhongBan.get() +
                ", tenChucVu='" + tenChucVu.get() + '\'' +
                ", tenPhongBan='" + tenPhongBan.get() + '\'' +
                '}';
    }
}