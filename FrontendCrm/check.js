import * as XLSX from 'xlsx';
const wb = XLSX.readFile('DuLieuTest_10000.xlsx');
const data = XLSX.utils.sheet_to_json(wb.Sheets['Sheet1']);
const phones = {};
let dupCount = 0;
data.forEach((row, i) => {
    if (phones[row.phoneNumber]) {
        console.log('Phát hiện trùng SĐT: ' + row.phoneNumber + ' (giữa dòng ' + (i+2) + ' và dòng ' + phones[row.phoneNumber] + ')');
        dupCount++;
    } else {
        phones[row.phoneNumber] = i+2;
    }
});
console.log('Tổng cộng có ' + dupCount + ' dòng SĐT bị trùng lặp trong file DuLieuTest_10000.xlsx');
