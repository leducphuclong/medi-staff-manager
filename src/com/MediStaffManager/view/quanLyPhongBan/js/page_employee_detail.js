// In js/page_employee_detail.js
console.log("JS: page_employee_detail.js loaded.");

let currentDepartmentIdForDetailPage = null; // Used by this module

function initializeEmployeeDetailPage() {
    console.log("JS: Initializing Employee Detail Page.");
    const urlParams = new URLSearchParams(window.location.search);
    const idPhongBanStr = urlParams.get('idPhongBan'); // Assuming ID is passed via URL query param
    const pageTitleElement = document.querySelector("h1.content-title#departmentDetailPageTitle"); // More specific selector

    if (idPhongBanStr && !isNaN(parseInt(idPhongBanStr))) {
        const idPhongBan = parseInt(idPhongBanStr);
        currentDepartmentIdForDetailPage = idPhongBan;
        if (pageTitleElement) {
            // Example: Fetch department name from Java for a better title
            // if (typeof window.javaApp !== 'undefined' && typeof window.javaApp.getDepartmentNameById === 'function') {
            //     const deptName = window.javaApp.getDepartmentNameById(idPhongBan);
            //     pageTitleElement.textContent = `Nhân sự ${deptName || `Phòng Ban`} (ID: ${idPhongBan})`;
            // } else {
                 pageTitleElement.textContent = `Nhân sự Phòng Ban (ID: ${idPhongBan})`;
            // }
        }
        loadEmployeeTableData(idPhongBan);
    } else {
        console.warn("JS: Không có idPhongBan hợp lệ trong URL cho trang chi tiết.");
        if (pageTitleElement) pageTitleElement.textContent = "Chi tiết Phòng Ban - Không rõ ID";
        const employeeTableBody = document.querySelector("#employeeDetailTable tbody");
        if (employeeTableBody) {
            employeeTableBody.innerHTML = '<tr><td colspan="6" style="text-align:center;">Không có ID phòng ban. Vui lòng chọn một phòng ban từ danh sách.</td></tr>';
        }
    }
    initializeTransferModal(); // Initialize transfer modal elements and listeners

    // Xử lý tự động mở modal nếu có marker
    if (document.getElementById('autoOpenModalMarker')) {
        console.log("JS: autoOpenModalMarker found. Attempting to open transfer modal.");
        handleAutoOpenModal(); // This function is now part of this file
    }
}


function loadEmployeeTableData(idPhongBan) {
    currentDepartmentIdForDetailPage = idPhongBan; // Keep this updated
    console.log(`JS: loadEmployeeTableData CALLED for department ID: ${idPhongBan}`);

    // Update title if not already done or if name is now available
    const pageTitleEl = document.querySelector("h1.content-title#departmentDetailPageTitle");
    if (pageTitleEl && !pageTitleEl.textContent.includes(`(ID: ${idPhongBan})`)) { // Basic check to avoid re-fetch if title is good
        // Potentially re-fetch name from Java for accuracy if needed
        pageTitleEl.textContent = `Nhân sự Phòng Ban (ID: ${idPhongBan})`;
    }

    if (typeof window.javaApp === 'undefined' || typeof window.javaApp.layNhanVienTheoPhongBan !== 'function') {
        console.warn("JS: javaApp.layNhanVienTheoPhongBan chưa sẵn sàng.");
        const tbody = document.querySelector("#employeeDetailTable tbody");
        if (tbody) tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;">Lỗi kết nối (Java bridge).</td></tr>';
        return;
    }

    try {
        const employeesJson = window.javaApp.layNhanVienTheoPhongBan(idPhongBan);
        console.log("JS: Received employees JSON:", employeesJson);
        const employees = JSON.parse(employeesJson);
        const tbody = document.querySelector("#employeeDetailTable tbody");

        if (!tbody) {
            console.error("JS: Không tìm thấy tbody cho #employeeDetailTable.");
            return;
        }
        tbody.innerHTML = '';

        if (!employees || employees.length === 0) {
            tbody.innerHTML = `<tr><td colspan="6" style="text-align:center;">Không có nhân viên nào trong phòng ban ID: ${idPhongBan}.</td></tr>`;
            initializeDynamicCheckboxes('#employeeDetailTable'); // from ui_checkbox_handler.js
            return;
        }

        employees.forEach(emp => {
            if (typeof emp.idNhanVien === 'undefined' || typeof emp.hoTen === 'undefined') {
                console.warn("JS: Dữ liệu nhân viên không hợp lệ từ Java:", emp);
                return;
            }
            const row = tbody.insertRow();
            const checkboxCell = row.insertCell();
            checkboxCell.style.textAlign = 'center';
            checkboxCell.innerHTML = `<input type="checkbox" class="row-checkbox" value="${emp.idNhanVien}">`;

            row.insertCell().textContent = emp.idNhanVien;
            row.insertCell().textContent = emp.hoTen;
            row.insertCell().textContent = emp.sdt || "N/A";
            row.insertCell().textContent = emp.email || "N/A";
            row.insertCell().textContent = emp.tenChucVu || "N/A";
        });
        initializeDynamicCheckboxes('#employeeDetailTable'); // from ui_checkbox_handler.js
    } catch (e) {
        console.error(`JS: Lỗi trong loadEmployeeTableData cho phòng ID ${idPhongBan}:`, e);
        const tbody = document.querySelector("#employeeDetailTable tbody");
        if (tbody) tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;">Lỗi xử lý dữ liệu nhân viên.</td></tr>';
    }
}

