# QUY TẮC TẠO DỮ LIỆU EXCEL TEST (WEB CRM)

File này lưu trữ quy chuẩn tạo dữ liệu giả (Seed Data) bằng Excel để chống trùng lặp dữ liệu với các lần import trước đó.

## 1. Quy tắc đặt tên file
- **Format**: `DuLieuTest_[Số_Lượng]_[Giờ]h[Phút]p_[Ngày][Tháng][Năm].xlsx`
- **Ví dụ**: `DuLieuTest_50_10h06p_08092026.xlsx`

## 2. Quy tắc các Cột (Header)
Thứ tự và tên các cột phải tuân thủ chính xác 100%:
1. `Tên KH` (Độ rộng 35)
2. `Email (Nhiều email)` (Độ rộng 60)
3. `Số Điện Thoại` (Độ rộng 20)
4. `Ngày Sinh` (Độ rộng 20)
5. `Địa Chỉ` (Độ rộng 20)
6. `Giới Tính` (Độ rộng 10)

## 3. Quy tắc Dữ liệu từng dòng (Chống Trùng Lặp)
Sử dụng timestamp để đảm bảo 100% dữ liệu (SĐT, Email, Tên) không bị trùng lặp với các lần Import trước đó của hệ thống.

- **Tên KH**: `Khách Hàng [Giờ]h[Phút]p[Ngày][Tháng][Năm] - [STT]`
  - *Ví dụ*: `Khách Hàng 10h06p08092026 - 1`
- **Email**: `khachhang_[Giờ][Phút]_[Ngày][Tháng][Năm]_[STT]_[Thứ tự phụ]@gmail.com`
  - *Ví dụ*: `khachhang_1006_08092026_1_1@gmail.com; khachhang_1006_08092026_1_2@gmail.com`
- **Số Điện Thoại**: `09` + `[Giờ][Phút]` + `[STT (padding 4 số)]` (Tổng 10 số)
  - *Ví dụ*: `0910060001` (Lúc 10h06, dòng số 1)
- **Ngày Sinh**: `2000-01-01`
- **Địa Chỉ**: `Hà Nội`
- **Giới Tính**: Chứa ký tự `0` hoặc `1` (0 = Nữ, 1 = Nam)

## 4. Công cụ sử dụng
- Script tạo file nằm tại: `FrontendCrm/scriptJS/seed_50_today.cjs`
- Thư mục lưu file Excel: `FrontendCrm/Excel/`
- Thư viện Node.js: `exceljs`
