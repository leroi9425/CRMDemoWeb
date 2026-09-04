import React, { useState } from 'react';
import axios from 'axios';
import * as XLSX from 'xlsx';
import { importCustomer } from '../api/customerApi';

const DB_FIELDS = [
  { key: 'name', label: 'Họ và Tên', required: true },
  { key: 'email', label: 'Email', required: true },
  { key: 'phoneNumber', label: 'Số điện thoại', required: false },
  { key: 'dateOfBirth', label: 'Ngày sinh', required: false },
  { key: 'location', label: 'Địa chỉ', required: false },
  { key: 'gender', label: 'Giới tính (TRUE/FALSE)', required: false },
  { key: 'companyId', label: 'ID Công ty', required: false },
  { key: 'userId', label: 'ID Người dùng', required: false },
];

export default function ExcelInOutPut({ onImportSuccess }) {
  const [file, setFile] = useState(null);
  const [excelHeaders, setExcelHeaders] = useState([]);
  const [mapping, setMapping] = useState({}); // format: { excelIndex: dbKey }
  const [isLoading, setIsLoading] = useState(false); // <--- Trạng thái Loading

  const handleExport = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/poi/export', {
        responseType: 'blob',
      });
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'danh_sach_nhan_vien.xlsx');
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      console.error("Lỗi xuất file:", error);
    }
  };

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    setFile(selectedFile);
    setExcelHeaders([]);
    setMapping({});
    
    if (!selectedFile) return;

    // Đọc lén file bằng thư viện XLSX để lấy tiêu đề
    const reader = new FileReader();
    reader.onload = (evt) => {
      const bstr = evt.target.result;
      const wb = XLSX.read(bstr, { type: 'binary' });
      const wsname = wb.SheetNames[0];
      const ws = wb.Sheets[wsname];
      
      const data = XLSX.utils.sheet_to_json(ws, { header: 1 });
      if (data.length > 0) {
        setExcelHeaders(data[0]); // Lấy mảng dữ liệu dòng đầu tiên (Header)
      }
    };
    reader.readAsBinaryString(selectedFile);
  };

  const handleMapChange = (excelIndex, dbKey) => {
    setMapping(prev => ({
      ...prev,
      [excelIndex]: dbKey
    }));
  };

  const handleImport = async (e) => {
    e.preventDefault();
    if (!file) return alert("Vui lòng chọn file trước!");
    if (isLoading) return; // Tránh double click khi đang tải

    // 1. Validation kiểm tra xem khách đã nối đủ các cột bắt buộc chưa
    const mappedDbKeys = Object.values(mapping);
    const missingFields = DB_FIELDS.filter(f => f.required && !mappedDbKeys.includes(f.key));
    
    if (missingFields.length > 0) {
      const missingNames = missingFields.map(f => f.label).join(', ');
      return alert(`Lỗi: Bạn chưa nối cột cho các trường bắt buộc: ${missingNames}`);
    }

    // 2. Chuyển đổi mapping thành dạng Backend cần: { "name": 0, "email": 2 }
    const finalMapping = {};
    for (const [excelIndex, dbKey] of Object.entries(mapping)) {
      if (dbKey) {
        finalMapping[dbKey] = parseInt(excelIndex);
      }
    }

    const formData = new FormData();
    formData.append('file', file);
    formData.append('mapping', JSON.stringify(finalMapping));

    setIsLoading(true); // BẬT LOADING
    try {
      const response = await importCustomer(formData);
      alert(response.data || "Import thành công!");
      if (onImportSuccess) {
          onImportSuccess(); // Quay về màn hình trước
      }
    } catch (error) {
      console.error("Lỗi import file:", error);
      alert("Lỗi, số điện thoại hoặc email đã tồn tại !");
    } finally {
      setIsLoading(false); // TẮT LOADING SAU KHI XONG HOẶC LỖI
    }
  };

  return (
    <div style={{ padding: '20px' }}>
      <h2>Quản lý Excel bằng Apache POI</h2>
      <button onClick={handleExport} style={{ marginRight: '10px' }}>
        Tải file Excel (Export)
      </button>

      <form onSubmit={handleImport} style={{ marginTop: '20px' }}>
        <input type="file" accept=".xlsx" onChange={handleFileChange} />
        
        {excelHeaders.length > 0 && (
          <div style={{ marginTop: '20px', marginBottom: '20px' }}>
            <h3>Bảng ánh xạ (Nối cột dữ liệu)</h3>
            <table border="1" cellPadding="10" style={{ borderCollapse: 'collapse', width: '100%' }}>
              <thead>
                <tr>
                  <th style={{ background: '#f4f4f4' }}>Cột trong Excel của bạn</th>
                  <th style={{ background: '#f4f4f4' }}>Dữ liệu hệ thống (Chọn)</th>
                </tr>
              </thead>
              <tbody>
                {excelHeaders.map((headerName, index) => (
                  <tr key={index}>
                    <td><strong>{headerName || `(Cột ${index})`}</strong></td>
                    <td>
                      <select 
                        value={mapping[index] || ''} 
                        onChange={(e) => handleMapChange(index, e.target.value)}
                        style={{ width: '100%', padding: '5px' }}
                      >
                        <option value="">-- Bỏ qua --</option>
                        {DB_FIELDS.map(field => (
                          <option key={field.key} value={field.key}>
                            {field.label} {field.required ? '*' : ''}
                          </option>
                        ))}
                      </select>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        <button type="submit" disabled={isLoading} style={{ marginTop: '10px', padding: '10px 20px', cursor: isLoading ? 'not-allowed' : 'pointer' }}>
          {isLoading ? '⏳ Đang xử lý...' : 'Upload File (Import)'}
        </button>
      </form>
    </div>
  );
}
