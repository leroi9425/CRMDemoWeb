import axios from "axios";

const axiosInstance = axios.create({
    baseURL: "http://localhost:8080/api",
    withCredentials: true,
});

axiosInstance.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem("token");
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

axiosInstance.interceptors.response.use(
    (response) => response,
    async (error) => {
        if (error.response && error.response.status === 401) {
            // Token expired or invalid
            localStorage.removeItem("token");
            localStorage.removeItem("user");
            localStorage.removeItem("role");
            localStorage.removeItem("permissions");
            window.location.href = "/login";
        }

        if (error.response && error.response.status === 403) {
            try {
                const token = localStorage.getItem("token");
                // Tự động gọi API lấy quyền mới nhất
                const res = await axios.get("http://localhost:8080/api/auth/me/permissions", {
                    headers: { Authorization: `Bearer ${token}` }
                });
                
                // Lưu đè mảng quyền mới vào Local Storage
                localStorage.setItem("permissions", JSON.stringify(res.data));
                
                // Thông báo và bắt buộc F5 giao diện
                if (window.confirm("Quyền của bạn vừa bị thay đổi bởi Quản trị viên! Bấm OK để tải lại giao diện.")) {
                    window.location.reload();
                }
            } catch (err) {
                console.error("Lỗi khi tải lại quyền", err);
            }
        }
        return Promise.reject(error);
    }
);

export default axiosInstance;
