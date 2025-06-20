// In js/ui_confirm_modal.js
console.log("JS: ui_confirm_modal.js loaded.");

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
    }    customConfirmOkButton.addEventListener('click', () => {
        if (customConfirmModalElement) customConfirmModalElement.style.display = 'none';
        if (typeof onCustomConfirmOkCallback === 'function') onCustomConfirmOkCallback();
    });

    customConfirmCancelButton.addEventListener('click', () => {
        if (customConfirmModalElement) customConfirmModalElement.style.display = 'none';
        if (typeof onCustomConfirmCancelCallback === 'function') onCustomConfirmCancelCallback();
    });

    // Optional: Close modal if clicking outside the content
    if (customConfirmModalElement) {
        customConfirmModalElement.addEventListener('click', (event) => {
            if (event.target === customConfirmModalElement) {
                customConfirmModalElement.style.display = 'none';
                if (typeof onCustomConfirmCancelCallback === 'function') onCustomConfirmCancelCallback(); // Or just close without calling cancel
            }
        });
    }
}

function showCustomConfirm(message, title = "Xác nhận", okCallback, cancelCallback) {
    if (!customConfirmModalElement || !customConfirmTitleElement || !customConfirmMessageElement) {
        console.error("JS: customConfirmModal chưa được khởi tạo đúng cách. Dùng confirm() mặc định.");
        if (confirm(message)) { if (okCallback) okCallback(); }
        else { if (cancelCallback) cancelCallback(); }
        return;
    }
    customConfirmTitleElement.textContent = title;
    customConfirmMessageElement.textContent = message;    onCustomConfirmOkCallback = okCallback;
    onCustomConfirmCancelCallback = cancelCallback;
    
    // Use the displayModalCentered helper if available, otherwise standard flex display
    if (typeof displayModalCentered === 'function') {
        displayModalCentered(customConfirmModalElement);
    } else {
        customConfirmModalElement.style.display = 'flex';
        customConfirmModalElement.style.justifyContent = 'center';
        customConfirmModalElement.style.alignItems = 'center';
    }
}