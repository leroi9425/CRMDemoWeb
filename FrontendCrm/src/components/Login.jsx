import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../api/authApi";
import { useAuth } from "../context/AuthContext";

export default function Login() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [showPassword, setShowPassword] = useState(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const { storeData } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");
        setLoading(true);

        try {
            console.log("bắt đầu gửi lệnh submit login với user pass: " + username + password);
            const res = await login({ username, password });  // đợi dữ liệu đổ vào res
            storeData({                                       // hàm lưu vào local storage
                token: res.data.token,
                username: res.data.username,
                role: res.data.role,
                permissions: res.data.permissions
            });
            console.log("res nhan duoc tu be: " + res);
            navigate("/");
        } catch (err) {
            setError("Thông tin đăng nhập không chính xác.");
            setLoading(false);
        }
    };

    const handleConnectFacebook = () => {
        window.location.href = `${import.meta.env.VITE_API_URL}/api/facebook/connect`;
    };

    return (
        <div className="bg-slate-50 min-h-screen flex items-center justify-center p-4">
            <div className="max-w-md w-full bg-white rounded-2xl shadow-xl border border-slate-100 overflow-hidden transform transition-all duration-500 translate-y-0 opacity-100">
                {/* Header Section */}
                <div className="px-8 pt-8 pb-6 text-center">
                    <div className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-blue-50 mb-4">
                        <i className="fa-solid fa-users text-primary text-3xl"></i>
                    </div>
                    <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Chào mừng trở lại123</h1>
                    <p className="text-sm text-slate-500 mt-2">Đăng nhập để quản lý khách hàng của bạn.</p>
                </div>

                {/* Form Section */}
                <div className="px-8 pb-8">
                    <form onSubmit={handleSubmit} className="space-y-5">
                        
                        <div>
                            <label htmlFor="username" className="block text-sm font-medium text-slate-700 mb-1">Tên đăng nhập</label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                    <i className="fa-solid fa-user text-slate-400 text-sm"></i>
                                </div>
                                <input 
                                    type="text" 
                                    id="username" 
                                    required 
                                    value={username}
                                    onChange={(e) => setUsername(e.target.value)}
                                    className="block w-full pl-10 pr-3 py-2.5 border border-slate-300 rounded-lg text-sm placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent transition-shadow" 
                                    placeholder="admin"
                                />
                            </div>
                        </div>

                        <div>
                            <div className="flex items-center justify-between mb-1">
                                <label htmlFor="password" className="block text-sm font-medium text-slate-700">Mật khẩu</label>
                            </div>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                    <i className="fa-solid fa-lock text-slate-400 text-sm"></i>
                                </div>
                                <input 
                                    type={showPassword ? "text" : "password"} 
                                    id="password" 
                                    required 
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    className="block w-full pl-10 pr-10 py-2.5 border border-slate-300 rounded-lg text-sm placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent transition-shadow" 
                                    placeholder="••••••••"
                                />
                                <button 
                                    type="button" 
                                    onClick={() => setShowPassword(!showPassword)} 
                                    className="absolute inset-y-0 right-0 pr-3 flex items-center text-slate-400 hover:text-slate-600 focus:outline-none"
                                >
                                    <i className={`fa-solid ${showPassword ? 'fa-eye-slash' : 'fa-eye'}`}></i>
                                </button>
                            </div>
                        </div>

                        {/* Error Message Container */}
                        {error && (
                            <div className="text-sm text-red-500 bg-red-50 p-3 rounded-lg flex items-start">
                                <i className="fa-solid fa-circle-exclamation mt-0.5 mr-2"></i>
                                <span>{error}</span>
                            </div>
                        )}

                        <button 
                            type="submit" 
                            disabled={loading}
                            className={`w-full flex justify-center items-center py-2.5 px-4 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary ${loading ? 'bg-blue-400 cursor-not-allowed' : 'bg-primary hover:bg-blue-600'}`}
                        >
                            <span>{loading ? 'Đang xử lý...' : 'Đăng nhập hệ thống'}</span>
                            {loading && <i className="fa-solid fa-circle-notch fa-spin ml-2"></i>}
                        </button>                        
                    </form>
                </div>
                <button
                    onClick={handleConnectFacebook}
                    className="flex items-center gap-3 rounded-lg bg-[#1877F2] px-5 py-3 font-medium text-white shadow-sm transition hover:bg-[#166FE5] active:scale-[0.98]"
                    >
                    <svg
                        className="h-5 w-5"
                        viewBox="0 0 24 24"
                        fill="currentColor"
                    >
                        <path d="M24 12.073C24 5.405 18.627 0 12 0S0 5.405 0 12.073C0 18.099 4.388 23.093 10.125 24v-8.437H7.078v-3.49h3.047V9.413c0-3.025 1.792-4.704 4.533-4.704 1.312 0 2.686.235 2.686.235v2.973h-1.514c-1.491 0-1.955.93-1.955 1.886v2.27h3.328l-.532 3.49h-2.796V24C19.612 23.093 24 18.099 24 12.073z" />
                    </svg>

                    Kết nối Facebook Page
                </button>
            </div>
        </div>
    );
}
