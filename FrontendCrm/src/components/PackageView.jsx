import { useState, useEffect } from "react";
import { getPackages } from "../api/packageApi";

export default function PackageView() {
    const [packages, setPackages] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState("");
    const [statusFilter, setStatusFilter] = useState("all");

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
        if (price === undefined || price === null) return "0 ₫";
        return new Intl.NumberFormat("vi-VN", {
            style: "currency",
            currency: "VND",
        }).format(price);
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

                {/* Nút Thêm mới - Vẽ giao diện, không bắt sự kiện */}
                <div className="flex items-center space-x-3">
                    <button
                        type="button"
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
                            className={`px-3 py-1.5 rounded-lg transition-all ${
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
                            className={`px-3 py-1.5 rounded-lg transition-all ${
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
                            className={`px-3 py-1.5 rounded-lg transition-all ${
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
                                            / chu kỳ
                                        </span>
                                    </div>

                                    {/* Gợi ý quyền / chức năng trong gói */}
                                    <div className="bg-slate-50 rounded-xl p-3 border border-slate-100 mb-2">
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
                                    </div>
                                </div>

                                {/* Card Footer: Action buttons (Xóa, Sửa) - Vẽ ra nhưng không bắt sự kiện */}
                                <div className="mt-5 pt-4 border-t border-slate-100 flex items-center justify-between gap-3">
                                    {/* Nút Xóa - Vẽ ra nhưng không bắt sự kiện */}
                                    <button
                                        type="button"
                                        className="flex-1 inline-flex items-center justify-center px-3 py-2 text-xs font-medium text-rose-600 bg-rose-50 hover:bg-rose-100 rounded-lg transition-colors cursor-pointer border border-rose-200/60"
                                        title="Xóa gói"
                                    >
                                        <i className="fa-solid fa-trash-can mr-1.5 text-xs"></i>
                                        <span>Xóa</span>
                                    </button>

                                    {/* Nút Sửa - Vẽ ra nhưng không bắt sự kiện */}
                                    <button
                                        type="button"
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
        </main>
    );
}
