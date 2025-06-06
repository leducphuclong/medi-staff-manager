// In js/page_department_list.js
console.log("JS: page_department_list.js loaded.");

function initializeDepartmentPage() {
	quanLyPhongBanBridge.log("Khởi tạo trang Phòng ban")
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
    
    // Thêm xử lý sự kiện tìm kiếm
    const btnSearch = document.getElementById("btnSearch");
    if (btnSearch) {
        const newBtnSearch = btnSearch.cloneNode(true);
        btnSearch.parentNode.replaceChild(newBtnSearch, btnSearch);
        newBtnSearch.addEventListener('click', handleSearch);
    }
    
    // Thêm xử lý sự kiện làm mới
    const btnReset = document.getElementById("btnReset");
    if (btnReset) {
        const newBtnReset = btnReset.cloneNode(true);
        btnReset.parentNode.replaceChild(newBtnReset, btnReset);
        newBtnReset.addEventListener('click', handleResetSearch);
    }
    
    // Xử lý sự kiện nhấn Enter trong ô tìm kiếm
    const searchInput = document.getElementById("searchInput");
    if (searchInput) {
        searchInput.addEventListener('keypress', (event) => {
            if (event.key === 'Enter') {
                handleSearch();
            }
        });
    }
}

// Hàm xử lý tìm kiếm
function handleSearch() {
    const searchType = document.getElementById("searchType").value;
    const searchValue = document.getElementById("searchInput").value.trim();
    
    if (!searchValue) {
        showNotification("Vui lòng nhập từ khóa tìm kiếm", "warning");
        return;
    }
    
    quanLyPhongBanBridge.log(`Tìm kiếm phòng ban: loại=${searchType}, giá trị=${searchValue}`);
    
    try {
        let resultJson = "";
        if (searchType === "ten") {
            resultJson = quanLyPhongBanBridge.timKiemPhongBanTheoTen(searchValue);
        } else if (searchType === "id") {
            if (isNaN(parseInt(searchValue))) {
                showNotification("ID phải là số nguyên", "error");
                return;
            }
            resultJson = quanLyPhongBanBridge.timKiemPhongBanTheoId(searchValue);
        }
        
        const results = JSON.parse(resultJson);
        displaySearchResults(results);
        
    } catch (e) {
        quanLyPhongBanBridge.log("Lỗi khi tìm kiếm: " + e);
        showNotification("Đã xảy ra lỗi khi tìm kiếm", "error");
    }
}

// Hàm hiển thị kết quả tìm kiếm
function displaySearchResults(results) {
    const tbody = document.querySelector("#departmentTable tbody");
    tbody.innerHTML = '';
    
    if (!results || results.length === 0) {
        tbody.innerHTML = '<tr><td colspan="3" style="text-align:center;">Không tìm thấy kết quả phù hợp.</td></tr>';
        return;
    }
    
    results.forEach(dept => {
        if (typeof dept.idPhongBan === 'undefined' || typeof dept.tenPhongBan === 'undefined') {
            quanLyPhongBanBridge.log("Dữ liệu phòng ban không hợp lệ từ kết quả tìm kiếm:");
            return;
        }

        const row = tbody.insertRow();
        row.insertCell().textContent = dept.idPhongBan;
        row.insertCell().textContent = dept.tenPhongBan;

        const actionsCell = row.insertCell();
        actionsCell.style.whiteSpace = "nowrap";

        const editButton = document.createElement('button');
        editButton.className = 'btn btn-edit';
        editButton.innerHTML = '<i class="fas fa-pencil-alt"></i>';
        editButton.title = "Sửa thông tin phòng ban";
        editButton.addEventListener('click', (event) => {
            event.stopPropagation();
            handleEditDepartment(dept.idPhongBan, dept.tenPhongBan);
        });
        actionsCell.appendChild(editButton);

        const deleteButton = document.createElement('button');
        deleteButton.className = 'btn btn-danger';
        deleteButton.innerHTML = '<i class="fas fa-trash-alt"></i>';
        deleteButton.title = "Xóa phòng ban";
        deleteButton.addEventListener('click', (event) => {
            event.stopPropagation();
            handleDeleteDepartment(dept.idPhongBan, dept.tenPhongBan);
        });
        actionsCell.appendChild(deleteButton);

        row.style.cursor = "pointer";
        row.setAttribute('title', `Xem chi tiết ${escapeJsString(dept.tenPhongBan)}`);
        row.addEventListener('click', (event) => {
            if (event.target.tagName === 'BUTTON' || event.target.closest('button')) return;
            if (typeof window.quanLyPhongBanBridge !== 'undefined' && typeof window.quanLyPhongBanBridge.navigateToDepartmentDetail === 'function') {
                window.quanLyPhongBanBridge.navigateToDepartmentDetail(dept.idPhongBan, dept.tenPhongBan);
            } else { console.error("JS: javaApp.navigateToDepartmentDetail is not available."); }
        });
    });
}

