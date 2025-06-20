/**
 * main.js
 * Chịu trách nhiệm khởi tạo chung cho ứng dụng web trong WebView và cung cấp các hàm tiện ích.
 * Giữ nguyên cơ chế timeout để đảm bảo Java Bridge sẵn sàng.
 */

// Chờ DOM được tải hoàn toàn rồi mới thực thi
document.addEventListener('DOMContentLoaded', function() {
    // Sử dụng setTimeout để đảm bảo Java Bridge (từ phía Android) đã được "tiêm" vào WebView.
    // Đây là một biện pháp an toàn cần thiết trong môi trường WebView.
    setTimeout(() => {
        if (typeof window.bridge !== 'undefined' && typeof window.bridge.log === 'function') {
            window.bridge.log("JS: DOMContentLoaded & Timeout hoàn tất. Bridge đã sẵn sàng. Bắt đầu khởi tạo trang...");
            initializeCorrectPage();
        } else {
            console.error("JS: Java bridge không khả dụng sau khi chờ. Không thể khởi tạo trang.");
            // Hiển thị thông báo lỗi trực tiếp nếu không có bridge
            alert("Lỗi nghiêm trọng: Không thể kết nối tới ứng dụng gốc. Vui lòng khởi động lại ứng dụng.");
        }
    }, 500); // 500ms là khoảng thời gian chờ an toàn
});

/**
 * Xác định trang nào đang được hiển thị dựa trên sự tồn tại của một phần tử HTML đặc trưng
 * và gọi hàm khởi tạo tương ứng.
 * Điều này giúp mã nguồn gọn gàng và tránh lỗi khi gọi hàm của một trang không hoạt động.
 */
function initializeCorrectPage() {
    // Nếu tìm thấy bảng phòng ban, đây là trang quản lý phòng ban.
    if (document.getElementById('departmentTable')) {
        window.bridge.log("JS: Phát hiện trang Quản lý Phòng Ban. Đang khởi tạo...");
        if (typeof initializeDepartmentPage === 'function') {
            initializeDepartmentPage();
        } else {
            window.bridge.log("JS LỖI: Hàm initializeDepartmentPage() không được tìm thấy!");
        }
    }
    // Nếu tìm thấy bảng chi tiết nhân viên, đây là trang chi tiết.
    // (Giả định trang chi tiết nhân viên có một table với id này)
    else if (document.getElementById('employeeDetailTable')) {
        window.bridge.log("JS: Phát hiện trang Chi tiết Nhân viên. Đang khởi tạo...");
        if (typeof initializeEmployeePage === 'function') {
            initializeEmployeePage();
        } else {
            window.bridge.log("JS LỖI: Hàm initializeEmployeePage() không được tìm thấy!");
        }
    }
    // Thêm các `else if` khác cho các trang khác nếu cần
    else {
        window.bridge.log("JS: Không phát hiện được nội dung trang cụ thể để khởi tạo.");
    }
}


// =========================================================================
// HÀM TIỆN ÍCH DÙNG CHUNG (Tập trung tại main.js)
// =========================================================================

/**
 * Hiển thị thông báo dạng modal tùy chỉnh với các loại: success, error, warning, info.
 * @param {string} message Nội dung thông báo.
 * @param {string} title Tiêu đề của modal.
 * @param {string} type Loại thông báo ('success', 'error', 'warning', 'info').
 * @param {function} onOkCallback Hàm sẽ được gọi khi người dùng nhấn nút OK.
 */
window.showNotificationModal = function(message, title, type = 'info', onOkCallback) {
    const modal = document.getElementById('notificationModal');
    if (!modal) return;

    document.getElementById('notificationTitle').textContent = title || 'Thông báo';
    document.getElementById('notificationModalMessage').innerHTML = message;

    const iconElement = document.getElementById('notificationIcon').querySelector('span');
    iconElement.className = ''; // Xóa class cũ
    const icons = {
        success: 'fas fa-check-circle',
        error: 'fas fa-times-circle',
        warning: 'fas fa-exclamation-triangle',
        info: 'fas fa-info-circle'
    };
    iconElement.classList.add(...(icons[type] || icons.info).split(' '));
    iconElement.parentElement.className = `notification-icon ${type}`;

    modal.style.display = 'flex';

    const okBtn = document.getElementById('notificationOkBtn');
    const closeBtn = document.getElementById('closeNotificationBtn');

    // Dùng cloneNode để xóa listener cũ
    const newOkBtn = okBtn.cloneNode(true);
    okBtn.parentNode.replaceChild(newOkBtn, okBtn);
    
    const newCloseBtn = closeBtn.cloneNode(true);
    closeBtn.parentNode.replaceChild(newCloseBtn, closeBtn);

    const closeModal = () => {
        modal.style.display = 'none';
        if (typeof onOkCallback === 'function') {
            onOkCallback();
        }
    };
    
    newOkBtn.addEventListener('click', closeModal);
    newCloseBtn.addEventListener('click', () => { modal.style.display = 'none'; });
};


/**
 * Hiển thị một modal xác nhận tùy chỉnh.
 * @param {string} message Nội dung câu hỏi xác nhận (hỗ trợ HTML).
 * @param {string} title Tiêu đề của modal.
 * @param {function} onOk Hàm callback khi người dùng nhấn "Đồng ý".
 * @param {function} [onCancel] Hàm callback tùy chọn khi người dùng nhấn "Hủy".
 */
window.showCustomConfirm = function(message, title, onOk, onCancel) {
    const modal = document.getElementById('customConfirmModal');
    const confirmTitle = document.getElementById('customConfirmTitle');
    const confirmMessage = document.getElementById('customConfirmMessage');
    const btnOk = document.getElementById('customConfirmOk');
    const btnCancel = document.getElementById('customConfirmCancel');

    if (!modal || !confirmTitle || !confirmMessage || !btnOk || !btnCancel) {
        console.error("JS Lỗi: Không tìm thấy các thành phần của modal xác nhận!");
        // Fallback về hàm confirm gốc của trình duyệt nếu UI bị lỗi
        if (confirm(message.replace(/<[^>]*>/g, ''))) { // Xóa tag HTML cho confirm gốc
            if (typeof onOk === 'function') onOk();
        } else {
            if (typeof onCancel === 'function') onCancel();
        }
        return;
    }

    confirmTitle.textContent = title || "Xác nhận hành động";
    confirmMessage.innerHTML = message; // Dùng innerHTML để hỗ trợ thẻ <br>, <strong>...

    // Sử dụng cloneNode để xóa các event listener cũ một cách an toàn
    const newBtnOk = btnOk.cloneNode(true);
    const newBtnCancel = btnCancel.cloneNode(true);
    btnOk.parentNode.replaceChild(newBtnOk, btnOk);
    btnCancel.parentNode.replaceChild(newBtnCancel, btnCancel);

    const closeModal = () => {
        modal.style.display = 'none';
    };

    newBtnOk.addEventListener('click', () => {
        closeModal();
        if (typeof onOk === 'function') onOk();
    }, { once: true }); // Đảm bảo chỉ chạy 1 lần

    newBtnCancel.addEventListener('click', () => {
        closeModal();
        if (typeof onCancel === 'function') onCancel();
    }, { once: true });

    modal.style.display = 'flex';
};