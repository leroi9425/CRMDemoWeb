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
        const storedRole = localStorage.getItem("role"); // raw string like "ADMIN"
        
        if (storedToken && storedUser) {
            const decoded = parseJwt(storedToken);
            setAuth({
                token: storedToken,
                username: storedUser,
                role: storedRole,
                permissions: decoded?.authorities || []
            });
        }
        setLoading(false);
    }, []);

    const loginUser = (data) => {
        localStorage.setItem("token", data.token);
        localStorage.setItem("user", data.username);
        localStorage.setItem("role", data.role);
        
        const decoded = parseJwt(data.token);
        
        setAuth({
            ...data,
            permissions: decoded?.authorities || []
        });
    };

    const logoutUser = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        localStorage.removeItem("role");
        setAuth(null);
    };

    return (
        <AuthContext.Provider value={{ auth, loginUser, logoutUser, loading }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);
