// Hàm khởi tạo trang nhân viên
function initializeEmployeePage() {
	const urlParams = new URLSearchParams(window.location.search);
	const tenPhongBan = urlParams.get('tenPhongBan') ;
	
	
	bridge.log("Ten PHong ban: " + tenPhongBan);
	
	// Lấy thẻ h1 có class "department-title" trong header
	const titleElement = document.querySelector('h1.department-title');
	if (titleElement) {
	  titleElement.textContent = tenPhongBan;
	}
	
    loadEmployeeTableData(); // This will also call attachActionButtonsListeners
    const btnAddEmp = document.getElementById("btnAddEmployee");
    if (btnAddEmp) {
        const newBtnAddEmp = btnAddEmp.cloneNode(true);
        btnAddEmp.parentNode.replaceChild(newBtnAddEmp, btnAddEmp);
        // Assuming handleAddEmployee is defined elsewhere or will be added if needed for this button
        // newBtnAddEmp.addEventListener('click', handleAddEmployee); 
        bridge.getbridge.getQuanLyPhongBanBridge()().log("JS: Add employee button event listener can be attached here if handleAddEmployee is defined.");
    }
}

// Checkbox "Chọn tất cả"
document.getElementById('select-all').addEventListener('change', function() {
    const checked = this.checked;
    document.querySelectorAll('.row-checkbox').forEach(cb => {
        cb.checked = checked;
    });
});

// Checkbox từng dòng
document.getElementById('employeeDetailTable').addEventListener('change', function(e) {
    if (e.target.classList.contains('row-checkbox')) {
        const all = document.querySelectorAll('.row-checkbox');
        const checked = document.querySelectorAll('.row-checkbox:checked');
        document.getElementById('select-all').checked = all.length === checked.length;
    }
});

function getSelectedEmployeeIdsCsv() {
    const checkboxes = document.querySelectorAll('.row-checkbox:checked');
    bridge.log(`getSelectedEmployeeIdsCsv: Found ${checkboxes.length} checked checkboxes.`);
    const ids = Array.from(checkboxes).map(cb => {
        bridge.log(`getSelectedEmployeeIdsCsv: Checkbox value: '${cb.value}', checked: ${cb.checked}`);
        return cb.value;
    });
    const csv = ids.join(',');
    bridge.log(`getSelectedEmployeeIdsCsv: Returning CSV: '${csv}' (length: ${csv.length})`);
    return csv;
}

function attachActionButtonsListeners() {
    bridge.log("JS: Attaching action button listeners.");
    // Delete button
    const btnDelete = document.getElementById('btnDeleteSelectedEmployees');
	bridge.log("hiiiiiiiiiiiiiiiiiiiiiiiiiiiii")
    if (btnDelete) {
        const newBtnDelete = btnDelete.cloneNode(true);
        btnDelete.parentNode.replaceChild(newBtnDelete, btnDelete);
        newBtnDelete.addEventListener('click', handleDeleteSelectedEmployees);
    } else {
        bridge.getbridge.getQuanLyPhongBanBridge()().log("JS: Delete button (btnDeleteSelectedEmployees) not found.");
    }

    // Transfer button
    const btnTransfer = document.getElementById('btn-transfer-department');
    if (btnTransfer) {
        const newBtnTransfer = btnTransfer.cloneNode(true);
        btnTransfer.parentNode.replaceChild(newBtnTransfer, btnTransfer);
        newBtnTransfer.addEventListener('click', handleTransferSelectedEmployees);
    } else {
        bridge.getbridge.getQuanLyPhongBanBridge()().log("JS: Transfer button (btn-transfer-department) not found.");
    }
    
    // Gắn sự kiện cho các nút edit
    document.querySelectorAll('.btn-edit').forEach(button => {
        button.addEventListener('click', function(event) {
            event.preventDefault();
            event.stopPropagation();
            const employeeId = this.getAttribute('data-id');
            // Thêm logic để xử lý khi nhấn nút edit
            bridge.getbridge.getQuanLyPhongBanBridge()().log("Clicked edit button for employee ID: " + employeeId);
            // Gọi hàm xử lý sửa nhân viên - để phần này trống vì đang chỉ tập trung vào UI
        });
    });
    
    // Gắn sự kiện cho các nút delete
    document.querySelectorAll('.btn-danger').forEach(button => {
        button.addEventListener('click', function(event) {
            event.preventDefault();
            event.stopPropagation();
            const employeeId = this.getAttribute('data-id');
            // Thêm logic để xử lý khi nhấn nút delete
            bridge.getbridge.getQuanLyPhongBanBridge()().log("Clicked delete button for employee ID: " + employeeId);
            // Gọi hàm xử lý xóa nhân viên - để phần này trống vì đang chỉ tập trung vào UI
        });
    });
}

