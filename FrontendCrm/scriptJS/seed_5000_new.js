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

// Sinh ra 5.000 dòng dữ liệu KHÔNG TRÙNG LẶP, áp dụng "Mẹo thời gian" của bạn
for (let i = 1; i <= 5000; i++) {
    // 4 số cuối là số thứ tự (0001 -> 5000)
    const indexStr = i.toString().padStart(4, '0'); 
    
    // SĐT: 09 + Giờ + Phút + Số thứ tự (VD: 0915320001) - Đảm bảo đủ 10 số và không bao giờ trùng!
    const phone = '09' + h + m + indexStr;
    
    // Email: Có kẹp Giờ Phút
    const email = `khachhang_${h}h${m}p_${i}@gmail.com`; 

    // Tên: Có kẹp Giờ Phút
    const name = `Khách Hàng Mới 5K (${h}h${m}p) - ${i}`;

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

const filename = `DuLieuTest_5k_Sieusach_${h}h${m}p_${d}${mo}${y}.xlsx`;

XLSX.writeFile(wb, filename);
console.log(`✅ Đã tạo xong file ${filename} với 5.000 dòng theo tuyệt chiêu của bạn!`);
