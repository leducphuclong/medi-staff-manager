<<<<<<< HEAD
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
=======
// // ThongKe.js

// const BRIDGE_NAME = 'luongNhanVienBridge';
// let currentChart = null; // Lưu reference đến Chart hiện tại

// document.addEventListener('DOMContentLoaded', () => {
//   // 1. Đặt giá trị mặc định cho ô tháng (dùng trong “Thống kê Tháng”)
//   const thangInput = document.getElementById('thangNamInput');
//   thangInput.value = new Date().toISOString().substring(0, 7);

//   // 2. Khởi tạo Flatpickr cho chọn tháng
//   flatpickr("#thangNamInput", {
//     plugins: [
//       new monthSelectPlugin({
//         shorthand: true,
//         dateFormat: "Y-m",
//         altFormat: "F Y"
//       })
//     ]
//   });

//   // 3. Đăng ký sự kiện cho select “Thống kê theo”
//   document.getElementById('typeSelect').addEventListener('change', onTypeChange);
// });

// function onTypeChange() {
//   const type = document.getElementById('typeSelect').value;

//   // Ẩn hết các nhóm input + biểu đồ
//   document.getElementById('group-thang').style.display = 'none';
//   document.getElementById('group-quy').style.display   = 'none';
//   document.getElementById('group-nam').style.display   = 'none';
//   document.getElementById('chartContainerWrapper').style.display = 'none';

//   if (type === 'thang') {
//     document.getElementById('group-thang').style.display = 'flex';
//   } else if (type === 'quy') {
//     document.getElementById('group-quy').style.display   = 'flex';
//   } else if (type === 'nam') {
//     document.getElementById('group-nam').style.display   = 'flex';
//   }
// }

// /**
//  * Hiển thị bảng từ mảng data với 2 trường: luongTrungBinh, tongLuong
//  * labels: mảng nhãn cho từng hàng (ví dụ ["2025-01","2025-02", ...] hoặc ["01","02","03"]).
//  */
// function renderTableFromData(dataArray, labels) {
//   if (!Array.isArray(dataArray) || dataArray.length === 0) {
//     return '<p>Không có dữ liệu.</p>';
//   }
//   let html = '<table><thead><tr>';
//   html += '<th>Tháng</th>';
//   html += '<th>Lương trung bình</th>';
//   html += '<th>Tổng lương</th>';
//   html += '</tr></thead><tbody>';

//   for (let i = 0; i < dataArray.length; i++) {
//     const obj = dataArray[i];
//     const label = labels[i];
//     html += '<tr>';
//     html += `<td>${label}</td>`;
//     html += `<td>${obj.luongTrungBinh !== null ? obj.luongTrungBinh : 0}</td>`;
//     html += `<td>${obj.tongLuong !== null ? obj.tongLuong : 0}</td>`;
//     html += '</tr>';
//   }
//   html += '</tbody></table>';
//   return html;
// }

// /**
//  * Vẽ biểu đồ cột kép:
//  *   - dataArray: mảng { luongTrungBinh, tongLuong } tương ứng từng nhãn
//  *   - labels: mảng nhãn (["01","02","03"] hoặc ["Q1","Q2",...])
//  *   - chartTitle: tiêu đề biểu đồ
//  */
// function drawGroupedBarChart(dataArray, labels, chartTitle) {
//   if (currentChart) {
//     currentChart.destroy();
//   }

//   const avgData = dataArray.map(item => item.luongTrungBinh || 0);
//   const totalData = dataArray.map(item => item.tongLuong || 0);

//   const ctx = document.getElementById('chartContainer').getContext('2d');
//   currentChart = new Chart(ctx, {
//     type: 'bar',
//     data: {
//       labels: labels,
//       datasets: [
//         {
//           label: 'Lương trung bình',
//           data: avgData,
//           backgroundColor: 'rgba(40, 167, 69, 0.7)',
//           borderColor: 'rgba(40, 167, 69, 1)',
//           borderWidth: 1
//         },
//         {
//           label: 'Tổng lương',
//           data: totalData,
//           backgroundColor: 'rgba(54, 162, 235, 0.7)',
//           borderColor: 'rgba(54, 162, 235, 1)',
//           borderWidth: 1
//         }
//       ]
//     },
//     options: {
//       plugins: {
//         title: {
//           display: true,
//           text: chartTitle,
//           font: { size: 18, weight: '600' },
//           color: '#005caa'
//         },
//         legend: {
//           position: 'top'
//         },
//         tooltip: {
//           enabled: true
//         }
//       },
//       responsive: true,
//       maintainAspectRatio: false,
//       scales: {
//         x: {
//           ticks: {
//             color: '#033e8c',
//             font: { size: 14, weight: '600' }
//           },
//           stacked: false
//         },
//         y: {
//           beginAtZero: true,
//           ticks: {
//             color: '#033e8c',
//             font: { size: 12 }
//           },
//           stacked: false
//         }
//       }
//     }
//   });

