import { useState, useEffect } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { useAuth } from "./context/AuthContext";
import CustomerView from "./components/CustomerView";
import UserView from "./components/UserView";
import Login from "./components/Login";
import ProductView from "./components/ProductView";

import RoleManagerTab from "./components/RoleManagerTab";
import CompanyView from "./components/CompanyView";
import { connectWebSocket } from "./services/websocket";

function MainLayout() {
  const [activeTab, setActiveTab] = useState("customers");
  const { auth, logoutUser } = useAuth();
  const [toastMessage, setToastMessage] = useState(null);

  useEffect(() => {
    // Chỉ kết nối khi đã có auth.username
    if (!auth || !auth.username) return;
    
    const client = connectWebSocket(
        auth.username,
        (notification) => {
            console.log("🔔 Notification:", notification);
            // Hiện Toast nổi lên
            setToastMessage(notification.message);
            // 5 giây sau tự tắt
            setTimeout(() => setToastMessage(null), 5000);
        }
    );

    return () => {
        client.deactivate();
    };
  }, [auth]);

  return (
    <div className="text-slate-800 antialiased min-h-screen w-full flex flex-col bg-slate-50 relative">
      {/* KHỐI TOAST THÔNG BÁO NỔI LÊN */}
      <div className={`fixed top-4 right-4 z-[9999] transition-all duration-500 transform ${toastMessage ? "translate-x-0 opacity-100" : "translate-x-full opacity-0"}`}>
        <div className="bg-white border-l-4 border-blue-500 shadow-xl rounded-md p-4 flex items-center max-w-sm">
          <div className="w-10 h-10 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center mr-3 flex-shrink-0">
            <i className="fa-solid fa-bell animate-wiggle"></i>
          </div>
          <div>
            <h4 className="text-slate-800 font-bold text-sm">Thông báo mới</h4>
            <p className="text-slate-600 text-sm mt-0.5">{toastMessage}</p>
          </div>
        </div>
      </div>
      <nav className="bg-white shadow-sm border-b border-slate-200 sticky top-0 z-10">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <i className="fa-solid fa-users text-blue-600 text-2xl mr-3"></i>
              <span className="font-bold text-xl tracking-tight text-slate-900">CRM Lite</span>
            </div>
            
            <div className="flex items-center h-full space-x-8">
              {auth?.permissions?.includes("QUAN_LY_KHACH_HANG") && (
                <button 
                onClick={() => setActiveTab("customers")}
                className={`h-full border-b-2 font-medium text-sm transition-colors ${activeTab === 'customers' ? 'border-blue-600 text-blue-600' : 'border-transparent text-slate-500 hover:text-slate-700 hover:border-slate-300'}`}
                >
                  Khách hàng
                </button>
              )}
              {auth?.permissions?.includes("QUAN_LY_SAN_PHAM") && (
                <button 
                onClick={() => setActiveTab("products")}
                className={`h-full border-b-2 font-medium text-sm transition-colors ${activeTab === 'products' ? 'border-blue-600 text-blue-600' : 'border-transparent text-slate-500 hover:text-slate-700 hover:border-slate-300'}`}
                >
                  Sản phẩm
                </button>
              )}              
              {auth?.permissions?.includes("QUAN_LY_QUYEN") && (
                <button
                onClick={() => setActiveTab("roles")}
                className={`h-full border-b-2 font-medium text-sm transition-colors ${activeTab === 'roles' ? 'border-blue-600 text-blue-600' : 'border-transparent text-slate-500 hover:text-slate-700 hover:border-slate-300'}`}
                >
                  Nhóm Quyền
                </button>
              )}              
              {auth?.permissions?.includes("QUAN_LY_USER") && (
                <button 
                  onClick={() => setActiveTab("users")}
                  className={`h-full border-b-2 font-medium text-sm transition-colors ${activeTab === 'users' ? 'border-blue-600 text-blue-600' : 'border-transparent text-slate-500 hover:text-slate-700 hover:border-slate-300'}`}
                >
                  Người dùng (System)
                </button>
              )}
              {auth?.permissions?.includes("QUAN_LY_CONG_TY") && (
                <button 
                  onClick={() => setActiveTab("companies")}
                  className={`h-full border-b-2 font-medium text-sm transition-colors ${activeTab === 'companies' ? 'border-blue-600 text-blue-600' : 'border-transparent text-slate-500 hover:text-slate-700 hover:border-slate-300'}`}
                >
                  Công ty
                </button>
              )}
            </div>

            <div className="flex items-center space-x-4">
                <span className="text-sm font-medium text-slate-600">Xin chào, {auth?.username}</span>
                <button onClick={logoutUser} className="text-sm font-medium text-red-500 hover:text-red-700 transition-colors">
                    Đăng xuất
                </button>
            </div>
          </div>
        </div>
      </nav>

      {activeTab === "customers" && auth?.permissions?.includes("QUAN_LY_KHACH_HANG") && <CustomerView />}
      {activeTab === "products" && auth?.permissions?.includes("QUAN_LY_SAN_PHAM") && <ProductView />}
      {activeTab === "roles" && auth?.permissions?.includes("QUAN_LY_QUYEN") && <RoleManagerTab />}
      {activeTab === "users" && auth?.permissions?.includes("QUAN_LY_USER") && <UserView />}
      {activeTab == "companies" && auth?.permissions?.includes("QUAN_LY_CONG_TY") && <CompanyView />}
    </div>
  );
}

function ProtectedRoute({ children }) {
  const { auth, loading } = useAuth();
  
  if (loading) return <div className="min-h-screen flex items-center justify-center">Đang tải...</div>;
  if (!auth) return <Navigate to="/login" />;
  
  return children;
}

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/*" element={
          <ProtectedRoute>
            <MainLayout />
          </ProtectedRoute>
        } />
      </Routes>
    </Router>
  );
}

export default App;
