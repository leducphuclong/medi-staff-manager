package com.MediStaffManager.controller;
import com.MediStaffManager.bean.TaiKhoan;
import com.MediStaffManager.dao.TaiKhoanDAO;
import com.MediStaffManager.bo.TaiKhoanBO;

import java.util.List;

public class TaiKhoanController {
    private TaiKhoanBO taiKhoanBO;

    // Constructor khởi tạo DAO để xử lý các nghiệp vụ liên quan đến TaiKhoan
    public TaiKhoanController() {
        this.taiKhoanBO = new TaiKhoanBO();
    }

    /**
     * Gọi hàm login từ DAO để kiểm tra thông tin đăng nhập.
     * @param tenDangNhap Tên đăng nhập
     * @param matKhau Mật khẩu (chưa hash)
     * @return Đối tượng TaiKhoan nếu đăng nhập thành công, null nếu thất bại.
     */
    public TaiKhoan login(String tenDangNhap, String matKhau) {
        return taiKhoanBO.login(tenDangNhap, matKhau);
    }

    /**
     * Gọi hàm register từ DAO để tạo mới một tài khoản.
     * @param taiKhoan Đối tượng TaiKhoan chứa thông tin đăng ký (TenDangNhap, MatKhau, VaiTro)
     * @return true nếu đăng ký thành công, false nếu thất bại (ví dụ: TenDangNhap đã tồn tại).
     */
    public boolean register(TaiKhoan taiKhoan) {
        return taiKhoanBO.register(taiKhoan);
    }

    /**
     * Gọi hàm lấy tài khoản theo ID từ DAO.
     * @param idNhanVien ID của tài khoản cần lấy.
     * @return Đối tượng TaiKhoan nếu tìm thấy, null nếu không có.
     */
    public TaiKhoan getTaiKhoanById(int idNhanVien) {
        return taiKhoanBO.getTaiKhoanById(idNhanVien);
    }

    /**
     * Gọi hàm lấy toàn bộ danh sách tài khoản từ DAO.
     * @return List<TaiKhoan> chứa tất cả tài khoản (không gồm MatKhau).
     */
    public List<TaiKhoan> getAllTaiKhoan() {
        return taiKhoanBO.getAllTaiKhoan();
    }

    /**
     * Gọi hàm cập nhật VaiTro của tài khoản từ DAO.
     * @param taiKhoan Đối tượng TaiKhoan chứa IDNhanVien và VaiTro mới.
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean updateVaiTro(TaiKhoan taiKhoan) {
        return taiKhoanBO.updateVaiTro(taiKhoan);
    }

    /**
     * Gọi hàm xóa tài khoản theo ID từ DAO.
     * @param idNhanVien ID của tài khoản cần xóa.
     * @return true nếu xóa thành công, false nếu thất bại.
     */
    public boolean deleteTaiKhoan(int idNhanVien) {
        return taiKhoanBO.deleteTaiKhoan(idNhanVien);
    }

    // Nếu cần thêm các phương thức khác (ví dụ: thay đổi mật khẩu, tìm kiếm tài khoản theo VaiTro, v.v.)
    // bạn có thể triển khai tương tự và gọi thẳng xuống TaiKhoanDAO.
}