function initializeTransferModal() {
    const transferButton = document.getElementById('btn-transfer-department');
    const modalOverlay = document.getElementById('transferModal');
    const closeModalButton = document.getElementById('closeTransferModal');
    const btnCancelTransfer = document.getElementById('btnCancelTransfer');
    const departmentSelect = document.getElementById('department-select');
    const btnConfirmTransferAction = document.getElementById('btn-confirm-transfer-action');

    if (!modalOverlay) {
        // console.log("JS: Transfer modal not found on this page.");
        return;
    }

    if (departmentSelect && typeof window.javaApp !== 'undefined' && typeof window.javaApp.layDanhSachPhongBan === 'function') {
        try {
            const departmentsJson = window.javaApp.layDanhSachPhongBan();
            const departments = JSON.parse(departmentsJson);
            departmentSelect.innerHTML = '<option value="" disabled selected>-- Chọn phòng ban mới --</option>';
            departments.forEach(dept => {
                // Do not add the current department to the list of destinations
                if (currentDepartmentIdForDetailPage && dept.idPhongBan === currentDepartmentIdForDetailPage) {
                    return;
                }
                const option = document.createElement('option');
                option.value = dept.idPhongBan;
                option.textContent = dept.tenPhongBan;
                departmentSelect.appendChild(option);
            });
        } catch (e) {
            console.error("JS: Lỗi tải phòng ban cho dropdown chuyển:", e);
            if(departmentSelect) departmentSelect.innerHTML = '<option value="">Lỗi tải DS phòng ban</option>';
        }
    } else if (departmentSelect) {
         departmentSelect.innerHTML = '<option value="">Không thể tải DS phòng ban (lỗi bridge)</option>';
    }

    if (transferButton) {
        transferButton.addEventListener('click', function() {
            let selectedCount = 0;
            document.querySelectorAll('#employeeDetailTable .row-checkbox:checked').forEach(() => selectedCount++);
            if (selectedCount === 0) {
                showNotification("Vui lòng chọn ít nhất một nhân viên để chuyển phòng ban.", "warning");
                return;
            }
            // Refresh department list in dropdown, excluding current one
            if (departmentSelect && typeof window.javaApp !== 'undefined' && typeof window.javaApp.layDanhSachPhongBan === 'function') {
                const departmentsJson = window.javaApp.layDanhSachPhongBan();
                const departments = JSON.parse(departmentsJson);
                departmentSelect.innerHTML = '<option value="" disabled selected>-- Chọn phòng ban mới --</option>'; // Reset
                departments.forEach(dept => {
                    if (currentDepartmentIdForDetailPage && dept.idPhongBan === currentDepartmentIdForDetailPage) {
                        return;
                    }
                    const option = document.createElement('option');
                    option.value = dept.idPhongBan;
                    option.textContent = dept.tenPhongBan;
                    departmentSelect.appendChild(option);
                });
            }
            modalOverlay.style.display = 'flex';
        });
    }

    const closeTransferModalFunc = () => {
        if (modalOverlay) modalOverlay.style.display = 'none';
    }
    if (closeModalButton) closeModalButton.addEventListener('click', closeTransferModalFunc);
    if (btnCancelTransfer) btnCancelTransfer.addEventListener('click', closeTransferModalFunc);

    if (modalOverlay) {
        modalOverlay.addEventListener('click', (event) => {
            if (event.target === modalOverlay) closeTransferModalFunc();
        });
    }

    if (btnConfirmTransferAction && departmentSelect) {
        btnConfirmTransferAction.addEventListener('click', () => {
            const selectedEmployeeIds = [];
            document.querySelectorAll('#employeeDetailTable .row-checkbox:checked').forEach(cb => {
                selectedEmployeeIds.push(parseInt(cb.value));
            });
            const newDepartmentIdStr = departmentSelect.value;

            if (selectedEmployeeIds.length === 0) { showNotification("Vui lòng chọn nhân viên.", "warning"); return; }
            if (!newDepartmentIdStr) { showNotification("Vui lòng chọn phòng ban mới.", "warning"); return; }
            const newDepartmentId = parseInt(newDepartmentIdStr);

            if (isNaN(newDepartmentId)) { showNotification("Phòng ban mới không hợp lệ.", "error"); return;}
            if (newDepartmentId === currentDepartmentIdForDetailPage) { // Check if new dept is same as current
                 showNotification("Không thể chuyển nhân viên vào chính phòng ban hiện tại của họ.", "warning"); return;
            }

            if (typeof window.javaApp !== 'undefined' && typeof window.javaApp.chuyenPhongBan === 'function') {
                if (window.javaApp.chuyenPhongBan(JSON.stringify(selectedEmployeeIds), newDepartmentId)) {
                    showNotification("Chuyển phòng ban thành công!", "success");
                    closeTransferModalFunc();
                    if (currentDepartmentIdForDetailPage) loadEmployeeTableData(currentDepartmentIdForDetailPage); // Reload current page data
                } else { showNotification("Chuyển phòng ban thất bại từ server.", "error"); }
            } else { showNotification("Lỗi kết nối (chuyenPhongBan).", "error");}
        });
    }
}

