// ThongKe.js

const BRIDGE_NAME = 'luongNhanVienBridge';
let currentChart = null; // Lưu reference đến Chart hiện tại

document.addEventListener('DOMContentLoaded', () => {
  // 1. Đặt giá trị mặc định cho ô tháng (dùng trong “Thống kê Tháng”)
  const thangInput = document.getElementById('thangNamInput');
  thangInput.value = new Date().toISOString().substring(0, 7);

  // 2. Khởi tạo Flatpickr cho chọn tháng
  flatpickr("#thangNamInput", {
    plugins: [
      new monthSelectPlugin({
        shorthand: true,
        dateFormat: "Y-m",
        altFormat: "F Y"
      })
    ]
  });

  // 3. Đăng ký sự kiện cho select “Thống kê theo”
  document.getElementById('typeSelect').addEventListener('change', onTypeChange);
});

function onTypeChange() {
  const type = document.getElementById('typeSelect').value;

  // Ẩn hết các nhóm input + biểu đồ
  document.getElementById('group-thang').style.display = 'none';
  document.getElementById('group-quy').style.display   = 'none';
  document.getElementById('group-nam').style.display   = 'none';
  document.getElementById('chartContainerWrapper').style.display = 'none';

  if (type === 'thang') {
    document.getElementById('group-thang').style.display = 'flex';
  } else if (type === 'quy') {
    document.getElementById('group-quy').style.display   = 'flex';
  } else if (type === 'nam') {
    document.getElementById('group-nam').style.display   = 'flex';
  }
}

/**
 * Hiển thị bảng từ mảng data với 2 trường: luongTrungBinh, tongLuong
 * labels: mảng nhãn cho từng hàng (ví dụ ["2025-01","2025-02", ...] hoặc ["01","02","03"]).
 */
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

/**
 * Vẽ biểu đồ cột kép:
 *   - dataArray: mảng { luongTrungBinh, tongLuong } tương ứng từng nhãn
 *   - labels: mảng nhãn (["01","02","03"] hoặc ["Q1","Q2",...])
 *   - chartTitle: tiêu đề biểu đồ
 */
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
        legend: {
          position: 'top'
        },
        tooltip: {
          enabled: true
        }
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

/**
 * loadThongKe: khi người dùng bấm “Xem”
 */
function loadThongKe() {
  const type     = document.getElementById('typeSelect').value;
  const resultDiv = document.getElementById('resultContainer');
  resultDiv.innerHTML = '';
  document.getElementById('chartContainerWrapper').style.display = 'none';

  if (!type) {
    alert('Vui lòng chọn loại thống kê (Tháng/Quý/Năm).');
    return;
  }

  // =========================
  // 1) Thống kê "Tháng"
  // =========================
  if (type === 'thang') {
    const thangNam = document.getElementById('thangNamInput').value;
    if (!thangNam) {
      alert('Vui lòng chọn tháng.');
      return;
    }
    if (!window[BRIDGE_NAME] || typeof window[BRIDGE_NAME].thongKeTheoThang !== 'function') {
      console.error('Bridge hoặc phương thức thongKeTheoThang không tồn tại.');
      return;
    }
    try {
      const jsonString = window[BRIDGE_NAME].thongKeTheoThang(thangNam);
      const dataArray = JSON.parse(jsonString);
      if (dataArray.length === 0) {
        resultDiv.innerHTML = '<p>Không có dữ liệu.</p>';
        return;
      }
      const obj = dataArray[0];

      resultDiv.innerHTML = `<div class="result-title">Kết quả Thống kê ${thangNam}</div>`;
      resultDiv.innerHTML += renderTableFromData([obj], [thangNam]);
      drawGroupedBarChart([obj], [thangNam], `Biểu đồ Thống kê ${thangNam}`);
    } catch (e) {
      console.error('Lỗi Thống kê:', e);
      alert('Có lỗi khi thống kê theo tháng.');
    }
    return;
  }

  // =========================
  // 2) Thống kê "Quý"
  // =========================
  if (type === 'quy') {
    const nam = document.getElementById('namQuyInput').value;
    const quy = parseInt(document.getElementById('quySelect').value, 10);
    if (!nam || !quy) {
      alert('Vui lòng nhập năm và chọn quý.');
      return;
    }
    if (!window[BRIDGE_NAME] || typeof window[BRIDGE_NAME].thongKeTheoThang !== 'function') {
      console.error('Bridge hoặc phương thức thongKeTheoQuy/thongKeTheoThang không tồn tại.');
      return;
    }
    try {
      // Xác định 3 tháng của quý
      const startMonth = (quy - 1) * 3 + 1;  // Ví dụ: Q1 -> 1, Q2 -> 4, Q3 -> 7, Q4 -> 10
      const monthLabels = [];
      const promises = [];

      for (let i = 0; i < 3; i++) {
        const m = startMonth + i;
        const mm = m < 10 ? '0' + m : '' + m;
        const thangNam = `${nam}-${mm}`;  // Ví dụ "2025-01"
        monthLabels.push(mm);

        promises.push(
          new Promise(resolve => {
            const jsonStr = window[BRIDGE_NAME].thongKeTheoThang(thangNam);
            const arr = JSON.parse(jsonStr);
            if (arr.length > 0) {
              resolve(arr[0]);
            } else {
              resolve({ luongTrungBinh: 0, tongLuong: 0 });
            }
          })
        );
      }

      Promise.all(promises).then(results => {
        // results: mảng 3 object tương ứng 3 tháng
        resultDiv.innerHTML = `<div class="result-title">Kết quả Thống kê Quý ${quy}/${nam}</div>`;
        resultDiv.innerHTML += renderTableFromData(results, monthLabels);
        drawGroupedBarChart(results, monthLabels, `Biểu đồ Thống kê Quý ${quy}/${nam}`);
      });
    } catch (e) {
      console.error('Lỗi Thống kê Quý:', e);
      alert('Có lỗi khi thống kê theo quý.');
    }
    return;
  }

  // =========================
  // 3) Thống kê "Năm"
  // =========================
  if (type === 'nam') {
    const nam = document.getElementById('namInput').value;
    if (!nam) {
      alert('Vui lòng nhập năm.');
      return;
    }
    if (!window[BRIDGE_NAME] || typeof window[BRIDGE_NAME].thongKeTheoThang !== 'function') {
      console.error('Bridge hoặc phương thức thongKeTheoThang không tồn tại.');
      return;
    }
    try {
      const monthLabels = [];
      const promises = [];

      for (let m = 1; m <= 12; m++) {
        const mm = m < 10 ? '0' + m : '' + m;
        const thangNam = `${nam}-${mm}`;
        monthLabels.push(mm);
        promises.push(
          new Promise(resolve => {
            const jsonStr = window[BRIDGE_NAME].thongKeTheoThang(thangNam);
            const arr = JSON.parse(jsonStr);
            if (arr.length > 0) {
              resolve(arr[0]);
            } else {
              resolve({ luongTrungBinh: 0, tongLuong: 0 });
            }
          })
        );
      }

      Promise.all(promises).then(results => {
        resultDiv.innerHTML = `<div class="result-title">Kết quả Thống kê Năm ${nam}</div>`;
        resultDiv.innerHTML += renderTableFromData(results, monthLabels);
        drawGroupedBarChart(results, monthLabels, `Biểu đồ Thống kê Năm ${nam}`);
      });
    } catch (e) {
      console.error('Lỗi Thống kê Năm:', e);
      alert('Có lỗi khi thống kê theo năm.');
    }
    return;
  }
}