function handleDeleteSelectedEmployees() {
    bridge.getQuanLyPhongBanBridge().log("JS: handleDeleteSelectedEmployees called.");
    const csvIds = getSelectedEmployeeIdsCsv();
    bridge.getQuanLyPhongBanBridge().log(`handleDeleteSelectedEmployees: Raw csvIds = '${csvIds}', type = ${typeof csvIds}, length = ${csvIds ? csvIds.length : 'N/A'}`);

    if (!csvIds || csvIds.trim() === "") {
        bridge.getQuanLyPhongBanBridge().log("handleDeleteSelectedEmployees: Condition (!csvIds || csvIds.trim() === \"\") is TRUE. Calling showNotification.");
        showNotification("Chưa có nhân viên nào được chọn", "warning");
        return;
    } else {
        bridge.getQuanLyPhongBanBridge().log("handleDeleteSelectedEmployees: Condition (!csvIds || csvIds.trim() === \"\") is FALSE. Proceeding.");
    }

    showCustomConfirm(
        'Bạn có chắc chắn muốn xóa các nhân viên đã chọn?',
        'Xác nhận xóa nhân viên',
        function onOk() { // OK callback
			bridge.log("Xac nhan xoa")
            if (typeof window.bridge !== 'undefined' ) {
                try {
                    const ok = window.bridge.getQuanLyPhongBanBridge().xoaNhieuNhanVien(csvIds);
                    bridge.getQuanLyPhongBanBridge().log("JS: Bridge xoaNhieuNhanVien called with: " + csvIds + ", result: " + ok);
                    if (ok) {
                        // Use showNotification if available, otherwise alert
                        if (typeof showNotification === 'function') {
                            showNotification("Xóa thành công!", "success");
                        } else {
                            alert("Xóa thành công!");
                        }
                        loadEmployeeTableData(); // Reload table and re-attach listeners
                    } else {
                        if (typeof showNotification === 'function') {
                            showNotification("Xóa thất bại! Kiểm tra lại thông tin hoặc liên hệ quản trị viên.", "error");
                        } else {
                            alert("Xóa thất bại! Kiểm tra lại thông tin hoặc liên hệ quản trị viên.");
                        }
                    }
                } catch (e) {
                    bridge.getQuanLyPhongBanBridge().log("JS: Error calling xoaNhieuNhanVien: " + e.message);
                    if (typeof showNotification === 'function') {
                        showNotification("Đã xảy ra lỗi khi xóa nhân viên.", "error");
                    } else {
                        alert("Đã xảy ra lỗi khi xóa nhân viên.");
                    }
                }
            } else {
                if (typeof showNotification === 'function') {
                    showNotification("Chức năng xóa nhiều nhân viên không khả dụng (lỗi bridge).", "error");
                } else {
                    alert("Chức năng xóa nhiều nhân viên không khả dụng (lỗi bridge).");
                }
            }
        },
        function onCancel() { // Cancel callback
            bridge.getQuanLyPhongBanBridge().log("JS: User cancelled deletion of employees.");
            if (typeof showNotification === 'function') {
                showNotification("Đã hủy thao tác xóa nhân viên.", "info");
            }
        }
    );
}

