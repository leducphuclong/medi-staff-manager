/**
 * quanLyTaiKhoan.js
 * PHIÊN BẢN SỬA LỖI HOÀN CHỈNH: Khôi phục chức năng Thêm/Sửa và giữ lại chức năng Xóa đã hoạt động.
 */

// === CÁC BIẾN TOÀN CỤC ===
// Form chính
let modal, tenDangNhapInput, matKhauInput, matKhauNhapLaiInput, vaiTroSelect, employeeSelect;
let formTitle, saveButton, accountTableBody;
let matKhauHelpText, matKhauRequiredStar, matKhauNhapLaiRequiredStar;
// Hộp thoại xác nhận xóa tùy chỉnh
let confirmDeleteModal, confirmDeleteMessage, confirmDeleteBtnOk, confirmDeleteBtnCancel;

let allEmployees = [];
let currentMode = 'add';

// === KHỞI TẠO ===
document.addEventListener('DOMContentLoaded', () => {
    setTimeout(() => {
        if (window.bridge && typeof window.bridge.getQuanLyTaiKhoanBridge === 'function') {
            window.bridge.log('QuanLyTaiKhoan bridge đã sẵn sàng. Bắt đầu khởi tạo trang.');
            initializePage();
        } else {
            showNotification('Lỗi nghiêm trọng: Không thể kết nối tới Java.', 'error');
        }
    }, 1000);
});

function initializePage() {
    // Lấy các phần tử của form chính
    modal = document.getElementById('accountFormModal');
    tenDangNhapInput = document.getElementById('tenDangNhap');
    matKhauInput = document.getElementById('matKhau');
    matKhauNhapLaiInput = document.getElementById('matKhauNhapLai');
    vaiTroSelect = document.getElementById('vaiTro');
    employeeSelect = document.getElementById('employeeSelect');
    formTitle = document.getElementById('formTitle');
    saveButton = document.getElementById('saveButton');
    accountTableBody = document.querySelector('#accountTable tbody');
    matKhauHelpText = document.getElementById('matKhauHelp');
    matKhauRequiredStar = document.getElementById('matKhauRequiredStar');
    matKhauNhapLaiRequiredStar = document.getElementById('matKhauNhapLaiRequiredStar');
    
    // Lấy các phần tử của hộp thoại xác nhận xóa
    confirmDeleteModal = document.getElementById('confirmDeleteModal');
    confirmDeleteMessage = document.getElementById('confirmDeleteMessage');
    confirmDeleteBtnOk = document.getElementById('confirmDeleteBtnOk');
    confirmDeleteBtnCancel = document.getElementById('confirmDeleteBtnCancel');

    // Gán sự kiện cho nút "Hủy" trong hộp thoại xác nhận
    if (confirmDeleteBtnCancel) {
        confirmDeleteBtnCancel.addEventListener('click', () => {
            confirmDeleteModal.style.display = 'none';
        });
    }
    
    // Tải dữ liệu ban đầu
    loadEmployeesAndThenAccounts();
    loadRoles();
}

// === HÀM TRỢ GIÚP ===
function getBridge() {
    return window.bridge.getQuanLyTaiKhoanBridge();
}

/**
 * Hàm hiển thị hộp thoại xác nhận tùy chỉnh.
 * @param {string} message - Tin nhắn cần hiển thị.
 * @param {function} onConfirmCallback - Hàm sẽ được gọi khi người dùng bấm "Xác Nhận".
 */
function showCustomConfirm(message, onConfirmCallback) {
    if (!confirmDeleteModal) {
        console.error("Lỗi: Không tìm thấy confirmDeleteModal trong HTML!");
        return;
    }
    confirmDeleteMessage.textContent = message;
    confirmDeleteModal.style.display = 'flex';

    confirmDeleteBtnOk.onclick = () => {
        confirmDeleteModal.style.display = 'none';
        onConfirmCallback();
    };
}

// === CÁC HÀM TẢI DỮ LIỆU ===
async function loadEmployeesAndThenAccounts() {
    await loadEmployeesForSelection();
    loadAccounts();
}

