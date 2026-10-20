import { useState, useEffect } from "react";
// TODO: tự build API, ví dụ:
import { getFacebookPage} from "../api/socialApi";
import { useAuth } from "../context/AuthContext";
import { getFacebookWebhook, getZaloWebhook } from "../api/webhookApi";

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

    const handleConnectFacebook = async () => {
      console.log("Kết nối Facebook Page");
      const res = await getFacebookWebhook();
      console.log("Kết nối Facebook Page thành công, chuyển hướng đến:", res.data.url);
      window.location.href = res.data.url;  // Chuyển hướng người dùng đến URL trả về từ API
    };
    const handleConnectZalo = async () => {
      console.log("Kết nối Zalo OA");
      const res = await getZaloWebhook();
      console.log("Kết nối Zalo OA thành công, chuyển hướng đến:", res.data.url);
      window.location.href = res.data.url;  // Chuyển hướng người dùng đến URL trả về từ API
    };

    return (
        <main className="flex-grow max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 w-full">

            <div className="flex flex-col md:flex-row md:items-end justify-between gap-4 mb-6">
                <div>
                    <h1 className="text-2xl font-bold text-slate-900">Mạng xã hội</h1>
                    <p className="text-sm text-slate-500 mt-1">Quản lý các trang Facebook và Zalo đã kết nối.</p>
                </div>

                <div className="flex flex-wrap items-center gap-3">
                    <button
                        onClick={handleConnectFacebook}
                        className="flex items-center gap-2 rounded-lg bg-[#1877F2] px-3.5 py-2 text-sm font-medium text-white shadow-sm transition hover:bg-[#166FE5] active:scale-[0.98] whitespace-nowrap"
                    >
                        <svg
                            className="h-4 w-4"
                            viewBox="0 0 24 24"
                            fill="currentColor"
                        >
                            <path d="M24 12.073C24 5.405 18.627 0 12 0S0 5.405 0 12.073C0 18.099 4.388 23.093 10.125 24v-8.437H7.078v-3.49h3.047V9.413c0-3.025 1.792-4.704 4.533-4.704 1.312 0 2.686.235 2.686.235v2.973h-1.514c-1.491 0-1.955.93-1.955 1.886v2.27h3.328l-.532 3.49h-2.796V24C19.612 23.093 24 18.099 24 12.073z" />
                        </svg>
                        <span>Kết nối Facebook Page</span>
                    </button>

                    <button
                        onClick={handleConnectZalo}
                        className="flex items-center gap-2 rounded-lg bg-[#0068FF] px-3.5 py-2 text-sm font-medium text-white shadow-sm transition hover:bg-[#0058DB] active:scale-[0.98] whitespace-nowrap"
                    >
                        <svg
                            className="h-4 w-4"
                            viewBox="0 0 24 24"
                            fill="currentColor"
                        >
                            <path d="M12.49 10.2722v-.4496h1.3467v6.3218h-.7704a.576.576 0 01-.5763-.5729l-.0006.0005a3.273 3.273 0 01-1.9372.6321c-1.8138 0-3.2844-1.4697-3.2844-3.2823 0-1.8125 1.4706-3.2822 3.2844-3.2822a3.273 3.273 0 011.9372.6321l.0006.0005zM6.9188 7.7896v.205c0 .3823-.051.6944-.2995 1.0605l-.03.0343c-.0542.0615-.1815.206-.2421.2843L2.024 14.8h4.8948v.7682a.5764.5764 0 01-.5767.5761H0v-.3622c0-.4436.1102-.6414.2495-.8476L4.8582 9.23H.1922V7.7896h6.7266zm8.5513 8.3548a.4805.4805 0 01-.4803-.4798v-7.875h1.4416v8.3548H15.47zM20.6934 9.6C22.52 9.6 24 11.0807 24 12.9044c0 1.8252-1.4801 3.306-3.3066 3.306-1.8264 0-3.3066-1.4808-3.3066-3.306 0-1.8237 1.4802-3.3044 3.3066-3.3044zm-10.1412 5.253c1.0675 0 1.9324-.8645 1.9324-1.9312 0-1.065-.865-1.9295-1.9324-1.9295s-1.9324.8644-1.9324 1.9295c0 1.0667.865 1.9312 1.9324 1.9312zm10.1412-.0033c1.0737 0 1.945-.8707 1.945-1.9453 0-1.073-.8713-1.9436-1.945-1.9436-1.0753 0-1.945.8706-1.945 1.9453 0 1.0746.8697 1.9453 1.945 1.9453z" />
                        </svg>
                        <span>Kết nối Zalo OA</span>
                    </button>

                    <div className="relative">
                        <span className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                            <i className="fa-solid fa-search text-slate-400"></i>
                        </span>
                        <input
                            type="text"
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="pl-9 pr-4 py-2 border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent transition-shadow w-56 md:w-64 bg-slate-50"
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