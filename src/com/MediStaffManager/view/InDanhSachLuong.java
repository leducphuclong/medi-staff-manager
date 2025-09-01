package com.MediStaffManager.view;
import com.MediStaffManager.bean.LuongNhanVien;
import com.MediStaffManager.controller.LuongNhanVienController;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class InDanhSachLuong extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private LuongNhanVienController controller;
    private JDateChooser dateChooser;


    public InDanhSachLuong() {
        controller = new LuongNhanVienController();
        setTitle("Danh sách lương nhân viên");
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Tạo bảng hiển thị dữ liệu
        String[] columnNames = {"ID Nhân viên","Họ Tên", "ID Chức vụ", "Tháng/Năm", "Lương thu nhập", "Thưởng", "Phụ cấp", "Tăng ca", "Tổng lương"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Panel điều khiển phía dưới
        JPanel bottomPanel = new JPanel();
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("MM/yyyy"); // Định dạng hiển thị tháng/năm
        JButton btnXem = new JButton("Xem");
        JButton btnThem = new JButton("Thêm");
        JButton btnXoa = new JButton("Xóa");
        JButton btnCapNhat = new JButton("Cập nhật"); 
        JButton btnTimKiem = new JButton("Tìm kiếm");

        bottomPanel.add(new JLabel("Chọn tháng/năm:"));
        bottomPanel.add(dateChooser);
        bottomPanel.add(btnXem);
        bottomPanel.add(btnThem);
        bottomPanel.add(btnXoa);
        bottomPanel.add(btnCapNhat);
        bottomPanel.add(btnTimKiem);

        add(bottomPanel, BorderLayout.SOUTH);

        // Xử lý sự kiện
        btnXem.addActionListener(e -> loadData());
        btnThem.addActionListener(e -> moFormThemLuong());
        btnXoa.addActionListener(e -> xoaLuong());
        btnCapNhat.addActionListener(e -> capNhatLuong());
        btnTimKiem.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Nhập ID Nhân Viên:");

            if (input != null && !input.trim().isEmpty()) {
                try {
                    int idNhanVien = Integer.parseInt(input);

                    
                    timKiemLuongTheoIDNhanVien(idNhanVien);

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "ID Nhân Viên không hợp lệ.");
                }
            }
        });
       }
    
    private void loadData() {
        tableModel.setRowCount(0);
        Date selectedDate = dateChooser.getDate();
        
        if (selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tháng/năm.");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
        String thangNam = sdf.format(selectedDate);

        List<LuongNhanVien> list = controller.layLuongTheoThang(thangNam);

        for (LuongNhanVien l : list) {
            int idChucVu = l.getIdChucVu();   
            int idNhanVien = l.getIdNhanVien();
            // Lấy HeSoLuong cho từng Chức vụ
            BigDecimal heSoLuong = controller.layHeSoLuongTheoChucVu(idChucVu);
            if (heSoLuong == null) {
                System.err.println("Không tìm thấy HeSoLuong cho IDChucVu: " + idChucVu);
                heSoLuong = BigDecimal.ZERO; // Giá trị mặc định nếu không tìm thấy
            }
            
            String hoTen = controller.layHoTenTheoIDNhanVien(idNhanVien); 
            if (hoTen == null) {
            	System.err.println("Không tìm thấy HoTen cho IDNhanVien: " + idNhanVien);
            }

            // Xử lý null an toàn cho các giá trị
            BigDecimal luongThuNhap = l.getLuongThuNhap() != null ? l.getLuongThuNhap() : BigDecimal.ZERO;
            BigDecimal thuong = l.getThuong() != null ? l.getThuong() : BigDecimal.ZERO;
            BigDecimal phuCap = l.getPhuCap() != null ? l.getPhuCap() : BigDecimal.ZERO;
            BigDecimal tangCa = l.getTangCa() != null ? l.getTangCa() : BigDecimal.ZERO;

            // Tính tổng lương
            BigDecimal tongLuong = luongThuNhap.multiply(heSoLuong)
                                    .add(thuong)
                                    .add(phuCap)
                                    .add(tangCa);

            // Thêm dữ liệu vào bảng
            Object[] row = {
                  l.getIdNhanVien(), hoTen, l.getIdChucVu(), l.getThangNam(),
                luongThuNhap, thuong, phuCap, tangCa, tongLuong
            };

            tableModel.addRow(row);
        }
    }

    private void moFormThemLuong() {
        JDialog dialog = new JDialog(this, "Thêm bảng lương", true);
        dialog.setLayout(new GridLayout(10, 2, 5, 5));
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);

        JTextField tfIDNhanVien = new JTextField();
        JTextField tfIDChucVu = new JTextField();
        JDateChooser dateChooserThangNam = new JDateChooser(); 
        dateChooserThangNam.setDateFormatString("MM/yyyy");
        JTextField tfLuongThuNhap = new JTextField();
        JTextField tfThuong = new JTextField();
        JTextField tfPhuCap = new JTextField();
        JTextField tfTangCa = new JTextField();

        JButton btnLuu = new JButton("Lưu");
        JButton btnHuy = new JButton("Hủy");

        dialog.add(new JLabel("ID Nhân viên:")); dialog.add(tfIDNhanVien);
        dialog.add(new JLabel("ID Chức vụ:")); dialog.add(tfIDChucVu);
        dialog.add(new JLabel("Tháng/Năm:")); dialog.add(dateChooserThangNam);
        dialog.add(new JLabel("Lương thu nhập:")); dialog.add(tfLuongThuNhap);
        dialog.add(new JLabel("Thưởng:")); dialog.add(tfThuong);
        dialog.add(new JLabel("Phụ cấp:")); dialog.add(tfPhuCap);
        dialog.add(new JLabel("Tăng ca:")); dialog.add(tfTangCa);
        dialog.add(btnLuu); dialog.add(btnHuy);

        btnLuu.addActionListener(e -> {
            try {
                if (tfIDNhanVien.getText().isEmpty() || tfIDChucVu.getText().isEmpty() ||
                    tfLuongThuNhap.getText().isEmpty() || tfThuong.getText().isEmpty() ||
                    tfPhuCap.getText().isEmpty() || tfTangCa.getText().isEmpty()) {

                    JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đầy đủ thông tin.");
                    return;
                }

                // Lấy dữ liệu từ form
                int idNhanVien = Integer.parseInt(tfIDNhanVien.getText());
                int idChucVu = Integer.parseInt(tfIDChucVu.getText());

                Date selectedDate = dateChooserThangNam.getDate();
                if (selectedDate == null) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng chọn Tháng/Năm.");
                    return;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
                String thangNam = sdf.format(selectedDate);

                BigDecimal luongThuNhap = new BigDecimal(tfLuongThuNhap.getText());
                BigDecimal thuong = new BigDecimal(tfThuong.getText());
                BigDecimal phuCap = new BigDecimal(tfPhuCap.getText());
                BigDecimal tangCa = new BigDecimal(tfTangCa.getText());

                // ====== Lấy HeSoLuong từ chuc_vu ======
                BigDecimal heSoLuong = controller.layHeSoLuongTheoChucVu(idChucVu);
                if (heSoLuong == null) {
                    JOptionPane.showMessageDialog(dialog, "ID Chức vụ không tồn tại.");
                    return;
                }

                // Tính Tổng lương
                BigDecimal tongLuong = luongThuNhap.multiply(heSoLuong)
                                        .add(thuong)
                                        .add(phuCap)
                                        .add(tangCa);

                // Tạo đối tượng LuongNhanVien
                LuongNhanVien lnv = new LuongNhanVien(0, idChucVu, idNhanVien, thangNam,
                        luongThuNhap, thuong, phuCap, tangCa, tongLuong);

                // Gọi controller để thêm lương
                boolean success = controller.themLuong(lnv);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Thêm lương thành công!");
                    dialog.dispose();
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Thêm thất bại.");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi nhập dữ liệu: Vui lòng kiểm tra các trường số.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage());
            }
        });

        btnHuy.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void xoaLuong() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int idLuong = (int) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = controller.xoaLuong(idLuong);
                if (success) {
                    tableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this, "Xóa thành công.");
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần xóa.");
        }
    }

    private void timKiemLuongTheoIDNhanVien(int idNhanVien) {
        List<LuongNhanVien> list = controller.timKiemLuongTheoIDNhanVien(idNhanVien);

        if (list.isEmpty()) {
            int option = JOptionPane.showConfirmDialog(
                this,
                "Nhân viên này chưa có bảng lương. Bạn có muốn thêm bảng lương không?",
                "Thêm bảng lương",
                JOptionPane.YES_NO_OPTION
            );

            if (option == JOptionPane.YES_OPTION) {
                moFormThemLuong();
            }

        } else {
            // Xóa dữ liệu cũ
            tableModel.setRowCount(0);
           
            String HoTen = controller.layHoTenTheoIDNhanVien(idNhanVien);
            for (LuongNhanVien l : list) {
                Object[] row = {
                    l.getIdNhanVien(),
                    HoTen,
                    l.getIdChucVu(),
                    l.getThangNam(),
                    l.getLuongThuNhap(),
                    l.getThuong(),
                    l.getPhuCap(),
                    l.getTangCa(),
                    l.getTongLuong()
                };

                tableModel.addRow(row);
            }
        }
    }

    private void capNhatLuong() {
        String input = JOptionPane.showInputDialog(this, "Nhập ID Nhân Viên muốn cập nhật lương:");

        if (input != null && !input.trim().isEmpty()) {
            try {
                int idNhanVien = Integer.parseInt(input);
                List<LuongNhanVien> list = controller.timKiemLuongTheoIDNhanVien(idNhanVien);

                if (list.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nhân viên này chưa có bảng lương.");
                } else if (list.size() == 1) {
                    // Nếu chỉ có một bản ghi, mở form cập nhật ngay
                    moFormCapNhatLuong(list.get(0));
                } else {
                    // Nếu có nhiều bản ghi, cho phép chọn Tháng/Năm
                    String[] thangNamOptions = list.stream()
                        .map(LuongNhanVien::getThangNam)
                        .toArray(String[]::new);
                    String selectedThangNam = (String) JOptionPane.showInputDialog(
                        this,
                        "Nhân viên có nhiều bảng lương. Chọn Tháng/Năm:",
                        "Chọn bảng lương",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        thangNamOptions,
                        thangNamOptions[0]
                    );

                    if (selectedThangNam != null) {
                        LuongNhanVien selectedLuong = list.stream()
                            .filter(l -> l.getThangNam().equals(selectedThangNam))
                            .findFirst()
                            .orElse(null);
                        if (selectedLuong != null) {
                            moFormCapNhatLuong(selectedLuong);
                        }
                    }
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID Nhân Viên không hợp lệ.");
            }
        }
    }
    
    private void moFormCapNhatLuong(LuongNhanVien luong) {
        JDialog dialog = new JDialog(this, "Cập nhật lương", true);
        dialog.setLayout(new GridLayout(10, 2, 5, 5));
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);

        JTextField tfIDNhanVien = new JTextField(String.valueOf(luong.getIdNhanVien()));
        tfIDNhanVien.setEnabled(false); // Không cho phép chỉnh sửa IDNhanVien
        
        JTextField tfIDChucVu = new JTextField(String.valueOf(luong.getIdChucVu()));
        JTextField tfThangNam = new JTextField(luong.getThangNam());
        tfThangNam.setEnabled(false);
        JTextField tfLuongThuNhap = new JTextField(luong.getLuongThuNhap().toString());
        JTextField tfThuong = new JTextField(luong.getThuong().toString());
        JTextField tfPhuCap = new JTextField(luong.getPhuCap().toString());
        JTextField tfTangCa = new JTextField(luong.getTangCa().toString());

        JButton btnLuu = new JButton("Lưu");
        JButton btnHuy = new JButton("Hủy");

        dialog.add(new JLabel("ID Nhân viên:")); dialog.add(tfIDNhanVien);
        dialog.add(new JLabel("ID Chức vụ:")); dialog.add(tfIDChucVu);
        dialog.add(new JLabel("Tháng/Năm:")); dialog.add(tfThangNam);
        dialog.add(new JLabel("Lương thu nhập:")); dialog.add(tfLuongThuNhap);
        dialog.add(new JLabel("Thưởng:")); dialog.add(tfThuong);
        dialog.add(new JLabel("Phụ cấp:")); dialog.add(tfPhuCap);
        dialog.add(new JLabel("Tăng ca:")); dialog.add(tfTangCa);
        dialog.add(btnLuu); dialog.add(btnHuy);

        btnLuu.addActionListener(e -> {
            try {
                int idChucVu = Integer.parseInt(tfIDChucVu.getText());
                String thangNam = tfThangNam.getText();
                BigDecimal luongThuNhap = new BigDecimal(tfLuongThuNhap.getText());
                BigDecimal thuong = new BigDecimal(tfThuong.getText());
                BigDecimal phuCap = new BigDecimal(tfPhuCap.getText());
                BigDecimal tangCa = new BigDecimal(tfTangCa.getText());
                BigDecimal heSoLuong = controller.layHeSoLuongTheoChucVu(idChucVu);

                // Kiểm tra Tháng/Năm
                if (thangNam == null || thangNam.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Tháng/Năm không được để trống.");
                    return;
                }

                // Tạo đối tượng LuongNhanVien để cập nhật
                LuongNhanVien updatedLuong = new LuongNhanVien(
                	0,
                    idChucVu,
                    luong.getIdNhanVien(),  // Không thay đổi IDNhanVien
                    thangNam,
                    luongThuNhap,
                    thuong,
                    phuCap,
                    tangCa,
                    BigDecimal.ZERO  // TongLuong sẽ được tính lại trong DAO
                );

                boolean success = controller.capNhatLuong(updatedLuong);

                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Cập nhật thành công!");
                    dialog.dispose();
                    loadData();  // Tải lại dữ liệu sau khi cập nhật
                } else {
                    JOptionPane.showMessageDialog(dialog, "Cập nhật thất bại.");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Dữ liệu không hợp lệ: " + ex.getMessage());
            }
        });

        btnHuy.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

////
//   public static void main(String[] args) {
//       SwingUtilities.invokeLater(() -> {
//           InDanhSachLuong view = new InDanhSachLuong();
//           view.setVisible(true);
//       });
//   }
}