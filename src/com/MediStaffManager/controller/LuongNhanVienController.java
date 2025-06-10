package com.MediStaffManager.controller;
import com.MediStaffManager.bean.LuongNhanVienBEAN;
import com.MediStaffManager.bean.PhongBanBEAN;
import com.MediStaffManager.bean.ThongKeResult;
import com.MediStaffManager.bo.LuongNhanVienBO;
import com.MediStaffManager.dao.PhongBanDAO; // Import mới
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class LuongNhanVienController {
    private LuongNhanVienBO luongNhanVienBO;
    private PhongBanDAO phongBanDAO; // DAO mới

    public LuongNhanVienController() {
        this.luongNhanVienBO = new LuongNhanVienBO();
        this.phongBanDAO = new PhongBanDAO(); // Khởi tạo
    }
    
    // ----- CÁC PHƯƠNG THỨC MỚI -----
    
    /**
     * Lấy danh sách tất cả phòng ban và trả về dưới dạng chuỗi JSON.
     * @return Chuỗi JSON của danh sách phòng ban.
     */
    public String layTatCaPhongBan() {
        List<PhongBanBEAN> list = phongBanDAO.layTatCaPhongBan();
        // Chuyển đổi thủ công sang JSON để tránh thêm thư viện
        return "[" + list.stream()
                .map(pb -> String.format("{\"idPhongBan\":%d,\"tenPhongBan\":\"%s\"}",
                        pb.getIdPhongBan(), pb.getTenPhongBan().replace("\"", "\\\"")))
                .collect(Collectors.joining(",")) + "]";
    }

    /**
     * Lấy danh sách lương của nhân viên theo tháng và phòng ban.
     * @param thangNam Tháng năm dạng "YYYY-MM".
     * @param idPhongBan ID của phòng ban.
     * @return Danh sách lương nhân viên.
     */
    public List<LuongNhanVienBEAN> layLuongTheoThangVaPhongBan(String thangNam, int idPhongBan) {
        return luongNhanVienBO.layLuongTheoThangVaPhongBan(thangNam, idPhongBan);
    }
    
    // ----- CÁC PHƯƠNG THỨC CŨ -----
    
    public List<LuongNhanVienBEAN> layLuongTheoThang(String thangNam){
        return luongNhanVienBO.layLuongTheoThang(thangNam);
    }

    public boolean themLuong(LuongNhanVienBEAN lnv) {
        return luongNhanVienBO.themLuong(lnv);
    }

    public BigDecimal layHeSoLuongTheoChucVu(int idChucVu) {
        return luongNhanVienBO.layHeSoLuongTheoChucVu(idChucVu);
    }
    
    public String layHoTenTheoIDNhanVien(int idNhanVien) {
        return luongNhanVienBO.layHoTenTheoIDNhanVien(idNhanVien);
    }
    
    public BigDecimal getLuongCoBanChuan() {
        return luongNhanVienBO.getLuongCoBanChuan();
    }
    
    public String layTenChucVuTheoIdCV(int idChucVu) {
        return luongNhanVienBO.layTenChucVuTheoIdCV(idChucVu);
    }
    
    public BigDecimal tinhTongLuong(LuongNhanVienBEAN luong){
        return luongNhanVienBO.tinhTongLuong(luong);
    }
    
    public boolean capNhatLuongCoBanChuanChoTatCa(BigDecimal luongCoBanChuan) {
        return luongNhanVienBO.capNhatLuongCoBanChuanChoTatCa(luongCoBanChuan);
    }
    
    public List<LuongNhanVienBEAN> timKiemLuongTheoIDNhanVien(int idNhanVien) {
        return luongNhanVienBO.timKiemLuongTheoIDNhanVien(idNhanVien);
    }
        
    public boolean capNhatLuong(LuongNhanVienBEAN luong) {
        return luongNhanVienBO.capNhatLuong(luong);
    }
    
    public boolean xoaLuong(int idLuong) {
        return luongNhanVienBO.xoaLuong(idLuong);
    }

    public List<LuongNhanVienBEAN> timKiemLuongTheoChucVu(int idChucVu){
        return luongNhanVienBO.timKiemLuongTheoChucVu(idChucVu);
    }

    public ThongKeResult thongKeTheoThang(String thangNam) {
        return luongNhanVienBO.thongKeTheoThang(thangNam);
    }

    public ThongKeResult thongKeTheoQuy(int nam, int quy) {
        return luongNhanVienBO.thongKeTheoQuy(nam, quy);
    }

    public ThongKeResult thongKeTheoNam(int nam) {
        return luongNhanVienBO.thongKeTheoNam(nam);
    }
}