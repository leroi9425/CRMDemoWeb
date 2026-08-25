import { useState, useEffect } from "react";
import { createCustomer, updateCustomer } from "../api/customerApi";

export default function ProductForm({ editingProduct, onSaveSuccess, onCancel }) {
  const [formData, setFormData] = useState({
    name: "",
    description: "",
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
   
  }, );

  const handleChange = (e) => {
   
  };

  const handleSubmit = async (e) => {
    
  };

  return (
    <div style={{ marginBottom: "20px", padding: "20px", border: "1px solid #ccc", borderRadius: "8px" }}>
      <h2>{editingProduct ? "Sửa sản phẩm" : "Thêm sản phẩm mới"}</h2>
      {error && <p style={{ color: "red" }}>{error}</p>}
      <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: "10px", maxWidth: "400px" }}>
        <label>
          Tên sản phẩm:
          <input type="text" name="name" value={formData.productName} onChange={handleChange} required style={{ width: "100%" }} />
        </label>
        <label>
          Mô tả:
          <input type="text" name="description" value={formData.detail} onChange={handleChange} required style={{ width: "100%" }} />
        </label>
        <div style={{ display: "flex", gap: "10px", marginTop: "10px" }}>
          <button type="submit" disabled={loading}>{loading ? "Đang lưu..." : "Lưu"}</button>
          {editingProduct && <button type="button" onClick={onCancel} disabled={loading}>Huỷ</button>}
        </div>
      </form>
    </div>
  );
}
