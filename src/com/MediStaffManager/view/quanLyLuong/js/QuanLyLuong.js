<<<<<<< HEAD
/**
 * Quản Lý Lương - JavaScript Logic (Refactored with consistent bridge structure)
 */

// --- Hằng số và Hàm tiện ích ---
const formatCurrency = (num) => num ? new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(num) : '0 ₫';

/**
 * Hàm "Gatekeeper" để truy cập đối tượng bridge của module Quản lý Lương.
 * Hàm này tuân theo cấu trúc mới: window.bridge.getQuanLyLuongBridge()
 * Nó kiểm tra sự tồn tại của các đối tượng cần thiết một cách an toàn.
 * @returns {object} Đối tượng bridge dành riêng cho Quản lý Lương.
 * @throws {Error} Nếu cấu trúc bridge không hợp lệ hoặc không tồn tại.
 */
function getBridge() {
    // 1. Kiểm tra xem đối tượng `bridge` chính có tồn tại và có hàm cần thiết không.
    if (window.bridge && typeof window.bridge.getQuanLyLuongBridge === 'function') {
        const quanLyLuongBridge = window.bridge.getQuanLyLuongBridge();
        
        // 2. Kiểm tra xem hàm có trả về một đối tượng hợp lệ không.
        if (quanLyLuongBridge) {
            return quanLyLuongBridge;
        } else {
            // Lỗi này xảy ra nếu hàm getQuanLyLuongBridge() được gọi nhưng trả về null/undefined.
            throw new Error("Lỗi Backend: Hàm getQuanLyLuongBridge() không trả về một đối tượng hợp lệ.");
        }
    } else {
        // Lỗi này xảy ra nếu đối tượng `window.bridge` chưa được khởi tạo.
        throw new Error("Lỗi kết nối tới Backend: Đối tượng 'bridge' hoặc hàm 'getQuanLyLuongBridge' không tồn tại.");
    }
}

// --- Khởi tạo ---
document.addEventListener('DOMContentLoaded', () => {
    let attempts = 0;
    const maxAttempts = 50; // Chờ khoảng 5 giây
    const interval = setInterval(() => {
        // SỬA Ở ĐÂY: Chờ đợi đúng cấu trúc bridge mới
        if (window.bridge && typeof window.bridge.getQuanLyLuongBridge === 'function') {
            clearInterval(interval);
            initializeApplication();
        } else {
            attempts++;
            if (attempts > maxAttempts) {
                clearInterval(interval);
                console.error("Lỗi nghiêm trọng: Không tìm thấy cấu trúc 'window.bridge.getQuanLyLuongBridge()' sau khi chờ.");
                document.body.innerHTML = '<h1 style="color:red; text-align:center; margin-top: 50px;">Lỗi kết nối tới Backend Java. Vui lòng tải lại trang.</h1>';
            }
        }
    }, 100);
});

// --- Tất cả các hàm bên dưới giữ nguyên vì chúng đã sử dụng getBridge() ---

function initializeApplication() {
    try {
        getBridge().log("JavaScript application initialized successfully.");

        document.getElementById('thangNamFilter').value = new Date().toISOString().slice(0, 7);
        document.getElementById('luongForm').addEventListener('submit', handleFormSubmit);
        document.getElementById('luongTableBody').addEventListener('click', handleTableActions);
        
        ['thuong', 'phuCap', 'tangCa'].forEach(id => {
            document.getElementById(id).addEventListener('input', updateTongLuongInModal);
        });

        document.getElementById('confirmOkBtn').addEventListener('click', () => onConfirm(true));
        document.getElementById('confirmCancelBtn').addEventListener('click', () => onConfirm(false));
        document.getElementById('confirmCloseBtn').addEventListener('click', () => onConfirm(false));
        
        loadDepartments();
        loadData();

    } catch (e) {
        console.error("Lỗi khi khởi tạo ứng dụng:", e);
        showNotification(`Lỗi nghiêm trọng khi khởi tạo: ${e.message}`, 'error');
    }
}

