package com.MediStaffManager.dao;

import java.sql.*;
import java.util.*;
import com.MediStaffManager.bean.NhanVien;
import com.MediStaffManager.utils.DBConnection;

public class NhanVienDAO {
	private Connection connection;

	// Constructor để thiết lập kết nối cơ sở dữ liệu
	public NhanVienDAO() {
		this.connection = DBConnection.connect();
	}

	// Phương thức lấy tất cả nhân viên, bao gồm cả thông tin chức vụ và phòng ban
	public List<NhanVien> layToanBoNhanVien() {
		List<NhanVien> employees = new ArrayList<>();
		String query = "SELECT nv.IDNhanVien, nv.CCCD, nv.HoTen, nv.Sdt, nv.Email, nv.GioiTinh, nv.NgaySinh, "
				+ "nv.IDChucVu, nv.IDPhongBan, cv.TenChucVu, pb.TenPhongBan " + "FROM nhan_vien nv "
				+ "JOIN chuc_vu cv ON nv.IDChucVu = cv.IDChucVu "
				+ "JOIN phong_ban pb ON nv.IDPhongBan = pb.IDPhongBan";

		try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				int idNhanVien = rs.getInt("IDNhanVien");
				String cccd = rs.getString("CCCD");
				String hoTen = rs.getString("HoTen");
				String sdt = rs.getString("Sdt");
				String email = rs.getString("Email");
				String gioiTinh = rs.getString("GioiTinh");
				String ngaySinh = rs.getString("NgaySinh");
				int idChucVu = rs.getInt("IDChucVu");
				int idPhongBan = rs.getInt("IDPhongBan");
				String tenChucVu = rs.getString("TenChucVu");
				String tenPhongBan = rs.getString("TenPhongBan");

				// Tạo đối tượng NhanVien và thêm vào danh sách
				NhanVien nhanVien = new NhanVien(idNhanVien, cccd, hoTen, sdt, email, gioiTinh, ngaySinh, idChucVu,
						idPhongBan, tenChucVu, tenPhongBan);
				employees.add(nhanVien);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employees;
	}

