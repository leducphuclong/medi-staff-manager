function initializePageAfterJavaBridgeInjection() {
	
	quanLyPhongBanBridge.log("Chuẩn bị khởi tạo UI.");
	
	const mainNavActive = document.querySelector(".main-nav a.active"); 
    const pageTitleElement = document.querySelector("h1.content-title");
	
	if (mainNavActive && mainNavActive.textContent.includes("Phòng ban")) {
		quanLyPhongBanBridge.log("Chuẩn bị tải lên dữ liệu cho trang Phòng ban")
       	loadDepartmentTableData();
   	}
}

document.addEventListener('DOMContentLoaded', function() {
    setTimeout(() => {
		quanLyPhongBanBridge.log("Tải trang");
		initializeDepartmentPage();
		initializeEmployeePage();
        if (typeof window.pageInitializationExecuted === 'undefined') {
            if (typeof window.quanLyPhongBanBridge !== 'undefined' && typeof initializePageAfterJavaBridgeInjection === 'function') {
                quanLyPhongBanBridge.log("JS: DOMContentLoaded timeout - Java bridge đã cài đặt, Chuẩn bị gọi hàm khởi tạo UI");
                initializePageAfterJavaBridgeInjection();
                window.pageInitializationExecuted = true; 
            } else {
                quanLyPhongBanBridge.log("JS: DOMContentLoaded timeout - Java bridge or pageIsReadyAndJavaBridgeIsInjected() not ready. Initialization might fail or be incomplete.");
            }
        } else {
            quanLyPhongBanBridge.log("JS: DOMContentLoaded timeout - pageIsReadyAndJavaBridgeIsInjected() already executed.");
        }
    }, 500); // Increased delay slightly
});