function handleTransferSelectedEmployees() {
    bridge.getQuanLyPhongBanBridge().log("JS: handleTransferSelectedEmployees called.");
    const csvIds = getSelectedEmployeeIdsCsv();
    bridge.getQuanLyPhongBanBridge().log(`handleTransferSelectedEmployees: Raw csvIds = '${csvIds}', type = ${typeof csvIds}, length = ${csvIds ? csvIds.length : 'N/A'}`);

    if (!csvIds || csvIds.trim() === "") {
        bridge.getQuanLyPhongBanBridge().log("handleTransferSelectedEmployees: Condition (!csvIds || csvIds.trim() === \"\") is TRUE. Calling showNotification.");
        showNotification("Chưa có nhân viên nào được chọn", "warning");
        return;
    } else {
        bridge.getQuanLyPhongBanBridge().log("handleTransferSelectedEmployees: Condition (!csvIds || csvIds.trim() === \"\") is FALSE. Proceeding.");
    }

    const transferModal = document.getElementById('transferModal');
    const departmentSelect = document.getElementById('department-select');
    const btnConfirmTransferOriginal = document.getElementById('btn-confirm-transfer-action');
    const btnCancelTransferOriginal = document.getElementById('btn-cancel-transfer-action');
    const closeTransferModalButtonOriginal = document.querySelector('#transferModal .close-button'); // Ensure this selector is correct for your HTML

    if (!transferModal) {
        bridge.getQuanLyPhongBanBridge().log("JS Error: transferModal not found!");
        showNotification("Lỗi giao diện: Modal chuyển phòng ban không tìm thấy (ID: transferModal).", "error");
        return;
    }
    if (!departmentSelect) {
        bridge.getQuanLyPhongBanBridge().log("JS Error: department-select not found!");
        showNotification("Lỗi giao diện: Dropdown chọn phòng ban không tìm thấy (ID: department-select).", "error");
        return;
    }
    if (!btnConfirmTransferOriginal) {
        bridge.getQuanLyPhongBanBridge().log("JS Error: btn-confirm-transfer-action not found!");
        showNotification("Lỗi giao diện: Nút xác nhận chuyển không tìm thấy (ID: btn-confirm-transfer-action).", "error");
        return;
    }
    if (!btnCancelTransferOriginal) {
        bridge.getQuanLyPhongBanBridge().log("JS Error: btn-cancel-transfer-action not found!");
        showNotification("Lỗi giao diện: Nút hủy chuyển không tìm thấy (ID: btn-cancel-transfer-action).", "error");
        return;
    }
    if (!closeTransferModalButtonOriginal) {
        bridge.getQuanLyPhongBanBridge().log("JS Error: #transferModal .close-button not found!");
        showNotification("Lỗi giao diện: Nút đóng modal (X) không tìm thấy (selector: #transferModal .close-button).", "error");
        return;
    }

    // Populate department select
    if (typeof window.bridge.getQuanLyPhongBanBridge() !== 'undefined' && typeof window.bridge.getQuanLyPhongBanBridge().layDanhSachPhongBan === 'function') {
        try {
            const departmentsJson = window.bridge.getQuanLyPhongBanBridge().layDanhSachPhongBan();
            bridge.getQuanLyPhongBanBridge().log("JS: Danh sách phòng ban JSON: " + departmentsJson);
            const departments = JSON.parse(departmentsJson);
            departmentSelect.innerHTML = '<option value="">-- Chọn phòng ban --</option>'; // Reset

            if (departments && departments.length > 0) {
                departments.forEach(dept => {
                    const option = document.createElement('option');
                    option.value = dept.idPhongBan;
                    option.textContent = dept.tenPhongBan;
                    departmentSelect.appendChild(option);
                });
            } else {
                bridge.getQuanLyPhongBanBridge().log("JS: Không có phòng ban nào được trả về từ bridge.");
            }        } catch (e) {
            bridge.getQuanLyPhongBanBridge().log("JS: Lỗi khi parse JSON phòng ban: " + e.message);
            showNotification("Lỗi khi tải danh sách phòng ban.", "error");
            return; 
        }    } else {
        showNotification("Chức năng tải danh sách phòng ban không khả dụng (lỗi bridge).", "error");
        return; 
    }

    transferModal.style.display = 'flex'; // Changed from 'block' to 'flex' for proper centering

    const btnConfirmTransfer = btnConfirmTransferOriginal.cloneNode(true);
    btnConfirmTransferOriginal.parentNode.replaceChild(btnConfirmTransfer, btnConfirmTransferOriginal);

    const btnCancelTransfer = btnCancelTransferOriginal.cloneNode(true);
    btnCancelTransferOriginal.parentNode.replaceChild(btnCancelTransfer, btnCancelTransferOriginal);
	
    const closeTransferModalButton = closeTransferModalButtonOriginal.cloneNode(true);
    closeTransferModalButtonOriginal.parentNode.replaceChild(closeTransferModalButton, closeTransferModalButtonOriginal);

    let windowClickListener;   
	 function onConfirmTransferAction() {
        const newDepartmentId = departmentSelect.value;
        if (!newDepartmentId) {
            showNotification('Vui lòng chọn phòng ban mới!', 'warning');
            return;
        }

        // Đóng modal chọn phòng ban ngay sau khi phòng ban mới đã được chọn và trước khi hiển thị confirm cuối cùng.
        closeModalAction(); 

        const selectedDeptName = departmentSelect.options[departmentSelect.selectedIndex].text;
		        
        showCustomConfirm(
            `Bạn có chắc chắn muốn chuyển các nhân viên đã chọn sang phòng ban "${selectedDeptName}"?`,
            'Xác nhận chuyển phòng ban',
            function onOk() { // OK callback for transfer
                if (typeof window.bridge.getQuanLyPhongBanBridge() !== 'undefined' && typeof window.bridge.getQuanLyPhongBanBridge().chuyenPhongBan === 'function') {
                    try {
                        const success = window.bridge.getQuanLyPhongBanBridge().chuyenPhongBan(csvIds, parseInt(newDepartmentId, 10));
                        bridge.getQuanLyPhongBanBridge().log("JS: Bridge chuyenPhongBan called. Result: " + success);
                        if (success) {
                            if (typeof showNotification === 'function') {
                                showNotification('Chuyển phòng ban thành công!', 'success');
                            } else {
                                alert('Chuyển phòng ban thành công!');
                            }
                            loadEmployeeTableData(); 
                        } else {
                            if (typeof showNotification === 'function') {
                                showNotification('Chuyển phòng ban thất bại!', 'error');
                            } else {
                                alert('Chuyển phòng ban thất bại!');
                            }
                        }
                    } catch (e) {
                        bridge.getQuanLyPhongBanBridge().log("JS: Error calling chuyenPhongBan: " + e.message);
                        if (typeof showNotification === 'function') {
                            showNotification("Đã xảy ra lỗi khi chuyển phòng ban.", "error");
                        } else {
                            alert("Đã xảy ra lỗi khi chuyển phòng ban.");
                        }
                    }
                } else {
                    if (typeof showNotification === 'function') {
                        showNotification('Chức năng chuyển phòng ban không khả dụng (lỗi bridge).', 'error');
                    } else {
                        alert('Chức năng chuyển phòng ban không khả dụng (lỗi bridge).');
                    }
                }
            },
            function onCancel() { // Cancel callback for transfer
                bridge.getQuanLyPhongBanBridge().log("JS: User cancelled transfer of employees via custom confirm.");
                if (typeof showNotification === 'function') {
                    showNotification("Đã hủy thao tác chuyển phòng ban.", "info");
                }
            }
        );
    }

    function closeModalAction() {
        transferModal.style.display = 'none';
        btnConfirmTransfer.removeEventListener('click', onConfirmTransferAction);
        btnCancelTransfer.removeEventListener('click', closeModalAction);
        closeTransferModalButton.removeEventListener('click', closeModalAction);
        if (windowClickListener) {
            window.removeEventListener('click', windowClickListener);
        }
    }
    
    windowClickListener = function(event) {
        if (event.target === transferModal) {
            closeModalAction();
        }
    };

    btnConfirmTransfer.addEventListener('click', onConfirmTransferAction);
    btnCancelTransfer.addEventListener('click', closeModalAction);
    closeTransferModalButton.addEventListener('click', closeModalAction);
    window.addEventListener('click', windowClickListener);
}

