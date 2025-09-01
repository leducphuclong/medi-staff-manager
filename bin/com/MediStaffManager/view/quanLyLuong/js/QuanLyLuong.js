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
        );
    }
}

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
