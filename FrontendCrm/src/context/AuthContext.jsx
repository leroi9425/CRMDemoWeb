import { createContext, useContext, useState, useEffect } from "react";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [auth, setAuth] = useState(null);
    const [loading, setLoading] = useState(true);

    const parseJwt = (token) => {
        try {
            return JSON.parse(atob(token.split('.')[1]));
        } catch (e) {
            return null;
        }
    };

    useEffect(() => {
        const storedToken = localStorage.getItem("token");
        const storedUser = localStorage.getItem("user");
        const storedRole = localStorage.getItem("role");
        const storedPermissions = localStorage.getItem("permissions");
        
        if (storedToken && storedUser) {
            setAuth({
                token: storedToken,
                username: storedUser,
                role: storedRole,
                permissions: storedPermissions ? JSON.parse(storedPermissions) : []
            });
        }
        setLoading(false);
    }, []);

    const storeData = (data) => {
        localStorage.setItem("token", data.token);
        localStorage.setItem("user", data.username);
        localStorage.setItem("role", data.role);
        
        // Nhận mảng permissions từ cục JSON response và lưu dạng chuỗi
        const perms = data.permissions || [];
        localStorage.setItem("permissions", JSON.stringify(perms));
        
        setAuth({
            ...data,
            permissions: perms
        });
    };

    const logoutUser = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        localStorage.removeItem("role");
        localStorage.removeItem("permissions");
        setAuth(null);
    };

    return (
        <AuthContext.Provider value={{ auth, storeData, logoutUser, loading }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);