// Hàm hiển thị modal xác nhận (copied from phongBan.js logic)
function showCustomConfirm(message, title, onOk, onCancel) {
    const modal = document.getElementById('customConfirmModal'); // Ensure this ID exists in your HTML for the confirm modal
    const confirmTitle = document.getElementById('customConfirmTitle'); // Ensure this ID exists
    const confirmMessage = document.getElementById('customConfirmMessage'); // Ensure this ID exists
    const btnOk = document.getElementById('customConfirmOk'); // Ensure this ID exists
    const btnCancel = document.getElementById('customConfirmCancel'); // Ensure this ID exists

    if (!modal || !confirmTitle || !confirmMessage || !btnOk || !btnCancel) {
        bridge.getQuanLyPhongBanBridge().log("JS Error: Custom confirm modal elements not found. Falling back to native confirm.");
        if (confirm(message)) {
            if (typeof onOk === 'function') onOk();
        } else {
            if (typeof onCancel === 'function') onCancel();
        }
        return;
    }

    confirmTitle.textContent = title || "Xác nhận";
    confirmMessage.textContent = message;

    // Clone buttons to remove previous listeners and attach new ones
    const newBtnOk = btnOk.cloneNode(true);
    btnOk.parentNode.replaceChild(newBtnOk, btnOk);

    const newBtnCancel = btnCancel.cloneNode(true);
    btnCancel.parentNode.replaceChild(newBtnCancel, btnCancel);

    function handleOk() {
        modal.style.display = 'none';
        newBtnOk.removeEventListener('click', handleOk);
        newBtnCancel.removeEventListener('click', handleCancel);
        if (typeof onOk === 'function') onOk();
    }
    function handleCancel() {
        modal.style.display = 'none';
        newBtnOk.removeEventListener('click', handleOk);
        newBtnCancel.removeEventListener('click', handleCancel);
        if (typeof onCancel === 'function') onCancel();
    }    newBtnOk.addEventListener('click', handleOk);
    newBtnCancel.addEventListener('click', handleCancel);

    modal.style.display = 'flex'; // Changed from 'block' to 'flex' for proper centering
}