// Hàm làm mới tìm kiếm
function handleResetSearch() {
    // Làm mới các trường tìm kiếm và hiển thị lại toàn bộ danh sách phòng ban
    document.getElementById("searchInput").value = '';
    document.getElementById("searchType").selectedIndex = 0;
    loadDepartmentTableData();
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
            editButton.innerHTML = '<i class="fas fa-pencil-alt"></i>'; // Changed from textContent
            editButton.title = "Sửa thông tin phòng ban"; // Added title
            editButton.addEventListener('click', (event) => {
                event.stopPropagation();
                handleEditDepartment(dept.idPhongBan, dept.tenPhongBan);
            });
            actionsCell.appendChild(editButton);            const deleteButton = document.createElement('button');
            deleteButton.className = 'btn btn-danger';
            deleteButton.innerHTML = '<i class="fas fa-trash-alt"></i>'; // Changed from textContent
            deleteButton.title = "Xóa phòng ban"; // Added title
            deleteButton.addEventListener('click', (event) => {
                event.stopPropagation();
                handleDeleteDepartment(dept.idPhongBan, dept.tenPhongBan);
            });
            actionsCell.appendChild(deleteButton);

            row.style.cursor = "pointer";
            row.setAttribute('title', `Xem chi tiết ${escapeJsString(dept.tenPhongBan)}`);
            row.addEventListener('click', (event) => {
                if (event.target.tagName === 'BUTTON' || event.target.closest('button')) return;
                if (typeof window.quanLyPhongBanBridge !== 'undefined' && typeof window.quanLyPhongBanBridge.navigateToDepartmentDetail === 'function') {
                    window.quanLyPhongBanBridge.navigateToDepartmentDetail(dept.idPhongBan, dept.tenPhongBan);
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
    idInput.value = currentId;    nameInput.value = currentName;

    modal.style.display = "flex"; // Changed from "block" to "flex" for proper centering

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

function handleAddDepartment() {
	quanLyPhongBanBridge.log("Da vao handle add");
    const modal = document.getElementById("addDepartmentModal");
    const idInput = document.getElementById("addDeptIdInput");
    const nameInput = document.getElementById("addDeptNameInput");
    const saveBtn = document.getElementById("addDeptSaveBtn");
    const cancelBtn = document.getElementById("addDeptCancelBtn");    // Reset input values
    idInput.value = '';
    nameInput.value = '';

    modal.style.display = "flex"; // Changed from "block" to "flex" for proper centering
    
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

        if (typeof window.quanLyPhongBanBridge !== "undefined" && typeof window.quanLyPhongBanBridge.themPhongBan === "function") {
            if (window.quanLyPhongBanBridge.themPhongBan(newId, newName)) {
                showNotification("Thêm phòng ban thành công!", "success");
                loadDepartmentTableData();
            } else {
                showNotification("Thêm phòng ban thất bại. ID có thể đã tồn tại hoặc lỗi máy chủ.", "error");
            }
        } else {
            showNotification("Lỗi kết nối (themPhongBan).", "error");
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
    quanLyPhongBanBridge.log(`JS: handleDeleteDepartment CALLED - ID: ${id}, Name: "${escapeJsString(name)}"`);
    const confirmMessage = `Bạn có chắc chắn muốn xóa phòng ban "${escapeJsString(name)}" (ID: ${id})? Hành động này không thể hoàn tác.`;
    const confirmTitle = "Xác nhận xóa Phòng Ban";

    // Use the custom confirm modal
    showCustomConfirm(confirmMessage, confirmTitle,
        function() { // This is the onOk callback
            console.log(`JS: User confirmed deletion for ID: ${id}. Calling bridge function.`);
            if (typeof window.quanLyPhongBanBridge !== 'undefined' && typeof window.quanLyPhongBanBridge.xoaPhongBan === 'function') {
                try {
                    const success = window.quanLyPhongBanBridge.xoaPhongBan(id);
                    console.log(`JS: Bridge xoaPhongBan returned: ${success}`);
                    if (success) {
                        showNotification(`Đã xóa phòng ban "${escapeJsString(name)}".`, "success");
                        loadDepartmentTableData(); // Reload table data
                    } else {
                        showNotification(`Lỗi khi xóa phòng ban "${escapeJsString(name)}". Có thể phòng ban còn nhân viên hoặc lỗi máy chủ.`, "error");
                    }
                } catch (e) {
                    console.error("JS: Error calling quanLyPhongBanBridge.xoaPhongBan:", e);
                    showNotification("Lỗi nghiêm trọng khi gọi hàm xóa phòng ban.", "error");
                }
            } else {
                showNotification("Lỗi kết nối (xoaPhongBan).", "error");
                console.error("JS: quanLyPhongBanBridge.xoaPhongBan is not available.");
            }
        },
        function() { // This is the onCancel callback
            console.log("JS: User cancelled deletion for ID: " + id);
            // No action needed here if modal closes itself, or add modal.style.display = 'none'; if needed
        }
    );
}

function showCustomConfirm(message, title, onOk, onCancel) {
    const modal = document.getElementById('customConfirmModal');
    document.getElementById('customConfirmTitle').textContent = title || "Xác nhận";
    document.getElementById('customConfirmMessage').textContent = message;

    function handleOk() {
        modal.style.display = 'none';
        btnOk.removeEventListener('click', handleOk);
        btnCancel.removeEventListener('click', handleCancel);
        if (typeof onOk === 'function') onOk();
    }
    function handleCancel() {
        modal.style.display = 'none';
        btnOk.removeEventListener('click', handleOk);
        btnCancel.removeEventListener('click', handleCancel);
        if (typeof onCancel === 'function') onCancel();
    }

    const btnOk = document.getElementById('customConfirmOk');
    const btnCancel = document.getElementById('customConfirmCancel');    btnOk.addEventListener('click', handleOk);
    btnCancel.addEventListener('click', handleCancel);

    modal.style.display = 'flex'; // Changed from 'block' to 'flex' for proper centering
}