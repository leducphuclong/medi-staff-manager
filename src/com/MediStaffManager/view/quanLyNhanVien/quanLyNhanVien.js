document.addEventListener('DOMContentLoaded', function() {
    setTimeout(() => {
        initializeNhanVienPageAfterJavaBridgeInjection()
    }, 500); // Increased delay slightly
});

function initializeNhanVienPageAfterJavaBridgeInjection() {
    if (window.bridge) {
        window.bridge.log("da vao 1");
        console.log("HTML: Java bridge 'nhanVienBridge' is ready. Initializing page.");
        loadInitialData();
        loadDropdownData();
    } else {
        console.warn("HTML: nhanVienBridge not ready yet. Waiting for injection.");
    }
}

function getBridge() {
    return window.bridge.getQuanLyNhanVienBridge();
}

function loadInitialData() { 
            const bridge = getBridge();
            if (!bridge) return;
            try {
                const employeesJson = getBridge().getAllEmployees();
                const employees = employeesJson ? JSON.parse(employeesJson) : [];
                renderTable(employees);
            } catch (e) {
                console.error("Error loading initial data:", e);
                showNotification("error", "Không thể tải danh sách nhân viên. " + (e.message || ""));
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
                    const chucVuList = JSON.parse(chucVuJson);
                    chucVuList.forEach(cv => chucVuSelect.add(new Option(cv, cv)));
                }
 
                const phongBanSelect = document.getElementById('tenPhongBan');
                const phongBanJson = bridge.getAllTenPhongBan();
                phongBanSelect.innerHTML = '<option value="">-- Chọn phòng ban --</option>';
                if (phongBanJson) {
                    const phongBanList = JSON.parse(phongBanJson);
                    phongBanList.forEach(pb => phongBanSelect.add(new Option(pb, pb)));
                }
            } catch (e) {
                console.error("Error loading dropdown data:", e);
                showNotification("error", "Không thể tải dữ liệu Chức Vụ/Phòng Ban.");
            }
        }
        function updateHeSoLuongFromChucVu() { 
             const bridge = getBridge();
            if (!bridge) return;
            const tenChucVu = document.getElementById('tenChucVu').value;
            const heSoLuongInput = document.getElementById('heSoLuong');
            if (tenChucVu) {
                try {
                    const hslJson = getBridge().getHeSoLuongByTenChucVu(tenChucVu);
                    if (hslJson) {
                        const hslString = JSON.parse(hslJson);
                        heSoLuongInput.value = (hslString !== null && hslString !== "") ? Number(hslString).toFixed(2) : '';
                    } else {
                        heSoLuongInput.value = '';
                    }
                } catch (e) {
                    console.error("Error fetching HSL for " + tenChucVu + ":", e);
                    heSoLuongInput.value = '';
                }
            } else {
                heSoLuongInput.value = '';
            }
        }
        function renderTable(employees) { 
            const tbody = document.getElementById('employeeTable').querySelector('tbody');
            tbody.innerHTML = '';
            if (!employees || employees.length === 0) {
                tbody.innerHTML = `<tr><td colspan="11" style="padding: 1.5rem; text-align: center; color: #64748b;">Không có nhân viên nào.</td></tr>`;
                return;
            }
            employees.forEach(emp => {
                const row = tbody.insertRow();
                
                const addCell = (text) => {
                    const cell = row.insertCell();
                    cell.textContent = text;
                    return cell;
                };
 
                addCell(emp.idNhanVien ?? 'N/A');
                addCell(emp.hoTen || '-');
                addCell(emp.cccd || '-');
                addCell(emp.sdt || '-');
                addCell(emp.email || '-');
                addCell(emp.gioiTinh || '-');
                addCell(emp.ngaySinh ? new Date(emp.ngaySinh).toLocaleDateString('vi-VN') : '-');
                addCell(emp.tenChucVu || '-');
                addCell(emp.tenPhongBan || '-');
                addCell((emp.heSoLuong !== undefined && emp.heSoLuong !== null) ? Number(emp.heSoLuong).toFixed(2) : '-');
 
                const actionsCell = row.insertCell();
                
                const buttonContainer = document.createElement('div');
                buttonContainer.className = 'action-buttons-cell';
 
                const editButton = document.createElement('button');
                editButton.innerHTML = '<i class="fas fa-edit"></i> Sửa';
                editButton.title = "Sửa thông tin";
                editButton.className = 'btn btn-primary action-btn';
                editButton.onclick = () => showEditForm(emp);
                buttonContainer.appendChild(editButton);
 
                const deleteButton = document.createElement('button');
                deleteButton.innerHTML = '<i class="fas fa-trash-alt"></i> Xóa';
                deleteButton.title = "Xóa nhân viên";
                deleteButton.className = 'btn btn-danger action-btn';
                deleteButton.onclick = () => confirmDeleteEmployeeFromRow(emp.idNhanVien, emp.hoTen);
                buttonContainer.appendChild(deleteButton);
                
                actionsCell.appendChild(buttonContainer);
            });
        }
        function searchEmployees() { 
            const bridge = getBridge();
            if (!bridge) return;
            let keyword = document.getElementById('searchKeyword').value.trim();
            const criteria = document.getElementById('searchCriteria').value;
            try {
                const employeesJson = bridge.searchEmployees(keyword, criteria);
                const employees = employeesJson ? JSON.parse(employeesJson) : [];
                renderTable(employees);
                if (employees.length === 0 && keyword) {
                    showNotification("info", "Không tìm thấy nhân viên nào phù hợp.");
                }
            } catch (e) {
                console.error("Error searching employees:", e);
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
            currentEditId = null;
        }
        function showAddForm() { 
            clearForm();
            document.getElementById('formTitle').textContent = 'Thêm Nhân Viên Mới';
            document.getElementById('saveButton').innerHTML = '<i class="fas fa-save"></i> Thêm Mới';
            document.getElementById('deleteButtonInModal').classList.add('hidden');
            
            employeeFormModal.classList.add('show');
            document.body.style.overflow = 'hidden';
            document.getElementById('hoTen').focus();
            updateHeSoLuongFromChucVu();
        }
        function showEditForm(employee) { 
            clearForm();
            document.getElementById('formTitle').textContent = `Chỉnh Sửa: ${employee.hoTen || 'Nhân Viên'}`;
            currentEditId = employee.idNhanVien;
            document.getElementById('hoTen').value = employee.hoTen || '';
            document.getElementById('cccd').value = employee.cccd || '';
            document.getElementById('sdt').value = employee.sdt || '';
            document.getElementById('email').value = employee.email || '';
            document.getElementById('gioiTinh').value = employee.gioiTinh || 'Nam';
            
            let ngaySinhFormatted = '';
            if (employee.ngaySinh) {
                if (employee.ngaySinh.includes('/')) { 
                    const parts = employee.ngaySinh.split('/');
                    if (parts.length === 3) {
                        ngaySinhFormatted = `${parts[2]}-${parts[1].padStart(2, '0')}-${parts[0].padStart(2, '0')}`;
                    }
                } else { 
                    ngaySinhFormatted = employee.ngaySinh;
                }
            }
            document.getElementById('ngaySinh').value = ngaySinhFormatted;
            
            document.getElementById('tenChucVu').value = employee.tenChucVu || '';
            updateHeSoLuongFromChucVu(); 
            document.getElementById('tenPhongBan').value = employee.tenPhongBan || '';
 
            document.getElementById('saveButton').innerHTML = '<i class="fas fa-save"></i> Cập Nhật';
            document.getElementById('deleteButtonInModal').classList.remove('hidden');
            
            employeeFormModal.classList.add('show');
            document.body.style.overflow = 'hidden';
            document.getElementById('hoTen').focus();
        }
        function hideFormModal() { 
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
        function saveEmployee() { 
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
            let resultMessage;
            const employeeJson = JSON.stringify(employeeData);
            try {
                if (currentEditId) {
                    resultMessage = bridge.updateEmployee(employeeJson);
                } else {
                    resultMessage = bridge.addEmployee(employeeJson);
                }
                console.log("Java Request:", employeeJson, "Java Response:", resultMessage);
                if (resultMessage && typeof resultMessage === 'string') {
                    if (resultMessage.toLowerCase().startsWith("success:")) {
                        showNotification("success", resultMessage.substring("Success:".length).trim());
                        loadInitialData();
                        hideFormModal();
                    } else if (resultMessage.toLowerCase().startsWith("error:")) {
                        showNotification("error", resultMessage.substring("Error:".length).trim());
                    } else { showNotification("info", resultMessage); }
                } else { showNotification("error", "Không nhận được phản hồi hợp lệ."); }
            } catch(e) {
                console.error("Error saving employee:", e);
                showNotification("error", "Lỗi khi lưu: " + (e.message || ""));
            }
        }
        function confirmDeleteEmployeeFromRow(employeeId, employeeName) { 
            if (confirm(`Bạn có chắc chắn muốn xóa nhân viên "${employeeName || 'N/A'}" (ID: ${employeeId})?`)) {
                deleteEmployeeById(employeeId);
            }
        }
        function confirmDeleteEmployeeFromModal() { 
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
                    } else { showNotification("info", resultMessage); }
                } else { showNotification("error", "Không nhận được phản hồi hợp lệ khi xóa."); }
            } catch (e) {
                console.error("Error deleting employee:", e);
                showNotification("error", "Lỗi khi xóa: " + (e.message || ""));
            }
        }
 
        let notificationTimeout;
        function showNotification(type, message) {
            if (notificationTimeout) clearTimeout(notificationTimeout);
            
            let typeClass = '';
            let iconClass = '';
            switch(type) {
                case 'success': typeClass = 'notification-success'; iconClass = 'fas fa-check-circle'; break;
                case 'error':   typeClass = 'notification-error';   iconClass = 'fas fa-times-circle'; break;
                case 'warning': typeClass = 'notification-warning'; iconClass = 'fas fa-exclamation-triangle'; break;
                default:        typeClass = 'notification-info';    iconClass = 'fas fa-info-circle';
            }
            
            notificationDiv.innerHTML = `<span class="notification-icon"><i class="${iconClass}"></i></span>
                                         <span class="notification-message">${message}</span>`;
            notificationDiv.className = `notification ${typeClass}`; // Gán lại class cơ sở và class type
            
            notificationDiv.classList.remove('hiding'); 
            void notificationDiv.offsetWidth; 
            notificationDiv.classList.add('show');
 
            const duration = (type === 'error' || type === 'warning') ? 7000 : 5000;
            notificationTimeout = setTimeout(() => {
                notificationDiv.classList.add('hiding'); 
                // Không cần xóa class 'show' ngay, CSS transition cho visibility khi 'hiding' được thêm sẽ làm việc đó
                // Sau một khoảng thời gian bằng transition, có thể reset hoàn toàn nếu muốn
                setTimeout(() => {
                    if(notificationDiv.classList.contains('hiding')) {
                       notificationDiv.classList.remove('show', 'hiding');
                       // Reset lại style ban đầu để chuẩn bị cho lần hiển thị sau
                       notificationDiv.style.opacity = '0'; 
                       notificationDiv.style.visibility = 'hidden';
                       // Nếu có transform trong trạng thái ẩn ban đầu, reset cả nó
                       // notificationDiv.style.transform = 'translateX(-50%) translateY(-100%)';
                    }
                }, 300); // Phải khớp với thời gian transition của opacity/transform khi ẩn
            }, duration);
        }
 
        document.getElementById('searchKeyword').addEventListener('keypress', function(event) { 
             if (event.key === 'Enter') {
                event.preventDefault();
                searchEmployees();
            }
        });
        employeeFormModal.addEventListener('click', (event) => { 
            if (event.target === employeeFormModal && employeeFormModal.classList.contains('show')) {
                hideFormModal();
            }
        });
        document.addEventListener('keydown', (event) => { 
            if (event.key === 'Escape' && employeeFormModal.classList.contains('show')) {
                hideFormModal();
            }
        });