// Function to show a custom warning alert using a modal
// function showCustomWarning(message) { ... }

function loadEmployeeTableData() {
	bridge.getQuanLyPhongBanBridge().log("get in...");
    if (
        typeof window.bridge.getQuanLyPhongBanBridge() === 'undefined' ||
        typeof window.bridge.getQuanLyPhongBanBridge().layNhanVienTheoIdPhongBan !== 'function'
    ) {
        console.warn("JS: quanLyNhanVienBridge.layDanhSachNhanVien chưa sẵn sàng.");
        const tbody = document.querySelector("#employeeTable tbody");
        if (tbody) {
            tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;">Lỗi kết nối (Java bridge).</td></tr>';
        }
        return;
    }

    try {
		const urlParams = new URLSearchParams(window.location.search);
		const idPhongBan = parseInt(urlParams.get('idPhongBan'), 10); 
		bridge.getQuanLyPhongBanBridge().log("chuan bi lay nhan vien:    " + idPhongBan);
        const employeesJson = window.bridge.getQuanLyPhongBanBridge().layNhanVienTheoIdPhongBan(idPhongBan);
		bridge.getQuanLyPhongBanBridge().log(employeesJson);
        const employees = JSON.parse(employeesJson);
        const tbody = document.querySelector("#employeeDetailTableContainer tbody");
        if (!tbody) {
			bridge.getQuanLyPhongBanBridge().log("Sai iddddddddddddddddddddddđ");
            console.error("JS: Không tìm thấy tbody cho #employeeTable.");
            return;
        }
        tbody.innerHTML = '';

        if (!employees || employees.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;">Không có nhân viên nào.</td></tr>';
            return;
        }

    		employees.forEach(emp => {
            if (
                typeof emp.idNhanVien === 'undefined' ||
                typeof emp.hoTen === 'undefined'
            ) {
                console.warn("JS: Dữ liệu nhân viên không hợp lệ từ Java:", emp);
                return;
            }
			const row = tbody.insertRow();

			// Ô checkbox (đầu tiên)
			const checkboxCell = row.insertCell();
			checkboxCell.style.textAlign = 'center';
			const checkbox = document.createElement('input');
			checkbox.type = 'checkbox';
			checkbox.className = 'row-checkbox';
			checkbox.value = emp.idNhanVien;
			checkboxCell.appendChild(checkbox);			// Các ô dữ liệu khác
			row.insertCell().textContent = emp.idNhanVien;
			row.insertCell().textContent = emp.hoTen;
			row.insertCell().textContent = emp.sdt || "N/A";
			row.insertCell().textContent = emp.email || "N/A";
			row.insertCell().textContent = emp.tenChucVu || "N/A";
			
			
        

        });
    } catch (e) {
        console.error("JS: Lỗi khi tải dữ liệu nhân viên:", e);
        const tbody = document.querySelector("#employeeTable tbody");
        if (tbody) {
            tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;">Lỗi xử lý dữ liệu nhân viên.</td></tr>';
        }
    }
    attachActionButtonsListeners(); // New: Attaches listeners for delete and transfer buttons
}

