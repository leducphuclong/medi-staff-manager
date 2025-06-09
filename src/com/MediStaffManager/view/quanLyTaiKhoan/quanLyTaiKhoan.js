/**
 * quanLyTaiKhoan.js
 * Logic for the account management interface.
 */
 
// This object allows JS to call Java methods. It will be set by the JavaFX application.
let javaBridge; 
 
// DOM Elements
let modal, tenDangNhapInput, matKhauInput, matKhauNhapLaiInput, vaiTroSelect, employeeSelect;
let formTitle, saveButton, deleteButtonInModal, accountTableBody;
let matKhauHelpText, matKhauRequiredStar, matKhauNhapLaiRequiredStar;
 
// Global cache for employees to avoid repeated lookups
let allEmployees = [];
 
// State
let currentMode = 'add'; // Can be 'add' or 'edit'
 
// This event listener waits for the HTML document to be fully loaded.
document.addEventListener('DOMContentLoaded', () => {
    // We poll for the javaBridge object because it's injected by JavaFX after the page loads.
    const checkBridgeInterval = setInterval(() => {
        if (window.javaBridge) {
            clearInterval(checkBridgeInterval);
            javaBridge = window.javaBridge;
            initializePage();
        }
    }, 100); // Check every 100ms
});
 
/**
 * Initializes the page by assigning DOM elements and loading initial data.
 */
function initializePage() {
    // Assign DOM elements to variables for easy access
    modal = document.getElementById('accountFormModal');
    tenDangNhapInput = document.getElementById('tenDangNhap');
    matKhauInput = document.getElementById('matKhau');
    matKhauNhapLaiInput = document.getElementById('matKhauNhapLai');
    vaiTroSelect = document.getElementById('vaiTro');
    employeeSelect = document.getElementById('employeeSelect');
    formTitle = document.getElementById('formTitle');
    saveButton = document.getElementById('saveButton');
    deleteButtonInModal = document.getElementById('deleteButtonInModal');
    accountTableBody = document.querySelector('#accountTable tbody');
    matKhauHelpText = document.getElementById('matKhauHelp');
    matKhauRequiredStar = document.getElementById('matKhauRequiredStar');
    matKhauNhapLaiRequiredStar = document.getElementById('matKhauNhapLaiRequiredStar');
 
    // Set current year in the footer
    document.getElementById('currentYear').textContent = new Date().getFullYear();
 
    // Initial data load from Java backend
    loadEmployeesAndThenAccounts();
    loadRoles();
}
 
/**
 * Chain data loading to ensure employees are loaded before accounts are rendered.
 */
async function loadEmployeesAndThenAccounts() {
    await loadEmployeesForSelection(); // Wait for employees to be loaded
    loadAccounts(); // Then load and render accounts
}
 
// --- Data Loading Functions ---
 
/**
 * Fetches all employees to populate the "link to employee" dropdown and caches them.
 */
async function loadEmployeesForSelection() {
    try {
        const employeesJson = await javaBridge.getAllEmployeesForSelection();
        allEmployees = JSON.parse(employeesJson); // Cache the list
 
        employeeSelect.innerHTML = '<option value="">-- Chọn một nhân viên --</option>';
        allEmployees.forEach(emp => {
            const option = document.createElement('option');
            // Use properties from the Employee.java bean
            option.value = emp.idNhanVien;
            option.textContent = `${emp.hoTen} (ID: ${emp.idNhanVien})`;
            employeeSelect.appendChild(option);
        });
    } catch (e) {
        showNotification('Lỗi tải danh sách nhân viên: ' + e.message, 'error');
        javaBridge.log('JS Error in loadEmployeesForSelection: ' + e);
    }
}
 
/**
 * Fetches the valid roles from Java and populates the role dropdown.
 */
function loadRoles() {
    try {
        const rolesJson = javaBridge.getAllVaiTro();
        const roles = JSON.parse(rolesJson);
 
        vaiTroSelect.innerHTML = ''; // Clear existing options
        roles.forEach(role => {
            const option = document.createElement('option');
            option.value = role;
            option.textContent = role;
            vaiTroSelect.appendChild(option);
        });
    } catch (e) {
        showNotification('Lỗi tải danh sách vai trò: ' + e.message, 'error');
        javaBridge.log('JS Error in loadRoles: ' + e);
    }
}
 
/**
 * Fetches all accounts from the Java backend and displays them in the table.
 */
function loadAccounts() {
    try {
        const accountsJson = javaBridge.getAllTaiKhoan();
        const accounts = JSON.parse(accountsJson);
        displayAccounts(accounts);
    } catch (e) {
        showNotification('Lỗi tải danh sách tài khoản: ' + e.message, 'error');
        javaBridge.log('JS Error in loadAccounts: ' + e);
    }
}
 
/**
 * Renders the list of accounts into the HTML table, including action buttons.
 * @param {Array} accounts - The list of account objects.
 */
