-- =====================================================
-- CSDL QUẢN LÝ SINH VIÊN (SQL Server)
-- Chạy toàn bộ file này trong SQL Server Management Studio.
-- File có thể chạy lại nhiều lần (tự xóa CSDL cũ).
-- =====================================================
USE master;
GO
IF DB_ID('QuanLySinhVien') IS NOT NULL
BEGIN
    ALTER DATABASE QuanLySinhVien SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE QuanLySinhVien;
END
GO
CREATE DATABASE QuanLySinhVien;
GO
USE QuanLySinhVien;
GO

CREATE TABLE KHOA (
    MaKhoa      VARCHAR(10)   NOT NULL,
    TenKhoa     NVARCHAR(100) NOT NULL,
    SoDienThoai VARCHAR(15)   NULL,
    CONSTRAINT PK_KHOA PRIMARY KEY (MaKhoa),
    CONSTRAINT UQ_KHOA_TenKhoa UNIQUE (TenKhoa)
);

CREATE TABLE LOP (
    MaLop   VARCHAR(10)   NOT NULL,
    TenLop  NVARCHAR(100) NOT NULL,
    KhoaHoc VARCHAR(10)   NULL,
    MaKhoa  VARCHAR(10)   NOT NULL,
    CONSTRAINT PK_LOP PRIMARY KEY (MaLop),
    CONSTRAINT FK_LOP_KHOA FOREIGN KEY (MaKhoa) REFERENCES KHOA(MaKhoa)
);

CREATE TABLE SINHVIEN (
    MaSV        VARCHAR(10)   NOT NULL,
    HoTen       NVARCHAR(100) NOT NULL,
    GioiTinh    NVARCHAR(5)   NOT NULL,
    NgaySinh    DATE          NOT NULL,
    DiaChi      NVARCHAR(200) NULL,
    Email       VARCHAR(100)  NULL,
    SoDienThoai VARCHAR(15)   NULL,
    MaLop       VARCHAR(10)   NOT NULL,
    CONSTRAINT PK_SINHVIEN PRIMARY KEY (MaSV),
    CONSTRAINT CK_SINHVIEN_GioiTinh CHECK (GioiTinh IN (N'Nam', N'Nữ')),
    CONSTRAINT UQ_SINHVIEN_Email UNIQUE (Email),
    CONSTRAINT FK_SINHVIEN_LOP FOREIGN KEY (MaLop) REFERENCES LOP(MaLop)
);

CREATE TABLE MONHOC (
    MaMH     VARCHAR(10)   NOT NULL,
    TenMH    NVARCHAR(100) NOT NULL,
    SoTinChi INT           NOT NULL,
    MaKhoa   VARCHAR(10)   NOT NULL,
    CONSTRAINT PK_MONHOC PRIMARY KEY (MaMH),
    CONSTRAINT CK_MONHOC_SoTinChi CHECK (SoTinChi BETWEEN 1 AND 10),
    CONSTRAINT FK_MONHOC_KHOA FOREIGN KEY (MaKhoa) REFERENCES KHOA(MaKhoa)
);

CREATE TABLE DIEM (
    MaSV         VARCHAR(10)  NOT NULL,
    MaMH         VARCHAR(10)  NOT NULL,
    HocKy        TINYINT      NOT NULL,
    NamHoc       VARCHAR(9)   NOT NULL,
    DiemQuaTrinh DECIMAL(4,2) NULL,
    DiemThi      DECIMAL(4,2) NULL,
    DiemTongKet  AS (CAST(DiemQuaTrinh * 0.3 + DiemThi * 0.7 AS DECIMAL(4,2))) PERSISTED,
    CONSTRAINT PK_DIEM PRIMARY KEY (MaSV, MaMH, HocKy, NamHoc),
    CONSTRAINT CK_DIEM_QuaTrinh CHECK (DiemQuaTrinh BETWEEN 0 AND 10),
    CONSTRAINT CK_DIEM_Thi CHECK (DiemThi BETWEEN 0 AND 10),
    CONSTRAINT FK_DIEM_SINHVIEN FOREIGN KEY (MaSV) REFERENCES SINHVIEN(MaSV) ON DELETE CASCADE,
    CONSTRAINT FK_DIEM_MONHOC FOREIGN KEY (MaMH) REFERENCES MONHOC(MaMH)
);
GO

-- ---------------- DỮ LIỆU MẪU ----------------
INSERT INTO KHOA VALUES ('CNTT', N'Công nghệ thông tin', '0241234567');
INSERT INTO KHOA VALUES ('KT',   N'Kinh tế',             '0241234568');

INSERT INTO LOP VALUES ('CNTT01', N'Công nghệ thông tin 01', 'K15', 'CNTT');
INSERT INTO LOP VALUES ('CNTT02', N'Công nghệ thông tin 02', 'K15', 'CNTT');
INSERT INTO LOP VALUES ('KT01',   N'Kinh tế 01',             'K15', 'KT');

INSERT INTO SINHVIEN VALUES ('SV001', N'Nguyễn Văn An', N'Nam', '2005-03-12', N'Hà Nội',    'an@example.com',    '0901000001', 'CNTT01');
INSERT INTO SINHVIEN VALUES ('SV002', N'Trần Thị Bình', N'Nữ',  '2005-07-25', N'Hải Phòng', 'binh@example.com',  '0901000002', 'CNTT01');
INSERT INTO SINHVIEN VALUES ('SV003', N'Lê Minh Cường', N'Nam', '2005-11-02', N'Nam Định',  'cuong@example.com', '0901000003', 'CNTT02');
INSERT INTO SINHVIEN VALUES ('SV004', N'Phạm Thu Hà',   N'Nữ',  '2005-01-18', N'Bắc Ninh',  'ha@example.com',    '0901000004', 'KT01');

INSERT INTO MONHOC VALUES ('LTJV', N'Lập trình Java', 3, 'CNTT');
INSERT INTO MONHOC VALUES ('CSDL', N'Cơ sở dữ liệu',  3, 'CNTT');
INSERT INTO MONHOC VALUES ('KTVM', N'Kinh tế vi mô',  2, 'KT');

INSERT INTO DIEM (MaSV, MaMH, HocKy, NamHoc, DiemQuaTrinh, DiemThi) VALUES ('SV001', 'LTJV', 1, '2025-2026', 8.0, 7.5);
INSERT INTO DIEM (MaSV, MaMH, HocKy, NamHoc, DiemQuaTrinh, DiemThi) VALUES ('SV001', 'CSDL', 1, '2025-2026', 9.0, 8.5);
INSERT INTO DIEM (MaSV, MaMH, HocKy, NamHoc, DiemQuaTrinh, DiemThi) VALUES ('SV002', 'LTJV', 1, '2025-2026', 7.0, 6.5);
INSERT INTO DIEM (MaSV, MaMH, HocKy, NamHoc, DiemQuaTrinh, DiemThi) VALUES ('SV003', 'CSDL', 1, '2025-2026', 6.5, 7.0);
INSERT INTO DIEM (MaSV, MaMH, HocKy, NamHoc, DiemQuaTrinh, DiemThi) VALUES ('SV004', 'KTVM', 1, '2025-2026', 8.5, 9.0);
GO