async function loadEmployeesForSelection() {
    try {
        const employeesJson = await getBridge().getAllEmployeesForSelection();
        allEmployees = JSON.parse(employeesJson);
        employeeSelect.innerHTML = '<option value="">-- Chọn một nhân viên --</option>';
        allEmployees.forEach(emp => {
            const option = document.createElement('option');
            option.value = emp.idNhanVien;
            option.textContent = `${emp.hoTen} (ID: ${emp.idNhanVien})`;
            employeeSelect.appendChild(option);
        });
    } catch (e) {
        showNotification('Lỗi tải danh sách nhân viên: ' + e.message, 'error');
        if (window.bridge && window.bridge.log) window.bridge.log('JS Error in loadEmployeesForSelection: ' + e);
    }
}

function loadRoles() {
    try {
        const rolesJson = getBridge().getAllVaiTro();
        const roles = JSON.parse(rolesJson);
        vaiTroSelect.innerHTML = '';
        roles.forEach(role => {
            const option = document.createElement('option');
            option.value = role;
            option.textContent = role;
            vaiTroSelect.appendChild(option);
        });
    } catch (e) {
        showNotification('Lỗi tải danh sách vai trò: ' + e.message, 'error');
        if (window.bridge && window.bridge.log) window.bridge.log('JS Error in loadRoles: ' + e);
    }
}

function loadAccounts() {
    try {
        const accountsJson = getBridge().getAllTaiKhoan();
        if (!accountsJson) {
            displayAccounts([]);
            return;
        }
        const accounts = JSON.parse(accountsJson);
        displayAccounts(accounts);
    } catch (e) {
        showNotification('Lỗi xử lý dữ liệu tài khoản: ' + e.message, 'error');
        if (window.bridge && window.bridge.log) window.bridge.log(`[JS ERROR] Lỗi trong hàm loadAccounts(): ${e.toString()}`);
    }
}