function loadDepartments() {
    try {
        const jsonString = getBridge().layTatCaPhongBan();
        const departments = JSON.parse(jsonString);
        const select = document.getElementById('phongBanFilter');
        
        while (select.options.length > 1) {
            select.remove(1);
        }

        departments.forEach(dept => {
            const option = document.createElement('option');
            option.value = dept.idPhongBan;
            option.textContent = dept.tenPhongBan;
            select.appendChild(option);
        });
    } catch (e) {
        console.error("Lỗi tải danh sách phòng ban:", e);
        showNotification(`Lỗi tải phòng ban: ${e.message}`, 'error');
    }
}

function loadData() {
    const thangNam = document.getElementById('thangNamFilter').value;
    const idPhongBan = document.getElementById('phongBanFilter').value;

    if (!thangNam) {
        showNotification("Vui lòng chọn tháng để xem dữ liệu.", "warning");
        return;
    }

    try {
        const bridge = getBridge();
        bridge.log(`JS: Yêu cầu dữ liệu cho tháng ${thangNam}, phòng ban ID: ${idPhongBan}`);
        let jsonString;
        
        if (idPhongBan && parseInt(idPhongBan, 10) > 0) {
            jsonString = bridge.layLuongTheoThangVaPhongBan(thangNam, parseInt(idPhongBan, 10));
        } else {
            jsonString = bridge.layLuongTheoThang(thangNam);
        }
        
        const data = JSON.parse(jsonString);
        renderTable(data);

    } catch (e) {
        console.error("Lỗi tải dữ liệu lương:", e);
        showNotification(`Lỗi tải dữ liệu: ${e.message}`, 'error');
    }
}

function renderTable(data) {
    const tbody = document.getElementById('luongTableBody');
    tbody.salaryData = data || []; 

    if (tbody.salaryData.length === 0) {
        tbody.innerHTML = '<tr><td colspan="10" style="text-align:center; padding: 30px; color: var(--text-muted);">Không tìm thấy dữ liệu phù hợp.</td></tr>';
        return;
    }

    tbody.innerHTML = tbody.salaryData.map((item, index) => {
        const hanhDongButton = `<button class="btn btn-primary btn-sm" data-action="edit"><i class="fas fa-pencil-alt"></i> Sửa</button>
                                <button class="btn btn-danger btn-sm" data-action="delete" ${item.idLuong ? '' : 'disabled'}><i class="fas fa-trash-alt"></i> Xóa</button>`;
        return `
            <tr data-index="${index}">
                <td>${item.idNhanVien || 'N/A'}</td>
                <td>${item.hoTen || 'N/A'}</td>
                <td>${item.tenChucVu || 'N/A'}</td>
                <td>${item.thangNam || 'N/A'}</td>
                <td>${formatCurrency(item.luongCoBanChuan)}</td>
                <td>${formatCurrency(item.thuong)}</td>
                <td>${formatCurrency(item.phuCap)}</td>
                <td>${formatCurrency(item.tangCa)}</td>
                <td><strong>${formatCurrency(item.tongLuong)}</strong></td>
                <td class="action-buttons">${hanhDongButton}</td>
            </tr>`;
    }).join('');
}

function filterTable() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    const originalData = document.getElementById('luongTableBody').salaryData || [];
    
    if (!searchTerm) {
        renderTable(originalData);
        return;
    }

    const filteredData = originalData.filter(item => {
        const hoTen = item.hoTen ? item.hoTen.toLowerCase() : '';
        const idNhanVien = item.idNhanVien ? String(item.idNhanVien) : '';
        return hoTen.includes(searchTerm) || idNhanVien.includes(searchTerm);
    });
    renderTable(filteredData);
}

