const ExcelJS = require('exceljs');

async function createExcel() {
    const workbook = new ExcelJS.Workbook();
    const sheet = workbook.addWorksheet('Customers');

    // Columns
    sheet.columns = [
        { header: 'Tên KH', key: 'name', width: 20 },
        { header: 'Email (Nhiều email)', key: 'email', width: 50 },
        { header: 'Số Điện Thoại', key: 'phoneNumber', width: 20 },
        { header: 'Ngày Sinh', key: 'dateOfBirth', width: 20 },
        { header: 'Địa Chỉ', key: 'location', width: 30 },
        { header: 'Giới Tính', key: 'gender', width: 10 }
    ];

    const now = new Date();
    const timeStr = `${now.getHours()}h${now.getMinutes()}p`;

    console.log("Đang tạo 5000 dòng dữ liệu Khách hàng...");
    
    for (let i = 1; i <= 5000; i++) {
        const email1 = `khachhang_${timeStr}_${i}_1@gmail.com`;
        const email2 = `khachhang_${timeStr}_${i}_2@gmail.com`;
        // Email ghép nhau bởi dấu ;
        const multiEmail = `${email1}; ${email2}`;
        
        sheet.addRow({
            name: `Khách Hàng Đa Email ${i}`,
            email: multiEmail,
            phoneNumber: `08${String(i).padStart(8, '0')}`,
            dateOfBirth: '2000-01-01',
            location: 'Hà Nội',
            gender: i % 2 === 0 ? '1' : '0'
        });
    }

    const fileName = '../Excel/Data_5k_MultiEmail.xlsx';
    await workbook.xlsx.writeFile(fileName);
    console.log(`Đã tạo thành công file Excel 5000 dòng tại: ${fileName}`);
}

createExcel().catch(err => console.error(err));