//   document.getElementById('chartContainerWrapper').style.display = 'block';
// }

// /**
//  * loadThongKe: khi người dùng bấm “Xem”
//  */
// function loadThongKe() {
//   const type     = document.getElementById('typeSelect').value;
//   const resultDiv = document.getElementById('resultContainer');
//   resultDiv.innerHTML = '';
//   document.getElementById('chartContainerWrapper').style.display = 'none';

//   if (!type) {
//     alert('Vui lòng chọn loại thống kê (Tháng/Quý/Năm).');
//     return;
//   }

//   // =========================
//   // 1) Thống kê "Tháng"
//   // =========================
//   if (type === 'thang') {
//     const thangNam = document.getElementById('thangNamInput').value;
//     if (!thangNam) {
//       alert('Vui lòng chọn tháng.');
//       return;
//     }
//     if (!window[BRIDGE_NAME] || typeof window[BRIDGE_NAME].thongKeTheoThang !== 'function') {
//       console.error('Bridge hoặc phương thức thongKeTheoThang không tồn tại.');
//       return;
//     }
//     try {
//       const jsonString = window[BRIDGE_NAME].thongKeTheoThang(thangNam);
//       const dataArray = JSON.parse(jsonString);
//       if (dataArray.length === 0) {
//         resultDiv.innerHTML = '<p>Không có dữ liệu.</p>';
//         return;
//       }
//       const obj = dataArray[0];

//       resultDiv.innerHTML = `<div class="result-title">Kết quả Thống kê ${thangNam}</div>`;
//       resultDiv.innerHTML += renderTableFromData([obj], [thangNam]);
//       drawGroupedBarChart([obj], [thangNam], `Biểu đồ Thống kê ${thangNam}`);
//     } catch (e) {
//       console.error('Lỗi Thống kê:', e);
//       alert('Có lỗi khi thống kê theo tháng.');
//     }
//     return;
//   }

//   // =========================
//   // 2) Thống kê "Quý"
//   // =========================
//   if (type === 'quy') {
//     const nam = document.getElementById('namQuyInput').value;
//     const quy = parseInt(document.getElementById('quySelect').value, 10);
//     if (!nam || !quy) {
//       alert('Vui lòng nhập năm và chọn quý.');
//       return;
//     }
//     if (!window[BRIDGE_NAME] || typeof window[BRIDGE_NAME].thongKeTheoThang !== 'function') {
//       console.error('Bridge hoặc phương thức thongKeTheoQuy/thongKeTheoThang không tồn tại.');
//       return;
//     }
//     try {
//       // Xác định 3 tháng của quý
//       const startMonth = (quy - 1) * 3 + 1;  // Ví dụ: Q1 -> 1, Q2 -> 4, Q3 -> 7, Q4 -> 10
//       const monthLabels = [];
//       const promises = [];

//       for (let i = 0; i < 3; i++) {
//         const m = startMonth + i;
//         const mm = m < 10 ? '0' + m : '' + m;
//         const thangNam = `${nam}-${mm}`;  // Ví dụ "2025-01"
//         monthLabels.push(mm);

//         promises.push(
//           new Promise(resolve => {
//             const jsonStr = window[BRIDGE_NAME].thongKeTheoThang(thangNam);
//             const arr = JSON.parse(jsonStr);
//             if (arr.length > 0) {
//               resolve(arr[0]);
//             } else {
//               resolve({ luongTrungBinh: 0, tongLuong: 0 });
//             }
//           })
//         );
//       }

//       Promise.all(promises).then(results => {
//         // results: mảng 3 object tương ứng 3 tháng
//         resultDiv.innerHTML = `<div class="result-title">Kết quả Thống kê Quý ${quy}/${nam}</div>`;
//         resultDiv.innerHTML += renderTableFromData(results, monthLabels);
//         drawGroupedBarChart(results, monthLabels, `Biểu đồ Thống kê Quý ${quy}/${nam}`);
//       });
//     } catch (e) {
//       console.error('Lỗi Thống kê Quý:', e);
//       alert('Có lỗi khi thống kê theo quý.');
//     }
//     return;
//   }

