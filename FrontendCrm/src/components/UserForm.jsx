import { useState, useEffect } from "react";
import { createUser, updateUser } from "../api/userApi";

export default function UserForm({ editingUser, onSaveSuccess, onCancel }) {
  const [formData, setFormData] = useState({
    username: "",
    email: "",
    password: "",
    fullName: "",
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (editingUser) {
      setFormData({
        username: editingUser.username || "",
        email: editingUser.email || "",
        password: "", // Không show password khi sửa
        fullName: editingUser.fullName || "",
      });
    } else {
      setFormData({ username: "", email: "", password: "", fullName: "" });
    }
  }, [editingUser]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      if (editingUser) {
        await updateUser(editingUser.id, formData);
      } else {
        await createUser(formData);
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
      <h2>{editingUser ? "Sửa người dùng" : "Thêm người dùng mới"}</h2>
      {error && <p style={{ color: "red" }}>{error}</p>}
      <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: "10px", maxWidth: "400px" }}>
        <label>
          Username:
          <input type="text" name="username" value={formData.username} onChange={handleChange} required disabled={!!editingUser} style={{ width: "100%" }} />
        </label>
        <label>
          Email:
          <input type="email" name="email" value={formData.email} onChange={handleChange} required style={{ width: "100%" }} />
        </label>
        <label>
          Họ Tên:
          <input type="text" name="fullName" value={formData.fullName} onChange={handleChange} style={{ width: "100%" }} />
        </label>
        <label>
          Password {editingUser && "(Bỏ trống nếu không đổi)"}:
          <input type="password" name="password" value={formData.password} onChange={handleChange} required={!editingUser} style={{ width: "100%" }} />
        </label>
        <div style={{ display: "flex", gap: "10px", marginTop: "10px" }}>
          <button type="submit" disabled={loading}>{loading ? "Đang lưu..." : "Lưu"}</button>
          {editingUser && <button type="button" onClick={onCancel} disabled={loading}>Huỷ</button>}
        </div>
      </form>
    </div>
  );
}
