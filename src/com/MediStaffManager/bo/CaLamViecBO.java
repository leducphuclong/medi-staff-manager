package com.MediStaffManager.bo;

import java.util.List;

import com.MediStaffManager.bean.CaLamViecBEAN;
import com.MediStaffManager.dao.CaLamViecDAO;

public class CaLamViecBO {
    private CaLamViecDAO caLamViecDAO;

    public CaLamViecBO() {
        caLamViecDAO = new CaLamViecDAO();
    }

    public List<CaLamViecBEAN> layCaLamViecTheoNgayLamViec(String ngayLamViec) {
        return caLamViecDAO.layCaLamViecTheoNgayLamViec(ngayLamViec);
    }
    
    public void capNhatCaLamViec(CaLamViecBEAN caLamViec) {
    	caLamViecDAO.capNhatCaLamViec(caLamViec);
    }
    
    public CaLamViecBEAN layCaLamViecTheoIdCaLam(int idCaLam) {
    	return caLamViecDAO.layCaLamViecTheoIdCaLam(idCaLam);
    }
    
    public void xoaCaLamViecTheoIdCaLam(int idCaLam) {
    	caLamViecDAO.xoaCaLamViecTheoIdCaLam(idCaLam);
    }
    
    public void themCaLamViec(CaLamViecBEAN caLamViec) {
    	caLamViecDAO.themCaLamViec(caLamViec);
    }
    
    public void xoaCaLamViecTheoThangNam(String monthYear) {
    	caLamViecDAO.xoaCaLamViecTheoThangNam(monthYear);
    }
}
