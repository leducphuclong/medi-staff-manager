// In js/ui_notifications.js
console.log("JS: ui_notifications.js loaded.");

function showNotification(message, type = 'success') { // type có thể là 'success', 'error', 'warning'
    const notificationArea = document.getElementById('notificationArea');
    const notificationMessage = document.getElementById('notificationMessage');

    if (!notificationArea || !notificationMessage) {
        console.warn("JS: Notification area elements not found. Falling back to alert.");
        alert(message); // Fallback nếu không tìm thấy khu vực thông báo
        return;
    }

    notificationMessage.textContent = message;
    notificationArea.className = `notification ${type}`; // Use classes for styling
    notificationArea.style.display = 'block';


    // Consider adding a close button to the notification
    // Example:
    // const closeButton = notificationArea.querySelector('.close-notification');
    // if (closeButton && !closeButton.getAttribute('listener')) {
    //     closeButton.setAttribute('listener', 'true');
    //     closeButton.onclick = () => notificationArea.style.display = 'none';
    // }


    // Auto-hide (optional, can be managed by CSS animations too)
    // setTimeout(() => {
    //     notificationArea.style.display = 'none';
    // }, 5000);
}