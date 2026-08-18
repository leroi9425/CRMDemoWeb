import { useEffect, useState } from "react";
import { getUsers, deleteUser } from "../api/userApi";

export default function UserList({ onEdit }) {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const res = await getUsers();
      setUsers(res.data);
    } catch (error) {
      console.error("Error fetching users", error);
    }
    setLoading(false);
  };

  useEffect(() => { fetchUsers(); }, []);

  const handleDelete = async (id) => {
    if (!confirm("Xoá user này?")) return;
    try {
      await deleteUser(id);
      fetchUsers();
    } catch (error) {
      console.error("Error deleting user", error);
    }
  };

  if (loading) return <p>Đang tải...</p>;

  return (
    <div>
      <h2>Danh sách người dùng</h2>
      <table border="1" cellPadding="10" style={{ borderCollapse: "collapse", width: "100%" }}>
        <thead>
          <tr>
            <th>ID</th>
            <th>Username</th>
            <th>Email</th>
            <th>Họ Tên</th>
            <th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          {users.map((u) => (
            <tr key={u.id}>
              <td>{u.id}</td>
              <td>{u.username}</td>
              <td>{u.email}</td>
              <td>{u.fullName}</td>
              <td>
                <button onClick={() => onEdit(u)} style={{ marginRight: "10px" }}>Sửa</button>
                <button onClick={() => handleDelete(u.id)}>Xoá</button>
              </td>
            </tr>
          ))}
          {users.length === 0 && (
            <tr>
              <td colSpan="5" style={{ textAlign: "center" }}>Không có người dùng nào.</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}
