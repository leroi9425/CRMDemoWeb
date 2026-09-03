import * as XLSX from 'xlsx';

const data = [];
data.push(['name', 'phoneNumber', 'email', 'UserId', 'location', 'dateOfBirth', 'companyId', 'gender']);

// Sinh ra 10.000 dòng dữ liệu KHÔNG TRÙNG LẶP
for (let i = 1; i <= 10000; i++) {
    // SĐT tăng dần đảm bảo 100% không bao giờ trùng: 0900000001, 0900000002...
    const phone = '09' + i.toString().padStart(8, '0');
    const email = 'khachhang_sach_' + i + '@gmail.com'; // Email cũng độc nhất

    data.push([
        'Khách Hàng ' + i,
        phone,
        email,
        1,
        'Hà Nội',
        '01/01/2000',
        1,
        true
    ]);
}

const ws = XLSX.utils.aoa_to_sheet(data);
const wb = XLSX.utils.book_new();
XLSX.utils.book_append_sheet(wb, ws, 'Sheet1');

XLSX.writeFile(wb, 'DuLieuTest_10000_Sach.xlsx');
console.log('✅ Đã tạo xong file DuLieuTest_10000_Sach.xlsx với 10.000 dòng SẠCH (Không trùng)!');
