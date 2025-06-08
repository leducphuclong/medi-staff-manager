document.addEventListener('DOMContentLoaded', function() {
    setTimeout(() => {
        bridge.log("chuan bi dang nhap");
        const loginForm = document.getElementById('loginForm');
        const usernameInput = document.getElementById('username');
        const passwordInput = document.getElementById('password');
        const errorMessageDiv = document.getElementById('error-message');

        loginForm.addEventListener('submit', function(event) {
            event.preventDefault();  // Prevent page reload
            bridge.log("dang dang nhap");

            const username = usernameInput.value;
            const password = passwordInput.value;

            try {
                const dangNhapBridge = bridge.getDangNhapBridge();
                if (dangNhapBridge) {
					bridge.log("dang dang nhap 2");
                    if (dangNhapBridge.dangNhap(username, password)) {
						bridge.log("Dang Nhap THanh COng");
						vaiTro = dangNhapBridge.getVaiTroByUsername(username);
						if (vaiTro == "QuanLyNhanSu")
							bridge.getQuanLyNhanSuBridge().taiTrang(bridge.getPrimaryStage(), bridge.getWebView());
						else
							bridge.getKeToanBridge().taiTrang(bridge.getPrimaryStage(), bridge.getWebView());
                        
                    } else {
						bridge.log("Dang Nhap that Bai");
						errorMessageDiv.textContent = 'Tên đăng nhập hoặc mật khẩu không đúng.';
						errorMessageDiv.style.display = 'block';
                    }
                } else {
					bridge.log("Loi 1")
                }
            } catch (error) {
				bridge.log("Loi 2");
                console.error("Error while accessing bridge: ", error);
                alert("Đã có lỗi xảy ra trong quá trình đăng nhập. Vui lòng thử lại.");
            }
        });
    }, 1000);
});

