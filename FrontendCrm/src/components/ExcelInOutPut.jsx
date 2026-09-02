import React, { useState } from 'react';
import axios from 'axios';
import { importCustomer } from '../api/customerApi';

export default function ExcelInOutPut() {
  const [file, setFile] = useState(null);

  // Hàm tải file từ Server về máy Client
  const handleExport = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/poi/export', {
        responseType: 'blob', // Ép Axios giữ nguyên dạng file nhị phân
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

  // Hàm gửi file từ máy Client lên Server
  const handleImport = async (e) => {
    e.preventDefault();
    if (!file) return alert("Vui lòng chọn file trước!");

    const formData = new FormData();
    formData.append('file', file); // Khớp với @RequestParam("file") ở Java

    try {
      const response = await importCustomer(formData);

      alert(response.data);
    } catch (error) {
      console.error("Lỗi import file:", error);
    }
  };

  return (
    <div style={{ padding: '20px' }}>
      <h2>Quản lý Excel bằng Apache POI</h2>
      <button onClick={handleExport} style={{ marginRight: '10px' }}>
        Tải file Excel (Export)
      </button>

      <form onSubmit={handleImport} style={{ marginTop: '20px' }}>
        <input type="file" accept=".xlsx" onChange={(e) => setFile(e.target.files[0])} />
        <button type="submit">Upload File (Import)</button>
      </form>
    </div>
  );
}
