// luong_nhan_vien.js

// --- Global State ---
window.pageInitializationExecuted = window.pageInitializationExecuted || false;

// --- Constants ---
const BRIDGE_NAME = 'luongNhanVienBridge'; // Name of the Java bridge object in JavaScript
// Biến toàn cục dùng để lưu giá trị thangNam gốc khi mở modal edit
let originalThangNam = "";

// --- Bridge Accessor ---
/**
 * Lấy đối tượng Java bridge cho quản lý lương.
 * @returns {object} Đối tượng luongNhanVienBridge.
 */
function getBridge() {
    // Giả định có một đối tượng 'bridge' toàn cục
    // và nó có phương thức getQuanLyLuongBridge()
    if (typeof bridge === 'undefined' || typeof bridge.getQuanLyLuongBridge !== 'function') {
        throw new Error("Lỗi: Đối tượng 'bridge' hoặc 'bridge.getQuanLyLuongBridge()' không tồn tại.");
    }
    return bridge.getQuanLyLuongBridge();
}

/**
 * Lấy đối tượng WebView từ bridge chính.
 * @returns {object} Đối tượng WebView.
 */
function getWebView() {
    if (typeof bridge === 'undefined' || typeof bridge.getWebView !== 'function') {
        throw new Error("Lỗi: Đối tượng 'bridge' hoặc 'bridge.getWebView()' không tồn tại.");
    }
    return bridge.getWebView();
}


// --- Callback Functions from Java ---
window.onThemCapNhatThanhCong = function(msg) {
    alert(msg);
    closeModal();
    loadLuongTheoThang();
};

window.onThemCapNhatThatBai = function(msg) {
    alert(msg);
};

window.onXoaThanhCong = function(msg) {
    alert(msg);
    loadLuongTheoThang();
};

window.onXoaThatBai = function(msg) {
    alert(msg);
};

// --- On page load ---
document.addEventListener('DOMContentLoaded', () => {
    // Timeout để đảm bảo bridge đã được inject bởi JavaFX
    setTimeout(() => {
        try {
            const luongBridge = getBridge();
            luongBridge.log("JS: Tải trang Quản Lý Lương Nhân Viên.");

            if (!window.pageInitializationExecuted) {
                luongBridge.log("JS: Chưa khởi tạo trang. Bắt đầu khởi tạo.");
                initializePage();
                window.pageInitializationExecuted = true;
                luongBridge.log("JS: Khởi tạo trang hoàn tất.");
            } else {
                luongBridge.log("JS: Trang đã khởi tạo trước đó.");
            }
        } catch (error) {
            console.error(error);
            alert(error.message);
        }
    }, 1000);

    // Khởi tạo Flatpickr
    flatpickr("#thangNamFilter", {
        plugins: [ new monthSelectPlugin({ shorthand: true, dateFormat: "Y-m", altFormat: "F Y" }) ]
    });
    flatpickr("#thangNam", {
        plugins: [ new monthSelectPlugin({ shorthand: true, dateFormat: "Y-m", altFormat: "F Y" }) ]
    });
});

// --- Initialization ---
function initializePage() {
    try {
        const thangNamFilter = document.getElementById('thangNamFilter');
        if (thangNamFilter) {
            thangNamFilter.value = new Date().toISOString().substring(0, 7);
        } else {
            getBridge().log("JS: Không tìm thấy thangNamFilter.");
        }

        const luongForm = document.getElementById('luongForm');
        if (luongForm) {
            luongForm.addEventListener('submit', handleFormSubmit);
        }

        loadLuongTheoThang();
    } catch (error) {
        console.error("Lỗi trong quá trình khởi tạo:", error);
        alert("Lỗi khởi tạo trang: " + error.message);
    }
}


