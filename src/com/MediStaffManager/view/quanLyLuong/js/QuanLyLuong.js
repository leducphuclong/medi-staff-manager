/**
 * Quản Lý Lương - JavaScript Logic (Refactored with consistent bridge structure)
 */

// --- Hằng số và Hàm tiện ích ---
const formatCurrency = (num) => num ? new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(num) : '0 ₫';

/**
 * Hàm "Gatekeeper" để truy cập đối tượng bridge của module Quản lý Lương.
 * Hàm này tuân theo cấu trúc mới: window.bridge.getQuanLyLuongBridge()
 * Nó kiểm tra sự tồn tại của các đối tượng cần thiết một cách an toàn.
 * @returns {object} Đối tượng bridge dành riêng cho Quản lý Lương.
 * @throws {Error} Nếu cấu trúc bridge không hợp lệ hoặc không tồn tại.
 */
function getBridge() {
    // 1. Kiểm tra xem đối tượng `bridge` chính có tồn tại và có hàm cần thiết không.
    if (window.bridge && typeof window.bridge.getQuanLyLuongBridge === 'function') {
        const quanLyLuongBridge = window.bridge.getQuanLyLuongBridge();
        
        // 2. Kiểm tra xem hàm có trả về một đối tượng hợp lệ không.
        if (quanLyLuongBridge) {
            return quanLyLuongBridge;
        } else {
            // Lỗi này xảy ra nếu hàm getQuanLyLuongBridge() được gọi nhưng trả về null/undefined.
            throw new Error("Lỗi Backend: Hàm getQuanLyLuongBridge() không trả về một đối tượng hợp lệ.");
        }
    } else {
        // Lỗi này xảy ra nếu đối tượng `window.bridge` chưa được khởi tạo.
        throw new Error("Lỗi kết nối tới Backend: Đối tượng 'bridge' hoặc hàm 'getQuanLyLuongBridge' không tồn tại.");
    }
}

// --- Khởi tạo ---
document.addEventListener('DOMContentLoaded', () => {
    let attempts = 0;
    const maxAttempts = 50; // Chờ khoảng 5 giây
    const interval = setInterval(() => {
        // SỬA Ở ĐÂY: Chờ đợi đúng cấu trúc bridge mới
        if (window.bridge && typeof window.bridge.getQuanLyLuongBridge === 'function') {
            clearInterval(interval);
            initializeApplication();
        } else {
            attempts++;
            if (attempts > maxAttempts) {
                clearInterval(interval);
                console.error("Lỗi nghiêm trọng: Không tìm thấy cấu trúc 'window.bridge.getQuanLyLuongBridge()' sau khi chờ.");
                document.body.innerHTML = '<h1 style="color:red; text-align:center; margin-top: 50px;">Lỗi kết nối tới Backend Java. Vui lòng tải lại trang.</h1>';
            }
        }
    }, 100);
});

// --- Tất cả các hàm bên dưới giữ nguyên vì chúng đã sử dụng getBridge() ---

function initializeApplication() {
    try {
        getBridge().log("JavaScript application initialized successfully.");

        document.getElementById('thangNamFilter').value = new Date().toISOString().slice(0, 7);
        document.getElementById('luongForm').addEventListener('submit', handleFormSubmit);
        document.getElementById('luongTableBody').addEventListener('click', handleTableActions);
        
        ['thuong', 'phuCap', 'tangCa'].forEach(id => {
            document.getElementById(id).addEventListener('input', updateTongLuongInModal);
        });

        document.getElementById('confirmOkBtn').addEventListener('click', () => onConfirm(true));
        document.getElementById('confirmCancelBtn').addEventListener('click', () => onConfirm(false));
        document.getElementById('confirmCloseBtn').addEventListener('click', () => onConfirm(false));
        
        loadDepartments();
        loadData();

    } catch (e) {
        console.error("Lỗi khi khởi tạo ứng dụng:", e);
        showNotification(`Lỗi nghiêm trọng khi khởi tạo: ${e.message}`, 'error');
    }
}

function loadDepartments() {
    try {
        const jsonString = getBridge().layTatCaPhongBan();
        const departments = JSON.parse(jsonString);
        const select = document.getElementById('phongBanFilter');
        
        while (select.options.length > 1) {
            select.remove(1);
        }

        departments.forEach(dept => {
            const option = document.createElement('option');
            option.value = dept.idPhongBan;
            option.textContent = dept.tenPhongBan;
            select.appendChild(option);
        });
    } catch (e) {
        console.error("Lỗi tải danh sách phòng ban:", e);
        showNotification(`Lỗi tải phòng ban: ${e.message}`, 'error');
    }
}

function loadData() {
    const thangNam = document.getElementById('thangNamFilter').value;
    const idPhongBan = document.getElementById('phongBanFilter').value;

    if (!thangNam) {
        showNotification("Vui lòng chọn tháng để xem dữ liệu.", "warning");
        return;
    }

    try {
        const bridge = getBridge();
        bridge.log(`JS: Yêu cầu dữ liệu cho tháng ${thangNam}, phòng ban ID: ${idPhongBan}`);
        let jsonString;
        
        if (idPhongBan && parseInt(idPhongBan, 10) > 0) {
            jsonString = bridge.layLuongTheoThangVaPhongBan(thangNam, parseInt(idPhongBan, 10));
        } else {
            jsonString = bridge.layLuongTheoThang(thangNam);
        }
        
        const data = JSON.parse(jsonString);
        renderTable(data);

    } catch (e) {
        console.error("Lỗi tải dữ liệu lương:", e);
        showNotification(`Lỗi tải dữ liệu: ${e.message}`, 'error');
    }
}

