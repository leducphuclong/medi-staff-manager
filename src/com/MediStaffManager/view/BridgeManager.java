package com.MediStaffManager.view;

import com.MediStaffManager.view.dangNhap.DangNhapBridge;
import com.MediStaffManager.view.quenMatKhau.QuenMatKhauBridge;
import javafx.scene.web.WebEngine;

public class BridgeManager {

    private static DangNhapBridge dangNhapBridge;
    private static QuenMatKhauBridge quenMatKhauBridge;
    private static WebEngine webEngine;
    
    public static BridgeManager getInstance(WebEngine engine) {
        if (webEngine == null) {
            webEngine = engine;
            setDangNhapBridge(webEngine);
            setDangKyBridge(webEngine);
        }
        return new BridgeManager(); 
    }
    
    public static void setDangNhapBridge(WebEngine webEngine) {
        if (dangNhapBridge == null) {
            dangNhapBridge = new DangNhapBridge(webEngine);
        }
    }

    public static void setDangKyBridge(WebEngine webEngine) {
        if (quenMatKhauBridge == null) {
        	quenMatKhauBridge = new QuenMatKhauBridge(webEngine);
        }
    }

    public static DangNhapBridge getDangNhapBridge() {
        return dangNhapBridge;
    }

    public static QuenMatKhauBridge getQuenMatKhauBridge() {
        return quenMatKhauBridge;
    }
}
