import { useState, useEffect } from "react";
import { createCustomer, updateCustomer } from "../api/customerApi";

export default function CustomerForm({ editingCustomer, onSaveSuccess, onCancel }) {
  const [formData, setFormData] = useState({
    name: "",
    phoneNumber: "",
    email: "",
    dateOfBirth: "",
    location: "",
    gender: true,
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (editingCustomer) {
      setFormData({
        name: editingCustomer.name || "",
        phoneNumber: editingCustomer.phoneNumber || "",
        email: editingCustomer.email || "",
        dateOfBirth: editingCustomer.dateOfBirth || "",
        location: editingCustomer.location || "",
        gender: editingCustomer.gender !== undefined ? editingCustomer.gender : true,
      });
    } else {
      setFormData({ name: "", phoneNumber: "", email: "", dateOfBirth: "", location: "", gender: true });
    }
  }, [editingCustomer]);

  const handleChange = (e) => {
    const value = e.target.type === 'checkbox' ? e.target.checked : e.target.value;
    const name = e.target.name;
    if (name === "gender") {
        setFormData({ ...formData, [name]: e.target.value === "true" });
    } else {
        setFormData({ ...formData, [name]: value });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      if (editingCustomer) {
        await updateCustomer(editingCustomer.id, formData);
      } else {
        await createCustomer(formData);
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
      <h2>{editingCustomer ? "Sửa khách hàng" : "Thêm khách hàng mới"}</h2>
      {error && <p style={{ color: "red" }}>{error}</p>}
      <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: "10px", maxWidth: "400px" }}>
        <label>
          Họ Tên:
          <input type="text" name="name" value={formData.name} onChange={handleChange} required style={{ width: "100%" }} />
        </label>
        <label>
          SĐT:
          <input type="text" name="phoneNumber" value={formData.phoneNumber} onChange={handleChange} required style={{ width: "100%" }} />
        </label>
        <label>
          Email:
          <input type="email" name="email" value={formData.email} onChange={handleChange} required style={{ width: "100%" }} />
        </label>
        <label>
          Ngày sinh:
          <input type="date" name="dateOfBirth" value={formData.dateOfBirth} onChange={handleChange} required style={{ width: "100%" }} />
        </label>
        <label>
          Địa chỉ:
          <input type="text" name="location" value={formData.location} onChange={handleChange} required style={{ width: "100%" }} />
        </label>
        <label>
          Giới tính:
          <select name="gender" value={formData.gender} onChange={handleChange} style={{ width: "100%", padding: "4px" }}>
            <option value="true">Nam</option>
            <option value="false">Nữ</option>
          </select>
        </label>
        <div style={{ display: "flex", gap: "10px", marginTop: "10px" }}>
          <button type="submit" disabled={loading}>{loading ? "Đang lưu..." : "Lưu"}</button>
          {editingCustomer && <button type="button" onClick={onCancel} disabled={loading}>Huỷ</button>}
        </div>
      </form>
    </div>
  );
}
