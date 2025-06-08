// ThongKe.js

let currentChart = null;

document.addEventListener('DOMContentLoaded', () => {
  setTimeout(() => {
    const bridge = getBridge();
    if (!bridge) {
      console.error(`JS: Java bridge không có sẵn.`);
      alert(`Bridge không tìm thấy.`);
      return;
    }

    if (!window.pageThongKeInitialized) {
      if (typeof initThongKe === 'function') {
        initThongKe();
        window.pageThongKeInitialized = true;
        bridge.log("JS: initThongKe đã khởi tạo xong.");
      } else {
        console.error("JS: Hàm initThongKe không tồn tại.");
        alert("Thiếu hàm initThongKe.");
      }
    } else {
      bridge.log("JS: Trang thống kê đã được khởi tạo trước đó.");
    }
  }, 300);
});

function getBridge() {
  return bridge.getThongKeLuongBridge()
}

function initThongKe() {
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
    const jsonStr = getBridge().thongKeTheoThang(thangNam);
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
        const jsonStr = getBridge().thongKeTheoThang(thangNam);
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
        const jsonStr = getBridge().thongKeTheoThang(thangNam);
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

window.initThongKe = initThongKe;
window.loadThongKe = loadThongKe;
