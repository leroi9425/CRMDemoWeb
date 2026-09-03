import * as XLSX from 'xlsx';
import fs from 'fs';

const data = [];
data.push(['name', 'phoneNumber', 'email', 'UserId', 'location', 'dateOfBirth', 'companyId', 'gender']);

// Sinh ra 10.000 dòng dữ liệu ngẫu nhiên
for (let i = 1; i <= 10000; i++) {
    const phone = '09' + Math.floor(Math.random() * 100000000).toString().padStart(8, '0');
    
    // Cố tình làm trùng 50 dòng cuối bằng cách ép số điện thoại giống 50 dòng đầu
    let finalPhone = phone;
    let finalEmail = 'khachhang' + i + '@gmail.com';
    
    if (i > 9950) {
        finalPhone = '09000000' + (i - 9950); // SĐT trùng
    } else if (i <= 50) {
        finalPhone = '09000000' + i; // SĐT gốc bị trùng
    }

    data.push([
        'Khách Hàng ' + i,
        finalPhone,
        finalEmail,
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

XLSX.writeFile(wb, 'DuLieuTest_10000.xlsx');
console.log('✅ Đã tạo xong file DuLieuTest_10000.xlsx với 10.000 dòng!');