// --- Data Loading ---
function loadLuongTheoThang() {
    const thangNamFilter = document.getElementById('thangNamFilter');
    if (!thangNamFilter || !thangNamFilter.value) {
        alert('Vui lòng chọn tháng và năm.');
        return;
    }
    const thangNam = thangNamFilter.value;

    try {
        const luongBridge = getBridge();
        luongBridge.log(`JS: Yêu cầu dữ liệu lương cho tháng: ${thangNam}`);
        // Hàm này không cần WebView
        const jsonString = luongBridge.layLuongTheoThang(thangNam);
        luongBridge.log("JS: Nhận JSON từ bridge: " + jsonString);
        const data = JSON.parse(jsonString);
        renderTable(data);
    } catch (e) {
        console.error('JS: Lỗi khi tải dữ liệu lương:', e);
        alert('Lỗi khi tải dữ liệu lương: ' + e.message);
    }
}

// --- Search Functions ---
function timKiemLuong() {
    const criterion = document.getElementById('searchCriterion').value;
    const rawValue = document.getElementById('searchValue').value.trim();
    if (!rawValue) {
        alert('Vui lòng nhập giá trị để tìm kiếm.');
        return;
    }
    const id = parseInt(rawValue, 10);
    if (isNaN(id) || id <= 0) {
        alert('ID không hợp lệ.');
        return;
    }

    try {
        let jsonString;
        const luongBridge = getBridge();
        switch (criterion) {
            case 'nv':
                luongBridge.log(`JS: Tìm lương theo ID Nhân Viên = ${id}`);
                jsonString = luongBridge.timKiemLuongTheoIDNhanVien(id); // Không cần WebView
                break;
            case 'cv':
                luongBridge.log(`JS: Tìm lương theo ID Chức Vụ = ${id}`);
                jsonString = luongBridge.timKiemLuongTheoChucVu(id); // Không cần WebView
                break;
            default:
                alert('Tiêu chí tìm kiếm không hợp lệ.');
                return;
        }
        const data = JSON.parse(jsonString);
        renderTable(data);
    } catch (e) {
        console.error('JS: Lỗi khi tìm kiếm:', e);
        alert('Lỗi khi tìm kiếm lương: ' + e.message);
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
        const escapedHoTen = item.hoTen ? item.hoTen.replace(/'/g, "\\'") : '';
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
              <div id="Button-HanhDong">
               <button class="edit-btn" onclick="openModalForEdit(${item.idLuong}, ${item.idChucVu}, ${item.idNhanVien},'${item.thangNam}', ${item.luongThuNhap}, ${item.thuong}, ${item.phuCap}, ${item.tangCa}, ${item.tongLuong})"><i class="icon icon-edit fas fa-edit"></i> Sửa</button>
               <button class="delete-btn" onclick="confirmDeleteLuong(${item.idLuong}, ${item.idNhanVien},'${escapedHoTen}')"><i class="far fa-trash-alt"></i> Xóa</button>
			  </div>
            </td>`;
        tbody.appendChild(tr);
    });
}

// --- Modal Handling ---
function displayModal(show) {
    const modal = document.getElementById('luongModal');
    if (modal) modal.style.display = show ? 'block' : 'none';
}

function openModalForAdd() {
    document.getElementById('luongForm').reset();
    document.getElementById('modalTitle').innerText = 'Thêm Lương';
    document.getElementById('idLuong').value = '';
    document.getElementById('idNhanVien').removeAttribute('readonly');
    document.getElementById('thangNam').removeAttribute('readonly');
    const inpThangNam = document.getElementById('thangNam');
    if (inpThangNam._flatpickr) {
        inpThangNam._flatpickr.set('clickOpens', true);
    }
    displayModal(true);
}

function openModalForEdit(idLuong, idChucVu, idNhanVien, thangNam, luongThuNhap, thuong, phuCap, tangCa, tongLuong) {
    document.getElementById('modalTitle').innerText = 'Sửa Lương';
    document.getElementById('idLuong').value = idLuong;
    document.getElementById('idChucVu').value = idChucVu;
    document.getElementById('idNhanVien').value = idNhanVien;
    document.getElementById('idNhanVien').setAttribute('readonly', true);
    document.getElementById('luongThuNhap').value = luongThuNhap;
    document.getElementById('thuong').value = thuong;
    document.getElementById('phuCap').value = phuCap;
    document.getElementById('tangCa').value = tangCa;
    document.getElementById('tongLuong').value = tongLuong;
    const inpThangNam = document.getElementById('thangNam');
    inpThangNam.setAttribute('readonly', true);
    originalThangNam = thangNam;
    if (inpThangNam._flatpickr) {
        inpThangNam._flatpickr.setDate(thangNam, true);
        inpThangNam._flatpickr.set('clickOpens', false);
    } else {
        inpThangNam.value = thangNam;
    }
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
    const thangNam = isUpdate ? originalThangNam : getVal('thangNam');
    const luongThuNhap = getVal('luongThuNhap');
    const thuong = getVal('thuong') || '0';
    const phuCap = getVal('phuCap') || '0';
    const tangCa = getVal('tangCa') || '0';

    if (!idChucVu || !idNhanVien || !thangNam || !luongThuNhap) {
        alert('Vui lòng điền đầy đủ các trường bắt buộc (ID Chức Vụ, ID Nhân Viên, Tháng Năm, Lương Thu Nhập).');
        return;
    }

    try {
        const luongBridge = getBridge();
        const webView = getWebView(); // Lấy WebView
        
        if (isUpdate) {
            // SỬA ĐỔI: Thêm webView vào cuối
            luongBridge.capNhatLuong(
                parseInt(idLuong, 10), parseInt(idChucVu, 10), parseInt(idNhanVien, 10),
                thangNam, luongThuNhap, thuong, phuCap, tangCa,
                webView // THÊM THAM SỐ CUỐI CÙNG
            );
        } else {
            // SỬA ĐỔI: Thêm webView vào cuối
            luongBridge.themLuong(
                parseInt(idChucVu, 10), parseInt(idNhanVien, 10),
                thangNam, luongThuNhap, thuong, phuCap, tangCa,
                webView // THÊM THAM SỐ CUỐI CÙNG
            );
        }
    } catch (e) {
        console.error("Lỗi khi gửi form:", e);
        alert("Lỗi khi thực hiện thao tác: " + e.message);
    }
}

// --- Confirmation Dialog ---
function showConfirm(message, onOk) {
    const modal = document.getElementById('confirmModal');
    document.getElementById('confirmMessage').innerText = message;
    modal.style.display = 'flex';
    document.getElementById('btnConfirmOk').onclick = () => {
        modal.style.display = 'none';
        if (typeof onOk === 'function') onOk();
    };
    document.getElementById('btnConfirmCancel').onclick = () => {
        modal.style.display = 'none';
    };
}

function confirmDeleteLuong(idLuong, idNhanVien, hoTen) {
    showConfirm(
        `Bạn có chắc muốn xóa bảng lương của nhân viên "${hoTen}" (ID: ${idNhanVien}) không?`,
        () => {
            try {
                // SỬA ĐỔI: Thêm webView vào cuối
                getBridge().xoaLuong(
                    idLuong, 
                    idNhanVien, 
                    getWebView() // THÊM THAM SỐ CUỐI CÙNG
                );
            } catch (e) {
                console.error("Lỗi khi gọi hàm xóa:", e);
                alert("Lỗi khi thực hiện xóa: " + e.message);
            }
        }
    );
}

// --- Expose functions to be called from HTML ---
window.loadLuongTheoThang = loadLuongTheoThang;
window.timKiemLuong = timKiemLuong;
window.openModalForAdd = openModalForAdd;
window.closeModal = closeModal;
window.confirmDeleteLuong = confirmDeleteLuong;


try {
    getBridge().log("JS: luong_nhan_vien.js đã tải xong và các hàm đã được expose.");
} catch (e) {
    // Lỗi sẽ được hiển thị khi gọi hàm
}