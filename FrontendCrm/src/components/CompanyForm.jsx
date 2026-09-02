import { useEffect, useRef, useState } from "react";
import { createCompany, updateCompany } from "../api/CompanyApi";

export default function CompanyForm({ editingCompany, onSaveSuccess, onCancel }) {
  // BƯỚC 1: Thay thế state formData bằng useRef để quản lý form
  const formRef = useRef(null);
  
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // BƯỚC 2: Thay đổi useEffect để đổ dữ liệu trực tiếp vào DOM (chỉ chạy 1 lần khi đổi khách hàng)
  useEffect(() => {
    if (formRef.current) {
      if (editingCompany) {
        formRef.current.elements.name.value = editingCompany.name || "";
        formRef.current.elements.phoneNumber.value = editingCompany.phoneNumber || "";
        formRef.current.elements.email.value = editingCompany.email || "";
        formRef.current.elements.dateOfBirth.value = editingCompany.dateOfBirth || "";
        formRef.current.elements.location.value = editingCompany.location || "";
        // Chuyển boolean thành string "true"/"false" cho thẻ select
        formRef.current.elements.gender.value = editingCompany.gender !== undefined ? String(editingCompany.gender) : "true";
      } else {
        formRef.current.reset(); // Nếu thêm mới thì xóa trắng form
      }
    }
  }, [editingCompany]);

  // BƯỚC 3: Sửa hàm Submit để gom tất cả file/trường dữ liệu đi một lần duy nhất
  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    // Thu thập dữ liệu từ form giống cơ chế gom Control trong C#
    const data = new FormData(formRef.current);
    const payload = {
      name: data.get("name"),
      phoneNumber: data.get("phoneNumber"),
      email: data.get("email"),
      dateOfBirth: data.get("dateOfBirth"),
      location: data.get("location"),
      gender: data.get("gender") === "true", // Ép chuỗi "true" thành kiểu boolean true
    };

    try {
      if (editingCompany) {
        await updateCompany(editingCompany.id, payload);
      } else {
        await createCompany(payload);
      }
      onSaveSuccess();
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.message || "Đã xảy ra lỗi");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ marginBottom: "20px", padding: "20px", border: "1px solid #ccc", borderRadius: "8px" }}>
      <h2>{editingCompany ? "Sửa khách hàng" : "Thêm khách hàng mới"}</h2>
      {error && <p style={{ color: "red" }}>{error}</p>}
      
      {/* Gắn ref vào thẻ form và xóa bỏ toàn bộ thuộc tính value, onChange ở các ô input bên dưới */}
      <form ref={formRef} onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: "10px", maxWidth: "400px" }}>
        <label>
          Họ Tên:
          <input type="text" name="nameProduct" required style={{ width: "100%" }} />
        </label>
        <label></label>
        <label>
          Giới tính:
          <select name="gender" style={{ width: "100%", padding: "4px" }}>
            <option value="true">Nam</option>
            <option value="false">Nữ</option>
          </select>
        </label>
        <div style={{ display: "flex", gap: "10px", marginTop: "10px" }}>
          <button type="submit" disabled={loading}>{loading ? "Đang lưu..." : "Lưu"}</button>
          {editingCompany && <button type="button" onClick={onCancel} disabled={loading}>Huỷ</button>}
        </div>
      </form>
    </div>
  );
}