function renderTable(data) {
    const tbody = document.getElementById('luongTableBody');
    tbody.salaryData = data || []; 

    if (tbody.salaryData.length === 0) {
        tbody.innerHTML = '<tr><td colspan="10" style="text-align:center; padding: 30px; color: var(--text-muted);">Không tìm thấy dữ liệu phù hợp.</td></tr>';
        return;
    }

    tbody.innerHTML = tbody.salaryData.map((item, index) => {
        const hanhDongButton = `<button class="btn btn-primary btn-sm" data-action="edit"><i class="fas fa-pencil-alt"></i> Sửa</button>
                                <button class="btn btn-danger btn-sm" data-action="delete" ${item.idLuong ? '' : 'disabled'}><i class="fas fa-trash-alt"></i> Xóa</button>`;
        return `
            <tr data-index="${index}">
                <td>${item.idNhanVien || 'N/A'}</td>
                <td>${item.hoTen || 'N/A'}</td>
                <td>${item.tenChucVu || 'N/A'}</td>
                <td>${item.thangNam || 'N/A'}</td>
                <td>${formatCurrency(item.luongCoBanChuan)}</td>
                <td>${formatCurrency(item.thuong)}</td>
                <td>${formatCurrency(item.phuCap)}</td>
                <td>${formatCurrency(item.tangCa)}</td>
                <td><strong>${formatCurrency(item.tongLuong)}</strong></td>
                <td class="action-buttons">${hanhDongButton}</td>
            </tr>`;
    }).join('');
}

function filterTable() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    const originalData = document.getElementById('luongTableBody').salaryData || [];
    
    if (!searchTerm) {
        renderTable(originalData);
        return;
    }

    const filteredData = originalData.filter(item => {
        const hoTen = item.hoTen ? item.hoTen.toLowerCase() : '';
        const idNhanVien = item.idNhanVien ? String(item.idNhanVien) : '';
        return hoTen.includes(searchTerm) || idNhanVien.includes(searchTerm);
    });
    renderTable(filteredData);
}

function handleTableActions(event) {
    const button = event.target.closest('button');
    if (!button || button.disabled) return;

    const row = button.closest('tr');
    const itemData = document.getElementById('luongTableBody').salaryData[row.dataset.index];
    const action = button.dataset.action;
    
    if (!itemData.idLuong && (action === 'edit' || action === 'delete')) {
         showNotification(`Nhân viên "${itemData.hoTen}" chưa có bản ghi lương để sửa/xóa.`, 'info');
         return;
    }

    if (action === 'edit') {
        openModal(itemData);
    } else if (action === 'delete') {
        showConfirm(
            `Bạn có chắc muốn xóa bản ghi lương tháng ${itemData.thangNam} của "${itemData.hoTen}"?`,
            () => { 
                try {
                    const result = getBridge().xoaLuong(itemData.idLuong); 
                    if (result) {
                        showNotification("Xóa lương thành công!", "success");
                        loadData();
                    } else {
                        showNotification("Xóa lương thất bại.", "error");
                    }
                } catch (e) {
                    showNotification(`Lỗi khi xóa: ${e.message}`, 'error');
                }
            }
        );
    }
}

function handleFormSubmit(event) {
    event.preventDefault();
    const luongData = {
        idLuong: parseInt(document.getElementById('idLuong').value),
        idNhanVien: parseInt(document.getElementById('idNhanVien').value),
        idChucVu: parseInt(document.getElementById('idChucVu').value),
        heSoLuong: parseFloat(document.getElementById('heSoLuong').value),
        thangNam: document.getElementById('thangNamFilter').value,
        luongCoBanChuan: parseFloat(document.getElementById('luongCoBanChuan').value),
        thuong: parseFloat(document.getElementById('thuong').value) || 0,
        phuCap: parseFloat(document.getElementById('phuCap').value) || 0,
        tangCa: parseFloat(document.getElementById('tangCa').value) || 0,
    };
    
    try {
        const result = getBridge().capNhatLuong(luongData.idLuong, JSON.stringify(luongData));
        if (result) {
            showNotification("Cập nhật lương thành công!", "success");
            closeModal();
            loadData();
        } else {
            showNotification("Cập nhật lương thất bại!", "error");
        }
    } catch(e) {
        showNotification(`Lỗi khi cập nhật: ${e.message}`, 'error');
    }
}

// ... các hàm modal và notification giữ nguyên ...
const luongModal = document.getElementById('luongModal');
const confirmModal = document.getElementById('confirmModal');
let confirmCallback = null;
function openModal(data){ /* ... */ }
function closeModal(){ /* ... */ }
function updateTongLuongInModal(){ /* ... */ }
function showConfirm(message, onOk){ /* ... */ }
function onConfirm(isOk){ /* ... */ }
function showNotification(message, type = 'info'){ /* ... */ }
// Gán hàm vào window để HTML gọi được
window.loadData = loadData;
window.filterTable = filterTable;
window.closeModal = closeModal;
window.showNotification = showNotification;
// (Đã rút gọn phần thân các hàm không đổi để tiết kiệm không gian)