	// Phương thức xóa nhân viên
	public boolean xoaNhanVien(int idNhanVien) {
		String query = "DELETE FROM nhan_vien WHERE IDNhanVien = ?";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, idNhanVien);
			return stmt.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean xoaNhieuNhanVien(List<Integer> danhSachIdNhanVien) {
		if (danhSachIdNhanVien == null || danhSachIdNhanVien.isEmpty()) return false;
		StringBuilder query = new StringBuilder("DELETE FROM nhan_vien WHERE IDNhanVien IN (");
		for (int i = 0; i < danhSachIdNhanVien.size(); i++) {
			query.append("?");
			if (i < danhSachIdNhanVien.size() - 1) query.append(",");
		}
		query.append(")");
		try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
			for (int i = 0; i < danhSachIdNhanVien.size(); i++) {
				stmt.setInt(i + 1, danhSachIdNhanVien.get(i));
			}
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// phương thức xóa 1 nhân viên trong phòng ban
	public boolean xoaNhanVienTrongPhongBan(int idNhanVien, int idPhongBan) {
		String query = "DELETE nv FROM nhan_vien nv " + "JOIN phong_ban pb ON nv.IDPhongBan = pb.IDPhongBan "
				+ "WHERE nv.IDNhanVien = ? AND pb.IDPhongBan = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, idNhanVien);
			stmt.setInt(2, idPhongBan);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// Phương thức lấy thông tin một nhân viên theo ID
	public NhanVien getNhanVienById(int idNhanVien) {
		String query = "SELECT nv.IDNhanVien, nv.CCCD, nv.HoTen, nv.Sdt, nv.Email, nv.GioiTinh, nv.NgaySinh, "
				+ "nv.IDChucVu, nv.IDPhongBan, cv.TenChucVu, pb.TenPhongBan " + "FROM nhan_vien nv "
				+ "JOIN chuc_vu cv ON nv.IDChucVu = cv.IDChucVu "
				+ "JOIN phong_ban pb ON nv.IDPhongBan = pb.IDPhongBan " + "WHERE nv.IDNhanVien = ?";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, idNhanVien);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					String cccd = rs.getString("CCCD");
					String hoTen = rs.getString("HoTen");
					String sdt = rs.getString("Sdt");
					String email = rs.getString("Email");
					String gioiTinh = rs.getString("GioiTinh");
					String ngaySinh = rs.getString("NgaySinh");
					int idChucVu = rs.getInt("IDChucVu");
					int idPhongBan = rs.getInt("IDPhongBan");
					String tenChucVu = rs.getString("TenChucVu");
					String tenPhongBan = rs.getString("TenPhongBan");

					return new NhanVien(idNhanVien, cccd, hoTen, sdt, email, gioiTinh, ngaySinh, idChucVu, idPhongBan,
							tenChucVu, tenPhongBan);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Phương thức lấy thông tin tất cả nhân viên theo phòng ban
	public List<NhanVien> layNhanVienTheoPhongBan(String tenPhongBan) {
		List<NhanVien> employees = new ArrayList<>();
		String query = "SELECT nv.IDNhanVien, nv.CCCD, nv.HoTen, nv.Sdt, nv.Email, nv.GioiTinh, nv.NgaySinh, "
				+ "nv.IDChucVu, nv.IDPhongBan, cv.TenChucVu, pb.TenPhongBan " + "FROM nhan_vien nv "
				+ "JOIN chuc_vu cv ON nv.IDChucVu = cv.IDChucVu "
				+ "JOIN phong_ban pb ON nv.IDPhongBan = pb.IDPhongBan " + "WHERE pb.TenPhongBan = ?";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, tenPhongBan);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					int idNhanVien = rs.getInt("IDNhanVien");
					String cccd = rs.getString("CCCD");
					String hoTen = rs.getString("HoTen");
					String sdt = rs.getString("Sdt");
					String email = rs.getString("Email");
					String gioiTinh = rs.getString("GioiTinh");
					String ngaySinh = rs.getString("NgaySinh");
					int idChucVu = rs.getInt("IDChucVu");
					int idPhongBan = rs.getInt("IDPhongBan");
					String tenChucVu = rs.getString("TenChucVu");
					String tenPhongBanResult = rs.getString("TenPhongBan");

					NhanVien nhanVien = new NhanVien(idNhanVien, cccd, hoTen, sdt, email, gioiTinh, ngaySinh, idChucVu,
							idPhongBan, tenChucVu, tenPhongBanResult);

					employees.add(nhanVien);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employees;
	}

	// Phương thức xóa tất cả nhân viên trong 1 phòng ban
	public boolean xoaTatCaNhanVienTrongPhongBan(String tenPhongBan) {
		String query = "DELETE nv FROM nhan_vien nv " + "JOIN phong_ban pb ON nv.IDPhongBan = pb.IDPhongBan "
				+ "WHERE pb.TenPhongBan = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, tenPhongBan);
			return stmt.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}

	public boolean chuyenPhongBan(List<Integer> danhSachIdNhanVien, int idPhongBanMoi) {
		String query = "UPDATE nhan_vien SET IDPhongBan = ? WHERE IDNhanVien = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			for (int idNhanVien : danhSachIdNhanVien) {
				stmt.setInt(1, idPhongBanMoi);
				stmt.setInt(2, idNhanVien);
				stmt.addBatch();
			}
			int[] results = stmt.executeBatch();
			for (int result : results) {
				if (result <= 0) {
					return false;
				}
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public List<NhanVien> layNhanVienTheoIdPhongBan(int idPhongBan) {
		List<NhanVien> danhSach = new ArrayList<>();
		String query = "SELECT nv.IDNhanVien, nv.CCCD, nv.HoTen, nv.Sdt, nv.Email, nv.GioiTinh, nv.NgaySinh, "
				+ "nv.IDChucVu, nv.IDPhongBan, cv.TenChucVu, pb.TenPhongBan " + "FROM nhan_vien nv "
				+ "JOIN chuc_vu cv ON nv.IDChucVu = cv.IDChucVu "
				+ "JOIN phong_ban pb ON nv.IDPhongBan = pb.IDPhongBan " + "WHERE nv.IDPhongBan = ?";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, idPhongBan);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					int idNhanVien = rs.getInt("IDNhanVien");
					String cccd = rs.getString("CCCD");
					String hoTen = rs.getString("HoTen");
					String sdt = rs.getString("Sdt");
					String email = rs.getString("Email");
					String gioiTinh = rs.getString("GioiTinh");
					String ngaySinh = rs.getString("NgaySinh");
					int idChucVu = rs.getInt("IDChucVu");
					String tenChucVu = rs.getString("TenChucVu");
					String tenPhongBan = rs.getString("TenPhongBan");

					NhanVien nv = new NhanVien(idNhanVien, cccd, hoTen, sdt, email, gioiTinh, ngaySinh, idChucVu,
							idPhongBan, tenChucVu, tenPhongBan);
					danhSach.add(nv);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("In ra nhanvien dayyyyyyyyyyyyyyyyyyyyy: ");
		for (NhanVien nv : danhSach) {
			System.out.println(nv.toString());
		}

		return danhSach;
	}

}