function displayAccounts(accounts) {
    accountTableBody.innerHTML = ''; // Clear table before rendering
    if (!accounts || accounts.length === 0) {
        accountTableBody.innerHTML = '<tr><td colspan="4" style="text-align: center; padding: 20px; color: #64748b;">Không có tài khoản nào.</td></tr>';
        return;
    }
 
    accounts.forEach(account => {
        // Find employee name from the cached list
        const employee = allEmployees.find(emp => emp.idNhanVien === account.idNhanVien);
        const employeeName = employee ? employee.hoTen : 'Không rõ';
 
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${account.tenDangNhap}</td>
            <td>${employeeName} (ID: ${account.idNhanVien})</td>
            <td>${account.vaiTro}</td>
            <td class="action-buttons-cell">
                <button class="btn btn-primary action-btn" onclick="showEditForm('${account.tenDangNhap}')">
                    <i class="fas fa-edit"></i> Sửa
                </button>
                <button class="btn btn-danger action-btn" onclick="confirmDeleteAccountFromTable('${account.tenDangNhap}')">
                    <i class="fas fa-trash"></i> Xóa
                </button>
            </td>
        `;
        accountTableBody.appendChild(row);
    });
}
 
// --- Modal and Form Handling ---
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
    deleteButtonInModal.classList.add('hidden');
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
        const accountJson = javaBridge.getTaiKhoanByTenDangNhap(tenDangNhap);
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
        employeeSelect.disabled = true;
 
        matKhauInput.value = ''; 
        matKhauNhapLaiInput.value = '';
        matKhauHelpText.style.display = 'block';
        matKhauRequiredStar.style.display = 'none';
        matKhauNhapLaiRequiredStar.style.display = 'none';
 
        deleteButtonInModal.classList.remove('hidden');
        showFormModal();
    } catch (e) {
        showNotification('Lỗi tải thông tin tài khoản: ' + e.message, 'error');
        javaBridge.log('JS Error in showEditForm: ' + e);
    }
}
 
// --- CRUD Actions ---
 
/**
 * Gathers data from the form, validates it, and sends it to Java for saving.
 */
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
        matKhauNhapLai: matKhauNhapLai,
        passwordChanged: matKhau !== '' 
    };
    
    const jsonData = JSON.stringify(taiKhoanData);
    let result;
 
    try {
        result = (currentMode === 'add') ? javaBridge.addTaiKhoan(jsonData) : javaBridge.updateTaiKhoan(jsonData);
 
        if (result.startsWith('Success:')) {
            showNotification(result.substring(8), 'success');
            hideFormModal();
            loadAccounts();
        } else {
            showNotification(result.substring(6), 'error');
        }
    } catch (e) {
        showNotification('Đã xảy ra lỗi không xác định khi lưu.', 'error');
        javaBridge.log('JS Error in saveAccount: ' + e);
    }
}
 
/**
 * Confirms and executes deletion directly from the table row.
 * @param {string} tenDangNhap - The username of the account to delete.
 */
function confirmDeleteAccountFromTable(tenDangNhap) {
    // Log ngay khi hàm được gọi
    javaBridge.log(`[JS] Nút xóa được bấm cho tài khoản: '${tenDangNhap}'`);

    if (confirm(`Bạn có chắc chắn muốn xóa tài khoản '${tenDangNhap}'? Hành động này không thể hoàn tác.`)) {
        javaBridge.log(`[JS] Người dùng đã xác nhận xóa.`);
        try {
            // Log ngay trước khi gọi Java
            javaBridge.log(`[JS] Đang gọi javaBridge.deleteTaiKhoan('${tenDangNhap}')...`);
            
            // Gọi hàm Java và nhận kết quả
            const result = javaBridge.deleteTaiKhoan(tenDangNhap); 
 
            // Log kết quả trả về từ Java
            javaBridge.log(`[JS] Kết quả trả về từ Java: "${result}"`);

            if (result && result.startsWith('Success:')) {
                javaBridge.log(`[JS] Xóa thành công. Hiển thị thông báo và tải lại bảng.`);
                showNotification(result.substring(8), 'success');
                loadAccounts(); // Tải lại bảng để thấy thay đổi
            } else {
                javaBridge.log(`[JS] Xóa thất bại. Hiển thị thông báo lỗi.`);
                // Hiển thị toàn bộ thông báo lỗi trả về
                const errorMessage = result ? result.substring(6) : "Lỗi không xác định từ backend.";
                showNotification(errorMessage, 'error');
            }
        } catch (e) {
            javaBridge.log('[JS] Đã xảy ra lỗi JavaScript trong khối try-catch: ' + e.toString());
            showNotification('Lỗi phía client khi thực hiện xóa.', 'error');
        }
    } else {
        javaBridge.log(`[JS] Người dùng đã hủy thao tác xóa.`);
    }
}
 
/**
 * Handles the delete button inside the modal form.
 */
function confirmDeleteAccountFromModal() {
    const tenDangNhap = tenDangNhapInput.value;
    confirmDeleteAccountFromTable(tenDangNhap);
    hideFormModal();
}
 
// --- UI Utility Functions ---
 
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
    }, 4000);
} 