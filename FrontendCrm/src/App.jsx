import { useState } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { useAuth } from "./context/AuthContext";
import CustomerView from "./components/CustomerView";
import UserView from "./components/UserView";
import Login from "./components/Login";

function MainLayout() {
  const [activeTab, setActiveTab] = useState("customers");
  const { auth, logoutUser } = useAuth();

  return (
    <div className="text-slate-800 antialiased min-h-screen flex flex-col">
      <nav className="bg-white shadow-sm border-b border-slate-200 sticky top-0 z-10">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <i className="fa-solid fa-users text-primary text-2xl mr-3"></i>
              <span className="font-bold text-xl tracking-tight text-slate-900">CRM Lite</span>
            </div>
            
            <div className="flex items-center h-full space-x-8">
              <button 
                onClick={() => setActiveTab("customers")}
                className={`h-full border-b-2 font-medium text-sm transition-colors ${activeTab === 'customers' ? 'border-primary text-primary' : 'border-transparent text-slate-500 hover:text-slate-700 hover:border-slate-300'}`}
              >
                Khách hàng
              </button>
              {auth?.permissions?.includes("QUAN_LY_USER") && (
                <button 
                  onClick={() => setActiveTab("users")}
                  className={`h-full border-b-2 font-medium text-sm transition-colors ${activeTab === 'users' ? 'border-primary text-primary' : 'border-transparent text-slate-500 hover:text-slate-700 hover:border-slate-300'}`}
                >
                  Người dùng (System)
                </button>
              )}
            </div>

            <div className="flex items-center space-x-4">
                <span className="text-sm font-medium text-slate-600">Xin chào, {auth?.username}</span>
                <button onClick={logoutUser} className="text-sm font-medium text-danger hover:text-red-700 transition-colors">
                    Đăng xuất
                </button>
            </div>
          </div>
        </div>
      </nav>

      {activeTab === "customers" && <CustomerView />}
      {activeTab === "users" && auth?.permissions?.includes("QUAN_LY_USER") && <UserView />}
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
