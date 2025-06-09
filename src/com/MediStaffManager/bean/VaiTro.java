
package com.MediStaffManager.bean;

public enum VaiTro {
    QUAN_LY("Quản lý"),
    KE_TOAN("Kế toán");

    private final String displayName;

    VaiTro(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return this.displayName;
    }
    
    // Tùy chọn: một phương thức để chuyển đổi từ String trong DB sang Enum
    public static VaiTro fromString(String text) {
        for (VaiTro b : VaiTro.values()) {
            if (b.displayName.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Không có vai trò nào ứng với giá trị: " + text);
    }
}