//   // =========================
//   // 3) Thống kê "Năm"
//   // =========================
//   if (type === 'nam') {
//     const nam = document.getElementById('namInput').value;
//     if (!nam) {
//       alert('Vui lòng nhập năm.');
//       return;
//     }
//     if (!window[BRIDGE_NAME] || typeof window[BRIDGE_NAME].thongKeTheoThang !== 'function') {
//       console.error('Bridge hoặc phương thức thongKeTheoThang không tồn tại.');
//       return;
//     }
//     try {
//       const monthLabels = [];
//       const promises = [];

//       for (let m = 1; m <= 12; m++) {
//         const mm = m < 10 ? '0' + m : '' + m;
//         const thangNam = `${nam}-${mm}`;
//         monthLabels.push(mm);
//         promises.push(
//           new Promise(resolve => {
//             const jsonStr = window[BRIDGE_NAME].thongKeTheoThang(thangNam);
//             const arr = JSON.parse(jsonStr);
//             if (arr.length > 0) {
//               resolve(arr[0]);
//             } else {
//               resolve({ luongTrungBinh: 0, tongLuong: 0 });
//             }
//           })
//         );
//       }

//       Promise.all(promises).then(results => {
//         resultDiv.innerHTML = `<div class="result-title">Kết quả Thống kê Năm ${nam}</div>`;
//         resultDiv.innerHTML += renderTableFromData(results, monthLabels);
//         drawGroupedBarChart(results, monthLabels, `Biểu đồ Thống kê Năm ${nam}`);
//       });
//     } catch (e) {
//       console.error('Lỗi Thống kê Năm:', e);
//       alert('Có lỗi khi thống kê theo năm.');
//     }
//     return;
//   }
// }

// --- Global State ---
window.pageThongKeInitialized = window.pageThongKeInitialized || false;

// --- Constants ---
const BRIDGE_NAME = 'thongKeBridge';
let currentChart = null;

// --- DOM Ready ---
document.addEventListener('DOMContentLoaded', () => {
  setTimeout(() => {
    if (typeof window[BRIDGE_NAME] === 'undefined') {
      console.error(`JS: Java bridge '${BRIDGE_NAME}' không có sẵn.`);
      alert(`Bridge '${BRIDGE_NAME}' không tìm thấy.`);
      return;
    }

    if (!window.pageThongKeInitialized) {
      if (typeof initThongKe === 'function') {
        initThongKe();
        window.pageThongKeInitialized = true;
        window[BRIDGE_NAME].log("JS: initThongKe đã khởi tạo xong.");
      } else {
        console.error("JS: Hàm initThongKe không tồn tại.");
        alert("Thiếu hàm initThongKe.");
      }
    } else {
      window[BRIDGE_NAME].log("JS: Trang thống kê đã được khởi tạo trước đó.");
    }
  }, 300);
});

// --- Hàm Khởi Tạo Chính ---
function initThongKe() {
  console.log("✅ Hàm initThongKe được gọi.");
  const input = document.getElementById('thangNamInput');
  if (input) {
    input.value = new Date().toISOString().substring(0, 7);
    flatpickr("#thangNamInput", {
      plugins: [
        new monthSelectPlugin({
          shorthand: true,
          dateFormat: "Y-m",
          altFormat: "F Y"
        })
      ]
    });
  }

  const select = document.getElementById('typeSelect');
  if (select) {
    select.addEventListener('change', onTypeChange);
  }

  onTypeChange();
}

function onTypeChange() {
  const type = document.getElementById('typeSelect').value;
  document.getElementById('group-thang').style.display = 'none';
  document.getElementById('group-quy').style.display = 'none';
  document.getElementById('group-nam').style.display = 'none';
  document.getElementById('chartContainerWrapper').style.display = 'none';

  if (type === 'thang') {
    document.getElementById('group-thang').style.display = 'flex';
  } else if (type === 'quy') {
    document.getElementById('group-quy').style.display = 'flex';
  } else if (type === 'nam') {
    document.getElementById('group-nam').style.display = 'flex';
  }
}

function renderTableFromData(dataArray, labels) {
  if (!Array.isArray(dataArray) || dataArray.length === 0) {
    return '<p>Không có dữ liệu.</p>';
  }
  let html = '<table><thead><tr>';
  html += '<th>Tháng</th>';
  html += '<th>Lương trung bình</th>';
  html += '<th>Tổng lương</th>';
  html += '</tr></thead><tbody>';
  for (let i = 0; i < dataArray.length; i++) {
    const obj = dataArray[i];
    const label = labels[i];
    html += '<tr>';
    html += `<td>${label}</td>`;
    html += `<td>${obj.luongTrungBinh !== null ? obj.luongTrungBinh : 0}</td>`;
    html += `<td>${obj.tongLuong !== null ? obj.tongLuong : 0}</td>`;
    html += '</tr>';
  }
  html += '</tbody></table>';
  return html;
}

