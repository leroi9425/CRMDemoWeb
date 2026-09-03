import * as XLSX from 'xlsx';

const data = [];
data.push(['name', 'phoneNumber', 'email', 'UserId', 'location', 'dateOfBirth', 'companyId', 'gender']);

// Sinh ra 5.000 dòng dữ liệu KHÔNG TRÙNG LẶP
for (let i = 1; i <= 5000; i++) {
    // Đổi đầu số thành 095... để chắc chắn không bị trùng với các file 1k hay 10k lúc nãy bạn đã import vào DB
    const phone = '095' + i.toString().padStart(7, '0');
    const email = 'khachhang_5k_' + i + '@gmail.com'; 

    data.push([
        'Khách Hàng 5K - ' + i,
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

XLSX.writeFile(wb, 'DuLieuTest_5000.xlsx');
console.log('✅ Đã tạo xong file DuLieuTest_5000.xlsx với 5.000 dòng!');
