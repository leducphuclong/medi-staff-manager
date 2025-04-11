CREATE DATABASE  IF NOT EXISTS `pbl3` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `pbl3`;
-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: pbl3
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
-- Table structure for table `chuc_vu`
--

DROP TABLE IF EXISTS `chuc_vu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chuc_vu` (
  `IDChucVu` int NOT NULL AUTO_INCREMENT,
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
INSERT INTO `chuc_vu` VALUES (1,'Nhân viên',1.00),(2,'Quản lý',1.50),(3,'Giám đốc',2.00);
/*!40000 ALTER TABLE `chuc_vu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lich_lam_viec`
--

DROP TABLE IF EXISTS `lich_lam_viec`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lich_lam_viec` (
  `IDLichLamViec` int NOT NULL,
  `IDNhanVien` int DEFAULT NULL,
  `NgayLamViec` date DEFAULT NULL,
  `CaLamViec` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`IDLichLamViec`),
  KEY `IDNhanVien` (`IDNhanVien`),
  CONSTRAINT `lich_lam_viec_ibfk_1` FOREIGN KEY (`IDNhanVien`) REFERENCES `nhan_vien` (`IDNhanVien`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lich_lam_viec`
--

LOCK TABLES `lich_lam_viec` WRITE;
/*!40000 ALTER TABLE `lich_lam_viec` DISABLE KEYS */;
/*!40000 ALTER TABLE `lich_lam_viec` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `luong_nhan_vien`
--

DROP TABLE IF EXISTS `luong_nhan_vien`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `luong_nhan_vien` (
  `IDChucVu` int DEFAULT NULL,
  `IDNhanVien` int NOT NULL,
  `ThangNam` varchar(20) NOT NULL,
  `LuongThuNhap` decimal(10,2) DEFAULT NULL,
  `HeSoLuong` decimal(5,2) DEFAULT NULL,
  `Thuong` decimal(10,2) DEFAULT NULL,
  `PhuCap` decimal(10,2) DEFAULT NULL,
  `TangCa` decimal(10,2) DEFAULT NULL,
  `TongLuong` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`IDNhanVien`,`ThangNam`),
  KEY `IDChucVu` (`IDChucVu`),
  CONSTRAINT `luong_nhan_vien_ibfk_1` FOREIGN KEY (`IDChucVu`) REFERENCES `chuc_vu` (`IDChucVu`),
  CONSTRAINT `luong_nhan_vien_ibfk_2` FOREIGN KEY (`IDNhanVien`) REFERENCES `nhan_vien` (`IDNhanVien`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `luong_nhan_vien`
--

LOCK TABLES `luong_nhan_vien` WRITE;
/*!40000 ALTER TABLE `luong_nhan_vien` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nhan_vien`
--

LOCK TABLES `nhan_vien` WRITE;
/*!40000 ALTER TABLE `nhan_vien` DISABLE KEYS */;
INSERT INTO `nhan_vien` VALUES (1,'123456789012','Nguyễn Văn A','0987654321','a.nguyen@example.com','Nam','1990-01-01',1,1),(2,'123456789013','Trần Thị B','0987654322','b.tran@example.com','Nữ','1992-02-02',2,2),(3,'123456789014','Lê Văn C','0987654323','c.le@example.com','Nam','1985-03-03',1,3),(4,'123456789015','Phạm Thị D','0987654324','d.pham@example.com','Nữ','1991-04-04',3,1),(5,'123456789016','Bùi Văn E','0987654325','e.bui@example.com','Nam','1987-05-05',2,2),(6,'123456789017','Nguyễn Thị F','0987654326','f.nguyen@example.com','Nữ','1993-06-06',1,3),(7,'123456789018','Trần Văn G','0987654327','g.tran@example.com','Nam','1989-07-07',2,1),(8,'123456789019','Lê Thị H','0987654328','h.le@example.com','Nữ','1994-08-08',3,2),(9,'123456789020','Phạm Văn I','0987654329','i.pham@example.com','Nam','1990-09-09',1,1),(10,'123456789021','Bùi Thị J','0987654330','j.bui@example.com','Nữ','1995-10-10',2,3);
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
INSERT INTO `phong_ban` VALUES (1,'Phòng Kinh doanh'),(2,'Phòng Kỹ thuật'),(3,'Phòng Nhân sự');
/*!40000 ALTER TABLE `phong_ban` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tai_khoan`
--

DROP TABLE IF EXISTS `tai_khoan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tai_khoan` (
  `IDNhanVien` int NOT NULL,
  `TenDangNhap` varchar(255) NOT NULL,
  `MatKhau` varchar(255) NOT NULL,
  `VaiTro` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`IDNhanVien`),
  CONSTRAINT `tai_khoan_ibfk_1` FOREIGN KEY (`IDNhanVien`) REFERENCES `nhan_vien` (`IDNhanVien`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tai_khoan`
--

LOCK TABLES `tai_khoan` WRITE;
/*!40000 ALTER TABLE `tai_khoan` DISABLE KEYS */;
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

-- Dump completed on 2025-04-02 11:25:13
