// In script_chung.js
console.log("JS: script_chung.js loaded.");

// =================================================================================
// HÀM TIỆN ÍCH CHUNG
// =================================================================================
function escapeJsString(str) {
    if (str === null || typeof str === 'undefined') return '';
    return String(str)
        .replace(/\\/g, '\\\\')
        .replace(/'/g, "\\'")
        .replace(/"/g, '\\"')
        .replace(/\n/g, '\\n')
        .replace(/\r/g, '\\r');
}

// =================================================================================
// BIẾN TOÀN CỤC (Hạn chế sử dụng, chỉ khi cần thiết)
// =================================================================================
let currentDepartmentIdForDetailPage = null; // Dùng để biết đang xem chi tiết phòng ban nào

// =================================================================================
// KHỞI TẠO TRANG VÀ BRIDGE (ĐƯỢC GỌI TỪ JAVA)
// =================================================================================
function initializePageAfterJavaBridgeInjection() {
	window.quanLyPhongBanBridge.log("Chuẩn bị khởi tạo UI bằng JS");

    // Xác định trang hiện tại và tải dữ liệu/gắn sự kiện tương ứng
    const mainNavActive = document.querySelector(".main-nav a.active"); // currently <a> is choose (find first)
    const pageTitleElement = document.querySelector("h1.content-title"); //currently title element is choose

    if (mainNavActive && mainNavActive.textContent.includes("Phòng ban")) {
		quanLyPhongBanBridge.log("Chuẩn bị tải lên dữ liệu cho trang Phòng ban")
        loadDepartmentTableData();
        const btnAddDept = document.getElementById("btnAddDepartment");
        if (btnAddDept) {
            btnAddDept.addEventListener('click', handleAddDepartment);
        }
    } else if (document.getElementById('employeeDetailTableContainer')) {
        console.log("JS: Detected Employee Detail page (or page with employee table).");
        const urlParams = new URLSearchParams(window.location.search); // Giả sử ID phòng ban được truyền qua URL
        const idPhongBanStr = urlParams.get('idPhongBan');

        if (idPhongBanStr && !isNaN(parseInt(idPhongBanStr))) {
            const idPhongBan = parseInt(idPhongBanStr);
            currentDepartmentIdForDetailPage = idPhongBan; // Lưu ID hiện tại
            if (pageTitleElement) {
                // Có thể gọi Java để lấy tên phòng ban dựa trên ID cho tiêu đề đẹp hơn
                // pageTitleElement.textContent = `Chi tiết ${window.javaApp.getDepartmentNameById(idPhongBan)}`;
                pageTitleElement.textContent = `Nhân sự Phòng Ban (ID: ${idPhongBan})`;
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
    } else {
        console.log("JS: Không thể xác định trang hiện tại để tải dữ liệu cụ thể.");
    }

    // Khởi tạo Modal xác nhận tùy chỉnh (nếu bạn dùng)
    initializeCustomConfirmModal();

    // Khởi tạo Modal chuyển phòng ban và các nút liên quan
    initializeTransferModal();

    // Gắn sự kiện cho nút "Trở lại" chung (nếu có trên trang)
    const btnBack = document.querySelector(".btn-back");
    if (btnBack && typeof window.javaApp !== 'undefined' && typeof window.javaApp.navigateToDepartmentManagement === 'function') {
        btnBack.addEventListener('click', () => {
            window.javaApp.navigateToDepartmentManagement();
        });
    }

    // Xử lý tự động mở modal nếu có marker
    if (document.getElementById('autoOpenModalMarker')) {
        console.log("JS: autoOpenModalMarker found. Attempting to open transfer modal.");
        handleAutoOpenModal();
    }

    // Khởi tạo logic checkbox (quan trọng là gọi sau khi bảng được điền)
    // initializeDynamicCheckboxes() sẽ được gọi bên trong loadDepartmentTableData và loadEmployeeTableData
}


// =================================================================================
// LOGIC TRANG QUẢN LÝ PHÒNG BAN
// =================================================================================
function loadDepartmentTableData() {
    console.log("JS: loadDepartmentTableData CALLED.");
    if (typeof window.javaApp === 'undefined' || typeof window.javaApp.layDanhSachPhongBan !== 'function') {
        console.warn("JS: javaApp.layDanhSachPhongBan chưa sẵn sàng.");
        const tbody = document.querySelector("#departmentTable tbody");
        if (tbody) tbody.innerHTML = '<tr><td colspan="3" style="text-align:center;">Lỗi kết nối (Java bridge).</td></tr>';
        return;
    }

    try {
        const departmentsJson = window.javaApp.layDanhSachPhongBan();
        console.log("JS: Received departments JSON:", departmentsJson);
        const departments = JSON.parse(departmentsJson); // Quan trọng: parse JSON
        const tbody = document.querySelector("#departmentTable tbody");

        if (!tbody) {
            console.error("JS: Không tìm thấy tbody cho #departmentTable.");
            return;
        }
        tbody.innerHTML = '';

        if (!departments || departments.length === 0) {
            tbody.innerHTML = '<tr><td colspan="3" style="text-align:center;">Không có phòng ban nào.</td></tr>';
            return;
        }

        departments.forEach(dept => {
            if (typeof dept.idPhongBan === 'undefined' || typeof dept.tenPhongBan === 'undefined') {
                console.warn("JS: Dữ liệu phòng ban không hợp lệ từ Java:", dept);
                return;
            }

            const row = tbody.insertRow();
            row.insertCell().textContent = dept.idPhongBan;
            row.insertCell().textContent = dept.tenPhongBan;

            const actionsCell = row.insertCell();
            actionsCell.style.whiteSpace = "nowrap"; // Ngăn nút xuống dòng

            const editButton = document.createElement('button');
            editButton.className = 'btn btn-edit';
            editButton.textContent = 'Sửa';
            editButton.addEventListener('click', (event) => {
                event.stopPropagation();
                handleEditDepartment(dept.idPhongBan, dept.tenPhongBan);
            });
            actionsCell.appendChild(editButton);

            const deleteButton = document.createElement('button');
            deleteButton.className = 'btn btn-danger';
            deleteButton.textContent = 'Xóa';
            deleteButton.style.marginLeft = '5px';
            deleteButton.addEventListener('click', (event) => {
                event.stopPropagation();
                console.log(`JS: Delete button clicked for ID: ${dept.idPhongBan}, Name: "${dept.tenPhongBan}"`);
                handleDeleteDepartment(dept.idPhongBan, dept.tenPhongBan);
            });
            actionsCell.appendChild(deleteButton);

            row.style.cursor = "pointer";
            row.setAttribute('title', `Xem chi tiết phòng ${escapeJsString(dept.tenPhongBan)}`);
            row.addEventListener('click', (event) => {
                if (event.target.tagName === 'BUTTON' || event.target.closest('button')) return;
                if (typeof window.javaApp !== 'undefined' && typeof window.javaApp.navigateToDepartmentDetail === 'function') {
                    window.javaApp.navigateToDepartmentDetail(dept.idPhongBan);
                } else { console.error("JS: javaApp.navigateToDepartmentDetail is not available."); }
            });
        });
        // Không có checkbox trên bảng này, không cần gọi initializeDynamicCheckboxes
    } catch (e) {
        console.error("JS: Lỗi trong loadDepartmentTableData:", e);
        const tbody = document.querySelector("#departmentTable tbody");
        if (tbody) tbody.innerHTML = '<tr><td colspan="3" style="text-align:center;">Lỗi xử lý dữ liệu phòng ban.</td></tr>';
    }
}

function handleAddDepartment() {
    console.log("JS: handleAddDepartment CALLED");
    const newIdStr = prompt("Nhập ID phòng ban mới (số):");
    if (newIdStr === null) return;
    const newId = parseInt(newIdStr);

    const newName = prompt("Nhập tên phòng ban mới:");
    if (newName === null) return;

    if (isNaN(newId) || newId <= 0 || !newName || newName.trim() === "") {
        alert("ID phải là số dương và Tên phòng ban không được để trống.");
        return;
    }
    if (typeof window.javaApp !== 'undefined' && typeof window.javaApp.themPhongBan === 'function') {
        if (window.javaApp.themPhongBan(newId, newName.trim())) {
            alert("Thêm phòng ban thành công!");
            loadDepartmentTableData();
        } else { alert("Thêm phòng ban thất bại. ID có thể đã tồn tại hoặc lỗi server."); }
    } else { alert("Lỗi kết nối (themPhongBan)."); }
}

function handleEditDepartment(id, currentName) {
    console.log(`JS: handleEditDepartment CALLED - ID: ${id}, Current Name: "${currentName}"`);
    const newIdStr = prompt(`Nhập ID cho phòng ban (hiện tại: ${id}). Bỏ qua nếu không đổi ID.`, id.toString());
    if (newIdStr === null) return;
    const newId = (newIdStr.trim() === "" || isNaN(parseInt(newIdStr))) ? id : parseInt(newIdStr); // Giữ ID cũ nếu không nhập hoặc nhập không phải số

    const newName = prompt(`Nhập tên mới cho phòng ban (hiện tại: "${escapeJsString(currentName)}"):`, currentName);
    if (newName === null) return;

    if (newId <= 0 || !newName || newName.trim() === "") {
        alert("ID phải là số dương và Tên phòng ban không được để trống.");
        return;
    }
    if (typeof window.javaApp !== 'undefined' && typeof window.javaApp.suaPhongBan === 'function') {
        if (window.javaApp.suaPhongBan(id, newId, newName.trim())) { // oldId, newId, newName
            alert("Sửa phòng ban thành công!");
            loadDepartmentTableData();
        } else { alert("Sửa phòng ban thất bại. ID mới có thể đã tồn tại hoặc lỗi server."); }
    } else { alert("Lỗi kết nối (suaPhongBan)."); }
}

// Thêm hàm này vào script_chung.js
function showNotification(message, type = 'success') { // type có thể là 'success', 'error', 'warning'
    const notificationArea = document.getElementById('notificationArea');
    const notificationMessage = document.getElementById('notificationMessage');

    if (!notificationArea || !notificationMessage) {
        console.warn("JS: Notification area elements not found. Falling back to alert.");
        alert(message); // Fallback nếu không tìm thấy khu vực thông báo
        return;
    }

    notificationMessage.textContent = message;
    notificationArea.style.display = 'block';

    if (type === 'success') {
        notificationArea.style.backgroundColor = '#28a745'; // Màu xanh lá
    } else if (type === 'error') {
        notificationArea.style.backgroundColor = '#dc3545'; // Màu đỏ
    } else if (type === 'warning') {
        notificationArea.style.backgroundColor = '#ffc107'; // Màu vàng
        notificationArea.style.color = '#333'; // Chữ đậm hơn cho nền vàng
    } else {
        notificationArea.style.backgroundColor = '#6c757d'; // Màu xám mặc định
    }

    // Tự động ẩn sau một khoảng thời gian (tùy chọn)
    // setTimeout(() => {
    //     notificationArea.style.display = 'none';
    // }, 5000); // Ẩn sau 5 giây
}

// --- Sửa hàm handleDeleteDepartment để dùng showNotification và showCustomConfirm ---
function handleDeleteDepartment(id, name) {
    console.log(`JS: handleDeleteDepartment CALLED - ID: ${id}, Name: "${escapeJsString(name)}"`);

    const confirmMessage = `Bạn có chắc chắn muốn xóa phòng ban "${escapeJsString(name)}" (ID: ${id})? Hành động này không thể hoàn tác.`;
    const confirmTitle = "Xác nhận xóa Phòng Ban";

    // Vẫn sử dụng Modal tùy chỉnh cho việc xác nhận (vì nó đáng tin cậy hơn confirm())
    showCustomConfirm( // Sử dụng hàm modal tùy chỉnh bạn đã có
        confirmMessage,
        confirmTitle,
        () => { // OK callback
            console.log(`JS: User confirmed (custom modal) deletion for ID: ${id}`);
            if (typeof window.javaApp !== 'undefined' && typeof window.javaApp.xoaPhongBan === 'function') {
                try {
                    if (window.javaApp.xoaPhongBan(id)) {
                        // Sử dụng khu vực thông báo thay vì alert
                        showNotification(`THÀNH CÔNG: Xóa phòng ban "${escapeJsString(name)}" (ID: ${id})!`, 'success');
                        loadDepartmentTableData();
                    } else {
                        showNotification(`THẤT BẠI: Xóa phòng ban "${escapeJsString(name)}" (ID: ${id}). Lý do có thể: phòng ban còn nhân viên, ID không tồn tại, hoặc lỗi server.`, 'error');
                    }
                } catch (e) {
                    console.error("JS: Exception calling javaApp.xoaPhongBan:", e);
                    showNotification("LỖI NGHIÊM TRỌNG khi gọi hàm xóa từ Java. Kiểm tra Console.", 'error');
                }
            } else {
                showNotification("LỖI KẾT NỐI (xoaPhongBan). Không thể thực hiện hành động.", 'error');
            }
        },
        () => { // Cancel callback
            console.log(`JS: User cancelled (custom modal) deletion for ID: ${id}`);
            // Tùy chọn: hiển thị thông báo hủy nhẹ nhàng
            // showNotification(`Đã hủy thao tác xóa phòng ban "${escapeJsString(name)}".`, 'warning');
        }
    );
}
// =================================================================================
// LOGIC TRANG CHI TIẾT PHÒNG BAN (DANH SÁCH NHÂN VIÊN)
// =================================================================================
function loadEmployeeTableData(idPhongBan) {
    currentDepartmentIdForDetailPage = idPhongBan; // Lưu lại để dùng cho các hành động khác
    console.log(`JS: loadEmployeeTableData CALLED for department ID: ${idPhongBan}`);

    const pageTitleEl = document.querySelector("h1.content-title#departmentDetailPageTitle");
    if (pageTitleEl) {
        // Nên gọi Java để lấy tên phòng ban dựa trên idPhongBan nếu có thể
        // String tenPhongBan = window.javaApp.getDepartmentNameById(idPhongBan);
        // pageTitleEl.textContent = `Nhân sự ${tenPhongBan} (ID: ${idPhongBan})`;
        pageTitleEl.textContent = `Nhân sự Phòng Ban (ID: ${idPhongBan})`; // Tạm thời
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
            initializeDynamicCheckboxes(); // Vẫn gọi để xử lý checkbox "Chọn tất cả" (nếu có)
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
            row.insertCell().textContent = emp.tenChucVu || "N/A"; // Giả sử có trường này
        });
        initializeDynamicCheckboxes(); // Gọi sau khi bảng đã được điền
    } catch (e) {
        console.error(`JS: Lỗi trong loadEmployeeTableData cho phòng ID ${idPhongBan}:`, e);
        const tbody = document.querySelector("#employeeDetailTable tbody");
        if (tbody) tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;">Lỗi xử lý dữ liệu nhân viên.</td></tr>';
    }
}


// =================================================================================
// LOGIC MODAL (Xác nhận tùy chỉnh và Chuyển phòng ban)
// =================================================================================
let customConfirmModalElement, customConfirmMessageElement, customConfirmOkButton, customConfirmCancelButton, customConfirmTitleElement;
let onCustomConfirmOkCallback, onCustomConfirmCancelCallback;

function initializeCustomConfirmModal() {
    customConfirmModalElement = document.getElementById('customConfirmModal');
    customConfirmMessageElement = document.getElementById('customConfirmMessage');
    customConfirmOkButton = document.getElementById('customConfirmOk');
    customConfirmCancelButton = document.getElementById('customConfirmCancel');
    customConfirmTitleElement = document.getElementById('customConfirmTitle');

    if (!customConfirmModalElement || !customConfirmMessageElement || !customConfirmOkButton || !customConfirmCancelButton || !customConfirmTitleElement) {
        console.warn("JS: Một hoặc nhiều phần tử của customConfirmModal không tìm thấy. Modal này có thể không hoạt động.");
        return;
    }

    customConfirmOkButton.addEventListener('click', () => {
        customConfirmModalElement.style.display = 'none';
        if (typeof onCustomConfirmOkCallback === 'function') onCustomConfirmOkCallback();
    });

    customConfirmCancelButton.addEventListener('click', () => {
        customConfirmModalElement.style.display = 'none';
        if (typeof onCustomConfirmCancelCallback === 'function') onCustomConfirmCancelCallback();
    });
}

function showCustomConfirm(message, title = "Xác nhận", okCallback, cancelCallback) {
	
    if (!customConfirmModalElement) {
        console.error("JS: customConfirmModal chưa được khởi tạo. Dùng confirm() mặc định.");
        if (confirm(message)) { if (okCallback) okCallback(); }
        else { if (cancelCallback) cancelCallback(); }
        return;
    }
    customConfirmTitleElement.textContent = title;
    customConfirmMessageElement.textContent = message;
    onCustomConfirmOkCallback = okCallback;
    onCustomConfirmCancelCallback = cancelCallback;
    customConfirmModalElement.style.display = 'flex';
}

function initializeTransferModal() {
    const transferButton = document.getElementById('btn-transfer-department');
    const modalOverlay = document.getElementById('transferModal');
    const closeModalButton = document.getElementById('closeTransferModal'); // Nút X trong modal chuyển
    const btnCancelTransfer = document.getElementById('btnCancelTransfer'); // Nút Hủy trong modal chuyển
    const departmentSelect = document.getElementById('department-select');
    const btnConfirmTransferAction = document.getElementById('btn-confirm-transfer-action');

    if (!modalOverlay) {
        // console.log("JS: Transfer modal not found on this page.");
        return; // Không có modal chuyển phòng ban trên trang này
    }

    // Tải danh sách phòng ban cho dropdown
    if (departmentSelect && typeof window.javaApp !== 'undefined' && typeof window.javaApp.layDanhSachPhongBan === 'function') {
        try {
            const departmentsJson = window.javaApp.layDanhSachPhongBan();
            const departments = JSON.parse(departmentsJson);
            departmentSelect.innerHTML = '<option value="" disabled selected>-- Chọn phòng ban mới --</option>';
            departments.forEach(dept => {
                const option = document.createElement('option');
                option.value = dept.idPhongBan; // value nên là ID
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
                alert("Vui lòng chọn ít nhất một nhân viên để chuyển phòng ban.");
                return;
            }
            modalOverlay.style.display = 'flex';
        });
    }

    const closeTransferModalFunc = () => modalOverlay.style.display = 'none';
    if (closeModalButton) closeModalButton.addEventListener('click', closeTransferModalFunc);
    if (btnCancelTransfer) btnCancelTransfer.addEventListener('click', closeTransferModalFunc);

    if (modalOverlay) { // Đóng khi click nền mờ
        modalOverlay.addEventListener('click', (event) => {
            if (event.target === modalOverlay) closeTransferModalFunc();
        });
    }

    if (btnConfirmTransferAction && departmentSelect) {
        btnConfirmTransferAction.addEventListener('click', () => {
            const selectedEmployeeIds = [];
            document.querySelectorAll('#employeeDetailTable .row-checkbox:checked').forEach(cb => {
                selectedEmployeeIds.push(parseInt(cb.value)); // Value là idNhanVien
            });
            const newDepartmentIdStr = departmentSelect.value;

            if (selectedEmployeeIds.length === 0) { alert("Vui lòng chọn nhân viên."); return; }
            if (!newDepartmentIdStr) { alert("Vui lòng chọn phòng ban mới."); return; }
            const newDepartmentId = parseInt(newDepartmentIdStr);

            if (isNaN(newDepartmentId)) { alert("Phòng ban mới không hợp lệ."); return;}

            if (typeof window.javaApp !== 'undefined' && typeof window.javaApp.chuyenPhongBan === 'function') {
                if (window.javaApp.chuyenPhongBan(JSON.stringify(selectedEmployeeIds), newDepartmentId)) {
                    alert("Chuyển phòng ban thành công!");
                    closeTransferModalFunc();
                    if (currentDepartmentIdForDetailPage) loadEmployeeTableData(currentDepartmentIdForDetailPage);
                } else { alert("Chuyển phòng ban thất bại từ server."); }
            } else { alert("Lỗi kết nối (chuyenPhongBan).");}
        });
    }
}

function handleAutoOpenModal() {
    console.log("JS: handleAutoOpenModal CALLED.");
    const modalOverlay = document.getElementById('transferModal');
    if (!modalOverlay) return;

    const departmentSelect = document.getElementById('department-select');
    const employeeTableCheckboxes = document.querySelectorAll('#employeeDetailTable .row-checkbox');

    // Chọn sẵn một vài nhân viên nếu có
    if (employeeTableCheckboxes.length > 0) employeeTableCheckboxes[0].checked = true;
    if (employeeTableCheckboxes.length > 1) employeeTableCheckboxes[1].checked = true;
    initializeDynamicCheckboxes(); // Cập nhật checkbox "Chọn tất cả"

    modalOverlay.style.display = 'flex';

    // Chọn sẵn một phòng ban trong dropdown (ví dụ: "Phòng Tài Chính" nếu có value 'ptch')
    if (departmentSelect) {
        let targetValue = "ptch"; // ID hoặc value của "Phòng Tài Chính"
        let found = false;
        for(let i=0; i < departmentSelect.options.length; i++){
            if(departmentSelect.options[i].value === targetValue){
                departmentSelect.value = targetValue;
                found = true;
                break;
            }
        }
        if(!found && departmentSelect.options.length > 1 && departmentSelect.options[0].disabled){
            departmentSelect.selectedIndex = 1; // Chọn option đầu tiên có thể chọn nếu 'ptch' không tìm thấy
        }
        console.log("JS: Auto-selected department in modal (if found):", departmentSelect.value);
    }
}

// =================================================================================
// LOGIC CHECKBOX CHUNG (Đã khá tốt, giữ nguyên hoặc tùy chỉnh nếu cần)
// =================================================================================
function initializeDynamicCheckboxes() {
    const selectAllCheckbox = document.getElementById('select-all');
    let targetTableSelector = '';

    // Xác định bảng mục tiêu dựa trên sự tồn tại của ID bảng
    if (document.getElementById('departmentTable')) {
        targetTableSelector = '#departmentTable';
    } else if (document.getElementById('employeeDetailTable')) {
        targetTableSelector = '#employeeDetailTable';
    } else {
        // console.warn("JS: No specific table found for checkbox initialization.");
        return; // Không có bảng nào phù hợp
    }

    const rowCheckboxes = document.querySelectorAll(`${targetTableSelector} .row-checkbox`);

    if (selectAllCheckbox) {
        // Gỡ bỏ listener cũ để tránh gắn nhiều lần nếu hàm này được gọi lại
        selectAllCheckbox.removeEventListener('change', handleSelectAllChangeGeneral);
        selectAllCheckbox.addEventListener('change', handleSelectAllChangeGeneral);
    }

    rowCheckboxes.forEach(checkbox => {
        // Gỡ bỏ listener cũ
        checkbox.removeEventListener('change', () => handleRowCheckboxChangeGeneral(targetTableSelector));
        checkbox.addEventListener('change', () => handleRowCheckboxChangeGeneral(targetTableSelector));
    });

    if (selectAllCheckbox) { // Cập nhật trạng thái ban đầu
        updateSelectAllCheckboxStateGeneral(targetTableSelector);
    }
}

function handleSelectAllChangeGeneral() { // Sẽ được gọi bởi event listener của selectAllCheckbox
    const selectAllCheckbox = document.getElementById('select-all');
    let targetTableSelector = '';
    if (document.getElementById('departmentTable')) {
        targetTableSelector = '#departmentTable';
    } else if (document.getElementById('employeeDetailTable')) {
        targetTableSelector = '#employeeDetailTable';
    } else { return; }

    const isChecked = selectAllCheckbox.checked;
    document.querySelectorAll(`${targetTableSelector} .row-checkbox`).forEach(checkbox => {
        checkbox.checked = isChecked;
    });
}

function handleRowCheckboxChangeGeneral(targetTableSelector) {
    // targetTableSelector được truyền vào từ event listener của từng row checkbox
    updateSelectAllCheckboxStateGeneral(targetTableSelector);
}

function updateSelectAllCheckboxStateGeneral(targetTableSelector) {
    const selectAllCheckbox = document.getElementById('select-all');
    if (!selectAllCheckbox) return;

    const rowCheckboxes = document.querySelectorAll(`${targetTableSelector} .row-checkbox`);
    if (rowCheckboxes.length === 0) {
        selectAllCheckbox.checked = false;
        selectAllCheckbox.indeterminate = false; // Trạng thái không xác định
        return;
    }

    let allChecked = true;
    let noneChecked = true;
    for (let cb of rowCheckboxes) {
        if (cb.checked) {
            noneChecked = false;
        } else {
            allChecked = false;
        }
        // Nếu đã tìm thấy cả check và uncheck, có thể thoát sớm
        if (!allChecked && !noneChecked) break;
    }

    if (allChecked) {
        selectAllCheckbox.checked = true;
        selectAllCheckbox.indeterminate = false;
    } else if (noneChecked) {
        selectAllCheckbox.checked = false;
        selectAllCheckbox.indeterminate = false;
    } else { // Một số được chọn, một số không
        selectAllCheckbox.checked = false;
        selectAllCheckbox.indeterminate = true;
    }
}

// --- Gắn sự kiện DOMContentLoaded để gọi hàm khởi tạo từ Java (nếu Java không gọi pageIsReadyAndJavaBridgeIsInjected) ---
// TUY NHIÊN, cách tốt nhất là để Java gọi pageIsReadyAndJavaBridgeIsInjected()
// Đoạn này chỉ để dự phòng nếu bạn chưa thể gọi từ Java ngay lập tức

document.addEventListener('DOMContentLoaded', function() {
    console.log("JS: DOMContentLoaded fired. Waiting for Java bridge call to pageIsReadyAndJavaBridgeIsInjected().");
    // Không làm gì nhiều ở đây, chờ Java gọi hàm chính.
    // Có thể thêm một timeout để thử gọi pageIsReadyAndJavaBridgeIsInjected nếu Java không gọi sau 1 khoảng thời gian.
	setTimeout(() => {
	    if (typeof initializePageAfterJavaBridgeInjection === 'function') {
	        initializePageAfterJavaBridgeInjection();
	    } else {
			quanLyPhongBanBridge.log("Chưa có hàm initializePageAfterJavaBridgeInjection()")
		}
   	}, 500); // Thử sau 1 giây
});
