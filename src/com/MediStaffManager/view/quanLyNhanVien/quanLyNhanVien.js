// Chờ cho toàn bộ cây DOM được tải xong trước khi chạy script
document.addEventListener('DOMContentLoaded', function() {
    
    // =========================================================================
    // KHAI BÁO CÁC BIẾN TOÀN CỤC VÀ ELEMENT THAM CHIẾU
    // Đây là bước quan trọng bị thiếu trong code gốc, gây ra lỗi ReferenceError.
    // =========================================================================
    let currentEditId = null; // Biến lưu ID của nhân viên đang được chỉnh sửa
    const employeeFormModal = document.getElementById('employeeFormModal'); // Tham chiếu đến modal form
    const notificationDiv = document.getElementById('notifications'); // Tham chiếu đến khu vực thông báo
    const searchKeywordInput = document.getElementById('searchKeyword'); // Tham chiếu đến ô tìm kiếm


    // =========================================================================
    // KHỞI TẠO TRANG - Giữ nguyên cơ chế chờ Java Bridge
    // =========================================================================
    // Cơ chế timeout để chờ Java Bridge được inject vào.
    // Nếu ứng dụng của bạn cần nhiều thời gian hơn để khởi động, bạn có thể tăng con số này.
    setTimeout(() => {
        initializeNhanVienPageAfterJavaBridgeInjection();
    }, 1000); // Giữ nguyên delay 1 giây

    function initializeNhanVienPageAfterJavaBridgeInjection() {
        // Kiểm tra xem `window.bridge` đã tồn tại chưa
        if (window.bridge && typeof window.bridge.getQuanLyNhanVienBridge === 'function') {
            console.log("HTML: Java bridge đã sẵn sàng. Bắt đầu tải dữ liệu.");
            // Tải dữ liệu ban đầu khi bridge đã sẵn sàng
            loadInitialData();
            loadDropdownData();
        } else {
            console.warn("HTML: Java bridge chưa sẵn sàng. Vui lòng kiểm tra lại quá trình khởi tạo phía Java.");
            showNotification("error", "Không thể kết nối với ứng dụng. Vui lòng thử lại.");
        }
    }


    // =========================================================================
    // CÁC HÀM TƯƠNG TÁC VỚI JAVA BRIDGE
    // =========================================================================
    
    /**
     * Hàm trợ giúp để lấy đối tượng bridge một cách an toàn.
     * @returns {object|null} Đối tượng bridge hoặc null nếu không tồn tại.
     */
    function getBridge() {
        if (window.bridge && typeof window.bridge.getQuanLyNhanVienBridge === 'function') {
            return window.bridge.getQuanLyNhanVienBridge();
        }
        console.error("Lỗi nghiêm trọng: window.bridge hoặc getQuanLyNhanVienBridge không tồn tại.");
        showNotification("error", "Mất kết nối với ứng dụng.");
        return null;
    }

    function loadInitialData() {
        const bridge = getBridge();
        if (!bridge) return;
        try {
            const employeesJson = bridge.getAllEmployees();
            const employees = employeesJson ? JSON.parse(employeesJson) : [];
            renderTable(employees);
        } catch (e) {
            console.error("Lỗi khi tải dữ liệu ban đầu:", e);
            showNotification("error", "Không thể tải danh sách nhân viên: " + (e.message || ""));
        }
    }

    function loadDropdownData() {
        const bridge = getBridge();
        if (!bridge) return;
        try {
            const chucVuSelect = document.getElementById('tenChucVu');
            const chucVuJson = bridge.getAllTenChucVu();
            chucVuSelect.innerHTML = '<option value="">-- Chọn chức vụ --</option>';
            if (chucVuJson) {
                JSON.parse(chucVuJson).forEach(cv => chucVuSelect.add(new Option(cv, cv)));
            }

            const phongBanSelect = document.getElementById('tenPhongBan');
            const phongBanJson = bridge.getAllTenPhongBan();
            phongBanSelect.innerHTML = '<option value="">-- Chọn phòng ban --</option>';
            if (phongBanJson) {
                JSON.parse(phongBanJson).forEach(pb => phongBanSelect.add(new Option(pb, pb)));
            }
        } catch (e) {
            console.error("Lỗi khi tải dữ liệu dropdown:", e);
            showNotification("error", "Không thể tải dữ liệu Chức Vụ/Phòng Ban.");
        }
    }

    window.updateHeSoLuongFromChucVu = function() {
        const bridge = getBridge();
        if (!bridge) return;
        const tenChucVu = document.getElementById('tenChucVu').value;
        const heSoLuongInput = document.getElementById('heSoLuong');
        if (tenChucVu) {
            try {
                const hslJson = bridge.getHeSoLuongByTenChucVu(tenChucVu);
                if (hslJson) {
                    const hslString = JSON.parse(hslJson);
                    heSoLuongInput.value = (hslString !== null && hslString !== "") ? Number(hslString).toFixed(2) : '';
                } else {
                    heSoLuongInput.value = '';
                }
            } catch (e) {
                console.error("Lỗi khi lấy HSL cho " + tenChucVu + ":", e);
                heSoLuongInput.value = '';
            }
        } else {
            heSoLuongInput.value = '';
        }
    }


    // =========================================================================
    // CÁC HÀM XỬ LÝ GIAO DIỆN (UI)
    // =========================================================================
    
    function renderTable(employees) {
        const tbody = document.getElementById('employeeTable').querySelector('tbody');
        tbody.innerHTML = '';
        if (!employees || employees.length === 0) {
            tbody.innerHTML = `<tr><td colspan="11" style="padding: 1.5rem; text-align: center; color: #64748b;">Không có dữ liệu nhân viên.</td></tr>`;
            return;
        }
        employees.forEach(emp => {
            const row = tbody.insertRow();
            const formatDate = (dateString) => {
                if (!dateString) return '-';
                // Giả sử dateString có thể là yyyy-MM-dd hoặc dd/MM/yyyy từ Java
                const date = new Date(dateString);
                if (isNaN(date.getTime())) { // Nếu không phải định dạng chuẩn, thử parse dd/MM/yyyy
                    const parts = dateString.split('/');
                    if (parts.length === 3) {
                       // new Date(year, monthIndex, day)
                       const isoDate = new Date(parts[2], parts[1] - 1, parts[0]);
                       return isoDate.toLocaleDateString('vi-VN');
                    }
                    return dateString; // Trả về chuỗi gốc nếu không parse được
                }
                return date.toLocaleDateString('vi-VN');
            };

            row.insertCell().textContent = emp.idNhanVien ?? 'N/A';
            row.insertCell().textContent = emp.hoTen || '-';
            row.insertCell().textContent = emp.cccd || '-';
            row.insertCell().textContent = emp.sdt || '-';
            row.insertCell().textContent = emp.email || '-';
            row.insertCell().textContent = emp.gioiTinh || '-';
            row.insertCell().textContent = formatDate(emp.ngaySinh);
            row.insertCell().textContent = emp.tenChucVu || '-';
            row.insertCell().textContent = emp.tenPhongBan || '-';
            row.insertCell().textContent = (emp.heSoLuong !== undefined && emp.heSoLuong !== null) ? Number(emp.heSoLuong).toFixed(2) : '-';

            const actionsCell = row.insertCell();
            actionsCell.innerHTML = `
                <div class="action-buttons-cell">
                    <button title="Sửa thông tin" class="btn btn-primary action-btn"><i class="fas fa-edit"></i> Sửa</button>
                    <button title="Xóa nhân viên" class="btn btn-danger action-btn"><i class="fas fa-trash-alt"></i> Xóa</button>
                </div>`;
            actionsCell.querySelector('.btn-primary').onclick = () => showEditForm(emp);
            actionsCell.querySelector('.btn-danger').onclick = () => confirmDeleteEmployeeFromRow(emp.idNhanVien, emp.hoTen);
        });
    }

    window.searchEmployees = function() {
        const bridge = getBridge();
        if (!bridge) return;
        const keyword = searchKeywordInput.value.trim();
        const criteria = document.getElementById('searchCriteria').value;
        try {
            const employeesJson = bridge.searchEmployees(keyword, criteria);
            const employees = employeesJson ? JSON.parse(employeesJson) : [];
            renderTable(employees);
            if (employees.length === 0 && keyword) {
                showNotification("info", "Không tìm thấy nhân viên nào phù hợp.");
            }
        } catch (e) {
            console.error("Lỗi khi tìm kiếm:", e);
            showNotification("error", "Lỗi khi tìm kiếm: " + (e.message || ""));
        }
    }

    function clearForm() {
        document.getElementById('hoTen').value = '';
        document.getElementById('cccd').value = '';
        document.getElementById('sdt').value = '';
        document.getElementById('email').value = '';
        document.getElementById('gioiTinh').value = 'Nam';
        document.getElementById('ngaySinh').value = '';
        document.getElementById('tenChucVu').value = '';
        document.getElementById('heSoLuong').value = '';
        document.getElementById('tenPhongBan').value = '';
        currentEditId = null; // Quan trọng: reset ID khi xóa form
    }

    window.showAddForm = function() {
        clearForm();
        document.getElementById('formTitle').textContent = 'Thêm Nhân Viên Mới';
        document.getElementById('saveButton').innerHTML = '<i class="fas fa-save"></i> Thêm Mới';
        document.getElementById('deleteButtonInModal').classList.add('hidden');
        
        employeeFormModal.classList.add('show');
        document.body.style.overflow = 'hidden';
        document.getElementById('hoTen').focus();
    }

    function showEditForm(employee) {
        clearForm();
        document.getElementById('formTitle').textContent = `Chỉnh Sửa: ${employee.hoTen || 'Nhân Viên'}`;
        currentEditId = employee.idNhanVien;

        // Điền dữ liệu vào form
        document.getElementById('hoTen').value = employee.hoTen || '';
        document.getElementById('cccd').value = employee.cccd || '';
        document.getElementById('sdt').value = employee.sdt || '';
        document.getElementById('email').value = employee.email || '';
        document.getElementById('gioiTinh').value = employee.gioiTinh || 'Nam';

        // Xử lý định dạng ngày tháng cho input type="date" (yyyy-MM-dd)
        let ngaySinhFormatted = '';
        if (employee.ngaySinh) {
            const date = new Date(employee.ngaySinh);
             if (!isNaN(date.getTime())) {
                // Hỗ trợ cả `yyyy-MM-dd` và `MM/dd/yyyy`
                 ngaySinhFormatted = date.toISOString().split('T')[0];
            } else {
                 // Thử parse định dạng `dd/MM/yyyy`
                const parts = employee.ngaySinh.split('/');
                if (parts.length === 3) {
                    ngaySinhFormatted = `${parts[2]}-${parts[1].padStart(2, '0')}-${parts[0].padStart(2, '0')}`;
                }
            }
        }
        document.getElementById('ngaySinh').value = ngaySinhFormatted;
        
        document.getElementById('tenChucVu').value = employee.tenChucVu || '';
        updateHeSoLuongFromChucVu(); // Cập nhật HSL sau khi set chức vụ
        document.getElementById('tenPhongBan').value = employee.tenPhongBan || '';

        document.getElementById('saveButton').innerHTML = '<i class="fas fa-save"></i> Cập Nhật';
        document.getElementById('deleteButtonInModal').classList.remove('hidden');
        
        employeeFormModal.classList.add('show');
        document.body.style.overflow = 'hidden';
        document.getElementById('hoTen').focus();
    }

    window.hideFormModal = function() {
        employeeFormModal.classList.remove('show');
        document.body.style.overflow = '';
        clearForm();
    }
    
    function validateFormData(data) {
        const errors = [];
        if (!data.hoTen) errors.push("Họ tên không được để trống.");
        if (!data.cccd || !/^\d{12}$/.test(data.cccd)) errors.push("CCCD phải là 12 chữ số.");
        if (!data.sdt || !/^\d{10,11}$/.test(data.sdt)) errors.push("SĐT phải là 10 hoặc 11 chữ số.");
        if (data.email && data.email.trim() !== "" && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(data.email)) {
             errors.push("Email không đúng định dạng.");
        }
        if (!data.ngaySinh) errors.push("Ngày sinh không được để trống.");
        else {
            const birthDate = new Date(data.ngaySinh);
            const today = new Date();
            today.setHours(0,0,0,0);
            if (birthDate > today) errors.push("Ngày sinh không thể ở tương lai.");
        }
        if (!data.tenChucVu) errors.push("Vui lòng chọn chức vụ.");
        if (!data.tenPhongBan) errors.push("Vui lòng chọn phòng ban.");

        if (errors.length > 0) return errors.join('<br>');
        return null;
    }

    window.saveEmployee = function() {
        const bridge = getBridge();
        if (!bridge) return;

        const employeeData = {
            idNhanVien: currentEditId,
            hoTen: document.getElementById('hoTen').value.trim(),
            cccd: document.getElementById('cccd').value.trim(),
            sdt: document.getElementById('sdt').value.trim(),
            email: document.getElementById('email').value.trim() || null,
            gioiTinh: document.getElementById('gioiTinh').value,
            ngaySinh: document.getElementById('ngaySinh').value,
            tenChucVu: document.getElementById('tenChucVu').value,
            heSoLuong: document.getElementById('heSoLuong').value ? parseFloat(document.getElementById('heSoLuong').value) : null,
            tenPhongBan: document.getElementById('tenPhongBan').value
        };

        const validationError = validateFormData(employeeData);
        if (validationError) {
            showNotification("warning", validationError);
            return;
        }

        try {
            const employeeJson = JSON.stringify(employeeData);
            const resultMessage = currentEditId 
                ? bridge.updateEmployee(employeeJson) 
                : bridge.addEmployee(employeeJson);

            console.log("Java Request:", employeeJson, "Java Response:", resultMessage);
            
            if (resultMessage && typeof resultMessage === 'string') {
                if (resultMessage.toLowerCase().startsWith("success:")) {
                    showNotification("success", resultMessage.substring("Success:".length).trim());
                    loadInitialData();
                    hideFormModal();
                } else if (resultMessage.toLowerCase().startsWith("error:")) {
                    showNotification("error", resultMessage.substring("Error:".length).trim());
                } else {
                    showNotification("info", resultMessage);
                }
            } else {
                showNotification("error", "Không nhận được phản hồi hợp lệ từ ứng dụng.");
            }
        } catch(e) {
            console.error("Lỗi khi lưu nhân viên:", e);
            showNotification("error", "Lỗi khi lưu: " + (e.message || ""));
        }
    }

    function confirmDeleteEmployeeFromRow(employeeId, employeeName) {
        if (confirm(`Bạn có chắc chắn muốn xóa nhân viên "${employeeName || 'N/A'}" (ID: ${employeeId})?`)) {
            deleteEmployeeById(employeeId);
        }
    }

    window.confirmDeleteEmployeeFromModal = function() {
         if (!currentEditId) {
            showNotification("warning", "Không có nhân viên nào đang được chọn để xóa.");
            return;
        };
        const hoTen = document.getElementById('hoTen').value || "Nhân viên này";
        if (confirm(`Bạn có chắc chắn muốn xóa "${hoTen}" (ID: ${currentEditId})?`)) {
            deleteEmployeeById(currentEditId);
        }
    }

    function deleteEmployeeById(employeeId) {
        const bridge = getBridge();
        if (!bridge) return;
        try {
            const resultMessage = bridge.deleteEmployee(String(employeeId));
            console.log("Java Delete Response:", resultMessage);
             if (resultMessage && typeof resultMessage === 'string') {
                if (resultMessage.toLowerCase().startsWith("success:")) {
                    showNotification("success", resultMessage.substring("Success:".length).trim());
                    loadInitialData();
                    if (currentEditId === employeeId) hideFormModal();
                } else if (resultMessage.toLowerCase().startsWith("error:")) {
                    showNotification("error", resultMessage.substring("Error:".length).trim());
                } else {
                    showNotification("info", resultMessage);
                }
            } else {
                showNotification("error", "Không nhận được phản hồi hợp lệ khi xóa.");
            }
        } catch (e) {
            console.error("Lỗi khi xóa nhân viên:", e);
            showNotification("error", "Lỗi khi xóa: " + (e.message || ""));
        }
    }

    // =========================================================================
    // HÀM TIỆN ÍCH VÀ EVENT LISTENERS
    // =========================================================================

    let notificationTimeout;
    function showNotification(type, message) {
        if (notificationTimeout) clearTimeout(notificationTimeout);
        
        const typeClasses = {
            success: 'notification-success',
            error: 'notification-error',
            warning: 'notification-warning',
            info: 'notification-info'
        };
        const iconClasses = {
            success: 'fa-check-circle',
            error: 'fa-times-circle',
            warning: 'fa-exclamation-triangle',
            info: 'fa-info-circle'
        };
        
        notificationDiv.innerHTML = `<span class="notification-icon"><i class="fas ${iconClasses[type] || 'fa-info-circle'}"></i></span>
                                     <span class="notification-message">${message}</span>`;
        notificationDiv.className = `notification show ${typeClasses[type] || 'notification-info'}`;
        
        const duration = (type === 'error' || type === 'warning') ? 7000 : 5000;
        notificationTimeout = setTimeout(() => {
            notificationDiv.classList.remove('show');
        }, duration);
    }
    
    // Gán các hàm vào window để HTML có thể gọi qua `onclick="..."`
    // Điều này cần thiết vì code được gói trong 'DOMContentLoaded'
    window.showAddForm = showAddForm;
    window.hideFormModal = hideFormModal;
    window.saveEmployee = saveEmployee;
    window.searchEmployees = searchEmployees;
    window.updateHeSoLuongFromChucVu = updateHeSoLuongFromChucVu;
    window.confirmDeleteEmployeeFromModal = confirmDeleteEmployeeFromModal;

    // Các Event Listener khác
    searchKeywordInput.addEventListener('keypress', function(event) {
        if (event.key === 'Enter') {
            event.preventDefault();
            searchEmployees();
        }
    });

    employeeFormModal.addEventListener('click', (event) => {
        // Đóng modal khi click vào vùng overlay bên ngoài
        if (event.target === employeeFormModal) {
            hideFormModal();
        }
    });

    document.addEventListener('keydown', (event) => {
        // Đóng modal khi nhấn phím Escape
        if (event.key === 'Escape' && employeeFormModal.classList.contains('show')) {
            hideFormModal();
        }
    });

});