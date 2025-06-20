document.addEventListener('DOMContentLoaded', function() {
    setTimeout(() => {
        const form = document.getElementById('forgotPasswordForm');
        const successContainer = document.getElementById('successMessageContainer');

        form.addEventListener('submit', function(event) {
          event.preventDefault();

          const email = document.getElementById('email').value;
          
          if (bridge.getQuenMatKhauBridge().sendPasswordByEmail(email)) {
            form.style.display = 'none';
            successContainer.style.display = 'block';

          } else {
			emailError.textContent = 'Không tìm thấy tài khoản với email này.';
			emailError.style.display = 'block';            
          }
        });
    }, 1000); 
});