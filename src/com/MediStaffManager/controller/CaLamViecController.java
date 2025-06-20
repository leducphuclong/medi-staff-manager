package com.MediStaffManager.controller;

import java.util.List;

import com.MediStaffManager.bean.CaLamViecBEAN;
import com.MediStaffManager.bo.CaLamViecBO;

public class CaLamViecController {
    private CaLamViecBO caLamViecBO;

    public CaLamViecController() {
        this.caLamViecBO = new CaLamViecBO();
    }

    public List<CaLamViecBEAN> layCaLamViecTheoNgayLamViec(String ngayLamViec) {
        return caLamViecBO.layCaLamViecTheoNgayLamViec(ngayLamViec);
    }
    
    public void capNhatCaLamViec(CaLamViecBEAN caLamViec) {
    	caLamViecBO.capNhatCaLamViec(caLamViec);
    }
    
    public CaLamViecBEAN layCaLamViecTheoIdCaLam(int idCaLam) {
    	return caLamViecBO.layCaLamViecTheoIdCaLam(idCaLam);
	}
    
    public void xoaCaLamViecTheoIdCaLam(int idCaLam) {
    	caLamViecBO.xoaCaLamViecTheoIdCaLam(idCaLam);
    }
    
    public void themCaLamViec(CaLamViecBEAN caLamViec) {
    	caLamViecBO.themCaLamViec(caLamViec);
    }
    
    public void xoaCaLamViecTheoThangNam(String monthYear) {
    	caLamViecBO.xoaCaLamViecTheoThangNam(monthYear);
    }
}
