CREATE DATABASE  IF NOT EXISTS `pbl3` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `pbl3`;
-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: pbl3
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ca_lam_viec`
--

DROP TABLE IF EXISTS `ca_lam_viec`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ca_lam_viec` (
  `IDCaLam` int NOT NULL AUTO_INCREMENT,
  `IDNhanVien` int NOT NULL,
  `NgayLamViec` date NOT NULL,
  `tenCa` varchar(50) NOT NULL,
  `moTaCa` text,
  `GioBatDauThucTe` datetime NOT NULL,
  `GioKetThucThucTe` datetime NOT NULL,
  `GioNghiBatDau` datetime DEFAULT NULL,
  `GioNghiKetThuc` datetime DEFAULT NULL,
  `DonVi` varchar(100) NOT NULL,
  `GhiChu` text,
  `laTrucOnCall` tinyint DEFAULT '0',
  PRIMARY KEY (`IDCaLam`),
  UNIQUE KEY `UC_NhanVien_Ngay_Ca` (`IDNhanVien`,`NgayLamViec`,`tenCa`),
  KEY `IDNhanVien` (`IDNhanVien`),
  CONSTRAINT `fk_idNhanVien` FOREIGN KEY (`IDNhanVien`) REFERENCES `nhan_vien` (`IDNhanVien`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ca_lam_viec`
--

LOCK TABLES `ca_lam_viec` WRITE;
/*!40000 ALTER TABLE `ca_lam_viec` DISABLE KEYS */;
INSERT INTO `ca_lam_viec` VALUES (50,3,'2025-05-16','Ca 2','Sáng','2025-05-16 08:00:00','2025-05-16 17:00:00','2025-05-09 12:00:00','2025-05-09 13:00:00','Phòng Hành Chính','Nghỉ trưa',0),(54,2,'2025-05-16','Ca 1','Chiều','2025-05-06 13:00:00','2025-05-06 17:00:00','2025-05-06 15:00:00','2025-05-06 16:00:00','Phòng Hành Chính','Nghỉ chiều',0);
/*!40000 ALTER TABLE `ca_lam_viec` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chuc_vu`
--

DROP TABLE IF EXISTS `chuc_vu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chuc_vu` (
  `IDChucVu` int NOT NULL,
  `TenChucVu` varchar(255) DEFAULT NULL,
  `HeSoLuong` decimal(5,2) DEFAULT NULL,
  PRIMARY KEY (`IDChucVu`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chuc_vu`
--

LOCK TABLES `chuc_vu` WRITE;
/*!40000 ALTER TABLE `chuc_vu` DISABLE KEYS */;
INSERT INTO `chuc_vu` VALUES (1,'Nhân viên',1.00),(2,'Quản lý',1.50),(3,'Giám đốc',2.00),(4,'Nhân viên bán hàng',1.10);
/*!40000 ALTER TABLE `chuc_vu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `luong_nhan_vien`
--

DROP TABLE IF EXISTS `luong_nhan_vien`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `luong_nhan_vien` (
  `IDLuong` int NOT NULL AUTO_INCREMENT,
  `IDNhanVien` int NOT NULL,
  `IDChucVu` int NOT NULL,
  `ThangNam` varchar(20) NOT NULL,
  `LuongThuNhap` decimal(10,2) DEFAULT NULL,
  `Thuong` decimal(10,2) DEFAULT NULL,
  `PhuCap` decimal(10,2) DEFAULT NULL,
  `TangCa` decimal(10,2) DEFAULT NULL,
  `TongLuong` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`IDLuong`),
  UNIQUE KEY `uq_nhanvien_thangnam` (`IDNhanVien`,`ThangNam`),
  KEY `fk_luong_cv` (`IDChucVu`),
  CONSTRAINT `fk_luong_cv` FOREIGN KEY (`IDChucVu`) REFERENCES `chuc_vu` (`IDChucVu`),
  CONSTRAINT `fk_luong_nhan_vien_cascade` FOREIGN KEY (`IDNhanVien`) REFERENCES `nhan_vien` (`IDNhanVien`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=98 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `luong_nhan_vien`
--

LOCK TABLES `luong_nhan_vien` WRITE;
/*!40000 ALTER TABLE `luong_nhan_vien` DISABLE KEYS */;
INSERT INTO `luong_nhan_vien` VALUES (5,12,2,'2025-02',8500000.00,10000.00,15000.00,1000000.00,1500000.00),(9,3,2,'2025-02',8900000.00,15000.00,25000.00,4000000.00,2255000.00),(13,9,1,'2025-03',7000000.00,5000.00,15000.00,3000000.00,2100000.00),(14,3,2,'2025-05',8600000.00,20000.00,20000.00,2000000.00,2160000.00),(16,2,1,'2025-01',9000000.00,50000.00,50000.00,10000000.00,2660000.00),(18,3,3,'2025-04',8500000.00,20000.00,20000.00,3000000.00,2170000.00),(27,10,1,'2025-02',7600000.00,20000.00,10000.00,2000000.00,1985000.00),(36,9,3,'2025-02',8500000.00,5000.00,15000.00,1000000.00,2065000.00),(37,12,2,'2025-01',8000000.00,10000.00,15000.00,1500000.00,1910000.00),(39,10,3,'2025-04',8800000.00,30000.00,20000.00,4000000.00,2325000.00),(43,9,2,'2025-04',8300000.00,20000.00,15000.00,2000000.00,2150000.00),(49,3,1,'2025-01',8200000.00,30000.00,25000.00,3000000.00,2305000.00),(51,9,1,'2025-01',7800000.00,25000.00,30000.00,4000000.00,2330000.00),(52,10,2,'2025-05',8200000.00,20000.00,20000.00,3000000.00,2155000.00),(53,10,2,'2025-01',8000000.00,20000.00,20000.00,2000000.00,1260000.00),(82,10,3,'2024-11',7800000.00,15000.00,20000.00,900000.00,8735000.00),(84,2,1,'2024-10',11500000.00,0.00,50000.00,1800000.00,13350000.00),(86,9,1,'2024-09',11800000.00,75000.00,45000.00,2200000.00,14120000.00),(87,3,2,'2024-09',8900000.00,25000.00,30000.00,1600000.00,10555000.00),(91,12,2,'2023-07',8600000.00,25000.00,30000.00,1300000.00,9955000.00),(93,2,1,'2023-05',10800000.00,40000.00,50000.00,1200000.00,12090000.00),(95,3,2,'2023-03',8800000.00,30000.00,30000.00,3000000.00,11860000.00),(97,10,3,'2023-01',7500000.00,20000.00,20000.00,1500000.00,9040000.00);
/*!40000 ALTER TABLE `luong_nhan_vien` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nhan_vien`
--

DROP TABLE IF EXISTS `nhan_vien`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nhan_vien` (
  `IDNhanVien` int NOT NULL AUTO_INCREMENT,
  `CCCD` varchar(20) DEFAULT NULL,
  `HoTen` varchar(255) DEFAULT NULL,
  `SDT` varchar(20) DEFAULT NULL,
  `Email` varchar(255) DEFAULT NULL,
  `GioiTinh` varchar(10) DEFAULT NULL,
  `NgaySinh` date DEFAULT NULL,
  `IDChucVu` int DEFAULT NULL,
  `IDPhongBan` int DEFAULT NULL,
  PRIMARY KEY (`IDNhanVien`),
  KEY `IDChucVu` (`IDChucVu`),
  KEY `IDPhongBan` (`IDPhongBan`),
  CONSTRAINT `nhan_vien_ibfk_1` FOREIGN KEY (`IDChucVu`) REFERENCES `chuc_vu` (`IDChucVu`),
  CONSTRAINT `nhan_vien_ibfk_2` FOREIGN KEY (`IDPhongBan`) REFERENCES `phong_ban` (`IDPhongBan`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nhan_vien`
--

LOCK TABLES `nhan_vien` WRITE;
/*!40000 ALTER TABLE `nhan_vien` DISABLE KEYS */;
INSERT INTO `nhan_vien` VALUES (2,'123456789013','Trần Thị B','0987654322','b.tran@example.com','Nữ','1992-02-02',2,2),(3,'123456789014','Lê Văn C','0987654323','c.le@example.com','Nam','1985-03-03',1,3),(9,'123456789020','Phạm Văn I','0987654329','i.pham@example.com','Nam','1990-09-09',1,6),(10,'123456789021','Bùi Thị J','0987654330','j.bui@example.com','Nữ','1995-10-10',2,3),(12,'12345678933','Huỳnh Duyên','03282828828','htthao@gmail.com','Nữ','1999-04-09',2,2),(13,'12345678934','Huỳnh Trương Thảo Duyên','03282828828','htthao012@gmail.com','Nữ','2005-02-08',3,2),(14,'091416754412','4234','0914167544','3424@gmail.com','Nam','2004-11-01',1,2);
/*!40000 ALTER TABLE `nhan_vien` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `phong_ban`
--

DROP TABLE IF EXISTS `phong_ban`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `phong_ban` (
  `IDPhongBan` int NOT NULL,
  `TenPhongBan` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`IDPhongBan`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `phong_ban`
--

LOCK TABLES `phong_ban` WRITE;
/*!40000 ALTER TABLE `phong_ban` DISABLE KEYS */;
INSERT INTO `phong_ban` VALUES (1,'Phòng Kinh doanhhh'),(2,'Phòng Kỹ thuật'),(3,'Phòng Nhân sự'),(4,'Phong Y Te'),(5,'Phòng Marketing'),(6,'Phòng Ngủ'),(7,'4324');
/*!40000 ALTER TABLE `phong_ban` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tai_khoan`
--

DROP TABLE IF EXISTS `tai_khoan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tai_khoan` (
  `TenDangNhap` varchar(255) NOT NULL,
  `MatKhau` varchar(255) NOT NULL,
  `VaiTro` varchar(50) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `IDNhanVien` int DEFAULT NULL,
  PRIMARY KEY (`TenDangNhap`),
  KEY `fk_tai_khoan_nhan_vien_idx` (`IDNhanVien`),
  CONSTRAINT `fk_tai_khoan_nhan_vien_cascade` FOREIGN KEY (`IDNhanVien`) REFERENCES `nhan_vien` (`IDNhanVien`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tai_khoan`
--

LOCK TABLES `tai_khoan` WRITE;
/*!40000 ALTER TABLE `tai_khoan` DISABLE KEYS */;
INSERT INTO `tai_khoan` VALUES ('abc','abc','user','leducphuclong@gmail.com',NULL),('abcgido','$2a$10$dAGUyFCoJjLWNxxOSBx0xO7CLDPwHtk5ZEJBZqoCrIj7wRPT3Lbk2','Quản lý',NULL,2),('ketoan','$2a$10$ZQRKWfpCJfdU2ZflOm.XH.n6I9pm1uzYaEv.N5HOe74MkguVEAA..','KeToan','leducphuclong@gmail.com',NULL),('ketoan321','$2a$10$X.uzZX6WunbGWG/Kt0LE8uBxT2i3J8j3ZhOOb9seDKeCyTDomD6Nq','Kế toán',NULL,12),('leducphuclong','$2a$10$e3GbMnOY2wOW5Lt/v7GCuu8NR7GLNsW/exqGOzJp7yUFd81PSD3lS','Quản lý',NULL,3),('nhansu','$2a$10$5SQ9r1ibLfkjlJFt89UiZupWlqTXEYaOyWUrbGsgrP6rkwxzvrnYy','Kế toán','leducphuclong@gmail.com',3),('nhansu123','$2a$10$BsWZqhtbAO07oOWT76WXUO3GJ2a.sIqkBfQDbSZPKmJ3W6HVnRH.S','QuanLyNhanSu','2342@gsa',NULL),('nhansu321','$2a$10$kYb9oGEoNx4P1bHKZBADOuydkJhm8nB4UNJNFM61ZjRLjyXdFuxtG','Quản lý Nhân sự',NULL,3),('staff1','staffpassword123','staff',NULL,NULL),('testtest','$2a$10$xxLdK1xKUeCiAjVWUR8/iOiAleyEO3tVbzQbXGMOEHC0rRyBsAiYO','Quản lý Nhân sự',NULL,3);
/*!40000 ALTER TABLE `tai_khoan` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-10 22:45:39
