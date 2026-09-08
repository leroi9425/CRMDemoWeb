const ExcelJS = require('exceljs');
const path = require('path');

async function createExcel() {
    const workbook = new ExcelJS.Workbook();
    const sheet = workbook.addWorksheet('Customers');

    sheet.columns = [
        { header: 'Tên KH', key: 'name', width: 25 },
        { header: 'Email (Nhiều email)', key: 'email', width: 60 },
        { header: 'Số Điện Thoại', key: 'phoneNumber', width: 20 },
        { header: 'Ngày Sinh', key: 'dateOfBirth', width: 20 },
        { header: 'Địa Chỉ', key: 'location', width: 20 },
        { header: 'Giới Tính', key: 'gender', width: 10 }
    ];

    const now = new Date();
    const h = now.getHours();
    const m = now.getMinutes();
    const day = String(now.getDate()).padStart(2, '0');
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const year = now.getFullYear();
    
    // Tên file: chữ gì cũng đc_số lượng_giờ+phút_ngày+tháng+năm
    const timeLabel = `${h}h${m}p`;
    const fileNameTime = `${h}h${m}p_${day}${month}${year}`;
    const fileName = path.join(__dirname, `../Excel/DuLieuTest_5000_${fileNameTime}.xlsx`);

    console.log("Đang tạo 5000 dòng dữ liệu chuẩn bài...");
    
    for (let i = 1; i <= 5000; i++) {
        const email1 = `khachhang_${timeLabel}_${i}_1@gmail.com`;
        const email2 = `khachhang_${timeLabel}_${i}_2@gmail.com`;
        const multiEmail = `${email1}; ${email2}`;
        
        sheet.addRow({
            name: `Khách Hàng Đa Email ${i}`,
            email: multiEmail,
            phoneNumber: `08${String(i).padStart(8, '0')}`,
            dateOfBirth: '2000-01-01',
            location: 'Hà Nội',
            gender: i % 2 !== 0 ? '0' : '1'
        });
    }

    await workbook.xlsx.writeFile(fileName);
    console.log(`Đã tạo thành công file Excel 5000 dòng tại: ../Excel/DuLieuTest_5000_${fileNameTime}.xlsx`);
}

createExcel().catch(err => console.error(err));
