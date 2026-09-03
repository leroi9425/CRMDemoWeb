import * as XLSX from 'xlsx';

const data = [];
data.push(['name', 'phoneNumber', 'email', 'UserId', 'location', 'dateOfBirth', 'companyId', 'gender']);

for (let i = 1; i <= 1000; i++) {
    const phone = '09' + i.toString().padStart(8, '0');
    const email = 'khachhang_1k_' + i + '@gmail.com'; 

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

XLSX.writeFile(wb, 'DuLieuTest_1000.xlsx');
console.log('✅ Đã tạo xong file DuLieuTest_1000.xlsx với 1.000 dòng!');