function handleAutoOpenModal() {
    console.log("JS: handleAutoOpenModal CALLED.");
    const modalOverlay = document.getElementById('transferModal');
    if (!modalOverlay) return;

    const departmentSelect = document.getElementById('department-select');
    const employeeTableCheckboxes = document.querySelectorAll('#employeeDetailTable .row-checkbox');

    // Auto-select some employees for demo if any
    if (employeeTableCheckboxes.length > 0) {
        if (employeeTableCheckboxes[0]) employeeTableCheckboxes[0].checked = true;
        if (employeeTableCheckboxes[1]) employeeTableCheckboxes[1].checked = true; // Select second if exists
        initializeDynamicCheckboxes('#employeeDetailTable'); // Update select-all
    }


    modalOverlay.style.display = 'flex';

    // Auto-select a department in dropdown (example)
    if (departmentSelect) {
        // Try to select the first available option that is not the current department
        let selected = false;
        for(let i=0; i < departmentSelect.options.length; i++){
            if(departmentSelect.options[i].value && departmentSelect.options[i].value != currentDepartmentIdForDetailPage){
                departmentSelect.value = departmentSelect.options[i].value;
                selected = true;
                break;
            }
        }
        if(!selected && departmentSelect.options.length > 0 && departmentSelect.options[0].disabled && departmentSelect.options.length > 1){
             // If placeholder is first, try second
             if (departmentSelect.options[1].value && departmentSelect.options[1].value != currentDepartmentIdForDetailPage) {
                departmentSelect.selectedIndex = 1;
             } else if (departmentSelect.options.length > 2 && departmentSelect.options[2].value && departmentSelect.options[2].value != currentDepartmentIdForDetailPage) {
                departmentSelect.selectedIndex = 2; // Try third if second is current
             }
        }
        console.log("JS: Auto-selected department in modal (if found):", departmentSelect.value);
    }
}