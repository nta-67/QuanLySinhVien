# Phần mềm quản lý sinh viên (Java Swing + SQL Server)

Đồ án Java: quản lý lớp, sinh viên, môn học và điểm. Giao diện Swing, kết nối SQL Server bằng JDBC.

## Chức năng
- 4 form: Quản lý lớp, Quản lý sinh viên, Quản lý môn học, Quản lý điểm (có form chính MainFrame).
- Mỗi form có: thêm, sửa, xóa, tìm kiếm, làm mới; kiểm tra dữ liệu nhập và báo lỗi bằng tiếng Việt.
- Điểm tổng kết tự tính trong CSDL (30% điểm quá trình + 70% điểm thi).

## Công nghệ
- Java 17, Maven, thư viện `mssql-jdbc`
- SQL Server + SQL Server Management Studio (SSMS)
- Mở bằng Apache NetBeans (File > Open Project)

## Cách chạy
1. Mở SSMS, chạy file `database/QuanLySinhVien.sql` để tạo CSDL và dữ liệu mẫu.
2. Mở file `src/main/java/qlsv/db/DBConnection.java`, sửa `USER` và `PASS` cho đúng với SQL Server của bạn.
3. Mở project trong NetBeans (File > Open Project, chọn thư mục này). NetBeans tự tải thư viện JDBC.
4. Chuột phải project > Run (hoặc chạy file `App.java`).

## Cấu trúc thư mục
```
database/QuanLySinhVien.sql      script tạo CSDL + dữ liệu mẫu
src/main/java/qlsv/App.java      điểm khởi chạy
src/main/java/qlsv/db/           kết nối CSDL
src/main/java/qlsv/model/        các lớp đối tượng (Lop, SinhVien, MonHoc, Diem)
src/main/java/qlsv/dao/          truy vấn SQL (thêm, sửa, xóa, tìm kiếm)
src/main/java/qlsv/ui/           các form giao diện
```
