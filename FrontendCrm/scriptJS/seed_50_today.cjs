const ExcelJS = require('exceljs');
const path = require('path');

async function createExcel() {
    const workbook = new ExcelJS.Workbook();
    const sheet = workbook.addWorksheet('Customers');

    sheet.columns = [
        { header: 'Tên KH', key: 'name', width: 35 },
        { header: 'Email (Nhiều email)', key: 'email', width: 60 },
        { header: 'Số Điện Thoại', key: 'phoneNumber', width: 20 },
        { header: 'Ngày Sinh', key: 'dateOfBirth', width: 20 },
        { header: 'Địa Chỉ', key: 'location', width: 20 },
        { header: 'Giới Tính', key: 'gender', width: 10 }
    ];

    const now = new Date();
    const h = String(now.getHours()).padStart(2, '0');
    const m = String(now.getMinutes()).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const year = now.getFullYear();
    
    const timeLabel = `${h}h${m}p`;
    const fileNameTime = `${timeLabel}_${day}${month}${year}`;
    const fileName = path.join(__dirname, `../Excel/DuLieuTest_50_${fileNameTime}.xlsx`);

    const nameTime = `${h}h${m}p${day}${month}${year}`;
    const prefixTime = `${h}${m}_${day}${month}${year}`;

    console.log(`Đang tạo 50 dòng dữ liệu test chống trùng lặp...`);
    
    for (let i = 1; i <= 50; i++) {
        // Email: khachhang_giờphút_ngàythángnăm_thứ tự cột_thứ tự email phụ
        const email1 = `khachhang_${prefixTime}_${i}_1@gmail.com`;
        const email2 = `khachhang_${prefixTime}_${i}_2@gmail.com`;
        const multiEmail = `${email1}; ${email2}`;
        
        // SĐT: 09 + giờ phút + 0000 + i (padding 4) => 10 số tròn trĩnh
        const phone = `09${h}${m}${String(i).padStart(4, '0')}`;

        sheet.addRow({
            name: `Khách Hàng ${nameTime} - ${i}`,
            email: multiEmail,
            phoneNumber: phone,
            dateOfBirth: '2000-01-01',
            location: 'Hà Nội',
            gender: i % 2 !== 0 ? '0' : '1'
        });
    }

    await workbook.xlsx.writeFile(fileName);
    console.log(`Đã tạo thành công file Excel 50 dòng tại: ../Excel/DuLieuTest_50_${fileNameTime}.xlsx`);
}

createExcel().catch(err => console.error(err));
