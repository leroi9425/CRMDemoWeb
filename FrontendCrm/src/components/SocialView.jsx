import { useState, useEffect } from "react";
import { useAuth } from "../context/AuthContext";

// API hiện có
import { deleteFacebookPage, deleteZaloOa, getFacebookPages, getZaloOAPages } from "../api/socialApi";
import { getFacebookWebhook, getZaloWebhook } from "../api/webhookApi";
import { SOCIAL_PLATFORMS } from "./SocialPlatforms";

// TODO: Sau khi bạn viết xong các API mới thì import vào đây:
// import { getZaloOAPage, disconnectZaloOA } from "../api/zaloApi";
// import { getTiktokAccounts, disconnectTiktok } from "../api/tiktokApi";
// import { getShopeeShops, disconnectShopee } from "../api/shopeeApi";



export default function SocialView() {
    const { auth } = useAuth();
    
    // Quản lý Tab hiện tại (Mặc định: facebook)
    const [activeTab, setActiveTab] = useState("facebook");

    // Dữ liệu danh sách kết nối của tab hiện tại
    const [items, setItems] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [loading, setLoading] = useState(false);

    // Phân trang
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPage, setTotalPage] = useState(1);

    // Cấu hình tab hiện tại
    const currentTab = SOCIAL_PLATFORMS.find(t => t.id === activeTab) || SOCIAL_PLATFORMS[0];

    // =========================================================================
    // 1. CÁC HÀM GỌI API LẤY DANH SÁCH (THEO TỪNG TAB)
    // =========================================================================

    // API Facebook: Đã có sẵn
    const fetchFacebookPages = async () => {
        setLoading(true);
        try {
            const res = await getFacebookPages(currentPage);
            setItems(res.data?.content || []);
            setTotalPage(res.data?.totalPages || 1);
        } catch (error) {
            console.error("Lỗi khi tải danh sách Facebook Page", error);
            setItems([]);
        } finally {
            setLoading(false);
        }
    };

    // API Zalo OA: (TODO: Bạn tự code API và lắp vào đây)
    const fetchZaloOaList = async () => {
        setLoading(true);
        try {
            console.log("TODO: Gọi API lấy danh sách Zalo OA, trang:", currentPage);
            // VÍ DỤ MẪU:
            const res = await getZaloOAPages(currentPage);
            setItems(res.data?.content || []);
            setTotalPage(res.data?.totalPages || 1);
        } catch (error) {
            console.error("Lỗi khi tải danh sách Zalo OA", error);
            setItems([]);
        } finally {
            setLoading(false);
        }
    };

    // API TikTok: (TODO: Bạn tự code API và lắp vào đây)
    const fetchTiktokList = async () => {
        setLoading(true);
        try {
            console.log("TODO: Gọi API lấy danh sách TikTok, trang:", currentPage);
            // VÍ DỤ MẪU:
            // const res = await getTiktokAccounts(currentPage);
            // setItems(res.data?.content || []);
            // setTotalPage(res.data?.totalPages || 1);
            setItems([]);
            setTotalPage(1);
        } catch (error) {
            console.error("Lỗi khi tải danh sách TikTok", error);
            setItems([]);
        } finally {
            setLoading(false);
        }
    };

    // API Shopee: (TODO: Bạn tự code API và lắp vào đây)
    const fetchShopeeList = async () => {
        setLoading(true);
        try {
            console.log("TODO: Gọi API lấy danh sách Shopee, trang:", currentPage);
            // VÍ DỤ MẪU:
            // const res = await getShopeeShops(currentPage);
            // setItems(res.data?.content || []);
            // setTotalPage(res.data?.totalPages || 1);
            setItems([]);
            setTotalPage(1);
        } catch (error) {
            console.error("Lỗi khi tải danh sách Shopee", error);
            setItems([]);
        } finally {
            setLoading(false);
        }
    };

    // Gọi API tương ứng mỗi khi chuyển tab hoặc đổi trang
    useEffect(() => {
        switch (activeTab) {
            case "facebook":
                fetchFacebookPages();
                break;
            case "zalo":
                fetchZaloOaList();
                break;
            case "tiktok":
                fetchTiktokList();
                break;
            case "shopee":
                fetchShopeeList();
                break;
            default:
                break;
        }
    }, [activeTab, currentPage]);

    // Đổi tab: reset lại trang về 0 và xóa từ khóa tìm kiếm
    const handleSwitchTab = (tabId) => {
        setActiveTab(tabId);
        setCurrentPage(0);
        setSearchTerm("");
    };

    // =========================================================================
    // 2. CÁC HÀM XỬ LÝ KẾT NỐI MỚI (CONNECT)
    // =========================================================================

    const handleConnectFacebook = async () => {
        try {
            console.log("Kết nối Facebook Page");
            const res = await getFacebookWebhook();
            if (res.data?.url) {
                window.location.href = res.data.url;
            }
        } catch (error) {
            console.error("Lỗi khi kết nối Facebook:", error);
        }
    };

    const handleConnectZalo = async () => {
        try {
            console.log("Kết nối Zalo OA");
            const res = await getZaloWebhook();
            if (res.data?.url) {
                window.location.href = res.data.url;
            }
        } catch (error) {
            console.error("Lỗi khi kết nối Zalo:", error);
        }
    };

    const handleConnectTiktok = async () => {
        // TODO: Viết API lấy URL OAuth của TikTok rồi redirect
        console.log("TODO: Gọi API kết nối TikTok");
        alert("Tính năng kết nối TikTok đang được hoàn thiện!");
    };

    const handleConnectShopee = async () => {
        // TODO: Viết API lấy URL OAuth của Shopee rồi redirect
        console.log("TODO: Gọi API kết nối Shopee");
        alert("Tính năng kết nối Shopee đang được hoàn thiện!");
    };

    // Kích hoạt hàm kết nối tùy theo tab đang chọn
    const handleConnectActivePlatform = () => {
        switch (activeTab) {
            case "facebook":
                handleConnectFacebook();
                break;
            case "zalo":
                handleConnectZalo();
                break;
            case "tiktok":
                handleConnectTiktok();
                break;
            case "shopee":
                handleConnectShopee();
                break;
            default:
                break;
        }
    };

    // =========================================================================
    // 3. CÁC HÀM THAO TÁC / NGẮT KẾT NỐI (DISCONNECT)
    // =========================================================================

    const handleDisconnect = async (item) => {
        const itemName = getItemName(item);
        const confirmDelete = window.confirm(`Bạn có chắc chắn muốn ngắt kết nối với "${itemName}"?`);
        if (!confirmDelete) return;

        switch (activeTab) {
            case "facebook":
                // TODO: Gọi API ngắt kết nối Facebook Page (ví dụ: disconnectFacebookPage(item.pageId))
                await deleteFacebookPage(item.id);
                console.log("TODO: Ngắt kết nối Facebook Page:", item);
                break;
            case "zalo":
                // TODO: Gọi API ngắt kết nối Zalo OA (ví dụ: disconnectZaloOA(item.oaId))
                await deleteZaloOa(item.id);
                console.log("TODO: Ngắt kết nối Zalo OA:", item);
                break;
            case "tiktok":
                // TODO: Gọi API ngắt kết nối TikTok
                console.log("TODO: Ngắt kết nối TikTok:", item);
                break;
            case "shopee":
                // TODO: Gọi API ngắt kết nối Shopee
                console.log("TODO: Ngắt kết nối Shopee:", item);
                break;
            default:
                break;
        }
    };

    // =========================================================================
    // 4. TIỆN ÍCH HIỂN THỊ DỮ LIỆU
    // =========================================================================

    // Hỗ trợ đọc linh hoạt tên và ID của mọi nền tảng (pageName, oaName, shopName, v.v.)
    const getItemName = (item) => item?.pageName || item?.oaName || item?.shopName || item?.name || "Chưa đặt tên";
    const getItemId = (item) => item?.pageId || item?.oaId || item?.shopId || item?.id || "N/A";
    const getInitials = (name) => name?.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase() || 'NA';

    // Lọc tìm kiếm theo tên hoặc ID
    const filteredItems = items.filter(item => {
        const name = getItemName(item).toLowerCase();
        const id = String(getItemId(item)).toLowerCase();
        const search = searchTerm.toLowerCase();
        return name.includes(search) || id.includes(search);
    });

    return (
        <main className="flex-grow max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 w-full">
            
            {/* Header: Tiêu đề + Nút kết nối + Ô tìm kiếm */}
            <div className="flex flex-col md:flex-row md:items-end justify-between gap-4 mb-6">
                <div>
                    <h1 className="text-2xl font-bold text-slate-900">Mạng xã hội</h1>
                    <p className="text-sm text-slate-500 mt-1">{currentTab.subTitle}</p>
                </div>

                <div className="flex flex-wrap items-center gap-3">
                    {/* Nút Kết nối cho nền tảng của Tab đang active */}
                    <button
                        onClick={handleConnectActivePlatform}
                        className={`flex items-center gap-2 rounded-lg px-3.5 py-2 text-sm font-medium text-white shadow-sm transition active:scale-[0.98] whitespace-nowrap ${currentTab.connectBtnColor}`}
                    >
                        {currentTab.icon}
                        <span>{currentTab.connectBtnText}</span>
                    </button>

                    {/* Ô tìm kiếm */}
                    <div className="relative">
                        <span className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                            <i className="fa-solid fa-search text-slate-400"></i>
                        </span>
                        <input
                            type="text"
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="pl-9 pr-4 py-2 border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent transition-shadow w-56 md:w-64 bg-slate-50"
                            placeholder="Tìm kiếm theo tên, ID..."
                        />
                    </div>
                </div>
            </div>

            {/* Thanh Tabs chuyển đổi nền tảng */}
            <div className="flex border-b border-slate-200 mb-6 gap-2 sm:gap-6 overflow-x-auto">
                {SOCIAL_PLATFORMS.map(tab => {
                    const isActive = activeTab === tab.id;
                    return (
                        <button
                            key={tab.id}
                            onClick={() => handleSwitchTab(tab.id)}
                            className={`flex items-center gap-2 py-3 px-3 text-sm font-medium border-b-2 transition-colors whitespace-nowrap ${
                                isActive
                                    ? "border-primary text-primary font-semibold"
                                    : "border-transparent text-slate-500 hover:text-slate-700 hover:border-slate-300"
                            }`}
                        >
                            {tab.icon}
                            <span>{tab.name}</span>
                        </button>
                    );
                })}
            </div>

            {/* Bảng danh sách kết nối */}
            <div className="bg-white shadow-sm rounded-xl border border-slate-200 overflow-hidden">
                <div className="overflow-x-auto">
                    <table className="w-full whitespace-nowrap">
                        <thead>
                            <tr className="bg-slate-50 border-b border-slate-200 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">
                                <th className="px-6 py-4">Tên tài khoản / Trang</th>
                                <th className="px-6 py-4">{currentTab.idLabel}</th>
                                <th className="px-6 py-4 text-right">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-200 text-sm">
                            {filteredItems.map((item, index) => {
                                const name = getItemName(item);
                                const id = getItemId(item);
                                return (
                                    <tr key={id !== "N/A" ? id : index} className="hover:bg-slate-50 transition-colors group">
                                        <td className="px-6 py-4 whitespace-nowrap">
                                            <div className="flex items-center">
                                                <div className="flex-shrink-0 h-10 w-10 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center font-bold text-sm">
                                                    {getInitials(name)}
                                                </div>
                                                <div className="ml-4">
                                                    <div className="font-medium text-slate-900 group-hover:text-primary transition-colors">
                                                        {name}
                                                    </div>
                                                </div>
                                            </div>
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap">
                                            <div className="text-slate-700 font-mono text-xs">{id}</div>
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap text-right font-medium">
                                            <div className="flex justify-end space-x-2 opacity-0 group-hover:opacity-100 transition-opacity">
                                                <button
                                                    onClick={() => handleDisconnect(item)}
                                                    className="p-2 text-danger hover:bg-red-50 rounded-lg transition-colors"
                                                    title="Ngắt kết nối"
                                                >
                                                    <i className="fa-solid fa-trash-can"></i>
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                );
                            })}
                        </tbody>
                    </table>

                    {/* Trạng thái rỗng */}
                    {filteredItems.length === 0 && !loading && (
                        <div className="flex flex-col items-center justify-center py-16 text-slate-500">
                            <div className="bg-slate-100 p-4 rounded-full mb-4">
                                <i className="fa-solid fa-folder-open text-3xl text-slate-400"></i>
                            </div>
                            <p className="text-lg font-medium text-slate-700 mb-1">{currentTab.emptyText}</p>
                            <p className="text-sm text-slate-400 mb-4">Hãy nhấn nút kết nối phía trên để liên kết tài khoản.</p>
                            <button
                                onClick={handleConnectActivePlatform}
                                className={`flex items-center gap-2 rounded-lg px-4 py-2 text-sm font-medium text-white shadow-sm transition ${currentTab.connectBtnColor}`}
                            >
                                {currentTab.icon}
                                <span>{currentTab.connectBtnText} ngay</span>
                            </button>
                        </div>
                    )}

                    {/* Trạng thái đang tải */}
                    {loading && (
                        <div className="flex justify-center items-center py-16 text-slate-500">
                            <i className="fa-solid fa-spinner fa-spin text-2xl mr-2 text-primary"></i>
                            <span>Đang tải dữ liệu...</span>
                        </div>
                    )}
                </div>

                {/* Thanh Phân Trang */}
                <div className="px-6 py-4 border-t border-slate-200 flex items-center justify-end bg-slate-50">
                    <div className="flex items-center space-x-2">
                        <button
                            disabled={currentPage === 0 || loading}
                            onClick={() => setCurrentPage(currentPage - 1)}
                            className="px-3 py-1.5 text-sm font-medium text-slate-500 bg-white border border-slate-300 rounded-md hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                        >
                            <i className="fa-solid fa-chevron-left mr-1"></i> Trước
                        </button>

                        <div className="flex items-center space-x-1 hidden sm:flex">
                            {(() => {
                                const delta = 1;
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

                                const itemsElements = [];
                                let prevPage = null;
                                for (const p of pageNumbers) {
                                    if (prevPage !== null && p - prevPage > 1) {
                                        itemsElements.push(
                                            <span key={`dots-${p}`} className="px-2 text-slate-400">...</span>
                                        );
                                    }
                                    itemsElements.push(
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
                                return itemsElements;
                            })()}
                        </div>

                        <button
                            disabled={currentPage >= totalPage - 1 || loading}
                            onClick={() => setCurrentPage(currentPage + 1)}
                            className="px-3 py-1.5 text-sm font-medium text-slate-500 bg-white border border-slate-300 rounded-md hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                        >
                            Sau <i className="fa-solid fa-chevron-right ml-1"></i>
                        </button>
                    </div>
                </div>
            </div>
        </main>
    );
}
