/**
 * phongBan.js
 * Chứa logic cho trang danh sách phòng ban (thêm, sửa, xóa, tìm kiếm).
 */

/**
 * Hàm khởi tạo chính cho trang phòng ban.
 */
function initializeDepartmentPage() {
    loadDepartmentTableData();
    setupDepartmentPageEventListeners();
}

/**
 * Gắn các sự kiện cho các nút và input trên trang phòng ban.
 */
function setupDepartmentPageEventListeners() {
    // Helper function để gắn lại sự kiện một cách an toàn
    const reattachListener = (elementId, handler, event = 'click') => {
        const element = document.getElementById(elementId);
        if (element) {
            const newElement = element.cloneNode(true);
            element.parentNode.replaceChild(newElement, element);
            newElement.addEventListener(event, handler);
            return newElement;
        }
        return null;
    };

    reattachListener('btnAddDepartment', handleAddDepartmentClick);
    reattachListener('btnSearch', handleSearchClick);
    reattachListener('btnReset', handleResetSearchClick);
    
    // Thêm sự kiện nhấn Enter cho ô tìm kiếm
    const searchInput = document.getElementById("searchInput");
    if (searchInput) {
        searchInput.addEventListener('keypress', (event) => {
            if (event.key === 'Enter') {
                event.preventDefault(); // Ngăn hành vi mặc định của form
                handleSearchClick();
            }
        });
    }
}

/**
 * Tải và hiển thị dữ liệu phòng ban từ Java Bridge.
 */
function loadDepartmentTableData() {
    const tbody = document.querySelector("#departmentTable tbody");
    tbody.innerHTML = '<tr><td colspan="3" style="text-align:center;">Đang tải dữ liệu...</td></tr>';
    try {
        // Giả định bridge đã sẵn sàng
        const bridge = window.bridge.getQuanLyPhongBanBridge();
        const departmentsJson = bridge.layDanhSachPhongBan();
        const departments = JSON.parse(departmentsJson);
        displayDepartmentData(departments);
    } catch (e) {
        console.error("JS Lỗi khi tải dữ liệu phòng ban:", e);
        showNotificationModal("Không thể tải dữ liệu phòng ban. Vui lòng thử lại.", "Lỗi", "error");
        tbody.innerHTML = '<tr><td colspan="3" style="text-align:center;">Lỗi tải dữ liệu.</td></tr>';
    }
}

/**
 * Render dữ liệu phòng ban ra bảng.
 * @param {Array} departments Mảng các đối tượng phòng ban.
 */
function displayDepartmentData(departments) {
    const tbody = document.querySelector("#departmentTable tbody");
    tbody.innerHTML = ''; // Xóa nội dung cũ

    if (!departments || departments.length === 0) {
        tbody.innerHTML = '<tr><td colspan="3" style="text-align:center;">Không có dữ liệu phòng ban.</td></tr>';
        return;
    }

    departments.forEach(dept => {
        const row = tbody.insertRow();
        row.setAttribute('data-id', dept.idPhongBan);
        row.innerHTML = `
            <td>${dept.idPhongBan}</td>
            <td>${dept.tenPhongBan}</td>
            <td class="actions-cell">
                <button class="btn btn-edit" title="Sửa thông tin"><i class="fas fa-pencil-alt"></i></button>
                <button class="btn btn-danger" title="Xóa phòng ban"><i class="fas fa-trash-alt"></i></button>
            </td>
        `;

        // Gắn sự kiện cho các nút hành động
        row.querySelector('.btn-edit').addEventListener('click', (event) => {
            event.stopPropagation(); // Ngăn sự kiện click của dòng
            openDepartmentModal(true, dept.idPhongBan, dept.tenPhongBan);
        });

        row.querySelector('.btn-danger').addEventListener('click', (event) => {
            event.stopPropagation();
            handleDeleteDepartmentClick(dept.idPhongBan, dept.tenPhongBan);
        });

        // Gắn sự kiện click cho cả dòng để xem chi tiết
        row.addEventListener('click', () => {
            try {
                 window.bridge.getQuanLyPhongBanBridge().navigateToDepartmentDetail(dept.idPhongBan, dept.tenPhongBan, window.bridge.getWebView());
            } catch (e) {
                console.error("JS Lỗi khi điều hướng:", e);
                showNotificationModal("Lỗi khi mở trang chi tiết.", "Lỗi", "error");
            }
        });
    });
}

/**
 * Xử lý sự kiện click nút tìm kiếm.
 */
function handleSearchClick() {
    const searchType = document.getElementById("searchType").value;
    const searchValue = document.getElementById("searchInput").value.trim();

    if (!searchValue) {
        showNotificationModal("Vui lòng nhập từ khóa tìm kiếm.", "Cảnh báo", "warning");
        return;
    }

    try {
        const bridge = window.bridge.getQuanLyPhongBanBridge();
        let resultJson;
        if (searchType === "id") {
            if (isNaN(parseInt(searchValue, 10)) || !Number.isInteger(Number(searchValue))) {
                showNotificationModal("ID phải là một số nguyên.", "Lỗi", "error");
                return;
            }
            resultJson = bridge.timKiemPhongBanTheoId(searchValue);
        } else { // searchType === "ten"
            resultJson = bridge.timKiemPhongBanTheoTen(searchValue);
        }
        const results = JSON.parse(resultJson);
        displayDepartmentData(results);
        if (results.length === 0) {
           showNotificationModal("Không tìm thấy kết quả nào phù hợp.", "Thông báo", "info");
        }
    } catch (e) {
        console.error("JS Lỗi khi tìm kiếm:", e);
        showNotificationModal("Đã xảy ra lỗi trong quá trình tìm kiếm.", "Lỗi", "error");
    }
}

