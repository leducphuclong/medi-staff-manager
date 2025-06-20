// --- Global State ---
window.pageThongKeInitialized = window.pageThongKeInitialized || false;
let avgChartInstance = null;
let totalChartInstance = null;

// --- Bridge Accessor ---
/**
 * Lấy đối tượng Java bridge cho thống kê lương.
 * @returns {object} Đối tượng Java.
 */
function getBridge() {
    if (typeof bridge === 'undefined' || typeof bridge.getThongKeLuongBridge !== 'function') {
        throw new Error("Lỗi: Đối tượng 'bridge' hoặc 'bridge.getThongKeLuongBridge()' không tồn tại.");
    }
    return bridge.getThongKeLuongBridge();
}

// --- On Page Load ---
document.addEventListener('DOMContentLoaded', () => {
    setTimeout(() => {
        try {
            if (!window.pageThongKeInitialized) {
                getBridge().log("JS: Bắt đầu khởi tạo trang Thống Kê.");
                initializePage();
                window.pageThongKeInitialized = true;
                getBridge().log("JS: Khởi tạo trang Thống Kê hoàn tất.");
            }
        } catch (error) {
            console.error("Lỗi khi khởi tạo trang Thống kê:", error);
            alert("Không thể kết nối đến backend: " + error.message);
        }
    }, 500); // 500ms là đủ để bridge được nạp
});

// --- Initialization ---
/**
 * Khởi tạo trang và gán các sự kiện.
 */
function initializePage() {
    // Lấy các element trên trang
    const typeSelect = document.getElementById('typeSelect');
    const viewButton = document.getElementById('viewButton');
    
    const groupQuy = document.getElementById('group-quy');
    const groupNam = document.getElementById('group-nam');

    const namQuyInput = document.getElementById('namQuyInput');
    const quySelect = document.getElementById('quySelect');
    const namInput = document.getElementById('namInput');

    const currentYear = new Date().getFullYear();
    
    // Đặt giá trị mặc định
    namQuyInput.value = currentYear;
    namInput.value = currentYear;
    
    // Sự kiện khi thay đổi loại thống kê
    typeSelect.addEventListener('change', () => {
        const selectedType = typeSelect.value;
        // Ẩn/hiện các nhóm input dựa trên lựa chọn
        groupQuy.style.display = selectedType === 'quy' ? 'flex' : 'none';
        groupNam.style.display = selectedType === 'nam' ? 'flex' : 'none';
        
        checkIfViewButtonShouldBeEnabled();
    });
    
    // Gán sự kiện 'input' cho các trường để kiểm tra và kích hoạt nút "Xem"
    [namQuyInput, quySelect, namInput].forEach(input => {
        input.addEventListener('input', checkIfViewButtonShouldBeEnabled);
    });

    // Hàm kiểm tra điều kiện để kích hoạt nút "Xem"
    function checkIfViewButtonShouldBeEnabled() {
        const type = typeSelect.value;
        let enabled = false;

        if (type === 'tat_ca_thang') {
            enabled = true;
        } else if (type === 'quy' && namQuyInput.value && quySelect.value) {
            enabled = true;
        } else if (type === 'nam' && namInput.value) {
            enabled = true;
        }
        viewButton.disabled = !enabled;
    }

    // Sự kiện click cho nút "Xem"
    viewButton.addEventListener('click', loadThongKe);
}

// --- Data Loading and Processing ---
/**
 * Tải và xử lý dữ liệu thống kê dựa trên lựa chọn của người dùng.
 */
function loadThongKe() {
    const type = document.getElementById('typeSelect').value;
    let params = { type: type };
    let title = "Thống Kê Lương ";

    // Thu thập tham số dựa trên loại thống kê
    switch (type) {
        case 'tat_ca_thang':
            title += `Toàn Bộ Các Tháng`;
            // Không cần tham số bổ sung
            break;
        case 'quy':
            params.nam = document.getElementById('namQuyInput').value;
            params.quy = document.getElementById('quySelect').value;
            if (!params.nam || !params.quy) { alert("Vui lòng chọn năm và quý."); return; }
            title += `Quý ${params.quy} Năm ${params.nam}`;
            break;
        case 'nam':
            params.nam = document.getElementById('namInput').value;
            if (!params.nam) { alert("Vui lòng chọn năm."); return; }
            title += `Năm ${params.nam}`;
            break;
        default:
            alert("Vui lòng chọn loại thống kê.");
            return;
    }

    clearResults(); // Xóa kết quả cũ

    try {
        const thongKeBridge = getBridge();
        thongKeBridge.log(`JS: Yêu cầu thống kê với tham số: ${JSON.stringify(params)}`);

        // Gọi hàm Java và nhận JSON
        const rawJsonData = thongKeBridge.getThongKeLuong(JSON.stringify(params));
        thongKeBridge.log(`JS: Nhận JSON: ${rawJsonData.substring(0, 300)}...`);
        const data = JSON.parse(rawJsonData);

        if (!data || data.length === 0) {
            displayNoData(title);
            return;
        }

        // Tách dữ liệu cho bảng và biểu đồ
        const labels = data.map(item => item.label); // e.g., "Phòng A" or "2023-01"
        const avgData = data.map(item => item.luongTrungBinh);
        const totalData = data.map(item => item.tongLuong);

        // Hiển thị kết quả
        document.getElementById('resultContainer').innerHTML = renderTable(data, title);
        renderCharts(labels, avgData, totalData, title);

    } catch (e) {
        console.error(`JS: Lỗi khi tải dữ liệu thống kê:`, e);
        alert("Đã xảy ra lỗi khi tải dữ liệu: " + e.message);
        clearResults();
    }
}