function displayAccounts(accounts) {
    accountTableBody.innerHTML = '';
    if (!accounts || accounts.length === 0) {
        accountTableBody.innerHTML = '<tr><td colspan="4" style="text-align: center; padding: 20px; color: #64748b;">Không có tài khoản nào.</td></tr>';
        return;
    }
    accounts.forEach(account => {
        const employee = allEmployees.find(emp => emp.idNhanVien === account.idNhanVien);
        const employeeName = employee ? employee.hoTen : 'Không rõ';
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${account.tenDangNhap}</td>
            <td>${employeeName} (ID: ${account.idNhanVien || 'N/A'})</td>
            <td>${account.vaiTro}</td>
            <td class="action-buttons-cell">
                <button class="btn btn-primary action-btn" onclick="showEditForm('${account.tenDangNhap}')"><i class="fas fa-edit"></i> Sửa</button>
                <button class="btn btn-danger action-btn" onclick="confirmDeleteAccountFromTable('${account.tenDangNhap}')"><i class="fas fa-trash"></i> Xóa</button>
            </td>
        `;
        accountTableBody.appendChild(row);
    });
}

// === CÁC HÀM FORM & MODAL ===
function showFormModal() { modal.classList.add('show'); }
function hideFormModal() { modal.classList.remove('show'); resetForm(); }

function resetForm() {
    tenDangNhapInput.value = '';
    matKhauInput.value = '';
    matKhauNhapLaiInput.value = '';
    vaiTroSelect.selectedIndex = 0;
    employeeSelect.selectedIndex = 0;
    tenDangNhapInput.readOnly = false;
    employeeSelect.disabled = false;
}

function showAddForm() {
    currentMode = 'add';
    resetForm();
    formTitle.textContent = 'Thêm Tài Khoản Mới';
    saveButton.textContent = 'Lưu';
    matKhauHelpText.style.display = 'none';
    matKhauRequiredStar.style.display = 'inline';
    matKhauNhapLaiRequiredStar.style.display = 'inline';
    showFormModal();
}

function showEditForm(tenDangNhap) {
    currentMode = 'edit';
    resetForm();
    try {
        const accountJson = getBridge().getTaiKhoanByTenDangNhap(tenDangNhap);
        if (!accountJson || accountJson === 'null') {
            showNotification(`Không tìm thấy tài khoản: ${tenDangNhap}`, 'error');
            return;
        }
        const account = JSON.parse(accountJson);
        formTitle.textContent = 'Cập nhật Tài Khoản';
        saveButton.textContent = 'Cập nhật';
        tenDangNhapInput.value = account.tenDangNhap;
        tenDangNhapInput.readOnly = true;
        vaiTroSelect.value = account.vaiTro;
        employeeSelect.value = account.idNhanVien;
        matKhauInput.value = '';
        matKhauNhapLaiInput.value = '';
        matKhauHelpText.style.display = 'block';
        matKhauRequiredStar.style.display = 'none';
        matKhauNhapLaiRequiredStar.style.display = 'none';
        showFormModal();
    } catch (e) {
        showNotification('Lỗi tải thông tin tài khoản: ' + e.message, 'error');
        if (window.bridge && window.bridge.log) window.bridge.log('JS Error in showEditForm: ' + e);
    }
}

// === CÁC HÀM XỬ LÝ NGHIỆP VỤ (CRUD) ===

function saveAccount() {
    const tenDangNhap = tenDangNhapInput.value.trim();
    const matKhau = matKhauInput.value;
    const matKhauNhapLai = matKhauNhapLaiInput.value;
    const vaiTro = vaiTroSelect.value;
    const idNhanVien = employeeSelect.value;

    if (!tenDangNhap) { showNotification('Tên đăng nhập không được để trống.', 'warning'); return; }
    if (currentMode === 'add' && !idNhanVien) { showNotification('Bạn phải chọn một nhân viên.', 'warning'); return; }
    if (currentMode === 'add' && !matKhau) { showNotification('Mật khẩu không được để trống khi thêm mới.', 'warning'); return; }
    if (matKhau && matKhau !== matKhauNhapLai) { showNotification('Mật khẩu và Nhập lại mật khẩu không khớp.', 'warning'); return; }

    const taiKhoanData = {
        tenDangNhap: tenDangNhap,
        matKhau: matKhau,
        vaiTro: vaiTro,
        idNhanVien: parseInt(idNhanVien, 10),
        passwordChanged: matKhau !== ''
    };
    const jsonData = JSON.stringify(taiKhoanData);

    try {
        const result = (currentMode === 'add') ? getBridge().addTaiKhoan(jsonData) : getBridge().updateTaiKhoan(jsonData);
        if (result.startsWith('Success:')) {
            showNotification(result.substring(8), 'success');
            hideFormModal();
            loadAccounts();
        } else {
            showNotification(result.substring(6), 'error');
        }
    } catch (e) {
        showNotification('Đã xảy ra lỗi không xác định khi lưu.', 'error');
        if (window.bridge && window.bridge.log) window.bridge.log('JS Error in saveAccount: ' + e);
    }
}

function confirmDeleteAccountFromTable(tenDangNhap) {
    const message = `Bạn có chắc muốn xóa tài khoản "${tenDangNhap}" không?`;
    const deleteAction = () => {
        try {
            window.bridge.log(`[JS] Người dùng đã xác nhận xóa. Gọi Java để xóa tài khoản: ${tenDangNhap}`);
            const result = getBridge().deleteTaiKhoan(tenDangNhap);
            window.bridge.log(`[JS] Kết quả từ Java: "${result}"`);

            if (result && result.startsWith('Success:')) {
                showNotification(result.substring(8), 'success');
                loadAccounts();
            } else {
                const errorMessage = result ? result.substring(6) : "Lỗi không xác định.";
                showNotification(errorMessage, 'error');
            }
        } catch (e) {
            showNotification('Lỗi phía client khi thực hiện xóa.', 'error');
            window.bridge.log(`[JS EXCEPTION] Lỗi khi đang xóa: ${e.toString()}`);
        }
    };
    showCustomConfirm(message, deleteAction);
}

// === HÀM THÔNG BÁO ===
let notificationTimeout;
function showNotification(message, type = 'info') {
    const notification = document.getElementById('notifications');
    if (!notification) return;
    if (notificationTimeout) clearTimeout(notificationTimeout);
    notification.textContent = message;
    notification.className = `notification notification-${type}`;
    notification.classList.add('show');
    notificationTimeout = setTimeout(() => {
        notification.classList.remove('show');
    }, 3000);
}