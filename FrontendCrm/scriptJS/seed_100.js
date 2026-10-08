import * as XLSX from 'xlsx';

const data = [];
data.push(['name', 'phoneNumber', 'email', 'UserId', 'location', 'dateOfBirth', 'companyId', 'gender']);

// Lấy thời gian hiện tại
const date = new Date();
const h = String(date.getHours()).padStart(2, '0');
const m = String(date.getMinutes()).padStart(2, '0');
const d = String(date.getDate()).padStart(2, '0');
const mo = String(date.getMonth() + 1).padStart(2, '0');
const y = date.getFullYear();

// Sinh ra 100 dòng dữ liệu KHÔNG TRÙNG LẶP
for (let i = 1; i <= 100; i++) {
    const indexStr = i.toString().padStart(4, '0'); 
    const phone = '09' + h + m + indexStr;
    const email = `khachhang_test100_${h}h${m}p_${i}@gmail.com`; 
    const name = `Khách Test 100 (${h}h${m}p) - ${i}`;

    data.push([
        name,
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

// Ghi file vào đúng thư mục Excel như bạn đã dặn
const filename = `./Excel/DuLieuTest_100_${h}h${m}p_${d}${mo}${y}.xlsx`;

XLSX.writeFile(wb, filename);
console.log(`✅ Đã tạo xong file ${filename} với 100 dòng!`);