function drawGroupedBarChart(dataArray, labels, chartTitle) {
  if (currentChart) {
    currentChart.destroy();
  }

  const avgData = dataArray.map(item => item.luongTrungBinh || 0);
  const totalData = dataArray.map(item => item.tongLuong || 0);

  const ctx = document.getElementById('chartContainer').getContext('2d');
  currentChart = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: labels,
      datasets: [
        {
          label: 'Lương trung bình',
          data: avgData,
          backgroundColor: 'rgba(40, 167, 69, 0.7)',
          borderColor: 'rgba(40, 167, 69, 1)',
          borderWidth: 1
        },
        {
          label: 'Tổng lương',
          data: totalData,
          backgroundColor: 'rgba(54, 162, 235, 0.7)',
          borderColor: 'rgba(54, 162, 235, 1)',
          borderWidth: 1
        }
      ]
    },
    options: {
      plugins: {
        title: {
          display: true,
          text: chartTitle,
          font: { size: 18, weight: '600' },
          color: '#005caa'
        },
        legend: { position: 'top' },
        tooltip: { enabled: true }
      },
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        x: {
          ticks: {
            color: '#033e8c',
            font: { size: 14, weight: '600' }
          },
          stacked: false
        },
        y: {
          beginAtZero: true,
          ticks: {
            color: '#033e8c',
            font: { size: 12 }
          },
          stacked: false
        }
      }
    }
  });

  document.getElementById('chartContainerWrapper').style.display = 'block';
}

function loadThongKe() {
  const type = document.getElementById('typeSelect').value;
  const resultDiv = document.getElementById('resultContainer');
  resultDiv.innerHTML = '';
  document.getElementById('chartContainerWrapper').style.display = 'none';

  if (!type) {
    alert('Vui lòng chọn loại thống kê.');
    return;
  }

  if (type === 'thang') {
    const thangNam = document.getElementById('thangNamInput').value;
    const jsonStr = window[BRIDGE_NAME].thongKeTheoThang(thangNam);
    const arr = JSON.parse(jsonStr);
    if (arr.length === 0) {
      resultDiv.innerHTML = '<p>Không có dữ liệu.</p>';
      return;
    }
    resultDiv.innerHTML = `<div class="result-title">Thống kê ${thangNam}</div>` + renderTableFromData(arr, [thangNam]);
    drawGroupedBarChart(arr, [thangNam], `Biểu đồ ${thangNam}`);
  } else if (type === 'quy') {
    const nam = document.getElementById('namQuyInput').value;
    const quy = parseInt(document.getElementById('quySelect').value, 10);
    const start = (quy - 1) * 3 + 1;
    const labels = [], promises = [];
    for (let i = 0; i < 3; i++) {
      const m = start + i;
      const mm = m < 10 ? '0' + m : '' + m;
      const thangNam = `${nam}-${mm}`;
      labels.push(mm);
      promises.push(Promise.resolve().then(() => {
        const jsonStr = window[BRIDGE_NAME].thongKeTheoThang(thangNam);
        const arr = JSON.parse(jsonStr);
        return arr.length > 0 ? arr[0] : { luongTrungBinh: 0, tongLuong: 0 };
      }));
    }
    Promise.all(promises).then(results => {
      resultDiv.innerHTML = `<div class="result-title">Thống kê Quý ${quy}/${nam}</div>` + renderTableFromData(results, labels);
      drawGroupedBarChart(results, labels, `Biểu đồ Quý ${quy}/${nam}`);
    });
  } else if (type === 'nam') {
    const nam = document.getElementById('namInput').value;
    const labels = [], promises = [];
    for (let m = 1; m <= 12; m++) {
      const mm = m < 10 ? '0' + m : '' + m;
      const thangNam = `${nam}-${mm}`;
      labels.push(mm);
      promises.push(Promise.resolve().then(() => {
        const jsonStr = window[BRIDGE_NAME].thongKeTheoThang(thangNam);
        const arr = JSON.parse(jsonStr);
        return arr.length > 0 ? arr[0] : { luongTrungBinh: 0, tongLuong: 0 };
      }));
    }
    Promise.all(promises).then(results => {
      resultDiv.innerHTML = `<div class="result-title">Thống kê Năm ${nam}</div>` + renderTableFromData(results, labels);
      drawGroupedBarChart(results, labels, `Biểu đồ Năm ${nam}`);
    });
  }
}

// Expose
window.initThongKe = initThongKe;
window.loadThongKe = loadThongKe;

>>>>>>> ThaoDuyen-QuanLyLuong-ThongKe
