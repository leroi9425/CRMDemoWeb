import { useState, useEffect } from "react";
import { getProducts, createProduct, updateProduct, deleteProduct } from "../api/productApi";
import { useAuth } from "../context/AuthContext";

export default function ProductView() {
    const { auth } = useAuth();
    const [products, setProducts] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
    const [editingProduct, setEditingProduct] = useState(null);
    const [deletingId, setDeletingId] = useState(null);
    const [toasts, setToasts] = useState([]);

    const [formData, setFormData] = useState({
        productName: "", detail: ""
    });

    const fetchProducts = async () => {
        try {
            const res = await getProducts();
            console.log("Fetched products:", res.data);
            setProducts(res.data);
        } catch (error) {
            console.log("Lỗi, chuẩn bị vào toast", error);
            showToast("Lỗi khi tải danh sách sản phẩm", "danger");
        }
    };

    useEffect(() => {
        fetchProducts();
    }, []);

    const showToast = (message, type = "success") => {
        const id = Date.now();
        setToasts(prev => [...prev, { id, message, type }]);
        setTimeout(() => {
            setToasts(prev => prev.filter(t => t.id !== id));
        }, 3000);
        console.log("Toast product added:", { id, message, type });
    };

    const openModal = (product = null) => {
        setEditingProduct(product);
        if (product) {
            setFormData({
                productName: product.productName || "",
                detail: product.detail || ""
            });
        } else {
            setFormData({ productName: "", detail: "" });
        }
        setIsModalOpen(true);
    };

    const closeModal = () => {
        setIsModalOpen(false);
        setTimeout(() => setEditingProduct(null), 300);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (editingProduct) {
                console.log("id va form cua thang update: ", editingProduct.id, formData);
                await updateProduct(editingProduct.id, formData);
                showToast("Đã cập nhật thông tin thành công!", "success");
            } else {
                console.log("form cua thang create: ", formData);
                await createProduct(formData);
                console.log("Đã thêm sản phẩm mới!");
                showToast("Đã thêm sản phẩm mới!", "success");
            }
            closeModal();
            fetchProducts();
        } catch (err) {
            console.error("Lỗi khi lưu sản phẩm", err);
            showToast(err.response?.data?.message || "Đã xảy ra lỗi", "danger");
        }
    };

    const confirmDelete = async () => {
        try {
            console.log("id cua thang delete: ", deletingId);
            await deleteProduct(deletingId);
            showToast("Đã xóa sản phẩm.", "danger");
            setIsDeleteModalOpen(false);
            fetchProducts();
        } catch (error) {
            showToast("Lỗi khi xóa", "danger");
            // set lại local storage ở đoạn này cho nét
            await getData
        }
    };

    const filteredProducts = products.filter(p => 
        p.productName?.toLowerCase().includes(searchTerm.toLowerCase()) || 
        p.detail?.includes(searchTerm)
    );

    const getInitials = (name) => name?.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase() || 'NA';

    return (
        <main className="flex-grow max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 w-full">
            
            <div className="flex justify-between items-end mb-6">
                <div>
                    <h1 className="text-2xl font-bold text-slate-900">Danh sách Sản phẩm</h1>
                    <p className="text-sm text-slate-500 mt-1">Quản lý thông tin sản phẩm và chi tiết sản phẩm.</p>
                </div>
                <div className="flex items-center space-x-4">
                    <div className="relative hidden md:block">
                         <span className="absolute inset-y-0 left-0 flex items-center pl-3">
                            <i className="fa-solid fa-search text-slate-400"></i>
                        </span>
                        <input 
                            type="text" 
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="pl-10 pr-4 py-2 border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent transition-shadow w-64 bg-slate-50" 
                            placeholder="Tìm kiếm sản phẩm..."
                        />
                    </div>
                    {auth?.permissions?.includes('THEM_SAN_PHAM') && (
                        <button onClick={() => openModal()} className="bg-primary hover:bg-blue-600 text-white font-medium py-2 px-4 rounded-lg shadow-sm transition-colors duration-200 flex items-center">
                            <i className="fa-solid fa-plus mr-2"></i> Thêm Mới
                        </button>
                    )}
                </div>
            </div>

            <div className="bg-white shadow-sm rounded-xl border border-slate-200 overflow-hidden">
                <div className="overflow-x-auto">
                    <table className="w-full whitespace-nowrap">
                        <thead>
                            <tr className="bg-slate-50 border-b border-slate-200 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">
                                <th className="px-6 py-4">ID</th>
                                <th className="px-6 py-4">Tên Sản phẩm</th>
                                <th className="px-6 py-4">Mô tả</th>
                                <th className="px-6 py-4">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-200 text-sm">
                            {filteredProducts.map(product => (
                                <tr key={product.id} className="hover:bg-slate-50 transition-colors group">
                                    <td className="px-6 py-4 whitespace-nowrap text-slate-500 font-mono text-xs">#{product.id}</td>
                                    <td className="px-6 py-4 whitespace-nowrap">
                                        <div className="flex items-center">
                                            <div className="ml-4">
                                                <div className="text-slate-900 text-xs">{product.productName}</div>
                                            </div>
                                        </div>
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap">
                                        <div className="flex items-center">
                                            <div className="ml-4">
                                                <div className="text-slate-900 text-xs">{product.detail}</div>
                                            </div>
                                        </div>
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap text-right font-medium">
                                        <div className="flex justify-end space-x-2 opacity-0 group-hover:opacity-100 transition-opacity">
                                            {auth?.permissions?.includes("THEM_KHACH_HANG") && (
                                                <button onClick={() => openModal(product)} className="p-2 text-primary hover:bg-blue-50 rounded-lg transition-colors" title="Sửa">
                                                    <i className="fa-solid fa-pen-to-square"></i>
                                                </button>
                                            )}
                                            {auth?.permissions?.includes("XOA_KHACH_HANG") && (
                                                <button onClick={() => { setDeletingId(product.id); setIsDeleteModalOpen(true); }} className="p-2 text-danger hover:bg-red-50 rounded-lg transition-colors" title="Xóa">
                                                    <i className="fa-solid fa-trash-can"></i>
                                                </button>
                                            )}
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                    
                    {filteredProducts.length === 0 && (
                        <div className="flex flex-col items-center justify-center py-16 text-slate-500">
                            <div className="bg-slate-100 p-4 rounded-full mb-4">
                                <i className="fa-solid fa-folder-open text-3xl text-slate-400"></i>
                            </div>
                            <p className="text-lg font-medium text-slate-700 mb-1">Không tìm thấy sản phẩm nào</p>
                            <button onClick={() => openModal()} className="text-primary hover:text-blue-700 font-medium">
                                + Thêm sản phẩm ngay
                            </button>
                        </div>
                    )}
                </div>
            </div>

            {/* Modal Edit/Add */}
            <div className={`fixed inset-0 bg-slate-900/50 backdrop-blur-sm z-50 flex items-center justify-center transition-opacity duration-300 ${isModalOpen ? 'opacity-100' : 'opacity-0 pointer-events-none'}`}>
                <div className={`bg-white rounded-2xl shadow-xl w-full max-w-lg mx-4 overflow-hidden flex flex-col max-h-[90vh] transition-transform duration-300 ${isModalOpen ? 'scale-100' : 'scale-95'}`}>
                    <div className="px-6 py-4 border-b border-slate-200 flex justify-between items-center bg-slate-50">
                        <h3 className="text-lg font-semibold text-slate-900">{editingProduct ? "Sửa Sản phẩm" : "Thêm Sản phẩm Mới"}</h3>
                        <button onClick={closeModal} className="text-slate-400 hover:text-slate-600 transition-colors p-1 rounded-md hover:bg-slate-200">
                            <i className="fa-solid fa-xmark text-xl"></i>
                        </button>
                    </div>
                    <div className="px-6 py-4 overflow-y-auto">
                        <form id="productForm" onSubmit={handleSubmit} className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-slate-700 mb-1">Tên Sản phẩm <span className="text-danger">*</span></label>
                                <input type="text" required value={formData.productName} onChange={e => setFormData({...formData, productName: e.target.value})} className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary" placeholder="Tên sản phẩm" />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-slate-700 mb-1">Mô tả <span className="text-danger">*</span></label>
                                <input type="text" required value={formData.detail} onChange={e => setFormData({...formData, detail: e.target.value})} className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary" placeholder="Mô tả sản phẩm" />
                            </div>
                        </form>
                    </div>
                    <div className="px-6 py-4 border-t border-slate-200 bg-slate-50 flex justify-end space-x-3">
                        <button type="button" onClick={closeModal} className="px-4 py-2 text-sm font-medium text-slate-700 bg-white border border-slate-300 rounded-lg hover:bg-slate-50 transition-colors">Hủy</button>
                        <button type="submit" form="productForm" className="px-4 py-2 text-sm font-medium text-white bg-primary rounded-lg hover:bg-blue-600 transition-colors shadow-sm flex items-center">
                            <i className="fa-solid fa-save mr-2"></i> Lưu thông tin
                        </button>
                    </div>
                </div>
            </div>

            {/* Delete Modal */}
            <div className={`fixed inset-0 bg-slate-900/50 backdrop-blur-sm z-50 flex items-center justify-center transition-opacity duration-300 ${isDeleteModalOpen ? 'opacity-100' : 'opacity-0 pointer-events-none'}`}>
                <div className={`bg-white rounded-2xl shadow-xl w-full max-w-sm mx-4 overflow-hidden transition-transform duration-300 ${isDeleteModalOpen ? 'scale-100' : 'scale-95'}`}>
                    <div className="p-6 text-center">
                        <div className="w-16 h-16 rounded-full bg-red-100 flex items-center justify-center mx-auto mb-4 text-danger">
                            <i className="fa-solid fa-triangle-exclamation text-3xl"></i>
                        </div>
                        <h3 className="text-lg font-bold text-slate-900 mb-2">Xác nhận xóa?</h3>
                        <p className="text-slate-500 text-sm mb-6">Bạn có chắc chắn muốn xóa sản phẩm này không? Hành động này không thể hoàn tác.</p>
                        <div className="flex justify-center space-x-3">
                            <button onClick={() => setIsDeleteModalOpen(false)} className="px-4 py-2 text-sm font-medium text-slate-700 bg-slate-100 rounded-lg hover:bg-slate-200 w-full">Hủy bỏ</button>
                            <button onClick={confirmDelete} className="px-4 py-2 text-sm font-medium text-white bg-danger rounded-lg hover:bg-red-600 w-full">Đồng ý Xóa</button>
                        </div>
                    </div>
                </div>
            </div>

            {/* Toasts */}
            <div className="fixed bottom-4 right-4 z-50 flex flex-col gap-2">
                {toasts.map(toast => (
                    <div key={toast.id} className={`flex items-center p-4 rounded shadow-lg border-l-4 min-w-[250px] bg-white text-slate-800 ${toast.type === 'success' ? 'border-emerald-500' : 'border-red-500'}`}>
                        {toast.type === 'success' ? <i className="fa-solid fa-circle-check text-emerald-500 mr-2"></i> : <i className="fa-solid fa-circle-exclamation text-red-500 mr-2"></i>}
                        <span className="font-medium text-sm">{toast.message}</span>
                    </div>
                ))}
            </div>

        </main>
    );
}
