package com.MediStaffManager.controller;
import com.MediStaffManager.bean.LuongNhanVien;
import com.MediStaffManager.bean.ThongKeResult;
import com.MediStaffManager.bo.LuongNhanVienBO; 
import java.math.BigDecimal;
import java.util.List;

public class LuongNhanVienController {
	private LuongNhanVienBO luongNhanVienBO;
	
	public LuongNhanVienController() {
		this.luongNhanVienBO = new LuongNhanVienBO();
	}
	
	public List<LuongNhanVien> layLuongTheoThang (String thangNam){
		return luongNhanVienBO.layLuongTheoThang(thangNam);
	}
	
	public boolean themLuong(LuongNhanVien lnv) {
		return luongNhanVienBO.themLuong(lnv);
		
	}
	public BigDecimal layHeSoLuongTheoChucVu(int idChucVu) {
	    return luongNhanVienBO.layHeSoLuongTheoChucVu(idChucVu);
	}
	 public String layHoTenTheoIDNhanVien(int idNhanVien) {
	    	return  luongNhanVienBO.layHoTenTheoIDNhanVien(idNhanVien);
	 }
	 
	 public BigDecimal tinhTongLuong(LuongNhanVien luong){
	    	return luongNhanVienBO.tinhTongLuong(luong);
	 }
	 
	 public List<LuongNhanVien> timKiemLuongTheoIDNhanVien(int idNhanVien) {
	    	return luongNhanVienBO.timKiemLuongTheoIDNhanVien(idNhanVien);
	 }
	    
	public boolean capNhatLuong(LuongNhanVien luong) {
		return luongNhanVienBO.capNhatLuong(luong);
	}
	public boolean xoaLuong(int idLuong) {
		return luongNhanVienBO.xoaNhanVien(idLuong);
	}
	
	public List<LuongNhanVien> timKiemLuongTheoChucVu(int idChucVu){
		return luongNhanVienBO.timKiemLuongTheoChucVu(idChucVu);
	}
	//các chức năng THỐNG KÊ
	/**
     * Gọi BO để thống kê theo Tháng
     */
    public ThongKeResult thongKeTheoThang(String thangNam) {
        return luongNhanVienBO.thongKeTheoThang(thangNam);
    }

    /**
     * Gọi DAO để thống kê theo Quý
     */
    public ThongKeResult thongKeTheoQuy(int nam, int quy) {
        return luongNhanVienBO.thongKeTheoQuy(nam, quy);
    }

    /**
     * Gọi DAO để thống kê theo Năm
     */
    public ThongKeResult thongKeTheoNam(int nam) {
        return luongNhanVienBO.thongKeTheoNam(nam);
    }
	

	
}
