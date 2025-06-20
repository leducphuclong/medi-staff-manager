/**
 * nhanVien.js
 * Chứa logic cho trang chi tiết nhân viên của một phòng ban.
 * File này chỉ hoạt động khi được tải trên trang chi tiết nhân viên.
 */

/**
 * Hàm khởi tạo chính cho trang chi tiết nhân viên.
 */
function initializeEmployeePage() {
    // Lấy thông tin từ URL
    const urlParams = new URLSearchParams(window.location.search);
    const tenPhongBan = urlParams.get('tenPhongBan');
    const idPhongBan = urlParams.get('idPhongBan');

    // Cập nhật tiêu đề trang
    // SỬA ĐỔI Ở ĐÂY: Sử dụng selector chung hơn để tìm thẻ h1 trong header
    const titleElement = document.querySelector('.page-header h1'); 
    if (titleElement && tenPhongBan) {
        // decodeURIComponent để hiển thị đúng các ký tự tiếng Việt có dấu
        titleElement.textContent = `Nhân viên phòng: ${decodeURIComponent(tenPhongBan)}`;
    }

    // Kiểm tra nếu idPhongBan tồn tại rồi mới thực hiện tiếp
    if (idPhongBan) {
        loadEmployeeTableData(idPhongBan);
        setupEmployeePageEventListeners(idPhongBan);
    } else {
        // Hiển thị lỗi nếu không tìm thấy id phòng ban
        const tbody = document.querySelector("#employeeDetailTable tbody");
        tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;">Lỗi: Không tìm thấy ID của phòng ban.</td></tr>';
        showNotificationModal("Không thể tải thông tin nhân viên do thiếu ID phòng ban.", "Lỗi", "error");
    }
}

/**
 * Tải và hiển thị danh sách nhân viên của một phòng ban cụ thể.
 * @param {string|number} idPhongBan ID của phòng ban.
 */
function loadEmployeeTableData(idPhongBan) {
    const tbody = document.querySelector("#employeeDetailTable tbody");
    tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;">Đang tải dữ liệu...</td></tr>';
    
    if (!idPhongBan) {
        tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;">ID phòng ban không hợp lệ.</td></tr>';
        return;
    }

    try {
        const bridge = window.bridge.getQuanLyPhongBanBridge();
        const employeesJson = bridge.layNhanVienTheoIdPhongBan(parseInt(idPhongBan, 10));
        const employees = JSON.parse(employeesJson);
        tbody.innerHTML = ''; // Xóa dòng "đang tải"

        if (!employees || employees.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;">Phòng ban này chưa có nhân viên.</td></tr>';
            return;
        }

        employees.forEach(emp => {
            const row = tbody.insertRow();
            // Thêm data-id để có thể lấy thông tin nếu cần
            row.setAttribute('data-id', emp.idNhanVien); 
            row.innerHTML = `
                <td style="text-align: center;"><input type="checkbox" class="row-checkbox" value="${emp.idNhanVien}"></td>
                <td>${emp.idNhanVien}</td>
                <td>${emp.hoTen || "Chưa có"}</td>
                <td>${emp.sdt || "Chưa có"}</td>
                <td>${emp.email || "Chưa có"}</td>
                <td>${emp.tenChucVu || "Chưa có"}</td>
            `;
        });
    } catch (e) {
        console.error("JS: Lỗi khi tải dữ liệu nhân viên:", e);
        showNotificationModal("Lỗi khi tải dữ liệu nhân viên.", "Lỗi", "error");
        document.querySelector("#employeeDetailTable tbody").innerHTML = '<tr><td colspan="6" style="text-align:center;">Lỗi xử lý dữ liệu.</td></tr>';
    }
    
    const selectAllCheckbox = document.getElementById('select-all-employees');
    if (selectAllCheckbox) selectAllCheckbox.checked = false;
}

/**
 * Gắn các sự kiện cho các nút và input trên trang nhân viên.
 * @param {string|number} currentDeptId ID của phòng ban hiện tại.
 */
function setupEmployeePageEventListeners(currentDeptId) {
    const reattachListener = (elementId, handler, event = 'click') => {
        const element = document.getElementById(elementId);
        if (element) {
            // Clone và replace để xóa listener cũ, tránh gọi nhiều lần
            const newElement = element.cloneNode(true);
            element.parentNode.replaceChild(newElement, element);
            newElement.addEventListener(event, handler);
        } else {
            console.error(`Không tìm thấy element với ID: ${elementId}`);
        }
    };
    
    // Gắn sự kiện cho các nút hành động chính
    reattachListener('btnDeleteSelected', handleDeleteSelectedEmployees);
    reattachListener('btnTransferSelected', () => handleTransferSelectedEmployees(currentDeptId));

    // Logic cho checkbox "Chọn tất cả"
    const selectAllCheckbox = document.getElementById('select-all-employees');
    const tableBody = document.querySelector('#employeeDetailTable tbody');

    if (selectAllCheckbox && tableBody) {
        selectAllCheckbox.addEventListener('change', function() {
            tableBody.querySelectorAll('.row-checkbox').forEach(cb => {
                cb.checked = this.checked;
            });
        });

        tableBody.addEventListener('change', function(e) {
            if (e.target.classList.contains('row-checkbox')) {
                const allCheckboxes = tableBody.querySelectorAll('.row-checkbox');
                const checkedCount = tableBody.querySelectorAll('.row-checkbox:checked').length;
                selectAllCheckbox.checked = allCheckboxes.length > 0 && allCheckboxes.length === checkedCount;
            }
        });
    }
}

