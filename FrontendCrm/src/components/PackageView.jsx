import { useState, useEffect } from "react";
import { getPackages, createPackage, updatePackage, deletePackage, requestCreate, requestEdit } from "../api/packageApi";
import TreeNode from "./Permission/TreeNode";

export default function PackageView() {
    const [currentView, setCurrentView] = useState("LIST"); // "LIST" | "CREATE" | "EDIT"
    const [editingId, setEditingId] = useState(null);
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
    const [deletingId, setDeletingId] = useState(null);
    const [toasts, setToasts] = useState([]);
    const [packages, setPackages] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState("");
    const [statusFilter, setStatusFilter] = useState("all");

    // State cho màn hình Tạo gói
    const [formData, setFormData] = useState({
        name: "",
        price: "",
        description: "",
        status: true,
    });
    const [treeData, setTreeData] = useState([]);
    const [checkedNames, setCheckedNames] = useState([]);
    const [loadingTree, setLoadingTree] = useState(false);
    const [submitting, setSubmitting] = useState(false);

    // Gọi API lấy toàn bộ danh sách gói
    const fetchPackages = async () => {
        setLoading(true);
        try {
            const res = await getPackages();
            console.log("Danh sách gói tải về:", res.data);
            setPackages(res.data || []);
        } catch (error) {
            console.error("Lỗi khi tải danh sách gói:", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchPackages();
    }, []);

    // Format tiền tệ Việt Nam (VNĐ)
    const formatPrice = (price) => {
        if (price === undefined || price === null || price === "") return "0 ₫";
        const num = Number(price);
        if (isNaN(num)) return "0 ₫";
        return new Intl.NumberFormat("vi-VN", {
            style: "currency",
            currency: "VND",
        }).format(num);
    };

    const showToast = (message, type = "success") => {
        const id = Date.now();
        setToasts(prev => [...prev, { id, message, type }]); 
        setTimeout(() => {
            setToasts(prev => prev.filter(t => t.id !== id));
        }, 3000);
    };

    // Trích xuất danh sách tên quyền có isChecked = true từ cây quyền phân cột
    const extractCheckedNames = (columns) => {
        const names = [];
        const traverse = (node) => {
            if (!node) return;
            if (node.isChecked) {
                names.push(node.name); // Thay vì push node.id, push node.name để phù hợp với backend
            }
            if (node.children && Array.isArray(node.children)) {
                node.children.forEach(traverse);
            }
        };
        if (Array.isArray(columns)) {
            columns.forEach((col) => {
                col.listPermission?.forEach(traverse);
            });
        }
        return names;
    };

    // Mở màn hình tạo gói & Gọi API cây quyền /api/permissions/tree/column
    const handleOpenCreate = async () => {
        setEditingId(null);
        setFormData({
            name: "",
            price: "",
            description: "",
            status: true,
        });
        setCheckedNames([]);
        setCurrentView("CREATE");
        setLoadingTree(true);

        try {
            const res = await requestCreate();
            console.log("Xác nhận có quyền thêm quyền, tải về cây quyền:", res.data);
            setTreeData(res.data || []);
        } catch (error) {
            console.error("Lỗi khi tải cây quyền:", error);
            alert("Lỗi khi tải cây quyền: " + (error.response?.data?.message || error.message));
        } finally {
            setLoadingTree(false);
        }
    };

    // Bắt sự kiện bấm nút sửa gói
    const handleOpenEdit = async (pkg) => {
        setEditingId(pkg.id);
        setCurrentView("EDIT");
        setLoadingTree(true);

        try {
            const res = await requestEdit(pkg.id);
            console.log("Dữ liệu gói tải về để chỉnh sửa:", res.data);
            const data = res.data;
            setFormData({
                name: data.name || "",
                price: data.price !== undefined && data.price !== null ? data.price : "",
                description: data.description || "",
                status: data.status !== undefined ? Boolean(data.status) : true,
            });
            // Hỗ trợ cả permissions lẫn permisisons (đề phòng sai chính tả từ response)
            const tree = data.permissions || data.permisisons || [];
            setTreeData(tree);

            // Tự động tích chọn checkbox theo trường isChecked trong response
            const initialCheckedNames = extractCheckedNames(tree);
            setCheckedNames(initialCheckedNames);
        } catch (error) {
            console.error("Lỗi khi tải dữ liệu gói để sửa:", error);
            alert("Lỗi khi tải dữ liệu gói: " + (error.response?.data?.message || error.message));
        } finally {
            setLoadingTree(false);
        }
    };

    // Đệ quy lấy tất cả tên của node và con cháu
    const getAllNames = (node, names = []) => {
        names.push(node.name);
        if (node.children) {
            node.children.forEach((child) => getAllNames(child, names));
        }
        return names;
    };

    // Xử lý chọn/bỏ chọn node trên cây quyền
    const handleCheck = (node, isChecked) => {
        console.log(`Node ${node.name} (Name: ${node.name}) đã được ${isChecked ? "chọn" : "bỏ chọn"}`);
        const familyNames = getAllNames(node);
        setCheckedNames((prev) => {
            if (isChecked) {
                return Array.from(new Set([...prev, ...familyNames]));
            } else {
                return prev.filter((name) => !familyNames.includes(name));
            }
        });
    };

    // Chọn tất cả quyền
    const handleSelectAll = () => {
        const allNames = [];
        treeData.forEach((col) => {
            col.listPermission?.forEach((rootNode) => {
                getAllNames(rootNode, allNames);
            });
        });
        setCheckedNames(Array.from(new Set(allNames)));
    };

    // Bỏ chọn tất cả quyền
    const handleDeselectAll = () => {
        setCheckedNames([]);
    };

    // Xử lý lưu gói (Tạo mới hoặc Cập nhật)
    const handleSavePackage = async (e) => {
        if (e) e.preventDefault();

        if (!formData.name.trim()) {
            return;
        }

        if (formData.price === "" || formData.price === null) {
            return;
        }

        const payload = {
            name: formData.name.trim(),
            price: Number(formData.price),
            description: formData.description.trim(),
            status: Boolean(formData.status),
            listPermissionNames: checkedNames,
        };

        setSubmitting(true);
        try {
            if (currentView === "EDIT" && editingId) {
                await updatePackage(editingId, payload);
                showToast("Đã cập nhật gói dịch vụ thành công!", "success");
            } else {
                await createPackage(payload);
                showToast("Đã tạo gói dịch vụ mới thành công!", "success");
            }
            setCurrentView("LIST");
            fetchPackages();
        } catch (error) {
            console.error(currentView === "EDIT" ? "Lỗi khi cập nhật gói:" : "Lỗi khi tạo gói:", error);
            showToast(error.response?.data?.message || "Có lỗi xảy ra", "danger");
        } finally {
            setSubmitting(false);
        }
    };

    // Mở popup xác nhận xóa
    const handleOpenDelete = (pkg) => {
        setDeletingId(pkg.id);
        setIsDeleteModalOpen(true);
    };

    // Xác nhận xóa gói dịch vụ
    const confirmDelete = async () => {
        if (!deletingId) return;
        try {
            await deletePackage(deletingId);
            showToast("Đã xóa gói dịch vụ thành công.", "success");
            setIsDeleteModalOpen(false);
            setDeletingId(null);
            fetchPackages();
        } catch (error) {
            console.error("Lỗi khi xóa gói:", error);
            showToast(error.response?.data?.message || "Lỗi khi xóa gói", "danger");
        }
    };

    // Lọc theo tìm kiếm và trạng thái
    const filteredPackages = packages.filter((pkg) => {
        const matchesSearch =
            pkg.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
            pkg.description?.toLowerCase().includes(searchTerm.toLowerCase()) ||
            String(pkg.id).includes(searchTerm);

        const matchesStatus =
            statusFilter === "all" ||
            (statusFilter === "active" && pkg.status === true) ||
            (statusFilter === "inactive" && pkg.status === false);

        return matchesSearch && matchesStatus;
    });

    // =========================================================================
    // MÀN HÌNH: TẠO GÓI MỚI / CHỈNH SỬA GÓI (FORM + CÂY QUYỀN)
    // =========================================================================
    if (currentView === "CREATE" || currentView === "EDIT") {
        const isEdit = currentView === "EDIT";

        return (
            <main className="flex-grow max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 w-full">
                {/* Header thanh điều hướng & nút hành động */}
                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
                    <div>
                        <div className="flex items-center space-x-2 text-sm text-slate-500 mb-1">
                            <button
                                type="button"
                                onClick={() => setCurrentView("LIST")}
                                className="hover:text-blue-600 transition-colors cursor-pointer flex items-center"
                            >
                                <i className="fa-solid fa-arrow-left mr-1.5 text-xs"></i>
                                Gói dịch vụ
                            </button>
                            <span>/</span>
                            <span className="text-slate-800 font-medium">
                                {isEdit ? "Chỉnh sửa" : "Thêm mới"}
                            </span>
                        </div>
                        <h1 className="text-2xl font-bold text-slate-900">
                            {isEdit ? "Cập Nhật Gói Dịch Vụ" : "Tạo Gói Dịch Vụ Mới"}
                        </h1>
                        <p className="text-sm text-slate-500 mt-0.5">
                            {isEdit
                                ? "Chỉnh sửa thông tin tên gói, giá bán và cấu hình cây quyền"
                                : "Thiết lập thông tin tên gói, giá bán và phân bổ cây quyền tính năng"}
                        </p>
                    </div>

                    <div className="flex items-center space-x-3">
                        <button
                            type="button"
                            onClick={() => setCurrentView("LIST")}
                            disabled={submitting}
                            className="px-4 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 font-medium text-sm rounded-xl transition-colors cursor-pointer"
                        >
                            <i className="fa-solid fa-xmark mr-1.5"></i>
                            Trở về
                        </button>
                        <button
                            type="button"
                            onClick={handleSavePackage}
                            disabled={submitting}
                            className="px-5 py-2 bg-blue-600 hover:bg-blue-700 text-white font-medium text-sm rounded-xl shadow-sm hover:shadow transition-all cursor-pointer flex items-center"
                        >
                            {submitting ? (
                                <>
                                    <i className="fa-solid fa-circle-notch fa-spin mr-2"></i>
                                    Đang lưu...
                                </>
                            ) : (
                                <>
                                    <i className="fa-solid fa-check mr-2"></i>
                                    {isEdit ? "Cập nhật gói" : "Lưu gói dịch vụ"}
                                </>
                            )}
                        </button>
                    </div>
                </div>

                <form onSubmit={handleSavePackage} className="space-y-6">
                    {/* KHỐI 1: THÔNG TIN CƠ BẢN CỦA GÓI */}
                    <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
                        <div className="flex items-center justify-between border-b border-slate-100 pb-3 mb-5">
                            <div className="flex items-center space-x-2">
                                <div className="w-8 h-8 rounded-lg bg-blue-50 text-blue-600 flex items-center justify-center text-sm">
                                    <i className="fa-solid fa-circle-info"></i>
                                </div>
                                <h3 className="text-base font-semibold text-slate-800">
                                    Thông tin gói dịch vụ
                                </h3>
                            </div>
                            <span className="text-xs text-slate-400">
                                Các trường có dấu <span className="text-rose-500">*</span> là bắt buộc
                            </span>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                            {/* Tên gói */}
                            <div className="space-y-1.5">
                                <label className="block text-sm font-semibold text-slate-700">
                                    Tên gói <span className="text-rose-500">*</span>
                                </label>
                                <input
                                    type="text"
                                    required
                                    value={formData.name}
                                    onChange={(e) =>
                                        setFormData({ ...formData, name: e.target.value })
                                    }
                                    placeholder="Ví dụ: Gói CRM Doanh Nghiệp Pro"
                                    className="w-full px-3.5 py-2.5 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                                />
                            </div>

                            {/* Giá bán */}
                            <div className="space-y-1.5">
                                <div className="flex items-center justify-between">
                                    <label className="block text-sm font-semibold text-slate-700">
                                        Giá bán (VNĐ) <span className="text-rose-500">*</span>
                                    </label>
                                    <span className="text-xs font-semibold text-blue-600">
                                        Xem trước: {formatPrice(formData.price)}
                                    </span>
                                </div>
                                <div className="relative">
                                    <input
                                        type="number"
                                        min="0"
                                        step="1000"
                                        required
                                        value={formData.price}
                                        onChange={(e) =>
                                            setFormData({ ...formData, price: e.target.value })
                                        }
                                        placeholder="Ví dụ: 5000000"
                                        className="w-full pl-3.5 pr-12 py-2.5 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                                    />
                                    <span className="absolute inset-y-0 right-0 flex items-center pr-3.5 text-xs font-medium text-slate-400 pointer-events-none">
                                        VNĐ
                                    </span>
                                </div>
                            </div>

                            {/* Mô tả */}
                            <div className="md:col-span-2 space-y-1.5">
                                <label className="block text-sm font-semibold text-slate-700">
                                    Mô tả gói
                                </label>
                                <textarea
                                    rows="3"
                                    value={formData.description}
                                    onChange={(e) =>
                                        setFormData({ ...formData, description: e.target.value })
                                    }
                                    placeholder="Mô tả quyền lợi, đối tượng khách hàng hoặc các tính năng nổi bật của gói cước này..."
                                    className="w-full px-3.5 py-2.5 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all resize-y"
                                />
                            </div>

                            {/* Trạng thái kích hoạt */}
                            <div className="md:col-span-2 flex items-center space-x-3 pt-1">
                                <label className="relative inline-flex items-center cursor-pointer">
                                    <input
                                        type="checkbox"
                                        checked={formData.status}
                                        onChange={(e) =>
                                            setFormData({ ...formData, status: e.target.checked })
                                        }
                                        className="sr-only peer"
                                    />
                                    <div className="w-11 h-6 bg-slate-200 peer-focus:outline-none peer-focus:ring-2 peer-focus:ring-blue-500 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-slate-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600"></div>
                                    <span className="ml-3 text-sm font-medium text-slate-700">
                                        Kích hoạt mở bán ngay khi tạo
                                    </span>
                                </label>
                            </div>
                        </div>
                    </div>

                    {/* KHỐI 2: CÂY QUYỀN SỬ DỤNG TreeNode THEO ĐÚNG MẪU YÊU CẦU */}
                    <div className="bg-white rounded-lg shadow-sm border border-slate-200 p-6">
                        <div className="flex flex-col sm:flex-row sm:items-center justify-between border-b pb-2 mb-4 gap-2">
                            <div className="flex items-center space-x-2">
                                <h3 className="text-lg font-semibold text-slate-800">Chọn quyền</h3>
                                <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-50 text-blue-700 border border-blue-200">
                                    Đã chọn: {checkedNames.length} quyền
                                </span>
                            </div>

                            <div className="flex items-center space-x-2">
                                <button
                                    type="button"
                                    onClick={handleSelectAll}
                                    className="px-3 py-1 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-medium rounded-lg transition-colors cursor-pointer"
                                >
                                    <i className="fa-solid fa-check-double mr-1 text-blue-600"></i>
                                    Chọn tất cả
                                </button>
                                <button
                                    type="button"
                                    onClick={handleDeselectAll}
                                    className="px-3 py-1 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-medium rounded-lg transition-colors cursor-pointer"
                                >
                                    <i className="fa-solid fa-rotate-left mr-1 text-slate-500"></i>
                                    Bỏ chọn
                                </button>
                            </div>
                        </div>

                        {loadingTree ? (
                            <div className="min-h-[400px] flex flex-col items-center justify-center text-slate-400">
                                <i className="fa-solid fa-circle-notch fa-spin text-3xl text-blue-600 mb-3"></i>
                                <p className="text-sm">Đang tải cây quyền hệ thống...</p>
                            </div>
                        ) : (
                            <div className="bg-slate-50 p-6 rounded border border-slate-200 min-h-[400px] overflow-x-auto">
                                <div className="flex justify-center flex-wrap gap-12 items-start min-w-max mx-auto">
                                    {treeData.map((colData, colIndex) => (
                                        <div
                                            key={colData.column ?? colIndex}
                                            className="space-y-4 min-w-[260px]"
                                        >
                                            {colData.listPermission?.map((rootNode) => (
                                                <TreeNode
                                                    key={rootNode.name}
                                                    node={rootNode}
                                                    checkedNames={checkedNames}
                                                    handleCheck={handleCheck}
                                                />
                                            ))}
                                        </div>
                                    ))}
                                </div>
                            </div>
                        )}
                    </div>

                    {/* NÚT THAO TÁC CUỐI TRANG */}
                    <div className="flex items-center justify-end space-x-3 pt-2 pb-6">
                        <button
                            type="button"
                            onClick={() => setCurrentView("LIST")}
                            disabled={submitting}
                            className="px-5 py-2.5 bg-slate-100 hover:bg-slate-200 text-slate-700 font-medium text-sm rounded-xl transition-colors cursor-pointer"
                        >
                            Hủy bỏ
                        </button>
                        <button
                            type="submit"
                            disabled={submitting}
                            className="px-6 py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-medium text-sm rounded-xl shadow-sm hover:shadow transition-all cursor-pointer flex items-center"
                        >
                            {submitting ? (
                                <>
                                    <i className="fa-solid fa-circle-notch fa-spin mr-2"></i>
                                    Đang lưu gói...
                                </>
                            ) : (
                                <>
                                    <i className="fa-solid fa-check mr-2"></i>
                                    {isEdit ? "Lưu thay đổi" : "Hoàn tất tạo gói"}
                                </>
                            )}
                        </button>
                    </div>
                </form>

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

    // =========================================================================
    // MÀN HÌNH 1: DANH SÁCH GÓI DẠNG LƯỚI (GRID CARDS)
    // =========================================================================
    return (
        <main className="flex-grow max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 w-full">
            {/* Header: Tiêu đề + Thống kê + Nút Thêm */}
            <div className="flex flex-col md:flex-row md:items-end justify-between gap-4 mb-8">
                <div>
                    <div className="flex items-center space-x-3">
                        <div className="w-10 h-10 rounded-xl bg-blue-100 text-blue-600 flex items-center justify-center font-bold">
                            <i className="fa-solid fa-cubes text-lg"></i>
                        </div>
                        <div>
                            <h1 className="text-2xl font-bold text-slate-900">Quản lý Gói dịch vụ</h1>
                            <p className="text-sm text-slate-500 mt-0.5">
                                Xem và quản lý các gói cước bán ra với các quyền & tính năng hệ thống
                            </p>
                        </div>
                    </div>
                </div>

                {/* Nút Thêm mới - Nhấn vào sẽ chuyển sang giao diện tạo gói */}
                <div className="flex items-center space-x-3">
                    <button
                        type="button"
                        onClick={handleOpenCreate}
                        className="inline-flex items-center px-4 py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-medium text-sm rounded-xl shadow-sm hover:shadow transition-all duration-200 cursor-pointer"
                    >
                        <i className="fa-solid fa-plus mr-2 text-xs"></i>
                        <span>Thêm gói mới</span>
                    </button>
                </div>
            </div>

            {/* Thanh công cụ tìm kiếm và lọc */}
            <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm mb-6 flex flex-col sm:flex-row items-center justify-between gap-4">
                <div className="relative w-full sm:w-80">
                    <span className="absolute inset-y-0 left-0 flex items-center pl-3.5 pointer-events-none text-slate-400">
                        <i className="fa-solid fa-magnifying-glass text-sm"></i>
                    </span>
                    <input
                        type="text"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        placeholder="Tìm kiếm gói theo tên, mô tả..."
                        className="w-full pl-10 pr-4 py-2 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all bg-slate-50"
                    />
                </div>

                <div className="flex items-center space-x-2 w-full sm:w-auto justify-end">
                    <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider hidden sm:inline">
                        Trạng thái:
                    </span>
                    <div className="inline-flex bg-slate-100 p-1 rounded-xl text-xs font-medium">
                        <button
                            type="button"
                            onClick={() => setStatusFilter("all")}
                            className={`px-3 py-1.5 rounded-lg transition-all cursor-pointer ${
                                statusFilter === "all"
                                    ? "bg-white text-slate-800 shadow-sm font-semibold"
                                    : "text-slate-500 hover:text-slate-800"
                            }`}
                        >
                            Tất cả ({packages.length})
                        </button>
                        <button
                            type="button"
                            onClick={() => setStatusFilter("active")}
                            className={`px-3 py-1.5 rounded-lg transition-all cursor-pointer ${
                                statusFilter === "active"
                                    ? "bg-white text-emerald-700 shadow-sm font-semibold"
                                    : "text-slate-500 hover:text-slate-800"
                            }`}
                        >
                            Hoạt động ({packages.filter((p) => p.status).length})
                        </button>
                        <button
                            type="button"
                            onClick={() => setStatusFilter("inactive")}
                            className={`px-3 py-1.5 rounded-lg transition-all cursor-pointer ${
                                statusFilter === "inactive"
                                    ? "bg-white text-rose-700 shadow-sm font-semibold"
                                    : "text-slate-500 hover:text-slate-800"
                            }`}
                        >
                            Tạm ngừng ({packages.filter((p) => !p.status).length})
                        </button>
                    </div>
                </div>
            </div>

            {/* Trạng thái Loading */}
            {loading && (
                <div className="py-20 flex flex-col items-center justify-center text-slate-400">
                    <i className="fa-solid fa-circle-notch fa-spin text-3xl text-blue-600 mb-3"></i>
                    <p className="text-sm">Đang tải danh sách các gói dịch vụ...</p>
                </div>
            )}

            {/* Trạng thái Không có dữ liệu */}
            {!loading && filteredPackages.length === 0 && (
                <div className="bg-white rounded-2xl border border-slate-200 p-12 text-center shadow-sm">
                    <div className="w-16 h-16 bg-slate-100 rounded-full flex items-center justify-center mx-auto mb-4 text-slate-400">
                        <i className="fa-solid fa-box-open text-2xl"></i>
                    </div>
                    <h3 className="text-base font-semibold text-slate-800 mb-1">
                        Không tìm thấy gói nào
                    </h3>
                    <p className="text-sm text-slate-500 max-w-sm mx-auto mb-6">
                        {searchTerm || statusFilter !== "all"
                            ? "Không có gói nào phù hợp với bộ lọc tìm kiếm hiện tại."
                            : "Hiện tại hệ thống chưa có gói dịch vụ nào được tạo."}
                    </p>
                    <button
                        type="button"
                        onClick={handleOpenCreate}
                        className="inline-flex items-center px-4 py-2 bg-blue-50 text-blue-600 hover:bg-blue-100 font-medium text-sm rounded-xl transition-colors cursor-pointer"
                    >
                        <i className="fa-solid fa-plus mr-2 text-xs"></i>
                        Tạo gói đầu tiên
                    </button>
                </div>
            )}

            {/* DANH SÁCH DẠNG LƯỚI (GRID CARDS) */}
            {!loading && filteredPackages.length > 0 && (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {filteredPackages.map((pkg) => (
                        <div
                            key={pkg.id}
                            className="bg-white rounded-2xl border border-slate-200/90 shadow-sm hover:shadow-md hover:border-blue-200 transition-all duration-200 flex flex-col overflow-hidden group"
                        >
                            {/* Card Header & Badges */}
                            <div className="p-6 pb-4 border-b border-slate-100">
                                <div className="flex items-start justify-between mb-3">
                                    <div className="flex items-center space-x-2">
                                        <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-blue-500 to-indigo-500 text-white flex items-center justify-center shadow-sm">
                                            <i className="fa-solid fa-crown text-sm"></i>
                                        </div>
                                        <span className="text-xs font-mono font-medium text-slate-400 bg-slate-100 px-2.5 py-1 rounded-md">
                                            ID #{pkg.id}
                                        </span>
                                    </div>

                                    {/* Trạng thái gói */}
                                    {pkg.status ? (
                                        <span className="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium bg-emerald-50 text-emerald-700 border border-emerald-200">
                                            <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 mr-1.5 animate-pulse"></span>
                                            Đang mở bán
                                        </span>
                                    ) : (
                                        <span className="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium bg-rose-50 text-rose-700 border border-rose-200">
                                            <span className="w-1.5 h-1.5 rounded-full bg-rose-400 mr-1.5"></span>
                                            Tạm khóa
                                        </span>
                                    )}
                                </div>

                                {/* Tên gói */}
                                <h3 className="text-lg font-bold text-slate-900 group-hover:text-blue-600 transition-colors">
                                    {pkg.name || "Gói không có tên"}
                                </h3>

                                {/* Mô tả gói */}
                                <p className="text-sm text-slate-500 mt-1 line-clamp-2 min-h-[40px]">
                                    {pkg.description || "Chưa có thông tin mô tả chi tiết cho gói này."}
                                </p>
                            </div>

                            {/* Card Body: Giá & Quyền hạn/Tính năng */}
                            <div className="p-6 pt-5 flex-grow flex flex-col justify-between">
                                <div>
                                    <div className="text-xs font-medium text-slate-400 uppercase tracking-wider mb-1">
                                        Giá bán
                                    </div>
                                    <div className="flex items-baseline space-x-1.5 mb-5">
                                        <span className="text-3xl font-extrabold text-slate-900 tracking-tight">
                                            {formatPrice(pkg.price)}
                                        </span>
                                        <span className="text-xs text-slate-400 font-normal">
                                            / Tháng
                                        </span>
                                    </div>

                                    {/* Gợi ý quyền / chức năng trong gói */}
                                    {/* <div className="bg-slate-50 rounded-xl p-3 border border-slate-100 mb-2">
                                        <div className="flex items-center text-xs font-semibold text-slate-600 mb-2">
                                            <i className="fa-solid fa-shield-halved text-blue-500 mr-1.5"></i>
                                            Quyền hạn & tính năng đi kèm
                                        </div>
                                        <ul className="text-xs text-slate-500 space-y-1.5">
                                            <li className="flex items-center">
                                                <i className="fa-solid fa-check text-emerald-500 mr-2 text-[10px]"></i>
                                                Tích hợp đầy đủ quyền nghiệp vụ
                                            </li>
                                            <li className="flex items-center">
                                                <i className="fa-solid fa-check text-emerald-500 mr-2 text-[10px]"></i>
                                                Hỗ trợ kỹ thuật ưu tiên
                                            </li>
                                        </ul>
                                    </div> */}
                                </div>

                                {/* Card Footer: Action buttons (Xóa, Sửa) */}
                                <div className="mt-5 pt-4 border-t border-slate-100 flex items-center justify-between gap-3">
                                    <button
                                        type="button"
                                        onClick={() => handleOpenDelete(pkg)}
                                        className="flex-1 inline-flex items-center justify-center px-3 py-2 text-xs font-medium text-rose-600 bg-rose-50 hover:bg-rose-100 rounded-lg transition-colors cursor-pointer border border-rose-200/60"
                                        title="Xóa gói"
                                    >
                                        <i className="fa-solid fa-trash-can mr-1.5 text-xs"></i>
                                        <span>Xóa</span>
                                    </button>

                                    <button
                                        type="button"
                                        onClick={() => handleOpenEdit(pkg)}
                                        className="flex-1 inline-flex items-center justify-center px-3 py-2 text-xs font-medium text-blue-600 bg-blue-50 hover:bg-blue-100 rounded-lg transition-colors cursor-pointer border border-blue-200/60"
                                        title="Sửa thông tin gói"
                                    >
                                        <i className="fa-solid fa-pen-to-square mr-1.5 text-xs"></i>
                                        <span>Sửa</span>
                                    </button>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}

            {/* Delete Modal */}
            <div className={`fixed inset-0 bg-slate-900/50 backdrop-blur-sm z-50 flex items-center justify-center transition-opacity duration-300 ${isDeleteModalOpen ? 'opacity-100' : 'opacity-0 pointer-events-none'}`}>
                <div className={`bg-white rounded-2xl shadow-xl w-full max-w-sm mx-4 overflow-hidden transition-transform duration-300 ${isDeleteModalOpen ? 'scale-100' : 'scale-95'}`}>
                    <div className="p-6 text-center">
                        <div className="w-16 h-16 rounded-full bg-red-100 flex items-center justify-center mx-auto mb-4 text-danger">
                            <i className="fa-solid fa-triangle-exclamation text-3xl"></i>
                        </div>
                        <h3 className="text-lg font-bold text-slate-900 mb-2">Xác nhận xóa?</h3>
                        <p className="text-slate-500 text-sm mb-6">Bạn có chắc chắn muốn xóa gói dịch vụ này không? Hành động này không thể hoàn tác.</p>
                        <div className="flex justify-center space-x-3">
                            <button onClick={() => setIsDeleteModalOpen(false)} className="px-4 py-2 text-sm font-medium text-slate-700 bg-slate-100 rounded-lg hover:bg-slate-200 w-full cursor-pointer">Hủy bỏ</button>
                            <button onClick={confirmDelete} className="px-4 py-2 text-sm font-medium text-white bg-danger rounded-lg hover:bg-red-600 w-full cursor-pointer">Đồng ý Xóa</button>
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