// --- UI Rendering ---

/**
 * Xóa các kết quả cũ (bảng, biểu đồ) khỏi giao diện.
 */
function clearResults() {
    document.getElementById('resultContainer').innerHTML = '';
    document.getElementById('avgChartWrapper').style.display = 'none';
    document.getElementById('totalChartWrapper').style.display = 'none';

    if (avgChartInstance) {
        avgChartInstance.destroy();
        avgChartInstance = null;
    }
    if (totalChartInstance) {
        totalChartInstance.destroy();
        totalChartInstance = null;
    }
}

/**
 * Hiển thị thông báo khi không có dữ liệu.
 * @param {string} title - Tiêu đề của loại thống kê đang xem.
 */
function displayNoData(title) {
    const resultDiv = document.getElementById('resultContainer');
    resultDiv.innerHTML = `<div class="result-title no-data">Không có dữ liệu cho ${title}</div>`;
}

/**
 * Tạo bảng HTML từ dữ liệu.
 * @param {Array<Object>} data - Mảng dữ liệu.
 * @param {string} title - Tiêu đề cho bảng.
 * @returns {string} - Chuỗi HTML của bảng.
 */
function renderTable(data, title) {
    let tableHtml = `<div class="result-title">${title}</div>`;
    tableHtml += '<table><thead><tr><th>Hạng mục</th><th>Lương Trung Bình (VNĐ)</th><th>Tổng Lương Chi Trả (VNĐ)</th></tr></thead><tbody>';
    
    data.forEach(item => {
        const avgSalary = (item.luongTrungBinh || 0).toLocaleString('vi-VN');
        const totalSalary = (item.tongLuong || 0).toLocaleString('vi-VN');
        tableHtml += `
            <tr>
                <td>${item.label}</td>
                <td>${avgSalary}</td>
                <td>${totalSalary}</td>
            </tr>
        `;
    });

    tableHtml += '</tbody></table>';
    return tableHtml;
}

/**
 * Vẽ các biểu đồ.
 * @param {Array<string>} labels - Nhãn cho trục X.
 * @param {Array<number>} avgData - Dữ liệu lương trung bình.
 * @param {Array<number>} totalData - Dữ liệu tổng lương.
 * @param {string} baseTitle - Tiêu đề cơ sở cho biểu đồ.
 */
function renderCharts(labels, avgData, totalData, baseTitle) {
    const avgChartWrapper = document.getElementById('avgChartWrapper');
    const totalChartWrapper = document.getElementById('totalChartWrapper');

    const commonOptions = (titleText) => ({
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            title: { display: true, text: titleText, font: { size: 16 } },
            tooltip: {
                callbacks: {
                    label: (context) => `${context.dataset.label}: ${context.parsed.y.toLocaleString('vi-VN')} VNĐ`
                }
            },
            legend: { position: 'top' }
        },
        scales: {
            y: {
                beginAtZero: true,
                ticks: {
                    callback: (value) => new Intl.NumberFormat('vi-VN', { notation: 'compact', compactDisplay: 'short' }).format(value)
                }
            }
        }
    });

    // Biểu đồ Tổng Lương (dạng cột)
    totalChartInstance = new Chart(document.getElementById('totalChartCanvas'), {
        type: 'bar',
        data: {
            labels,
            datasets: [{
                label: 'Tổng Lương',
                data: totalData,
                backgroundColor: 'rgba(54, 162, 235, 0.7)',
            }]
        },
        options: commonOptions(`Biểu đồ Tổng Lương`)
    });

    // Biểu đồ Lương Trung Bình (dạng đường)
    avgChartInstance = new Chart(document.getElementById('avgChartCanvas'), {
        type: 'line',
        data: {
            labels,
            datasets: [{
                label: 'Lương Trung Bình',
                data: avgData,
                borderColor: 'rgba(255, 99, 132, 1)',
                backgroundColor: 'rgba(255, 99, 132, 0.2)',
                fill: true,
            }]
        },
        options: commonOptions(`Biểu đồ Lương Trung Bình`)
    });

    avgChartWrapper.style.display = 'block';
    totalChartWrapper.style.display = 'block';
}