import { useState, useEffect } from "react";
// TODO: tự build API, ví dụ:
import { getFacebookPage} from "../api/socialApi";
import { useAuth } from "../context/AuthContext";

export default function SocialView() {
    const { auth } = useAuth();
    const [pages, setPages] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");

    // phân trang
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPage, setTotalPage] = useState(1);

    const fetchPages = async () => {
        try {
            // TODO: gọi API lấy danh sách page theo trang, ví dụ:
            const res = await getFacebookPage(currentPage);
            setPages(res.data.content);   // mỗi phần tử: { pageId, pageName }
            setTotalPage(res.data.totalPages);
        } catch (error) {
            console.error("Lỗi khi tải danh sách trang", error);
        }
    };

    useEffect(() => {
        fetchPages();
    }, [currentPage]);

    const handleAction = (page) => {
        // TODO: xử lý hành động (ví dụ ngắt kết nối page)
        console.log("Hành động với page:", page);
    };

    const filteredPages = pages.filter(p =>
        p.pageName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        p.pageId?.includes(searchTerm)
    );

    const getInitials = (name) => name?.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase() || 'NA';

    return (
        <main className="flex-grow max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 w-full">

            <div className="flex justify-between items-end mb-6">
                <div>
                    <h1 className="text-2xl font-bold text-slate-900">Mạng xã hội</h1>
                    <p className="text-sm text-slate-500 mt-1">Quản lý các trang Facebook đã kết nối.</p>
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
                            placeholder="Tìm kiếm trang..."
                        />
                    </div>
                </div>
            </div>

            <div className="bg-white shadow-sm rounded-xl border border-slate-200 overflow-hidden">
                <div className="overflow-x-auto">
                    <table className="w-full whitespace-nowrap">
                        <thead>
                            <tr className="bg-slate-50 border-b border-slate-200 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">
                                <th className="px-6 py-4">Tên trang</th>
                                <th className="px-6 py-4">Page ID</th>
                                <th className="px-6 py-4 text-right">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-200 text-sm">
                            {filteredPages.map(page => (
                                <tr key={page.pageId} className="hover:bg-slate-50 transition-colors group">
                                    <td className="px-6 py-4 whitespace-nowrap">
                                        <div className="flex items-center">
                                            <div className="flex-shrink-0 h-10 w-10 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center font-bold text-sm">
                                                {getInitials(page.pageName)}
                                            </div>
                                            <div className="ml-4">
                                                <div className="font-medium text-slate-900 group-hover:text-primary transition-colors">{page.pageName}</div>
                                            </div>
                                        </div>
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap">
                                        <div className="text-slate-700 font-mono text-xs">{page.pageId}</div>
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap text-right font-medium">
                                        <div className="flex justify-end space-x-2 opacity-0 group-hover:opacity-100 transition-opacity">
                                            <button onClick={() => handleAction(page)} className="p-2 text-danger hover:bg-red-50 rounded-lg transition-colors" title="Ngắt kết nối">
                                                <i className="fa-solid fa-trash-can"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>

                    {filteredPages.length === 0 && (
                        <div className="flex flex-col items-center justify-center py-16 text-slate-500">
                            <div className="bg-slate-100 p-4 rounded-full mb-4">
                                <i className="fa-solid fa-folder-open text-3xl text-slate-400"></i>
                            </div>
                            <p className="text-lg font-medium text-slate-700 mb-1">Chưa có trang nào được kết nối</p>
                        </div>
                    )}
                </div>

                {/* Thanh Phân Trang */}
                <div className="px-6 py-4 border-t border-slate-200 flex items-center justify-end bg-slate-50">
                    <div className="flex items-center space-x-2">
                        <button
                        disabled={currentPage === 0}
                        onClick={() => setCurrentPage(currentPage-1)}
                        className="px-3 py-1.5 text-sm font-medium text-slate-500 bg-white border border-slate-300 rounded-md hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors">
                            <i className="fa-solid fa-chevron-left mr-1"></i> Trước
                        </button>

                        <div className="flex items-center space-x-1 hidden sm:flex">
                            {(() => {
                                const delta = 1; // số trang hiện quanh currentPage
                                const pageNumbers = [];
                                for (let i = 0; i < totalPage; i++) {
                                    if (
                                        i === 0 ||
                                        i === totalPage - 1 ||
                                        (i >= currentPage - delta && i <= currentPage + delta)
                                    ) {
                                        pageNumbers.push(i);
                                    }
                                }

                                const items = [];
                                let prevPage = null;
                                for (const p of pageNumbers) {
                                    if (prevPage !== null && p - prevPage > 1) {
                                        items.push(
                                            <span key={`dots-${p}`} className="px-2 text-slate-400">...</span>
                                        );
                                    }
                                    items.push(
                                        <button
                                            key={p}
                                            onClick={() => setCurrentPage(p)}
                                            className={
                                                currentPage === p
                                                    ? "px-3 py-1.5 text-sm font-medium text-white bg-primary border border-primary rounded-md shadow-sm"
                                                    : "px-3 py-1.5 text-sm font-medium text-slate-700 bg-white border border-slate-300 rounded-md hover:bg-slate-50 transition-colors"
                                            }
                                        >
                                            {p + 1}
                                        </button>
                                    );
                                    prevPage = p;
                                }
                                return items;
                            })()}
                        </div>

                        <button
                        disabled={currentPage === totalPage-1}
                        onClick={() => setCurrentPage(currentPage+1)}
                        className="px-3 py-1.5 text-sm font-medium text-slate-500 bg-white border border-slate-300 rounded-md hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors">
                            Sau <i className="fa-solid fa-chevron-right ml-1"></i>
                        </button>
                    </div>
                </div>
            </div>
        </main>
    );
}