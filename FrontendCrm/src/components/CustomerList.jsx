import { useEffect, useState } from "react";
import { getCustomers, deleteCustomer } from "../api/customerApi";

export default function CustomerList({ onEdit }) {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchCustomers = async () => {
    setLoading(true);
    try {
      const res = await getCustomers();
      setCustomers(res.data);
    } catch (error) {
      console.error("Error fetching customers", error);
    }
    setLoading(false);
  };

  useEffect(() => { fetchCustomers(); }, []);

  const handleDelete = async (id) => {
    if (!confirm("Xoá khách hàng này?")) return;
    try {
      await deleteCustomer(id);
      fetchCustomers();
    } catch (error) {
      console.error("Error deleting customer", error);
    }
  };

  if (loading) return <p>Đang tải danh sách khách hàng...</p>;

  return (
    <div>
      <h2>Danh sách Khách hàng</h2>
      <table border="1" cellPadding="10" style={{ borderCollapse: "collapse", width: "100%" }}>
        <thead>
          <tr>
            <th>ID</th>
            <th>Họ Tên</th>
            <th>Email</th>
            <th>SĐT</th>
            <th>Ngày sinh</th>
            <th>Giới tính</th>
            <th>Địa chỉ</th>
            <th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          {customers.map((c) => (
            <tr key={c.id}>
              <td>{c.id}</td>
              <td>{c.name}</td>
              <td>{c.email}</td>
              <td>{c.phoneNumber}</td>
              <td>{c.dateOfBirth}</td>
              <td>{c.gender ? "Nam" : "Nữ"}</td>
              <td>{c.location}</td>
              <td>
                <button onClick={() => onEdit(c)} style={{ marginRight: "10px" }}>Sửa</button>
                <button onClick={() => handleDelete(c.id)}>Xoá</button>
              </td>
            </tr>
          ))}
          {customers.length === 0 && (
            <tr>
              <td colSpan="8" style={{ textAlign: "center" }}>Không có khách hàng nào.</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}
