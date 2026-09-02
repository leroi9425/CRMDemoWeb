import { useState, useEffect } from "react";
import { getCompanies, createCompany, updateCompany, deleteCompany, getCompanyDetail, getCompanyPerPage } from "../api/CompanyApi";
import { useAuth } from "../context/AuthContext";
export default function CompanyView() {
    const { auth } = useAuth();
    const [Companies, setCompanies] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
    const [editingCompany, setEditingCompany] = useState(null);
    const [deletingId, setDeletingId] = useState(null);
    const [toasts, setToasts] = useState([]);
    const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
    const [selectedCompanyDetail, setSelectedCompanyDetail] = useState(null);
    const [companyUsers, setCompanyUsers] = useState([]);
    const [detailLoading, setDetailLoading] = useState(false);
    const [formData, setFormData] = useState({
        name:"", description:"",maxUser:""
    });

    // phân trang cho oách
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPage, setTotalPage] = useState(1);


    const fetchCompanies = async () => {
        try {
            const res = await getCompanyPerPage(currentPage);
            setCompanies(res.data.content);
            setTotalPage(res.data.totalPages);
        } catch (error) {
            showToast("Lỗi khi tải danh sách công ty", "danger");
        }
    };

    useEffect(() => {
        fetchCompanies();
        console.log("tong so trang: ", totalPage);
    }, [currentPage]);

    const showToast = (message, type = "success") => {
        const id = Date.now();
        setToasts(prev => [...prev, { id, message, type }]); 
        setTimeout(() => {
            setToasts(prev => prev.filter(t => t.id !== id));
        }, 3000);
    };

    const handleViewDetail = async (company) => {
        setSelectedCompanyDetail(company);
        setIsDetailModalOpen(true);
        setDetailLoading(true);
        try {
            const res = await getCompanyDetail(company.id);
            setCompanyUsers(res.data);
        } catch (error) {
            showToast("Lỗi khi tải danh sách nhân viên", "danger");
            setCompanyUsers([]);
        } finally {
            setDetailLoading(false);
        }
    };

    const openModal = (Company = null) => {
        setEditingCompany(Company);
        if (Company) {
            setFormData({
                name: Company.name || "",
                description: Company.description || "",
                maxUser: Company.maxUser || ""
            });
        } else {
            setFormData({name: "", description: "", maxUser: ""});
        }
        setIsModalOpen(true);
    };

    const closeModal = () => {
        setIsModalOpen(false);
        setTimeout(() => setEditingCompany(null), 300);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        console.log(formData);
        try {
            if (editingCompany) {
                console.log("form sua");
                await updateCompany(editingCompany.id, formData);
                showToast("Đã cập nhật thông tin thành công!", "success");
            } else {
                console.log("chay zo them ne");
                await createCompany(formData);
                console.log("them cong ty moi va day la form data: ");
                console.log(formData);
                showToast("Đã thêm công ty mới!", "success");
            }
            closeModal();
            fetchCompanies();
        } catch (err) {
            console.log(err);
            showToast(err.response?.data?.message || "Đã xảy ra lỗi", "danger");
        }
    };

    const confirmDelete = async () => {
        try {
            await deleteCompany(deletingId);
            showToast("Đã xóa công ty.", "danger");
            setIsDeleteModalOpen(false);
            fetchCompanies();
        } catch (error) {
            showToast("Lỗi khi xóa", "danger");
        }
    };

    const filteredCompanies = Companies.filter(c => 
        c.name?.toLowerCase().includes(searchTerm.toLowerCase()) || 
        c.phoneNumber?.includes(searchTerm) ||
        c.email?.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const getInitials = (name) => name?.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase() || 'NA';

    return (
        <main className="flex-grow max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 w-full">
            
            <div className="flex justify-between items-end mb-6">
                <div>
                    <h1 className="text-2xl font-bold text-slate-900">Danh sách Công ty</h1>
                    <p className="text-sm text-slate-500 mt-1">Quản lý thông tin và giới hạn số lượng user của công ty.</p>
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
                            placeholder="Tìm kiếm công ty..."
                        />
                    </div>
                    {auth?.permissions?.includes('THEM_CONG_TY') && (
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
                                <th className="px-6 py-4">Công ty</th>
                                <th className="px-6 py-4">Mô tả</th>
                                <th className="px-6 py-4">Max user</th>
                                <th className="px-6 py-4 text-right">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-200 text-sm">
                            {filteredCompanies.map(Company => (
                                <tr key={Company.id} onClick={() => handleViewDetail(Company)} className="hover:bg-slate-50 transition-colors group cursor-pointer">
                                    <td className="px-6 py-4 whitespace-nowrap text-slate-500 font-mono text-xs">#{Company.id}</td>
                                    <td className="px-6 py-4 whitespace-nowrap">
                                        <div className="flex items-center">
                                            <div className="flex-shrink-0 h-10 w-10 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center font-bold text-sm">
                                                {getInitials(Company.name)}
                                            </div>
                                            <div className="ml-4">
                                                <div className="font-medium text-slate-900 group-hover:text-primary transition-colors">{Company.name}</div>
                                                <div className="text-slate-500 text-xs">ID: #{Company.id}</div>
                                            </div>
                                        </div>
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap">
                                        <div className="text-slate-700"><i className="text-slate-400 mr-2 w-4"></i>{Company.description}</div>
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap">
                                        <div className="text-slate-700"><i className="text-slate-400 mr-2 w-4"></i>{Company.maxUser}</div>
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap text-right font-medium">
                                        <div className="flex justify-end space-x-2 opacity-0 group-hover:opacity-100 transition-opacity">
                                            {auth?.permissions?.includes("THEM_CONG_TY") && (
                                                <button onClick={(e) => { e.stopPropagation(); openModal(Company); }} className="p-2 text-primary hover:bg-blue-50 rounded-lg transition-colors" title="Sửa">
                                                    <i className="fa-solid fa-pen-to-square"></i>
                                                </button>
                                            )}
                                            {auth?.permissions?.includes("XOA_CONG_TY") && (
                                                <button onClick={(e) => { e.stopPropagation(); setDeletingId(Company.id); setIsDeleteModalOpen(true); }} className="p-2 text-danger hover:bg-red-50 rounded-lg transition-colors" title="Xóa">
                                                    <i className="fa-solid fa-trash-can"></i>
                                                </button>
                                            )}
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                    
                    {filteredCompanies.length === 0 && (
                        <div className="flex flex-col items-center justify-center py-16 text-slate-500">
                            <div className="bg-slate-100 p-4 rounded-full mb-4">
                                <i className="fa-solid fa-folder-open text-3xl text-slate-400"></i>
                            </div>
                            <p className="text-lg font-medium text-slate-700 mb-1">Không tìm thấy công ty nào</p>
                            <button onClick={() => openModal()} className="text-primary hover:text-blue-700 font-medium">
                                + Thêm công ty ngay
                            </button>
                        </div>
                    )}
                </div>
                
                {/* Thanh Phân Trang (Chỉ có UI) */}
                <div className="px-6 py-4 border-t border-slate-200 flex items-center justify-end bg-slate-50">
                    <div className="flex items-center space-x-2">
                        {/* Nút Trước (Ví dụ cách thêm disabled khi ở trang 0) */}
                        <button
                        disabled={currentPage === 0}
                        onClick={() => setCurrentPage(currentPage-1)} 
                        className="px-3 py-1.5 text-sm font-medium text-slate-500 bg-white border border-slate-300 rounded-md hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors">
                            <i className="fa-solid fa-chevron-left mr-1"></i> Trước
                        </button>
                        
                        {/* Khối các số trang */}
                        <div className="flex items-center space-x-1 hidden sm:flex">
                            {(() => {
                                const delta = 1; // số trang hiện quanh currentPage
                                const pages = [];
                                for (let i = 0; i < totalPage; i++) {
                                    if (
                                        i === 0 ||
                                        i === totalPage - 1 ||
                                        (i >= currentPage - delta && i <= currentPage + delta)
                                    ) {
                                        pages.push(i);
                                    }
                                }

                                const items = [];
                                let prevPage = null;
                                for (const page of pages) {
                                    if (prevPage !== null && page - prevPage > 1) {
                                        items.push(
                                            <span key={`dots-${page}`} className="px-2 text-slate-400">...</span>
                                        );
                                    }
                                    items.push(
                                        <button
                                            key={page}
                                            onClick={() => setCurrentPage(page)}
                                            className={
                                                currentPage === page
                                                    ? "px-3 py-1.5 text-sm font-medium text-white bg-primary border border-primary rounded-md shadow-sm"
                                                    : "px-3 py-1.5 text-sm font-medium text-slate-700 bg-white border border-slate-300 rounded-md hover:bg-slate-50 transition-colors"
                                            }
                                        >
                                            {page + 1}
                                        </button>
                                    );
                                    prevPage = page;
                                }
                                return items;
                            })()}
                        </div>

                        {/* Nút Sau */}
                        <button
                        disabled={currentPage === totalPage-1}
                        onClick={() => setCurrentPage(currentPage+1)}
                        className="px-3 py-1.5 text-sm font-medium text-slate-500 bg-white border border-slate-300 rounded-md hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors">
                            Sau <i className="fa-solid fa-chevron-right ml-1"></i>
                        </button>
                    </div>
                </div>
            </div>

            {/* Modal Edit/Add */}
            <div className={`fixed inset-0 bg-slate-900/50 backdrop-blur-sm z-50 flex items-center justify-center transition-opacity duration-300 ${isModalOpen ? 'opacity-100' : 'opacity-0 pointer-events-none'}`}>
                <div className={`bg-white rounded-2xl shadow-xl w-full max-w-lg mx-4 overflow-hidden flex flex-col max-h-[90vh] transition-transform duration-300 ${isModalOpen ? 'scale-100' : 'scale-95'}`}>
                    <div className="px-6 py-4 border-b border-slate-200 flex justify-between items-center bg-slate-50">
                        <h3 className="text-lg font-semibold text-slate-900">{editingCompany ? "Sửa Công ty" : "Thêm Công ty Mới"}</h3>
                        <button onClick={closeModal} className="text-slate-400 hover:text-slate-600 transition-colors p-1 rounded-md hover:bg-slate-200">
                            <i className="fa-solid fa-xmark text-xl"></i>
                        </button>
                    </div>
                    <div className="px-6 py-4 overflow-y-auto">
                        <form id="CompanyForm" onSubmit={handleSubmit} className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-slate-700 mb-1">Tên công ty <span className="text-danger">*</span></label>
                                <input type="text" required value={formData.name} onChange={e => setFormData({...formData, name: e.target.value})} className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"/>
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-slate-700 mb-1">User tối đa <span className="text-danger">*</span></label>
                                <input type="number" required value={formData.maxUser} onChange={e => setFormData({...formData, maxUser: e.target.value})} className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"/>
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-slate-700 mb-1">Mô tả <span className="text-danger">*</span></label>
                                <input type="text" required value={formData.description} onChange={e => setFormData({...formData, description: e.target.value})} className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"/>
                            </div>
                        </form>
                    </div>
                    <div className="px-6 py-4 border-t border-slate-200 bg-slate-50 flex justify-end space-x-3">
                        <button type="button" onClick={closeModal} className="px-4 py-2 text-sm font-medium text-slate-700 bg-white border border-slate-300 rounded-lg hover:bg-slate-50 transition-colors">Hủy</button>
                        <button type="submit" form="CompanyForm" className="px-4 py-2 text-sm font-medium text-white bg-primary rounded-lg hover:bg-blue-600 transition-colors shadow-sm flex items-center">
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
                        <p className="text-slate-500 text-sm mb-6">Bạn có chắc chắn muốn xóa công ty này không? Hành động này không thể hoàn tác.</p>
                        <div className="flex justify-center space-x-3">
                            <button onClick={() => setIsDeleteModalOpen(false)} className="px-4 py-2 text-sm font-medium text-slate-700 bg-slate-100 rounded-lg hover:bg-slate-200 w-full">Hủy bỏ</button>
                            <button onClick={confirmDelete} className="px-4 py-2 text-sm font-medium text-white bg-danger rounded-lg hover:bg-red-600 w-full">Đồng ý Xóa</button>
                        </div>
                    </div>
                </div>
            </div>

            {/* Detail Modal */}
            <div className={`fixed inset-0 bg-slate-900/50 backdrop-blur-sm z-50 flex items-center justify-center transition-opacity duration-300 ${isDetailModalOpen ? 'opacity-100' : 'opacity-0 pointer-events-none'}`} onClick={() => setIsDetailModalOpen(false)}>
                <div className={`bg-white rounded-2xl shadow-xl w-full max-w-4xl mx-4 overflow-hidden flex flex-col max-h-[90vh] transition-transform duration-300 ${isDetailModalOpen ? 'scale-100' : 'scale-95'}`} onClick={e => e.stopPropagation()}>
                    <div className="px-6 py-4 border-b border-slate-200 flex justify-between items-center bg-slate-50">
                        <h3 className="text-lg font-semibold text-slate-900">Danh sách nhân viên: {selectedCompanyDetail?.name}</h3>
                        <button onClick={() => setIsDetailModalOpen(false)} className="text-slate-400 hover:text-slate-600 transition-colors p-1 rounded-md hover:bg-slate-200">
                            <i className="fa-solid fa-xmark text-xl"></i>
                        </button>
                    </div>
                    <div className="p-6 overflow-y-auto bg-slate-50">
                        {detailLoading ? (
                            <div className="flex justify-center py-8">
                                <i className="fa-solid fa-circle-notch fa-spin text-3xl text-primary"></i>
                            </div>
                        ) : companyUsers.length > 0 ? (
                            <div className="bg-white border border-slate-200 rounded-lg overflow-hidden shadow-sm">
                                <table className="w-full text-left text-sm whitespace-nowrap">
                                    <thead className="bg-slate-100 border-b border-slate-200 text-slate-600 font-semibold">
                                        <tr>
                                            <th className="px-6 py-4">ID</th>
                                            <th className="px-6 py-4">Tài khoản</th>
                                            <th className="px-6 py-4">Họ tên</th>
                                            <th className="px-6 py-4">Email</th>
                                        </tr>
                                    </thead>
                                    <tbody className="divide-y divide-slate-100">
                                        {companyUsers.map(u => (
                                            <tr key={u.id} className="hover:bg-slate-50">
                                                <td className="px-6 py-4 text-slate-500 font-mono text-xs">#{u.id}</td>
                                                <td className="px-6 py-4 font-medium text-slate-800">{u.username}</td>
                                                <td className="px-6 py-4 text-slate-600">{u.fullName || u.full_name || 'N/A'}</td>
                                                <td className="px-6 py-4 text-slate-500">{u.email}</td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        ) : (
                            <div className="text-center py-12 text-slate-500 bg-white rounded-lg border border-slate-200 border-dashed">
                                <i className="fa-solid fa-users-slash text-4xl mb-3 text-slate-300"></i>
                                <p className="text-lg">Công ty này chưa có nhân viên nào.</p>
                            </div>
                        )}
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