/**
 * Hiển thị thông báo dạng toast trong ứng dụng
 * @param {string} message - Nội dung thông báo
 * @param {string} type - Loại thông báo: 'success', 'error', 'warning', 'info'
 * @param {number} duration - Thời gian hiển thị thông báo (ms), mặc định là 3000ms
 */
function showNotification(message, type = 'info', duration = 3000) {
    bridge.getQuanLyPhongBanBridge().log(`JS: Showing notification - ${type}: ${message}`);
    
    // Xóa thông báo cũ nếu có
    const existingNotification = document.querySelector('.toast-notification');
    if (existingNotification) {
        existingNotification.remove();
    }

    // Tạo element thông báo mới
    const notification = document.createElement('div');
    notification.className = `toast-notification ${type}`;
    
    // Định nghĩa icon phù hợp với từng loại thông báo
    let icon = '';
    let title = '';
    switch(type) {
        case 'success':
            icon = '<i class="fas fa-check-circle"></i>';
            title = 'Thành công';
            break;
        case 'error':
            icon = '<i class="fas fa-exclamation-circle"></i>';
            title = 'Lỗi';
            break;
        case 'warning':
            icon = '<i class="fas fa-exclamation-triangle"></i>';
            title = 'Cảnh báo';
            break;
        case 'info':
        default:
            icon = '<i class="fas fa-info-circle"></i>';
            title = 'Thông báo';
            break;
    }

    // Cấu trúc HTML của thông báo
    notification.innerHTML = `
        <div class="toast-header">
            <span class="toast-icon">${icon}</span>
            <strong class="toast-title">${title}</strong>
            <button type="button" class="close-button">&times;</button>
        </div>
        <div class="toast-body">${message}</div>
    `;

    // Thêm vào DOM
    document.body.appendChild(notification);
    
    // Xử lý sự kiện đóng thông báo
    const closeButton = notification.querySelector('.close-button');
    closeButton.addEventListener('click', () => {
        notification.remove();
    });

    // Tự động xóa thông báo sau khoảng thời gian cố định
    if (duration > 0) {
        setTimeout(() => {
            if (notification.parentNode) {
                notification.remove();
            }
        }, duration);
    }
    
    // Đảm bảo thông báo được hiển thị trên cùng
    notification.style.zIndex = '2000';
    
    return notification;
}