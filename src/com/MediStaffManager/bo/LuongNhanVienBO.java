//NhanVienBO (Business Object) đóng vai trò làm tầng trung gian giữa giao diện (View) và DAO. 
package com.MediStaffManager.bo;
import java.math.BigDecimal;
import com.MediStaffManager.bean.LuongNhanVien;
import com.MediStaffManager.bean.ThongKeResult;
import com.MediStaffManager.dao.LuongNhanVienDAO;
import java.util.List;

public class LuongNhanVienBO {

    private LuongNhanVienDAO salaryDAO;

  
    public LuongNhanVienBO() {
        this.salaryDAO = new LuongNhanVienDAO();  // Initializes the DAO to interact with DB
    }

    public BigDecimal layHeSoLuongTheoChucVu(int idChucVu) {
    	return salaryDAO.layHeSoLuongTheoChucVu(idChucVu);
    }

    public String layHoTenTheoIDNhanVien(int idNhanVien) {
    	return salaryDAO.layHoTenTheoIDNhanVien(idNhanVien);
    }
    
    public BigDecimal tinhTongLuong(LuongNhanVien luong) {
		return salaryDAO.tinhTongLuong(luong);
    }
    
    public List<LuongNhanVien> timKiemLuongTheoIDNhanVien(int idNhanVien) {
    	return salaryDAO.timKiemLuongTheoIDNhanVien(idNhanVien);
    }
    public List<LuongNhanVien> timKiemLuongTheoChucVu(int idChucVu) {
		return salaryDAO.timKiemLuongTheoChucVu(idChucVu);
	}
    
    public List<LuongNhanVien> layLuongTheoThang(String thangNam) {
        return salaryDAO.layLuongTheoThang(thangNam);  
    }
    
    public boolean themLuong(LuongNhanVien lnv) {
        return salaryDAO.themLuong(lnv);
    }

    public boolean capNhatLuong(LuongNhanVien lnv) {
    	return salaryDAO.capNhatLuong(lnv);
    }
    
    public boolean xoaNhanVien(int idLuong) {
    	return salaryDAO.xoaLuong(idLuong);
    }
    
    //Các chức năng thống kê
	/**
     * Gọi DAO để thống kê theo Tháng
     */
    public ThongKeResult thongKeTheoThang(String thangNam) {
        return salaryDAO.thongKeTheoThang(thangNam);
    }

    /**
     * Gọi DAO để thống kê theo Quý
     */
    public ThongKeResult thongKeTheoQuy(int nam, int quy) {
        return salaryDAO.thongKeTheoQuy(nam, quy);
    }

    /**
     * Gọi DAO để thống kê theo Năm
     */
    public ThongKeResult thongKeTheoNam(int nam) {
        return salaryDAO.thongKeTheoNam(nam);
    }
	
    
}
