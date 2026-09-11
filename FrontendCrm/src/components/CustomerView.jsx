import { useState, useEffect } from "react";
import { getCustomers, createCustomer, updateCustomer, deleteCustomer, getCustomersPage, getCustomerDetail, filterCustomersPage, exportCustomer, getExportTemplates, saveExportTemplate } from "../api/customerApi";
import { useAuth } from "../context/AuthContext";
import ExcelInOutPut from "./ExcelInOutPut";
import ExportExcelModal from "./ExportExcelModal";

export default function CustomerView() {
    const [customers, setCustomers] = useState([]);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
    const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
    const [detailCustomer, setDetailCustomer] = useState(null);
    const [editingCustomer, setEditingCustomer] = useState(null);
    const [deletingId, setDeletingId] = useState(null);
    const [showFilter, setShowFilter] = useState(false);
    const [filterData, setFilterData] = useState({
        search: "",
        customerName: "",
        phoneNumber: "",
        location: "",
        gender: "",
        fromDateOfBirth: "",
        toDateOfBirth: ""
    });
    const [toasts, setToasts] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPage, setTotalPage] = useState(0);
    const { auth } = useAuth();
    const [formData, setFormData] = useState({
        name: "", email: "", phoneNumber: "", dateOfBirth: "", location: "", gender: true, companyId: "", userId: ""
    });
    
    // State để điều khiển màn hình Import Excel
    const [isImportMode, setIsImportMode] = useState(false);

    // State cho Modal Export
    const [isExportModalOpen, setIsExportModalOpen] = useState(false);


    const fetchCustomers = async () => {
        try {
            console.log("filter data" + filterData);
            const payload = {
                ...filterData,
                gender: filterData.gender === "" ? null : filterData.gender === "true",
            };
            console.log("SEARCH FE:", payload.search);
            console.log("SEARCH FE JSON:", JSON.stringify(payload.search));
            const res = await filterCustomersPage(currentPage, payload);
            console.log("res: "+ res)
            setCustomers(res.data.content);
            setTotalPage(res.data.totalPages);
        } catch (error) {
            showToast("Lỗi khi tải danh sách khách hàng", "danger");
        }
    };

    useEffect(() => {
        fetchCustomers();
    }, [currentPage]);

    const showToast = (message, type = "success") => {
        const id = Date.now() + Math.random().toString(36).substr(2, 9);
        setToasts(prev => [...prev, { id, message, type }]);
        setTimeout(() => {
            setToasts(prev => prev.filter(t => t.id !== id));
        }, 3000);
    };

    const formatPhoneNumber = (phone) => {
        if(!phone) return "";

        const cleanPhone = phone.replace(/\D/g, '');
        const match = cleanPhone.match(/^(\d{3})(\d{3})(\d{4,})$/);
        return match ? `${match[1]}.${match[2]}.${match[3]}` : phone;
    }

    const openModal = (customer = null) => {
        setEditingCustomer(customer);
        if (customer) {
            setFormData({
                name: customer.name || "",
                email: customer.email || "",
                phoneNumber: customer.phoneNumber || "",
                dateOfBirth: customer.dateOfBirth || "",
                location: customer.location || "",
                gender: customer.gender,
                companyId: customer.companyId || "",
                userId: customer.userId || ""
            });
        } else {
            setFormData({
                name: "", email: "", phoneNumber: "", dateOfBirth: "", location: "", gender: true, companyId: "", userId: ""
            });
        }
        setIsModalOpen(true);
    };

    const closeModal = () => {
        setIsModalOpen(false);
        setEditingCustomer(null);
    };

    const openDetailModal = async (id) => {
        try {
            const res = await getCustomerDetail(id);
            setDetailCustomer(res.data);
            setIsDetailModalOpen(true);
        } catch (error) {
            showToast("Không thể tải chi tiết khách hàng", "danger");
        }
    };

    const closeDetailModal = () => {
        setIsDetailModalOpen(false);
        setDetailCustomer(null);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (editingCustomer) {
                await updateCustomer(editingCustomer.id, formData);
                showToast("Đã cập nhật thông tin thành công!", "success");
            } else {
                console.log(formData);
                await createCustomer(formData);
                showToast("Đã thêm khách hàng mới!", "success");
            }
            closeModal();
            fetchCustomers();
        } catch (err) {
            showToast(err.response?.data?.message || "Đã xảy ra lỗi", "danger");
        }
    };

    const confirmDelete = async () => {
        try {
            await deleteCustomer(deletingId);
            showToast("Đã xóa khách hàng.", "danger");
            setIsDeleteModalOpen(false);
            fetchCustomers();
        } catch (error) {
            showToast("Lỗi khi xóa", "danger");
        }
    };

    const filteredCustomers = customers;

    const getInitials = (name) => name?.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase() || 'NA';

    const handleSort = (field) => {
        let newDir = "DESC";
        if (filterData.sortField === field) {
            if (filterData.sortDirection === "DESC") newDir = "ASC";
            else if (filterData.sortDirection === "ASC") newDir = "";
        }
        
        const newFilterData = {
            ...filterData,
            sortField: newDir === "" ? "" : field,
            sortDirection: newDir
        };
        setFilterData(newFilterData);
        
        // Gọi API ngay lập tức để Sort ăn liền
        const payload = {
            ...newFilterData,
            gender: newFilterData.gender === "" ? null : newFilterData.gender === "true",
            fromDateOfBirth: newFilterData.fromDateOfBirth || null,
            toDateOfBirth: newFilterData.toDateOfBirth || null
        };
        
        filterCustomersPage(currentPage, payload)
            .then(res => {
                setCustomers(res.data.content);
                setTotalPage(res.data.totalPages);
            })
            .catch(() => showToast("Lỗi khi sắp xếp", "danger"));
    };

    const openExportModal = () => {
        setIsExportModalOpen(true);
    };

    const renderSortIcon = (field) => {
        if (filterData.sortField !== field || !filterData.sortDirection) {
            return <i className="fa-solid fa-sort ml-1 text-slate-300"></i>;
        }
        if (filterData.sortDirection === "DESC") {
            return <i className="fa-solid fa-sort-down ml-1 text-primary"></i>;
        }
        return <i className="fa-solid fa-sort-up ml-1 text-primary"></i>;
    };
    if (isImportMode) {
        return (
            <main className="flex-grow max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 w-full">
                <div className="mb-6 flex items-center">
                    <button 
                        onClick={() => {
                            setIsImportMode(false);
                            fetchCustomers();
                        }} 
                        className="text-slate-500 hover:text-slate-700 font-medium flex items-center transition-colors bg-white px-4 py-2 border border-slate-300 rounded-lg shadow-sm"
                    >
                        <i className="fa-solid fa-arrow-left mr-2"></i> Quay lại danh sách
                    </button>
                </div>
                <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
                    <ExcelInOutPut onImportSuccess={() => {
                        setIsImportMode(false);
                        fetchCustomers();
                    }} />
                </div>
            </main>
        );
    }

    if (isImportMode) {
        return (
            <main className="flex-grow max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 w-full">
                <div className="mb-6 flex items-center">
                    <button 
                        onClick={() => {
                            setIsImportMode(false);
                            fetchCustomers();
                        }} 
                        className="text-slate-500 hover:text-slate-700 font-medium flex items-center transition-colors bg-white px-4 py-2 border border-slate-300 rounded-lg shadow-sm"
                    >
                        <i className="fa-solid fa-arrow-left mr-2"></i> Quay lại danh sách
                    </button>
                </div>
                <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
                    <ExcelInOutPut onImportSuccess={() => {
                        setIsImportMode(false);
                        fetchCustomers();
                    }} />
                </div>
            </main>
        );
    }

    return (
        <main className="flex-grow max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 w-full">
            <div className="flex justify-between items-end mb-6">
                <div>
                    <h1 className="text-2xl font-bold text-slate-900">Danh sách Khách hàng</h1>
                    <p className="text-sm text-slate-500 mt-1">Quản lý thông tin liên hệ và chi tiết khách hàng.</p>
                </div>
                <div className="flex flex-col items-end space-y-3">
                    {/* Cụm Search nằm bên trên */}
                    <div className="flex items-center space-x-2">
                        <div className="relative hidden md:block">
                            <span className="absolute inset-y-0 left-0 flex items-center pl-3">
                                <i className="fa-solid fa-search text-slate-400"></i>
                            </span>
                            <input 
                                type="text" 
                                value={filterData.search}
                                onChange={(e) => setFilterData({...filterData, search: e.target.value})}
                                className="pl-10 pr-4 py-2 border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent transition-shadow w-64 bg-slate-50" 
                                placeholder="Tìm kiếm nhanh..."
                            />
                        </div>
                        <button type="button" 
                        onClick={() => {setCurrentPage(0); fetchCustomers();}}
                        className="bg-primary hover:bg-blue-600 text-white text-sm font-medium py-2 px-4 rounded-lg shadow-sm transition-colors duration-200 whitespace-nowrap">
                            Tìm kiếm
                        </button>
                        <button 
                        onClick={() => setShowFilter(!showFilter)} 
                        className="bg-white border border-slate-300 hover:bg-slate-50 text-slate-700 font-medium py-2 px-4 rounded-lg shadow-sm transition-colors duration-200 flex items-center whitespace-nowrap">
                            <i className="fa-solid fa-filter"></i>
                        </button>
                    </div>

                    {/* Hàng nút bấm nằm bên dưới */}
                    <div className="flex items-center space-x-3">
                        {auth?.permissions?.includes('THEM_KHACH_HANG') && (
                            <>
                                <button onClick={() => openModal()} className="bg-primary hover:bg-blue-600 text-white font-medium py-2 px-4 rounded-lg shadow-sm transition-colors duration-200 flex items-center whitespace-nowrap">
                                    <i className="fa-solid fa-plus mr-2"></i> Thêm Mới
                                </button>
                                <button 
                                    onClick={() => setIsImportMode(true)} 
                                    className="bg-emerald-600 hover:bg-emerald-700 text-white font-medium py-2 px-4 rounded-lg shadow-sm transition-colors duration-200 flex items-center whitespace-nowrap"
                                >
                                    <i className="fa-solid fa-file-excel mr-2"></i> Thêm bằng file Excel
                                </button>
                                <button 
                                    onClick={openExportModal} 
                                    className="bg-emerald-600 hover:bg-emerald-700 text-white font-medium py-2 px-4 rounded-lg shadow-sm transition-colors duration-200 flex items-center whitespace-nowrap"
                                >
                                    <i className="fa-solid fa-file-excel mr-2"></i> Xuất ra file Excel
                                </button>
                            </>
                        )}
                    </div>
                </div>
            </div>
            
            {showFilter && (
                <div className="bg-white p-4 rounded-xl shadow-sm border border-slate-200 mb-6 transition-all duration-300">
                    <h3 className="text-sm font-semibold text-slate-700 mb-3"><i className="fa-solid fa-filter text-primary mr-2"></i>Bộ lọc tìm kiếm</h3>
                    <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                        <div>
                            <label className="block text-xs font-medium text-slate-500 mb-1">Tên khách hàng</label>
                            <input type="text" 
                            value={filterData.customerName} 
                            onChange={(e) => setFilterData({...filterData, customerName: e.target.value})} 
                            className="w-full px-3 py-2 border border-slate-300 rounded-md text-sm focus:outline-none focus:ring-1 focus:ring-primary" placeholder="Nhập tên..." />
                        </div>
                        <div>
                            <label className="block text-xs font-medium text-slate-500 mb-1">Số điện thoại</label>
                            <input type="text" 
                            value={filterData.phoneNumber} 
                            onChange={(e) => setFilterData({...filterData, phoneNumber: e.target.value})} 
                            className="w-full px-3 py-2 border border-slate-300 rounded-md text-sm focus:outline-none focus:ring-1 focus:ring-primary" placeholder="Nhập SĐT..." />
                        </div>
                        <div>
                            <label className="block text-xs font-medium text-slate-500 mb-1">Khu vực</label>
                            <input type="text" 
                            value={filterData.location} 
                            onChange={(e) => setFilterData({...filterData, location: e.target.value})} 
                            className="w-full px-3 py-2 border border-slate-300 rounded-md text-sm focus:outline-none focus:ring-1 focus:ring-primary" placeholder="Nhập khu vực..." />
                        </div>
                        <div>
                            <label className="block text-xs font-medium text-slate-500 mb-1">Giới tính</label>
                            <select value={filterData.gender} 
                            onChange={(e) => setFilterData({...filterData, gender: e.target.value})} 
                            className="w-full px-3 py-2 border border-slate-300 rounded-md text-sm focus:outline-none focus:ring-1 focus:ring-primary bg-white">
                                <option value="">Tất cả</option>
                                <option value="true">Nam</option>
                                <option value="false">Nữ</option>
                            </select>
                        </div>
                    </div>
                    
                    {/* Phần Giao diện Ngày sinh (Chưa nối logic State theo yêu cầu) */}
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
                        <div className="bg-slate-50 p-3 rounded-lg border border-slate-200">
                            <h4 className="text-xs font-semibold text-slate-700 mb-2"><i className="fa-regular fa-calendar text-slate-400 mr-1"></i> Khoảng ngày sinh</h4>
                            <div className="grid grid-cols-2 gap-3">
                                <div>
                                    <label className="block text-xs font-medium text-slate-500 mb-1">Từ ngày</label>
                                    <input type="date" 
                                    value={filterData.fromDateOfBirth}
                                    onChange={(e) => setFilterData({...filterData, fromDateOfBirth: e.target.value})}
                                    className="w-full px-3 py-1.5 border border-slate-300 rounded-md text-sm focus:outline-none focus:ring-1 focus:ring-primary bg-white" />
                                </div>
                                <div>
                                    <label className="block text-xs font-medium text-slate-500 mb-1">Đến ngày</label>
                                    <input type="date" 
                                    value={filterData.toDateOfBirth}
                                    onChange={(e) => {
                                        console.log("toDateOfBirth e target value: " + e.target.value);
                                        setFilterData({...filterData, toDateOfBirth: e.target.value});
                                    }}
                                    className="w-full px-3 py-1.5 border border-slate-300 rounded-md text-sm focus:outline-none focus:ring-1 focus:ring-primary bg-white" />
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="mt-4 flex justify-end space-x-3">
                        <button onClick={() => {
                            setFilterData({
                                customerName: "", 
                                phoneNumber: "", 
                                location: "", 
                                gender: "",
                                fromDateOfBirth: "",
                                toDateOfBirth: ""
                            });
                        }} className="px-4 py-2 text-sm text-slate-600 bg-slate-100 hover:bg-slate-200 rounded-md transition-colors">Xóa điều kiện</button>
                        <button 
                            onClick={() => { setCurrentPage(0); fetchCustomers(); }} 
                            className="px-4 py-2 text-sm text-white bg-primary hover:bg-blue-600 rounded-md transition-colors shadow-sm">
                                <i className="fa-solid fa-search mr-2"></i> Áp dụng lọc
                        </button>
                    </div>
                </div>
            )}
            
            <div className="bg-white shadow-sm rounded-xl border border-slate-200 overflow-hidden">
                <div className="overflow-x-auto">
                    <table className="w-full min-w-max table-auto whitespace-nowrap">
                        <thead>
                            <tr className="bg-slate-50 border-b border-slate-200 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">
                                <th className="sticky left-0 bg-slate-50 px-6 py-4 min-w-[80px] z-10">Mã khách hàng</th>
                                <th className="sticky left-[170px] bg-slate-50 px-6 py-4 min-w-[200px] cursor-pointer hover:bg-slate-200 transition-colors select-none z-10" onClick={() => handleSort('customerName')} title="Sắp xếp theo Tên">
                                    Tên khách hàng {renderSortIcon('customerName')}
                                </th>
                                <th className="px-6 py-4 min-w-[200px] cursor-pointer hover:bg-slate-200 transition-colors select-none" onClick={() => handleSort('phoneNumber')} title="Sắp xếp theo SĐT">
                                    Số điện thoại {renderSortIcon('phoneNumber')}
                                </th>
                                <th className="px-6 py-4 min-w-[150px] cursor-pointer hover:bg-slate-200 transition-colors select-none" onClick={() => handleSort('dateOfBirth')} title="Sắp xếp theo Ngày sinh">
                                    Ngày sinh {renderSortIcon('dateOfBirth')}
                                </th>
                                <th className="px-6 py-4 min-w-[150px] cursor-pointer hover:bg-slate-200 transition-colors select-none" onClick={() => handleSort('location')} title="Sắp xếp theo Khu vực">
                                    Khu vực {renderSortIcon('location')}
                                </th>
                                <th className="px-6 py-4 min-w-[120px] text-right">Thao tác</th>
                                <th className="px-6 py-4 min-w-[120px] text-right">Thao tác</th>
                                <th className="px-6 py-4 min-w-[120px] text-right">Thao tác</th>
                                <th className="px-6 py-4 min-w-[120px] text-right">Thao tác</th>
                                <th className="px-6 py-4 min-w-[120px] text-right">Thao tác</th>
                                <th className="px-6 py-4 min-w-[120px] text-right">Thao tác</th>
                                <th className="px-6 py-4 min-w-[120px] text-right">Thao tác</th>
                                <th className="px-6 py-4 min-w-[120px] text-right">Thao tác</th>
                                <th className="px-6 py-4 min-w-[120px] text-right">Thao tác</th>
                                <th className="px-6 py-4 min-w-[120px] text-right">Thao tác</th>
                                <th className="px-6 py-4 min-w-[120px] text-right">Thao tác</th>
                                <th className="px-6 py-4 min-w-[120px] text-right">Thao tác</th>
                                <th className="px-6 py-4 min-w-[120px] text-right">Thao tác</th>
                                <th className="px-6 py-4 min-w-[120px] text-right">Thao tác</th>
                                <th className="px-6 py-4 min-w-[120px] text-right">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-200 text-sm">
                            {filteredCustomers.map((customer, i)=> (
                                <tr key={customer.id} onClick={() => openDetailModal(customer.id)} className="hover:bg-slate-50 transition-colors group cursor-pointer">
                                    <td className="sticky left-0 bg-white group-hover:bg-slate-50 px-6 py-4 whitespace-nowrap text-slate-500 font-mono text-xs z-10 transition-colors">#{customer.customerCode}</td>
                                    <td className="sticky left-[170px] bg-white group-hover:bg-slate-50 px-6 py-4 whitespace-nowrap z-10 transition-colors">
                                        <div className="flex items-center">
                                            <div className="h-10 w-10 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center font-bold text-sm">
                                                {getInitials(customer.name)}
                                            </div>
                                            <div className="ml-4">
                                                <div className="font-medium text-slate-900 group-hover:text-primary transition-colors">{customer.name}</div>
                                                <div className="text-slate-500 text-xs">{customer.gender ? "Nam" : "Nữ"}</div>
                                            </div>
                                        </div>
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap">
                                        <div className="text-slate-700"><i className="fa-solid fa-phone text-slate-400 mr-2 w-4"></i>{formatPhoneNumber(customer.phoneNumber)}</div>
                                        <div className="text-slate-500 text-xs mt-1"><i className="fa-solid fa-envelope text-slate-400 mr-2 w-4"></i>{customer.email || 'N/A'}</div>
                                    </td>
                                        <td className="px-6 py-4 whitespace-nowrap text-slate-700">
                                        <i className="fa-solid fa-calendar text-slate-400 mr-2"></i>{customer.dateOfBirth}
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap text-slate-700">
                                        <i className="fa-solid fa-location-dot text-slate-400 mr-2"></i>{customer.location}
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap text-right font-medium" onClick={(e) => e.stopPropagation()}>
                                        <div className="flex justify-end space-x-2 opacity-0 group-hover:opacity-100 transition-opacity">
                                            {auth?.permissions?.includes("THEM_KHACH_HANG") && (
                                                <button onClick={() => openModal(customer)} className="p-2 text-primary hover:bg-blue-50 rounded-lg transition-colors" title="Sửa">
                                                    <i className="fa-solid fa-pen-to-square"></i>
                                                </button>
                                            )}
                                            {auth?.permissions?.includes("XOA_KHACH_HANG") && (
                                                <button onClick={() => { setDeletingId(customer.id); setIsDeleteModalOpen(true); }} className="p-2 text-danger hover:bg-red-50 rounded-lg transition-colors" title="Xóa">
                                                    <i className="fa-solid fa-trash-can"></i>
                                                </button>
                                            )}
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>

                {/* Pagination Controls */}
                    <div className="flex items-center justify-between px-6 py-3 border-t border-slate-200 bg-slate-50">
                        <div className="text-sm text-slate-500">
                            Trang {currentPage + 1} / {totalPage}
                        </div>
                        <div className="flex items-center space-x-2">
                            <button
                                disabled={currentPage === 0}
                                onClick={() => setCurrentPage(currentPage - 1)}
                                className="px-3 py-1.5 text-sm font-medium text-slate-500 bg-white border border-slate-300 rounded-md hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                            >
                                <i className="fa-solid fa-chevron-left mr-1"></i> Trước
                            </button>
                            <div className="flex items-center space-x-1 hidden sm:flex">
                                {(() => {
                                    const delta = 1;
                                    const pages = [];
                                    for (let i = 0; i < totalPage; i++) {
                                        if (i === 0 || i === totalPage - 1 || (i >= currentPage - delta && i <= currentPage + delta)) {
                                            pages.push(i);
                                        }
                                    }
                                    const items = [];
                                    let prevPage = null;
                                    for (const page of pages) {
                                        if (prevPage !== null && page - prevPage > 1) {
                                            items.push(<span key={`dots-${page}`} className="px-2 text-slate-400">...</span>);
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
                            <button
                                disabled={currentPage === totalPage - 1 || totalPage === 0}
                                onClick={() => setCurrentPage(currentPage + 1)}
                                className="px-3 py-1.5 text-sm font-medium text-slate-500 bg-white border border-slate-300 rounded-md hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                            >
                                Sau <i className="fa-solid fa-chevron-right ml-1"></i>
                            </button>
                        </div>
                    </div>
                    
                    {filteredCustomers.length === 0 && (
                        <div className="flex flex-col items-center justify-center py-16 text-slate-500">
                            <div className="bg-slate-100 p-4 rounded-full mb-4">
                                <i className="fa-solid fa-folder-open text-3xl text-slate-400"></i>
                            </div>
                            <p className="text-lg font-medium text-slate-700 mb-1">Không tìm thấy khách hàng nào</p>
                            <button onClick={() => openModal()} className="text-primary hover:text-blue-700 font-medium">
                                + Thêm khách hàng ngay
                            </button>
                        </div>
                    )}
            </div>

            {/* Modal Edit/Add */}
            <div className={`fixed inset-0 bg-slate-900/50 backdrop-blur-sm z-50 flex items-center justify-center transition-opacity duration-300 ${isModalOpen ? 'opacity-100' : 'opacity-0 pointer-events-none'}`}>
                <div className={`bg-white rounded-2xl shadow-xl w-full max-w-lg mx-4 overflow-hidden flex flex-col max-h-[90vh] transition-transform duration-300 ${isModalOpen ? 'scale-100' : 'scale-95'}`}>
                    <div className="px-6 py-4 border-b border-slate-200 flex justify-between items-center bg-slate-50">
                        <h3 className="text-lg font-semibold text-slate-900">{editingCustomer ? "Sửa Khách hàng" : "Thêm Khách hàng Mới"}</h3>
                        <button onClick={closeModal} className="text-slate-400 hover:text-slate-600 transition-colors p-1 rounded-md hover:bg-slate-200">
                            <i className="fa-solid fa-xmark text-xl"></i>
                        </button>
                    </div>
                    <div className="px-6 py-4 overflow-y-auto">
                        <form id="customerForm" onSubmit={handleSubmit} className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-slate-700 mb-1">Họ và tên <span className="text-danger">*</span></label>
                                <input type="text" required value={formData.name} onChange={e => setFormData({...formData, name: e.target.value})} className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary" placeholder="Nguyễn Văn A" />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-slate-700 mb-1">Email <span className="text-danger">*</span></label>
                                <input type="email" required value={formData.email} onChange={e => setFormData({...formData, email: e.target.value})} className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary" placeholder="email@example.com" />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-slate-700 mb-1">Số điện thoại <span className="text-danger">*</span></label>
                                <input type="tel" required value={formData.phoneNumber} onChange={e => setFormData({...formData, phoneNumber: e.target.value})} className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary" placeholder="0901234567" />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-slate-700 mb-1">Ngày sinh <span className="text-danger">*</span></label>
                                <input type="date" required value={formData.dateOfBirth} onChange={e => setFormData({...formData, dateOfBirth: e.target.value})} className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary" />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-slate-700 mb-1">Địa chỉ <span className="text-danger">*</span></label>
                                <input type="text" required value={formData.location} onChange={e => setFormData({...formData, location: e.target.value})} className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary" placeholder="Hà Nội" />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-slate-700 mb-1">Giới tính</label>
                                <select value={formData.gender} onChange={e => setFormData({...formData, gender: e.target.value === "true"})} className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary bg-white">
                                    <option value="true">Nam</option>
                                    <option value="false">Nữ</option>
                                </select>
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-slate-700 mb-1">Company ID <span className="text-danger">*</span></label>
                                <input type="number" required value={formData.companyId} onChange={e => setFormData({...formData, companyId: e.target.value})} className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary" placeholder="Nhập ID công ty" />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-slate-700 mb-1">User ID <span className="text-danger">*</span></label>
                                <input type="number" required value={formData.userId} onChange={e => setFormData({...formData, userId: e.target.value})} className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary" placeholder="Nhập ID nhân viên phụ trách" />
                            </div>
                        </form>
                    </div>
                    <div className="px-6 py-4 border-t border-slate-200 bg-slate-50 flex justify-end space-x-3">
                        <button type="button" onClick={closeModal} className="px-4 py-2 text-sm font-medium text-slate-700 bg-white border border-slate-300 rounded-lg hover:bg-slate-50 transition-colors">Hủy</button>
                        <button type="submit" form="customerForm" className="px-4 py-2 text-sm font-medium text-white bg-primary rounded-lg hover:bg-blue-600 transition-colors shadow-sm flex items-center">
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
                        <p className="text-slate-500 text-sm mb-6">Bạn có chắc chắn muốn xóa khách hàng này không? Hành động này không thể hoàn tác.</p>
                        <div className="flex justify-center space-x-3">
                            <button onClick={() => setIsDeleteModalOpen(false)} className="px-4 py-2 text-sm font-medium text-slate-700 bg-slate-100 rounded-lg hover:bg-slate-200 w-full">Hủy bỏ</button>
                            <button onClick={confirmDelete} className="px-4 py-2 text-sm font-medium text-white bg-danger rounded-lg hover:bg-red-600 w-full">Đồng ý Xóa</button>
                        </div>
                    </div>
                </div>
            </div>

            {/* Detail Modal */}
            <div className={`fixed inset-0 bg-slate-900/50 backdrop-blur-sm z-50 flex items-center justify-center transition-opacity duration-300 ${isDetailModalOpen ? 'opacity-100' : 'opacity-0 pointer-events-none'}`}>
                <div className={`bg-white rounded-2xl shadow-xl w-full max-w-7xl mx-4 overflow-hidden flex flex-col max-h-[90vh] transition-transform duration-300 ${isDetailModalOpen ? 'scale-100' : 'scale-95'}`}>
                    <div className="px-6 py-4 border-b border-slate-200 flex justify-between items-center bg-slate-50">
                        <h3 className="text-lg font-semibold text-slate-900">Chi tiết Khách hàng</h3>
                        <button onClick={closeDetailModal} className="text-slate-400 hover:text-slate-600 transition-colors p-1 rounded-md hover:bg-slate-200">
                            <i className="fa-solid fa-xmark text-xl"></i>
                        </button>
                    </div>
                    <div className="px-6 py-4 overflow-y-auto bg-slate-50/50">
                        {detailCustomer ? (
                            <div className="space-y-6">
                                {/* Thông tin chung */}
                                <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm">
                                    <h4 className="text-base font-semibold text-slate-800 mb-4 flex items-center">
                                        <i className="fa-regular fa-address-card mr-2 text-primary"></i> Thông tin chung
                                    </h4>
                                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
                                        <div><span className="text-slate-500 block mb-1">Khách hàng:</span> <span className="font-medium text-slate-900 text-base">{detailCustomer.customerName}</span></div>
                                        <div><span className="text-slate-500 block mb-1">Điện thoại chính:</span> <span className="font-medium text-slate-900">{formatPhoneNumber(detailCustomer.phoneNumber)}</span></div>
                                        <div><span className="text-slate-500 block mb-1">Ngày sinh:</span> <span className="font-medium text-slate-900">{detailCustomer.dateOfBirth}</span></div>
                                        <div><span className="text-slate-500 block mb-1">Giới tính:</span> <span className="font-medium text-slate-900">{detailCustomer.gender ? "Nam" : "Nữ"}</span></div>
                                        <div><span className="text-slate-500 block mb-1">Email:</span> <span className="font-medium text-slate-900">{detailCustomer.emails[0] ? detailCustomer.emails[0].emailAddress : "Không có email"}</span></div>
                                    </div>
                                </div>

                                {/* Lưới 2 cột cho Email và Người liên hệ */}
                                <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                                    {/* Cột Danh sách Email */}
                                    <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden flex flex-col">
                                        <div className="px-4 py-3 bg-slate-50 border-b border-slate-200">
                                            <h4 className="font-semibold text-slate-800 text-sm flex items-center">
                                                <i className="fa-solid fa-envelope-open-text mr-2 text-emerald-500"></i> Danh sách Email phụ
                                            </h4>
                                        </div>
                                        <div className="p-4 flex-grow">
                                            {detailCustomer.emails?.length > 0 ? (
                                                <ul className="space-y-2">
                                                    {detailCustomer.emails.slice(1).map(email => (
                                                        <li key={email.id} className="flex items-center text-sm p-2 rounded-lg bg-slate-50 border border-slate-100">
                                                            <div className="w-8 h-8 rounded-full bg-emerald-100 text-emerald-600 flex items-center justify-center mr-3 flex-shrink-0">
                                                                <i className="fa-regular fa-envelope"></i>
                                                            </div>
                                                            <span className="text-slate-700 font-medium">{email.emailAddress}</span>
                                                        </li>
                                                    ))}
                                                </ul>
                                            ) : (
                                                <div className="text-center py-6 text-slate-400 text-sm italic">
                                                    Không có email phụ nào.
                                                </div>
                                            )}
                                        </div>
                                    </div>

                                    {/* Cột Người liên hệ */}
                                    <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden flex flex-col">
                                        <div className="px-4 py-3 bg-slate-50 border-b border-slate-200">
                                            <h4 className="font-semibold text-slate-800 text-sm flex items-center">
                                                <i className="fa-solid fa-users mr-2 text-blue-500"></i> Người liên hệ
                                            </h4>
                                        </div>
                                        <div className="p-4 flex-grow">
                                            {detailCustomer.contactPositions?.length > 0 ? (
                                                <div className="space-y-3">
                                                    {detailCustomer.contactPositions.map(contactP => (
                                                        <div key={contactP.id} className="flex flex-col p-3 rounded-lg border border-slate-200 hover:border-blue-300 transition-colors bg-white">
                                                            <div className="flex justify-between items-start mb-1">
                                                                <span className="font-bold text-slate-800 text-sm">{contactP.contactName}</span>
                                                                {contactP.positionName && (
                                                                    <span className="px-2 py-0.5 bg-blue-50 text-blue-600 text-xs font-medium rounded border border-blue-100">
                                                                        {contactP.positionName}
                                                                    </span>
                                                                )}
                                                            </div>
                                                            <div className="text-xs text-slate-600 grid grid-cols-1 sm:grid-cols-2 gap-1 mt-2">
                                                                <div className="flex items-center"><i className="fa-solid fa-phone w-4 text-slate-400"></i> {formatPhoneNumber(contactP.phoneNumber)}</div>
                                                                {contactP.email && <div className="flex items-center"><i className="fa-solid fa-envelope w-4 text-slate-400"></i> {contactP.email}</div>}
                                                            </div>
                                                        </div>
                                                    ))}
                                                </div>
                                            ) : (
                                                <div className="text-center py-6 text-slate-400 text-sm italic">
                                                    Chưa có người liên hệ nào được thêm.
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ) : (
                            <div className="flex justify-center items-center py-12">
                                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
                            </div>
                        )}
                    </div>
                </div>
            </div>

            <ExportExcelModal 
                isOpen={isExportModalOpen} 
                onClose={() => setIsExportModalOpen(false)} 
                filterData={filterData} 
                showToast={showToast} 
            />

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
