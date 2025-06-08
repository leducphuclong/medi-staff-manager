package com.MediStaffManager.view;

import java.sql.Connection;


import com.MediStaffManager.utils.DBConnection;
import com.MediStaffManager.utils.SwingWindowUtil;
import com.MediStaffManager.view.dangNhap.DangNhapBridge;
import com.MediStaffManager.view.lichthang.LichViewer;
import com.MediStaffManager.view.quanLyLuong.QuanLyLuongBridge;
import com.MediStaffManager.view.quanLyLuong.ThongKeLuongBridge;
import com.MediStaffManager.view.quanLyNhanVien.QuanLyNhanVienBridge;
import com.MediStaffManager.view.quanLyPhongBan.QuanLyPhongBanBridge;
import com.MediStaffManager.view.quenMatKhau.QuenMatKhauBridge;
import com.MediStaffManager.view.trangChu.KeToanBridge;
import com.MediStaffManager.view.trangChu.QuanLyNhanSuBridge;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class Bridge {
    
    private DangNhapBridge dangNhapBridge;
    private QuenMatKhauBridge quenMatKhauBridge;
    private QuanLyNhanSuBridge quanLyNhanSuBridge;
    private QuanLyNhanVienBridge nhanVienBridge;
    private QuanLyPhongBanBridge phongBanBridge;
    private QuanLyLuongBridge quanLyLuongBridge;
    private ThongKeLuongBridge thongKeLuongBridge;
    private KeToanBridge keToanBridge;
    
    private Connection conn;
    private WebView webView;
    private Stage primaryStage;

    public Bridge(WebView webView, Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.webView = webView;
        this.conn = DBConnection.connect();
        initializeBridges();
    }

    private void initializeBridges() {
        setDangNhapBridge();
        setQuenMatKhauBridge();
        setQuanLyNhanSuBridge();
        setQuanLyNhanVienBridge();
        setQuanLyPhongBanBridge();
        setKeToanBridge();
        setQuanLyLuongBridge();
        setThongKeLuongBridge();
    }

    public WebEngine getWebEngine() {
        return webView.getEngine();
    }

    public WebView getWebView() {
        return webView;
    }

    public DangNhapBridge getDangNhapBridge() {
        return dangNhapBridge;
    }

    public void setDangNhapBridge() {
        if (this.dangNhapBridge == null) {
            this.dangNhapBridge = new DangNhapBridge(getWebEngine());
        }
    }

    public QuenMatKhauBridge getQuenMatKhauBridge() {
        return quenMatKhauBridge;
    }

    public void setQuenMatKhauBridge() {
        if (this.quenMatKhauBridge == null) {
            this.quenMatKhauBridge = new QuenMatKhauBridge(getWebEngine());
        }
    }

    public QuanLyNhanSuBridge getQuanLyNhanSuBridge() {
        return quanLyNhanSuBridge;
    }
    
    public void showCalendar() {
        System.out.println("JavaScript bridge received a call to showCalendar(). Opening window...");

        SwingWindowUtil.showLichThangWindow(this.primaryStage);
        
        System.out.println("Calendar window was closed.");
    }
    
    public void setQuanLyNhanSuBridge() {
        if (this.quanLyNhanSuBridge == null) {
            this.quanLyNhanSuBridge = new QuanLyNhanSuBridge(getWebEngine());
        }
    }

    public QuanLyNhanVienBridge getQuanLyNhanVienBridge() {
        return nhanVienBridge;
    }

    public void setQuanLyNhanVienBridge() {
        if (this.nhanVienBridge == null) {
            this.nhanVienBridge = new QuanLyNhanVienBridge(getWebEngine());
        }
    }

    public QuanLyPhongBanBridge getQuanLyPhongBanBridge() {
        return phongBanBridge;
    }

    public void setQuanLyPhongBanBridge() {
        if (this.phongBanBridge == null) {
            this.phongBanBridge = new QuanLyPhongBanBridge(getWebEngine());
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void log(String message) {
        System.out.println(message);
    }

    public void hello() {
        System.out.println("hello");
    }

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}
	
	public void showLich(Stage primaryStage) {
		LichViewer lichViewer = new LichViewer();
	    lichViewer.showLich(); // Gọi hàm để mở cửa sổ mới
	}

	public KeToanBridge getKeToanBridge() {
		return keToanBridge;
	}

	public void setKeToanBridge() {
		if (this.keToanBridge == null) {
            this.keToanBridge = new KeToanBridge(getWebEngine());
        }
	}

	public QuanLyLuongBridge getQuanLyLuongBridge() {
		return quanLyLuongBridge;
	}

	public void setQuanLyLuongBridge() {
		if (this.quanLyLuongBridge == null) {
            this.quanLyLuongBridge = new QuanLyLuongBridge(getWebEngine());
        }
	}

	public ThongKeLuongBridge getThongKeLuongBridge() {
		return thongKeLuongBridge;
	}

	public void setThongKeLuongBridge() {
		if (this.thongKeLuongBridge == null) {
            this.thongKeLuongBridge = new ThongKeLuongBridge(getWebEngine());
        }
	}

}
