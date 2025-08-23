# Medi Staff Manager – Medical HR Management System

## 👥 Contributors

* [@leducphuclong](https://github.com/leducphuclong) – Project Lead, Backend & Architecture
* [@phunolg](https://github.com/phunolg) – UI/UX Design, JavaFX Frontend
* [@TuaLearnCode](https://github.com/TuaLearnCode) – Database Design & Integration
* [@hoi936](https://github.com/hoi936) – Report, Testing & Chart.js Visualization

---

## 📘 Introduction

**Medi Staff Manager** is a comprehensive desktop application developed with Java and JavaFX to manage human resources, payroll, and work schedules in medical facilities. It provides secure, efficient, and user-friendly tools to streamline HR operations and statistical reporting.

---

## ✨ Key Highlights

- **Secure Authentication**: Password encryption with BCrypt
- **Modern UI/UX**: JavaFX + CSS3 + responsive WebView integration
- **Detailed Reports**: Statistical charts powered by Chart.js
- **Excel Integration**: Import/export data using Apache POI
- **Clean Architecture**: Separation of concerns using MVC
- **Bridge Pattern**: Connects WebView with JavaFX controllers

---

## 🔑 Core Features

### 👥 Employee Management
- Add, edit, delete employee profiles
- Search and filter by name, department, ID
- Validate input data
- Manage roles, departments, personal details

### 🏢 Department Management
- Create and manage department structure
- Assign employees to departments
- Transfer employees between departments

### 💰 Salary Management
- Auto-calculate salary based on job coefficient
- Manage base salary, bonus, allowance, overtime
- Update base salary system-wide
- Filter salary by month, department

### 📊 Statistics & Reports
- Monthly and departmental salary statistics
- Visualized data with Chart.js
- Export reports to Excel

### 📅 Schedule Management
- Create and manage working shifts
- Import schedule via Excel
- Track working hours, rest periods

### 🔐 Account Management
- Role-based access (HR Manager, Accountant)
- Secure password encryption with BCrypt
- Forgot password via email integration

---

## 🛠️ Technologies Used

### Backend
- Java 11+
- JavaFX (Desktop UI)
- MySQL
- JDBC (Database connection)
- BCrypt (Password encryption)
- Gson (JSON handling)
- Apache POI (Excel handling)

### Frontend
- HTML5, CSS3, JavaScript (in WebView)
- Chart.js for statistical graphs
- Font Awesome for icons

### Additional Libraries
- Jakarta Mail (Email service)
- Spring Security (Authorization layer)
- Commons IO (File handling)

---

## 📁 Project Structure

```
medi-staff-manager/
├── src/
│   ├── application/Main.java                # Entry point
│   ├── com/MediStaffManager/
│   │   ├── bean/                            # Data models
│   │   ├── bo/                              # Business logic
│   │   ├── controller/                      # Controllers
│   │   ├── dao/                             # Data access layer
│   │   ├── utils/                           # Utilities
│   │   └── view/                            # UI components
│   │       ├── dangNhap/                   # Login module
│   │       ├── quanLyNhanVien/             # Employee management
│   │       ├── quanLyPhongBan/             # Department management
│   │       ├── quanLyLuong/                # Salary management
│   │       ├── quanLyTaiKhoan/             # Account management
│   │       └── lichthang/                  # Schedule management
├── lib/                                     # External libraries
├── database/                                # SQL dump & scripts
├── bin/                                     # Compiled classes
└── README.md
```

---

## 🧪 Data Validation Rules

### Employee Info
- **CCCD**: 12 digits, unique
- **Name**: Required, valid string
- **Phone**: 10–11 digits, required
- **Email**: Valid format (optional)
- **DOB**: Format `YYYY-MM-DD`
- **Gender**: Male/Female (required)
- **Position / Department**: Selected from existing lists

### Account
- **Username**: 3–50 chars, unique
- **Password**: Min 6 characters
- **Role**: "HR Manager" or "Accountant"
- **Linking**: One account per employee

### Salary
- **Base Salary**: Auto-calculated by position coefficient
- **Bonus/Allowance/Overtime**: Optional, numeric
- **Total**: Automatically computed

---

## 🧩 Bridge Pattern API (JavaFX ↔ WebView)

JavaScript can call Java functions via bridge:

```js
bridge.getQuanLyNhanVienBridge().layDanhSachNhanVien();
bridge.getQuanLyLuongBridge().layLuongTheoThang("2024-01");
```

### Available Bridges
- `DangNhapBridge`: Login handling
- `QuanLyNhanVienBridge`: Employee CRUD
- `QuanLyPhongBanBridge`: Department operations
- `QuanLyLuongBridge`: Salary calculations
- `QuanLyTaiKhoanBridge`: Account access
- `ThongKeLuongBridge`: Statistical data

---

## ⚙️ Setup Guide

### Prerequisites
- Java JDK 11+
- MySQL 5.7+
- Maven 3.6+ (optional)
- IDE: Eclipse, IntelliJ, or VS Code

### Step 1: Clone Repository
```bash
git clone https://github.com/yourusername/medi-staff-manager.git
cd medi-staff-manager
```

### Step 2: Configure MySQL Database
```sql
CREATE DATABASE medi_staff_db;
```

Then import the SQL file:
```bash
mysql -u root -p medi_staff_db < database/Dump20250610.sql
```

Update `DBConnection.java` with your credentials:
```java
private static final String URL = "jdbc:mysql://localhost:3306/medi_staff_db";
private static final String USERNAME = "your_username";
private static final String PASSWORD = "your_password";
```

### Step 3: Add Dependencies
Use Maven or manually add JARs:

```xml
<dependency>
  <groupId>org.openjfx</groupId>
  <artifactId>javafx-controls</artifactId>
  <version>17</version>
</dependency>
<!-- Add more dependencies as needed -->
```

### Step 4: Compile & Run
```bash
# Compile
javac -cp "lib/*" src/application/Main.java

# Run
java -cp "lib/*:src" application.Main
```

---

## 🔐 Default Accounts (for demo)

### Accountant
- Username: `nhanvien`
- Password: `password123`

### HR Manager
- Username: `quanly`
- Password: `password123`

---

## 📊 Usage Guide

### 1. Employee Management
- Use menu to add/edit/delete employees
- Search by name, ID, or department

### 2. Salary Management
- Filter by month and department
- Edit salary components (bonus, allowance)
- View calculated total salary

### 3. Reports
- Compare salary data by department or time
- Export Excel reports
- Visualize with Chart.js

---

## 📝 License

This project is licensed under the MIT License.