/**
 * Xử lý sự kiện click nút làm mới tìm kiếm.
 */
function handleResetSearchClick() {
    document.getElementById("searchInput").value = '';
    document.getElementById("searchType").selectedIndex = 0;
    loadDepartmentTableData();
}

/**
 * Xử lý sự kiện click nút thêm phòng ban.
 */
function handleAddDepartmentClick() {
    openDepartmentModal(false); // false = chế độ thêm mới
}

/**
 * Xử lý logic xóa phòng ban.
 * @param {number} id ID phòng ban.
 * @param {string} name Tên phòng ban.
 */
function handleDeleteDepartmentClick(id, name) {
    showCustomConfirm(
        `Bạn có chắc chắn muốn xóa phòng ban <strong>"${name}"</strong> (ID: ${id})?<br>Hành động này sẽ không thể hoàn tác.`,
        "Xác nhận Xóa",
        () => { // onOk callback
            try {
                const bridge = window.bridge.getQuanLyPhongBanBridge();
                const success = bridge.xoaPhongBan(id);
                if (success) {
                    showNotificationModal(`Đã xóa thành công phòng ban "${name}".`, "Thành công", "success", () => {
                         loadDepartmentTableData();
                    });
                } else {
                    showNotificationModal("Xóa thất bại. Phòng ban có thể vẫn còn nhân viên hoặc đã xảy ra lỗi.", "Lỗi", "error");
                }
            } catch (e) {
                console.error("JS Lỗi khi gọi bridge.xoaPhongBan:", e);
                showNotificationModal("Lỗi nghiêm trọng khi thực hiện thao tác xóa.", "Lỗi", "error");
            }
        }
    );
}

/**
 * Mở và quản lý modal cho việc Thêm hoặc Sửa phòng ban.
 * @param {boolean} isEdit - true nếu là Sửa, false nếu là Thêm.
 * @param {number|null} currentId - ID hiện tại (chỉ dùng khi Sửa).
 * @param {string} currentName - Tên hiện tại (chỉ dùng khi Sửa).
 */
function openDepartmentModal(isEdit, currentId = null, currentName = '') {
    // Chọn đúng modal dựa trên hành động
    const modal = document.getElementById(isEdit ? 'departmentModal' : 'addDepartmentModal');
    if (!modal) {
        console.error(`JS Lỗi: Không tìm thấy modal cho hành động ${isEdit ? 'sửa' : 'thêm'}`);
        return;
    }

    // Lấy các element trong modal
    const titleElement = modal.querySelector('h3');
    const idInput = modal.querySelector('input[type="number"]');
    const nameInput = modal.querySelector('input[type="text"]');
    const saveBtn = modal.querySelector('button[id*="SaveBtn"]');
    const cancelBtn = modal.querySelector('button[id*="CancelBtn"]');
    const closeBtn = modal.querySelector('.close-button');

    // Cấu hình modal
    titleElement.textContent = isEdit ? 'Sửa Phòng Ban' : 'Thêm Phòng Ban';
    idInput.value = isEdit ? currentId : '';
    nameInput.value = isEdit ? currentName : '';
    // Khi sửa, ID gốc không thể thay đổi
    idInput.readOnly = isEdit;


    const closeModal = () => {
        modal.style.display = 'none';
    };

    const onSave = () => {
        const newId = parseInt(idInput.value.trim(), 10);
        const newName = nameInput.value.trim();

        if (!newName) {
            showNotificationModal("Tên phòng ban không được để trống.", "Lỗi", "error");
            return;
        }
        
        if (isNaN(newId) || newId <= 0) {
            showNotificationModal("ID phòng ban phải là một số nguyên dương.", "Lỗi", "error");
            return;
        }

        try {
            const bridge = window.bridge.getQuanLyPhongBanBridge();
            let success = false;
            if (isEdit) {
                // Khi sửa, ID mới có thể giống ID cũ, hoặc khác (nếu cho phép đổi ID)
                // Giả sử API sửa chỉ cần ID gốc và dữ liệu mới
                success = bridge.suaPhongBan(currentId, newId, newName);
            } else {
                success = bridge.themPhongBan(newId, newName);
            }

            if (success) {
                const actionText = isEdit ? 'Cập nhật' : 'Thêm mới';
                 showNotificationModal(`${actionText} phòng ban thành công!`, "Thành công", "success", () => {
                    loadDepartmentTableData();
                    closeModal();
                 });
            } else {
                const actionText = isEdit ? 'Cập nhật' : 'Thêm mới';
                showNotificationModal(`${actionText} thất bại. ID có thể đã tồn tại hoặc dữ liệu không hợp lệ.`, "Lỗi", "error");
            }
        } catch (e) {
            console.error(`JS Lỗi khi ${isEdit ? 'sửa' : 'thêm'} phòng ban:`, e);
            showNotificationModal("Đã xảy ra lỗi phía ứng dụng. Vui lòng thử lại.", "Lỗi", "error");
        }
    };

    // Gắn lại sự kiện bằng cloneNode để tránh bị lặp
    const newSaveBtn = saveBtn.cloneNode(true);
    saveBtn.parentNode.replaceChild(newSaveBtn, saveBtn);
    newSaveBtn.addEventListener('click', onSave);

    const newCancelBtn = cancelBtn.cloneNode(true);
    cancelBtn.parentNode.replaceChild(newCancelBtn, cancelBtn);
    newCancelBtn.addEventListener('click', closeModal);
    
    const newCloseBtn = closeBtn.cloneNode(true);
    closeBtn.parentNode.replaceChild(newCloseBtn, closeBtn);
    newCloseBtn.addEventListener('click', closeModal);

    modal.style.display = 'flex';
    nameInput.focus();
}