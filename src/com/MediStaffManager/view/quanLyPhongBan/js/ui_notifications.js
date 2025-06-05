// In js/ui_notifications.js
console.log("JS: ui_notifications.js loaded.");

function showNotification(message, type = 'success') { // type có thể là 'success', 'error', 'warning', 'info'
    // Sử dụng modal thông báo thay vì thanh thông báo
    const notificationModal = document.getElementById('notificationModal');
    const notificationTitle = document.getElementById('notificationTitle');
    const notificationModalMessage = document.getElementById('notificationModalMessage');
    const notificationOkBtn = document.getElementById('notificationOkBtn');
    const closeNotificationBtn = document.getElementById('closeNotificationBtn');
    
    if (!notificationModal || !notificationModalMessage) {
        console.warn("JS: Notification modal elements not found. Using old notification area.");
        useOldNotificationMethod(message, type);
        return;
    }
    
    // Cập nhật nội dung
    notificationModalMessage.textContent = message;
    
    // Đặt tiêu đề dựa trên loại thông báo
    switch(type) {
        case 'error':
            notificationTitle.textContent = "Lỗi";
            break;
        case 'warning':
            notificationTitle.textContent = "Cảnh báo";
            break;
        case 'info':
            notificationTitle.textContent = "Thông tin";
            break;
        default:
            notificationTitle.textContent = "Thành công";
    }
    
    // Làm mới class
    notificationModal.className = 'modal-overlay'; // Xóa class cũ
    notificationModal.classList.add('modal-overlay');
    
    // Thêm class cho modal content
    const modalContent = notificationModal.querySelector('.modal-content');
    if (modalContent) {
        modalContent.className = 'modal-content notification-modal';
        modalContent.classList.add(type);
    }
    
    // Gán sự kiện
    const handleClose = () => {
        notificationModal.style.display = 'none';
    };
    
    // Clone và thay thế các button để xóa event cũ
    const newOkBtn = notificationOkBtn.cloneNode(true);
    notificationOkBtn.parentNode.replaceChild(newOkBtn, notificationOkBtn);
    newOkBtn.addEventListener('click', handleClose);
    
    const newCloseBtn = closeNotificationBtn.cloneNode(true);
    closeNotificationBtn.parentNode.replaceChild(newCloseBtn, closeNotificationBtn);
    newCloseBtn.addEventListener('click', handleClose);
    
    // Hiển thị modal    notificationModal.style.display = 'flex';
    notificationModal.style.justifyContent = 'center';
    notificationModal.style.alignItems = 'center';
    
    // Click outside để đóng modal (tùy chọn)
    notificationModal.addEventListener('click', (e) => {
        if (e.target === notificationModal) {
            handleClose();
        }
    });
    
    // Tự động đóng sau 3 giây (tùy chọn)
    // setTimeout(handleClose, 3000);
}

function useOldNotificationMethod(message, type) {
    const notificationArea = document.getElementById('notificationArea');
    const notificationMessage = document.getElementById('notificationMessage');

    if (!notificationArea || !notificationMessage) {
        console.warn("JS: Old notification area elements not found. Falling back to alert.");
        alert(message);
        return;
    }

    notificationMessage.textContent = message;
    notificationArea.className = 'notification';
    notificationArea.classList.add(type);
    notificationArea.style.display = 'block';
}