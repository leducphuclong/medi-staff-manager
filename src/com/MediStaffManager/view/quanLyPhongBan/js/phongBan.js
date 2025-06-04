// In js/page_department_list.js
console.log("JS: page_department_list.js loaded.");

function initializeDepartmentPage() {
    console.log("JS: Initializing Department Page.");
    if (typeof window.quanLyPhongBanBridge !== 'undefined') { // Ensure bridge is available
         window.quanLyPhongBanBridge.log("Chuẩn bị tải lên dữ liệu cho trang Phòng ban");
    }
    loadDepartmentTableData();
    const btnAddDept = document.getElementById("btnAddDepartment");
    if (btnAddDept) {
        // Remove existing listener to prevent duplicates if re-initialized
        const newBtnAddDept = btnAddDept.cloneNode(true);
        btnAddDept.parentNode.replaceChild(newBtnAddDept, btnAddDept);
        newBtnAddDept.addEventListener('click', handleAddDepartment);
    }
}

function loadDepartmentTableData() {
    quanLyPhongBanBridge.log("Lấy dữ liệu các phòng ban vào bảng");
    if (typeof window.quanLyPhongBanBridge === 'undefined' || typeof window.quanLyPhongBanBridge.layDanhSachPhongBan !== 'function') {
        console.log("quanLyPhongBanBridge.layDanhSachPhongBan chưa sẵn sàng.");
        const tbody = document.querySelector("#departmentTable tbody");
        if (tbody) tbody.innerHTML = '<tr><td colspan="3" style="text-align:center;">Lỗi kết nối (Java bridge).</td></tr>';
        return;
    }

    try {
        const departmentsJson = window.quanLyPhongBanBridge.layDanhSachPhongBan();
		quanLyPhongBanBridge.log("đã lấy danh sách phòng ban");
        const departments = JSON.parse(departmentsJson); // converts that JSON string into a JavaScript object or array.
		
        const tbody = document.querySelector("#departmentTable tbody");

        if (!tbody) {
            quanLyPhongBanBridge.log("JS: Không tìm thấy tbody cho #departmentTable.");
            return;
        }
        tbody.innerHTML = '';

        if (!departments || departments.length === 0) {
            tbody.innerHTML = '<tr><td colspan="3" style="text-align:center;">Không có phòng ban nào.</td></tr>';
            return;
        }

        departments.forEach(dept => {
            if (typeof dept.idPhongBan === 'undefined' || typeof dept.tenPhongBan === 'undefined') {
                quanLyPhongBanBridge.log("Dữ liệu phòng ban không hợp lệ từ Java:");
                return;
            }

            const row = tbody.insertRow();
            row.insertCell().textContent = dept.idPhongBan;
            row.insertCell().textContent = dept.tenPhongBan;

            const actionsCell = row.insertCell(); // Thêm một ô vào dòng    
            actionsCell.style.whiteSpace = "nowrap"; // keep on single lines

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
                handleDeleteDepartment(dept.idPhongBan, dept.tenPhongBan);
            });
            actionsCell.appendChild(deleteButton);

            row.style.cursor = "pointer";
            row.setAttribute('title', `Xem chi tiết ${escapeJsString(dept.tenPhongBan)}`);
            row.addEventListener('click', (event) => {
                if (event.target.tagName === 'BUTTON' || event.target.closest('button')) return;
                if (typeof window.javaApp !== 'undefined' && typeof window.javaApp.navigateToDepartmentDetail === 'function') {
                    window.javaApp.navigateToDepartmentDetail(dept.idPhongBan);
                } else { console.error("JS: javaApp.navigateToDepartmentDetail is not available."); }
            });
        });
    } catch (e) {
        quanLyPhongBanBridge.log("JS: Lỗi trong loadDepartmentTableData:", e);
        const tbody = document.querySelector("#departmentTable tbody");
        if (tbody) tbody.innerHTML = '<tr><td colspan="3" style="text-align:center;">Lỗi xử lý dữ liệu phòng ban.</td></tr>';
    }
}

function handleEditDepartment(currentId, currentName) {
	quanLyPhongBanBridge.log("JS: handleAddDepartment ĐƯỢC GỌI");

    const modal = document.getElementById("departmentModal");
    const idInput = document.getElementById("deptIdInput");
    const nameInput = document.getElementById("deptNameInput");
    const saveBtn = document.getElementById("deptSaveBtn");
    const cancelBtn = document.getElementById("deptCancelBtn");

    // Set current values as input values (not placeholders, so they can be edited directly)
    idInput.value = currentId;
    nameInput.value = currentName;

    modal.style.display = "block";

    function closeModal() {
        modal.style.display = "none";
        saveBtn.removeEventListener("click", onSave);
        cancelBtn.removeEventListener("click", onCancel);
    }

    function onSave() {
        const newId = parseInt(idInput.value);
        const newName = nameInput.value.trim();

        if (isNaN(newId) || newId <= 0 || !newName) {
            showNotification("ID phải là số dương và tên phòng ban không được để trống.", "error");
            return;
        }

        if (typeof window.quanLyPhongBanBridge !== "undefined" && typeof window.quanLyPhongBanBridge.suaPhongBan === "function") {
            if (window.quanLyPhongBanBridge.suaPhongBan(currentId, newId, newName)) {
                showNotification("Sửa phòng ban thành công!", "success");
                loadDepartmentTableData();
            } else {
                showNotification("Sửa phòng ban thất bại. ID mới có thể đã tồn tại hoặc lỗi máy chủ.", "error");
            }
        } else {
            showNotification("Lỗi kết nối (suaPhongBan).", "error");
        }
        closeModal();
    }

    function onCancel() {
        closeModal();
    }

    saveBtn.addEventListener("click", onSave);
    cancelBtn.addEventListener("click", onCancel);
}


function handleDeleteDepartment(id, name) {
    console.log(`JS: handleDeleteDepartment CALLED - ID: ${id}, Name: "${escapeJsString(name)}"`);
    const confirmMessage = `Bạn có chắc chắn muốn xóa phòng ban "${escapeJsString(name)}" (ID: ${id})? Hành động này không thể hoàn tác.`;
    const confirmTitle = "Xác nhận xóa Phòng Ban";

    showCustomConfirm( // from ui_confirm_modal.js
        confirmMessage,
        confirmTitle,
        () => { // OK callback
            console.log(`JS: User confirmed deletion for ID: ${id}`);
            if (typeof window.javaApp !== 'undefined' && typeof window.javaApp.xoaPhongBan === 'function') {
                try {
                    if (window.javaApp.xoaPhongBan(id)) {
                        showNotification(`THÀNH CÔNG: Xóa phòng ban "${escapeJsString(name)}" (ID: ${id})!`, 'success');
                        loadDepartmentTableData();
                    } else {
                        showNotification(`THẤT BẠI: Xóa phòng ban "${escapeJsString(name)}" (ID: ${id}). Có thể phòng ban còn nhân viên hoặc lỗi server.`, 'error');
                    }
                } catch (e) {
                    console.error("JS: Exception calling javaApp.xoaPhongBan:", e);
                    showNotification("LỖI NGHIÊM TRỌNG khi gọi hàm xóa từ Java. Kiểm tra Console.", 'error');
                }
            } else {
                showNotification("LỖI KẾT NỐI (xoaPhongBan).", 'error');
            }
        },
        () => { // Cancel callback
            console.log(`JS: User cancelled deletion for ID: ${id}`);
            // showNotification(`Đã hủy thao tác xóa phòng ban "${escapeJsString(name)}".`, 'warning'); // Optional
        }
    );
}