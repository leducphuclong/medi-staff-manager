package com.MediStaffManager.bo;

import com.MediStaffManager.bean.LuongNhanVienBEAN;
import com.MediStaffManager.bean.ThongKeResult;
import com.MediStaffManager.dao.LuongNhanVienDAO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LuongNhanVienBO {
    private final LuongNhanVienDAO luongNhanVienDAO;

    public LuongNhanVienBO() {
        this.luongNhanVienDAO = new LuongNhanVienDAO();
    }

    /**
     * Calculates the total salary based on the components in the bean.
     * Formula: LuongCoBan = LuongCoBanChuan * HeSoLuong
     *          TongLuong  = LuongCoBan + Thuong + PhuCap + TangCa
     * @param luong The bean containing salary components.
     * @return The calculated total salary.
     */
    public BigDecimal tinhTongLuong(LuongNhanVienBEAN luong) {
        if (luong.getLuongCoBanChuan() == null || luong.getHeSoLuong() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal luongCoBan = luong.getLuongCoBanChuan().multiply(luong.getHeSoLuong());
        BigDecimal thuong = (luong.getThuong() != null) ? luong.getThuong() : BigDecimal.ZERO;
        BigDecimal phuCap = (luong.getPhuCap() != null) ? luong.getPhuCap() : BigDecimal.ZERO;
        BigDecimal tangCa = (luong.getTangCa() != null) ? luong.getTangCa() : BigDecimal.ZERO;

        return luongCoBan.add(thuong).add(phuCap).add(tangCa).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Adds a new salary record. It first calculates the total salary before saving.
     * @param lnv The salary bean to add.
     * @return true if successful, false otherwise.
     */
    public boolean themLuong(LuongNhanVienBEAN lnv) {
        // Ensure TongLuong is calculated before saving
        lnv.setTongLuong(tinhTongLuong(lnv));
        return luongNhanVienDAO.themLuong(lnv);
    }

    /**
     * Updates an existing salary record. It recalculates the total salary before saving.
     * @param luong The salary bean to update.
     * @return true if successful, false otherwise.
     */
    public boolean capNhatLuong(LuongNhanVienBEAN luong) {
        // Ensure TongLuong is recalculated before updating
        luong.setTongLuong(tinhTongLuong(luong));
        return luongNhanVienDAO.capNhatLuong(luong);
    }

    /**
     * Deletes a salary record by its ID.
     * @param idLuong The ID of the salary to delete.
     * @return true if successful, false otherwise.
     */
    public boolean xoaLuong(int idLuong) {
        return luongNhanVienDAO.xoaLuong(idLuong);
    }

    // --- Passthrough methods (delegating directly to DAO) ---

    public List<LuongNhanVienBEAN> layLuongTheoThang(String thangNam) {
        return luongNhanVienDAO.layLuongTheoThang(thangNam);
    }

    // NEW METHOD
    public List<LuongNhanVienBEAN> layLuongTheoThangVaPhongBan(String thangNam, int idPhongBan) {
        return luongNhanVienDAO.layLuongTheoThangVaPhongBan(thangNam, idPhongBan);
    }

    public BigDecimal layHeSoLuongTheoChucVu(int idChucVu) {
        return luongNhanVienDAO.layHeSoLuongTheoChucVu(idChucVu);
    }

    public String layHoTenTheoIDNhanVien(int idNhanVien) {
        return luongNhanVienDAO.layHoTenTheoIDNhanVien(idNhanVien);
    }

    public BigDecimal getLuongCoBanChuan() {
        return luongNhanVienDAO.getLuongCoBanChuan();
    }

    public String layTenChucVuTheoIdCV(int idChucVu) {
        return luongNhanVienDAO.layTenChucVuTheoIdCV(idChucVu);
    }

    public List<LuongNhanVienBEAN> timKiemLuongTheoIDNhanVien(int idNhanVien) {
        return luongNhanVienDAO.timKiemLuongTheoIDNhanVien(idNhanVien);
    }

    /**
     * Finds salary records based on position.
     */
    public List<LuongNhanVienBEAN> timKiemLuongTheoChucVu(int idChucVu) {
        System.err.println("BO.timKiemLuongTheoChucVu: This method requires a corresponding DAO method for efficiency.");
        return Collections.emptyList();
    }

    /**
     * This method is potentially dangerous and inefficient as it updates many records.
     */
    public boolean capNhatLuongCoBanChuanChoTatCa(BigDecimal luongCoBanChuan) {
        System.err.println("capNhatLuongCoBanChuanChoTatCa is a complex operation not fully implemented.");
        return false;
    }


    // --- Statistics Methods ---

    public ThongKeResult thongKeTheoThang(String thangNam) {
        return luongNhanVienDAO.thongKeTrongKhoangThoiGian(Collections.singletonList(thangNam));
    }

    public ThongKeResult thongKeTheoQuy(int nam, int quy) {
        List<String> thangNamList = new ArrayList<>();
        switch (quy) {
            case 1:
                thangNamList.addAll(Arrays.asList(nam + "-01", nam + "-02", nam + "-03"));
                break;
            case 2:
                thangNamList.addAll(Arrays.asList(nam + "-04", nam + "-05", nam + "-06"));
                break;
            case 3:
                thangNamList.addAll(Arrays.asList(nam + "-07", nam + "-08", nam + "-09"));
                break;
            case 4:
                thangNamList.addAll(Arrays.asList(nam + "-10", nam + "-11", nam + "-12"));
                break;
            default:
                return new ThongKeResult(); // Return empty result for invalid quarter
        }
        return luongNhanVienDAO.thongKeTrongKhoangThoiGian(thangNamList);
    }

    public ThongKeResult thongKeTheoNam(int nam) {
        List<String> thangNamList = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            thangNamList.add(String.format("%d-%02d", nam, i));
        }
        return luongNhanVienDAO.thongKeTrongKhoangThoiGian(thangNamList);
    }
}