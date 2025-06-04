package com.MediStaffManager.view;

import com.MediStaffManager.view.dangNhap.DangNhapBridge;
import com.MediStaffManager.view.quenMatKhau.QuenMatKhauBridge;
import com.MediStaffManager.view.trangChu.QuanLyNhanSuBridge;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class Bridge {

	private DangNhapBridge dangNhapBridge;
	private QuenMatKhauBridge quenMatKhauBridge;
	private QuanLyNhanSuBridge quanLyNhanSuBridge;
	private WebView webView;
	private Stage PrimaryStage;

	public Bridge(WebView webView, Stage PrimaryStage) {
		this.PrimaryStage = PrimaryStage;
		this.webView = webView;
		setDangNhapBridge();
		setDangKyBridge();
		setQuanLyNhanSuBridge();
	}

	public WebEngine getWebEngine() {
		return webView.getEngine();
	}

	public WebView getWebView() {
		return webView;
	}

	public void setDangNhapBridge() {
		if (dangNhapBridge == null) {
			dangNhapBridge = new DangNhapBridge(getWebEngine());
		}
	}

	public void setDangKyBridge() {
		if (quenMatKhauBridge == null) {
			quenMatKhauBridge = new QuenMatKhauBridge(getWebEngine());
		}
	}
	
	public void setQuanLyNhanSuBridge() {
		if (quanLyNhanSuBridge == null) {
			quanLyNhanSuBridge = new QuanLyNhanSuBridge(getWebEngine());
		}
	}

	public DangNhapBridge getDangNhapBridge() {
		return dangNhapBridge;
	}

	public QuenMatKhauBridge getQuenMatKhauBridge() {
		return quenMatKhauBridge;
	}
	
	public QuanLyNhanSuBridge getQuanLyNhanSuBridge() {
		return quanLyNhanSuBridge;
	}


	public void hello() {
		System.out.println("hello");
	}

	public Stage getPrimaryStage() {
		return PrimaryStage;
	}

	public void setPrimaryStage(Stage primaryStage) {
		PrimaryStage = primaryStage;
	}
}
