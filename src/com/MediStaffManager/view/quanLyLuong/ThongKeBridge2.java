package com.MediStaffManager.view.quanLyLuong;
import com.MediStaffManager.controller.LuongNhanVienController;
import com.MediStaffManager.bean.ThongKeResult;
import javafx.application.Platform;
import java.io.File;
import java.net.URL;

public class ThongKeBridge2 {
    private LuongNhanVienController controller;
    private ThongKeView2 view;

    // Tương tự, đường dẫn tới folder html
    private final String htmlBase = "src/com/MediStaffManager/view/quanLyLuong/html/";

    public ThongKeBridge2(LuongNhanVienController controller, ThongKeView2 view) {
        this.controller = controller;
        this.view = view;
    }

    public void loadPage(String page) {
        File f = new File(htmlBase + page);
        try {
            URL url = f.toURI().toURL();
            System.out.println("[ThongKeBridge] Đang load: " + url.toExternalForm());

            Platform.runLater(() -> view.getWebEngine().load(url.toExternalForm()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Thống kê theo tháng
    public String thongKeTheoThang(String thangNam) {
        try {
            ThongKeResult tk = controller.thongKeTheoThang(thangNam);
            return "[{"
                + "\"soNhanVien\":" + tk.getSoNhanVien() + ","
                + "\"luongTrungBinh\":" + tk.getLuongTrungBinh() + ","
                + "\"tongLuong\":" + tk.getTongLuong()
                + "}]";
        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }

    // Thống kê theo quý
    public String thongKeTheoQuy(int nam, int quy) {
        try {
            ThongKeResult tk = controller.thongKeTheoQuy(nam, quy);
            return "[{"
                + "\"soNhanVien\":" + tk.getSoNhanVien() + ","
                + "\"luongTrungBinh\":" + tk.getLuongTrungBinh() + ","
                + "\"tongLuong\":" + tk.getTongLuong()
                + "}]";
        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }

    // Thống kê theo năm
    public String thongKeTheoNam(int nam) {
        try {
            ThongKeResult tk = controller.thongKeTheoNam(nam);
            return "[{"
                + "\"soNhanVien\":" + tk.getSoNhanVien() + ","
                + "\"luongTrungBinh\":" + tk.getLuongTrungBinh() + ","
                + "\"tongLuong\":" + tk.getTongLuong()
                + "}]";
        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }
}