function handleTableActions(event) {
    const button = event.target.closest('button');
    if (!button || button.disabled) return;

    const row = button.closest('tr');
    const itemData = document.getElementById('luongTableBody').salaryData[row.dataset.index];
    const action = button.dataset.action;
    
    if (!itemData.idLuong && (action === 'edit' || action === 'delete')) {
         showNotification(`Nhân viên "${itemData.hoTen}" chưa có bản ghi lương để sửa/xóa.`, 'info');
         return;
    }

    if (action === 'edit') {
        openModal(itemData);
    } else if (action === 'delete') {
        showConfirm(
            `Bạn có chắc muốn xóa bản ghi lương tháng ${itemData.thangNam} của "${itemData.hoTen}"?`,
            () => { 
                try {
                    const result = getBridge().xoaLuong(itemData.idLuong); 
                    if (result) {
                        showNotification("Xóa lương thành công!", "success");
                        loadData();
                    } else {
                        showNotification("Xóa lương thất bại.", "error");
                    }
                } catch (e) {
                    showNotification(`Lỗi khi xóa: ${e.message}`, 'error');
                }
            }
=======
// luong_nhan_vien.js

// --- Global State ---
window.pageInitializationExecuted = window.pageInitializationExecuted || false;

// --- Constants ---
const BRIDGE_NAME = 'luongNhanVienBridge'; // Name of the Java bridge object in JavaScript
// Biến toàn cục dùng để lưu giá trị thangNam gốc khi mở modal edit
let originalThangNam = "";


window.onThemCapNhatThanhCong = function(msg) {
    alert(msg);
    closeModal();
    loadLuongTheoThang();
};

window.onThemCapNhatThatBai = function(msg) {
    alert(msg);
};

// Khi Java gọi window.onXoaThanhCong(msg)
window.onXoaThanhCong = function(msg) {
    // Hiện thông báo cho người dùng
    alert(msg);
    // Sau khi xóa thành công, tải lại bảng lương để cập nhật giao diện
    loadLuongTheoThang();
};

// Khi Java gọi window.onXoaThatBai(msg)
window.onXoaThatBai = function(msg) {
    // Hiện thông báo cho người dùng
    alert(msg);
    // Không cần reload vì xóa thất bại
};

// --- On page load ---
document.addEventListener('DOMContentLoaded', () => {
    setTimeout(() => {
        if (typeof window[BRIDGE_NAME] === 'undefined') {
            console.error(`JS: Java bridge '${BRIDGE_NAME}' không có sẵn.`);
            alert(`Lỗi nghiêm trọng: Java bridge '${BRIDGE_NAME}' không tìm thấy.`);
            return;
        }
        window[BRIDGE_NAME].log("JS: Tải trang Quản Lý Lương Nhân Viên.");

        if (!window.pageInitializationExecuted) {
            window[BRIDGE_NAME].log("JS: Chưa khởi tạo trang. Bắt đầu khởi tạo.");
            if (typeof initializePage === 'function') {
                initializePage();
                window.pageInitializationExecuted = true;
                window[BRIDGE_NAME].log("JS: Khởi tạo trang hoàn tất.");
            } else {
                console.error("JS: Hàm initializePage() chưa định nghĩa.");
                alert("Lỗi: Hàm khởi tạo trang bị thiếu.");
            }
        } else {
            window[BRIDGE_NAME].log("JS: Trang đã khởi tạo trước đó.");
        }
    }, 500);
	flatpickr("#thangNamFilter", {
	       plugins: [
	           new monthSelectPlugin({
	               shorthand: true,
	               dateFormat: "Y-m",
	               altFormat: "F Y"
	           })
	       ]
		 
	   });
	  flatpickr("#thangNam", {
	   	       plugins: [
	   	           new monthSelectPlugin({
	   	               shorthand: true,
	   	               dateFormat: "Y-m",
	   	               altFormat: "F Y"
	   	           })
	   	       ]
	   	   });
	
});


// --- Initialization ---
function initializePage() {
    // Thiết lập giá trị mặc định cho bộ lọc tháng
    const thangNamFilter = document.getElementById('thangNamFilter');
    if (thangNamFilter) {
        thangNamFilter.value = new Date().toISOString().substring(0, 7);
    } else {
        window[BRIDGE_NAME].log("JS: Không tìm thấy thangNamFilter.");
    }

    // Đăng ký form submit
    const luongForm = document.getElementById('luongForm');
    if (luongForm) {
        luongForm.addEventListener('submit', handleFormSubmit);
    }

    // First load
    loadLuongTheoThang();
}


// Hàm thực hiện việc tải dữ liệu lương theo tháng đã chọn
function loadLuongTheoThang() {
    // Lấy phần tử input có id là 'thangNamFilter' (kiểu <input type="month">)
    const thangNamFilter = document.getElementById('thangNamFilter');

    // Nếu không tìm thấy phần tử hoặc người dùng chưa chọn tháng-năm
    if (!thangNamFilter || !thangNamFilter.value) {
        // Hiển thị thông báo yêu cầu người dùng chọn tháng-năm
        alert('Vui lòng chọn tháng và năm.');
        return; // Dừng hàm nếu thiếu thông tin
    }

    // Lấy giá trị tháng-năm từ ô input, ví dụ: "2025-05"
    const thangNam = thangNamFilter.value;

    // Ghi log thông báo (qua bridge) là đang yêu cầu dữ liệu lương cho tháng đã chọn
    window[BRIDGE_NAME].log(`JS: Yêu cầu dữ liệu lương cho tháng: ${thangNam}`);

    try {
        // Gọi hàm từ bridge để lấy dữ liệu lương dưới dạng chuỗi JSON
        const jsonString = window[BRIDGE_NAME].layLuongTheoThang(thangNam);

        // Ghi log nội dung JSON nhận được (để kiểm tra/debug)
        window[BRIDGE_NAME].log("JS: Nhận JSON từ bridge: " + jsonString);

        // Chuyển đổi chuỗi JSON thành đối tượng JavaScript
        const data = JSON.parse(jsonString);

        // Gọi hàm renderTable để hiển thị dữ liệu lương lên giao diện
        renderTable(data);

    } catch (e) {
        // Nếu có lỗi (ví dụ: lỗi khi gọi bridge, lỗi phân tích JSON, v.v.)
        console.error('JS: Lỗi khi lấy hoặc phân tích JSON:', e);

        // Hiển thị thông báo lỗi cho người dùng
        alert('Lỗi khi tải dữ liệu lương: ' + e.message);
    }
}


function timKiemLuongTheoIDNV() {
    const idFilter = document.getElementById('searchValue');
    if (!idFilter || !idFilter.value) {
        alert('Vui lòng nhập ID Nhân Viên.');
        return;
    }
    const id = parseInt(idFilter.value, 10);
    if (isNaN(id) || id <= 0) {
        alert('ID Nhân Viên không hợp lệ.');
        return;
    }
    window[BRIDGE_NAME].log(`JS: Tìm lương cho NV ID: ${id}`);
    try {
        const jsonString = window[BRIDGE_NAME].timKiemLuongTheoIDNhanVien(id);
        const data = JSON.parse(jsonString);
        renderTable(data);
    } catch (e) {
        console.error('JS: Lỗi khi tìm kiếm:', e);
        alert('Lỗi khi tìm kiếm lương: ' + e.message);
    }
}
/**
 * Tìm kiếm bảng lương theo ID Chức Vụ
 */
function timKiemLuongTheoChucVu() {
  const sel = document.getElementById('searchValue');
  if (!sel) {
    alert('Không tìm thấy ô nhập Chức Vụ.');
    return;
  }
  const idChucVu = parseInt(sel.value, 10);
  if (isNaN(idChucVu) || idChucVu <= 0) {
    alert('Vui lòng nhập ID Chức Vụ hợp lệ.');
    sel.focus();
    return;
  }

  // Ghi log cho debug
  window[BRIDGE_NAME].log(`JS: Tìm lương cho Chức Vụ ID: ${idChucVu}`);

  try {
    // Gọi bridge mới
    const jsonString = window[BRIDGE_NAME].timKiemLuongTheoChucVu(idChucVu);
    const data = JSON.parse(jsonString);
    // Vẽ lại bảng với dữ liệu nhận được
    renderTable(data);
  } catch (e) {
    console.error('JS: Lỗi khi tìm kiếm theo Chức Vụ:', e);
    alert('Lỗi khi tìm kiếm lương theo Chức Vụ: ' + e.message);
  }
}

function timKiemLuong() {
  const criterion = document.getElementById('searchCriterion').value;
  const raw = document.getElementById('searchValue').value.trim();
  if (!raw) {
    alert('Vui lòng nhập ID để tìm kiếm.');
    return;
  }
  const id = parseInt(raw, 10);
  if (isNaN(id) || id <= 0) {
    alert('ID không hợp lệ.');
    return;
  }

  switch (criterion) {
    case 'nv':
      window[BRIDGE_NAME].log(`JS: Tìm lương theo ID Nhân Viên = ${id}`);
      var json = window[BRIDGE_NAME].timKiemLuongTheoIDNhanVien(id);
      break;
    case 'cv':
      window[BRIDGE_NAME].log(`JS: Tìm lương theo ID Chức Vụ = ${id}`);
      var json = window[BRIDGE_NAME].timKiemLuongTheoChucVu(id);
      break;
    default:
      alert('Tiêu chí không hợp lệ.');
      return;
  }

  try {
    const data = JSON.parse(json);
    renderTable(data);
  } catch (e) {
    console.error('Lỗi khi parse JSON:', e);
    alert('Lỗi khi xử lý kết quả tìm kiếm.');
  }
}

// --- UI Rendering ---
function renderTable(data) {
    const tbody = document.getElementById('luongTableBody');
    if (!tbody) return;

    if (!data || data.length === 0) {
        tbody.innerHTML = '<tr><td colspan="10" style="text-align:center;">Không có dữ liệu</td></tr>';
        return;
    }

    tbody.innerHTML = '';
    data.forEach(item => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${item.idChucVu}</td>
            <td>${item.idNhanVien}</td>
            <td>${item.hoTen || ''}</td>
            <td>${item.thangNam}</td>
            <td>${item.luongThuNhap}</td>
            <td>${item.thuong}</td>
            <td>${item.phuCap}</td>
            <td>${item.tangCa}</td>
            <td>${item.tongLuong}</td>
            <td>
			  <div id = "Button-HanhDong">
               <button class="edit-btn" onclick="openModalForEdit(
                   ${item.idLuong}, ${item.idChucVu}, ${item.idNhanVien},
                   '${item.thangNam}', ${item.luongThuNhap},
                   ${item.thuong}, ${item.phuCap}, ${item.tangCa}, ${item.tongLuong}
               )"><i class="icon icon-edit fas fa-edit"></i> Sửa</button>
               <button class="delete-btn" 			  
			        onclick="confirmDeleteLuong(
			               ${item.idLuong},
			               ${item.idNhanVien},
			               '${item.hoTen.replace(/'/g,"\\'")}'
			           )">
			   <i class="far fa-trash-alt"></i> Xóa</button>
			   </div>
            </td>`;
        tbody.appendChild(tr);
    });
}

function renderTableError(msg) {
    const tbody = document.getElementById('luongTableBody');
    if (tbody) {
        tbody.innerHTML = `<tr><td colspan="10" style="color:red;text-align:center;">${msg}</td></tr>`;
    }
}


// --- Modal Handling ---
function displayModal(show) {
    const modal = document.getElementById('luongModal');
    if (modal) modal.style.display = show ? 'block' : 'none';
}

function openModalForAdd() {
	// Gọi flatpickr lại sau khi hiển thị
	 flatpickr("#thangNam", {
	   plugins: [
	     new monthSelectPlugin({
	       shorthand: true,
	       dateFormat: "Y-m",
	       altFormat: "F Y"
	     })
	   ]
	 });
    document.getElementById('modalTitle').innerText = 'Thêm Lương';
    document.getElementById('idLuong').value = '';
    document.getElementById('idChucVu').value = '';
    document.getElementById('idNhanVien').value = '';
    document.getElementById('idNhanVien').removeAttribute('readonly'); // Allow editing
	document.getElementById('thangNam').removeAttribute('readonly'); // Allow editing
    document.getElementById('thangNam').value = '';
    document.getElementById('luongThuNhap').value = '';
    document.getElementById('thuong').value = '';
    document.getElementById('phuCap').value = '';
    document.getElementById('tangCa').value = '';
    document.getElementById('tongLuong').value = '';
    displayModal(true);
}

function openModalForEdit(idLuong, idChucVu, idNhanVien, thangNam, luongThuNhap, thuong, phuCap, tangCa, tongLuong) {
    document.getElementById('modalTitle').innerText = 'Sửa Lương';
    document.getElementById('idLuong').value = idLuong;
    document.getElementById('idChucVu').value = idChucVu;
    const inpIDNV = document.getElementById('idNhanVien');
	inpIDNV.value = idNhanVien;
	inpIDNV.setAttribute('readonly', true);
	// Gán Tháng Năm vào input flatpickr, rồi khóa không cho chọn
	const inpThangNam = document.getElementById('thangNam');
    inpThangNam.value = thangNam;           // gán giá trị gốc
	originalThangNam = thangNam;            // lưu vào biến toàn cục
	inpThangNam.setAttribute('readonly', true);
		// Nếu muốn hoàn toàn không cho mở lịch, ta có thể gán clickOpens = false:
		  if (inpThangNam._flatpickr) {
			  inpThangNam._flatpickr.setDate(thangNam, true);
		      inpThangNam._flatpickr.set('clickOpens', false);
		  }
    document.getElementById('luongThuNhap').value = luongThuNhap;
    document.getElementById('thuong').value = thuong;
    document.getElementById('phuCap').value = phuCap;
    document.getElementById('tangCa').value = tangCa;
    document.getElementById('tongLuong').value = tongLuong;
    displayModal(true);
}

function closeModal() {
    displayModal(false);
}


// --- Form Submission ---
function handleFormSubmit(event) {
    event.preventDefault();

    const idLuong = document.getElementById('idLuong').value;
    const isUpdate = idLuong.trim() !== '';

    const getVal = (id) => document.getElementById(id).value.trim();
    const idChucVu = getVal('idChucVu');
    const idNhanVien = getVal('idNhanVien');
    const thangNam = getVal('thangNam');
    const luongThuNhap = getVal('luongThuNhap');
    const thuong = getVal('thuong') || '0';
    const phuCap = getVal('phuCap') || '0';
    const tangCa = getVal('tangCa') || '0';


    if (!idChucVu || !thangNam || !luongThuNhap) {
        alert('Vui lòng điền đầy đủ các trường bắt buộc.');
        return;
    }

    if (isUpdate) {
        window[BRIDGE_NAME].capNhatLuong(
            parseInt(idLuong, 10),
            parseInt(idChucVu, 10),
            parseInt(idNhanVien, 10),
            thangNam,
            luongThuNhap, thuong, phuCap, tangCa
        );
    } else {
        window[BRIDGE_NAME].themLuong(
            parseInt(idChucVu, 10),
            parseInt(idNhanVien, 10),
            thangNam,
            luongThuNhap, thuong, phuCap, tangCa,
>>>>>>> ThaoDuyen-QuanLyLuong-ThongKe
        );
    }
}

<<<<<<< HEAD
function handleFormSubmit(event) {
    event.preventDefault();
    const luongData = {
        idLuong: parseInt(document.getElementById('idLuong').value),
        idNhanVien: parseInt(document.getElementById('idNhanVien').value),
        idChucVu: parseInt(document.getElementById('idChucVu').value),
        heSoLuong: parseFloat(document.getElementById('heSoLuong').value),
        thangNam: document.getElementById('thangNamFilter').value,
        luongCoBanChuan: parseFloat(document.getElementById('luongCoBanChuan').value),
        thuong: parseFloat(document.getElementById('thuong').value) || 0,
        phuCap: parseFloat(document.getElementById('phuCap').value) || 0,
        tangCa: parseFloat(document.getElementById('tangCa').value) || 0,
    };
    
    try {
        const result = getBridge().capNhatLuong(luongData.idLuong, JSON.stringify(luongData));
        if (result) {
            showNotification("Cập nhật lương thành công!", "success");
            closeModal();
            loadData();
        } else {
            showNotification("Cập nhật lương thất bại!", "error");
        }
    } catch(e) {
        showNotification(`Lỗi khi cập nhật: ${e.message}`, 'error');
    }
}

// ... các hàm modal và notification giữ nguyên ...
const luongModal = document.getElementById('luongModal');
const confirmModal = document.getElementById('confirmModal');
let confirmCallback = null;
function openModal(data){ /* ... */ }
function closeModal(){ /* ... */ }
function updateTongLuongInModal(){ /* ... */ }
function showConfirm(message, onOk){ /* ... */ }
function onConfirm(isOk){ /* ... */ }
function showNotification(message, type = 'info'){ /* ... */ }
// Gán hàm vào window để HTML gọi được
window.loadData = loadData;
window.filterTable = filterTable;
window.closeModal = closeModal;
window.showNotification = showNotification;
// (Đã rút gọn phần thân các hàm không đổi để tiết kiệm không gian)
=======
// --- Hàm hiển thị modal confirm ---
function showConfirm(message, onOk, onCancel) {
  const modal = document.getElementById('confirmModal');
  const msgSpan = document.getElementById('confirmMessage');
  const btnOk = document.getElementById('btnConfirmOk');
  const btnCancel = document.getElementById('btnConfirmCancel');

  msgSpan.innerText = message;
  modal.style.display = 'flex';

  // Khi bấm OK
  btnOk.onclick = () => {
    modal.style.display = 'none';
    if (typeof onOk === 'function') onOk();
  };
  // Khi bấm Cancel
  btnCancel.onclick = () => {
    modal.style.display = 'none';
    if (typeof onCancel === 'function') onCancel();
  };
}

/**
 * Confirm delete với tên nhân viên và ID
 * @param {number} idLuong    – ID bản ghi lương
 * @param {number} idNhanVien – ID nhân viên
 * @param {string} hoTen      – Họ tên nhân viên
 */
function confirmDeleteLuong(idLuong, idNhanVien, hoTen) {
  showConfirm(
    `Bạn có chắc muốn xóa bảng lương của nhân viên "${hoTen}"- ID Nhân Viên "${idNhanVien}" không?`,
    () => {
      // Nếu OK → gọi xóa trên bridge
      window[BRIDGE_NAME].xoaLuong(idLuong, idNhanVien);
    },
    () => {
      // Nếu Hủy → không làm gì
    }
  );
}


// --- Expose to HTML ---
window.loadLuongTheoThang = loadLuongTheoThang;
window.timKiemLuongTheoIDNV = timKiemLuongTheoIDNV;
window.openModalForAdd = openModalForAdd;
window.openModalForEdit = openModalForEdit;
window.closeModal = closeModal;
window.confirmDeleteLuong = confirmDeleteLuong;
window.onThemCapNhatThanhCong = onThemCapNhatThanhCong;
window.onThemCapNhatThatBai = onThemCapNhatThatBai;
window.onXoaThanhCong         = onXoaThanhCong;
window.onXoaThatBai           = onXoaThatBai;


window[BRIDGE_NAME].log("JS: luong_nhan_vien.js đã tải xong.");
>>>>>>> ThaoDuyen-QuanLyLuong-ThongKe
