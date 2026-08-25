import { useEffect, useState } from "react";
import { getCustomers, deleteCustomer } from "../api/customerApi";

export default function ProductList({ onEdit }) {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchProducts = async () => {
    setLoading(true);
    try {
      const res = await getProducts();
      setProducts(res.data);
    } catch (error) {
      console.error("Error fetching products", error);
    }
    setLoading(false);
  };

  useEffect(() => { fetchProducts(); }, []);

  const handleDelete = async (id) => {
    if (!confirm("Xoá sản phầm này?")) return;
    try {
      await deleteCustomer(id);
      fetchProducts();
    } catch (error) {
      console.error("Error deleting product", error);
    }
  };

  if (loading) return <p>Đang tải danh sách sản phầm...</p>;

  return (
    <div>
      <h2>Danh sách Sản phẩm</h2>
      <table border="1" cellPadding="10" style={{ borderCollapse: "collapse", width: "100%" }}>
        <thead>
          <tr>
            <th>ID</th>
            <th>Tên Sản phẩm</th>
            <th>Mô tả</th>
          </tr>
        </thead>
        <tbody>
          {products.map((p) => (
            <tr key={p.id}>
              <td>{p.id}</td>
              <td>{p.nameProduct}</td>
              <td>{p.description}</td>
              <td>
                <button onClick={() => onEdit(p)} style={{ marginRight: "10px" }}>Sửa</button>
                <button onClick={() => handleDelete(p.id)}>Xoá</button>
              </td>
            </tr>
          ))}
          {products.length === 0 && (
            <tr>
              <td colSpan="8" style={{ textAlign: "center" }}>Không có sản phầm nào.</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}