/**
 * Lấy danh sách ID của các nhân viên được chọn dưới dạng chuỗi CSV.
 * @returns {string} Chuỗi ID phân cách bởi dấu phẩy, hoặc chuỗi rỗng.
 */
function getSelectedEmployeeIdsCsv() {
    const ids = Array.from(document.querySelectorAll('#employeeDetailTable .row-checkbox:checked'))
                     .map(cb => cb.value);
    return ids.join(',');
}

/**
 * Xử lý logic xóa các nhân viên đã chọn.
 */
function handleDeleteSelectedEmployees() {
    const csvIds = getSelectedEmployeeIdsCsv();
    if (!csvIds) {
        showNotificationModal("Vui lòng chọn ít nhất một nhân viên để xóa.", "Cảnh báo", "warning");
        return;
    }

    showCustomConfirm(
        'Bạn có chắc chắn muốn xóa vĩnh viễn các nhân viên đã chọn?',
        'Xác nhận xóa nhân viên',
        () => { // onOk callback
            try {
                const bridge = window.bridge.getQuanLyPhongBanBridge();
                const success = bridge.xoaNhieuNhanVien(csvIds);
                if (success) {
                    showNotificationModal("Xóa nhân viên thành công!", "Thành công", "success", () => {
                        const urlParams = new URLSearchParams(window.location.search);
                        const idPhongBan = urlParams.get('idPhongBan');
                        loadEmployeeTableData(idPhongBan);
                    });
                } else {
                    showNotificationModal("Xóa thất bại. Vui lòng thử lại.", "Lỗi", "error");
                }
            } catch (e) {
                console.error("JS: Lỗi khi gọi bridge.xoaNhieuNhanVien:", e);
                showNotificationModal("Đã xảy ra lỗi trong quá trình xóa.", "Lỗi", "error");
            }
        }
    );
}

/**
 * Xử lý logic chuyển phòng ban cho các nhân viên đã chọn.
 * @param {string|number} currentDeptId ID của phòng ban hiện tại để loại khỏi danh sách chuyển đến.
 */
function handleTransferSelectedEmployees(currentDeptId) {
    const csvIds = getSelectedEmployeeIdsCsv();
    if (!csvIds) {
        showNotificationModal("Vui lòng chọn ít nhất một nhân viên để chuyển.", "Cảnh báo", "warning");
        return;
    }
    
    const transferModal = document.getElementById('transferEmployeeModal');
    const departmentSelect = document.getElementById('transfer-department-select');
    if (!transferModal || !departmentSelect) {
        showNotificationModal("Lỗi giao diện: Không tìm thấy modal chuyển phòng ban.", "Lỗi", "error");
        return;
    }

    // Tải danh sách phòng ban để chọn
    try {
        const bridge = window.bridge.getQuanLyPhongBanBridge();
        const departmentsJson = bridge.layDanhSachPhongBan();
        const departments = JSON.parse(departmentsJson);
        
        departmentSelect.innerHTML = '<option value="">-- Chọn phòng ban mới --</option>'; // Reset
        departments.forEach(dept => {
            if (dept.idPhongBan != currentDeptId) {
                 const option = new Option(dept.tenPhongBan, dept.idPhongBan);
                 departmentSelect.appendChild(option);
            }
        });
    } catch (e) {
        console.error("JS: Lỗi khi tải danh sách phòng ban để chuyển:", e);
        showNotificationModal("Lỗi khi tải danh sách phòng ban.", "Lỗi", "error");
        return;
    }

    transferModal.style.display = 'flex';
    
    const confirmBtn = document.getElementById('btn-confirm-transfer');
    const cancelBtn = document.getElementById('btn-cancel-transfer');

    const newConfirmBtn = confirmBtn.cloneNode(true);
    confirmBtn.parentNode.replaceChild(newConfirmBtn, confirmBtn);

    newConfirmBtn.addEventListener('click', () => {
        const newDepartmentId = departmentSelect.value;
        if (!newDepartmentId) {
            showNotificationModal('Vui lòng chọn phòng ban muốn chuyển đến!', 'Cảnh báo', 'warning');
            return;
        }
        
        transferModal.style.display = 'none';
        
        const selectedDeptName = departmentSelect.options[departmentSelect.selectedIndex].text;
        showCustomConfirm(
            `Bạn có chắc chắn muốn chuyển các nhân viên đã chọn sang phòng ban <strong>"${selectedDeptName}"</strong>?`,
            'Xác nhận Chuyển Phòng Ban',
            () => { // onOk callback
                try {
                    const bridge = window.bridge.getQuanLyPhongBanBridge();
                    const success = bridge.chuyenPhongBan(csvIds, parseInt(newDepartmentId, 10));
                    if (success) {
                        showNotificationModal('Chuyển phòng ban thành công!', 'Thành công', 'success', () => {
                            loadEmployeeTableData(currentDeptId);
                        });
                    } else {
                        showNotificationModal('Chuyển phòng ban thất bại!', 'Lỗi', 'error');
                    }
                } catch (e) {
                    console.error("JS: Lỗi khi gọi bridge.chuyenPhongBan:", e);
                    showNotificationModal("Đã xảy ra lỗi trong quá trình chuyển.", "Lỗi", "error");
                }
            }
        );
    });

    const newCancelBtn = cancelBtn.cloneNode(true);
    cancelBtn.parentNode.replaceChild(newCancelBtn, cancelBtn);
    newCancelBtn.addEventListener('click', () => transferModal.style.display = 'none');
}