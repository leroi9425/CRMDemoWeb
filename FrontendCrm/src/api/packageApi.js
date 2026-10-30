import axiosInstance from "./axiosInstance";

// Lấy danh sách toàn bộ gói (GET /api/packages)
export const getPackages = () => axiosInstance.get("/packages");

// Lấy chi tiết gói theo ID (GET /api/packages/{id})
export const getPackageById = (id) => axiosInstance.get(`/packages/${id}`);

// Thêm gói mới (POST /api/packages)
export const createPackage = (data) => axiosInstance.post("/packages", data);

// Cập nhật gói (PUT /api/packages/{id})
export const updatePackage = (id, data) => axiosInstance.put(`/packages/${id}`, data);

// Xóa gói (DELETE /api/packages/{id})
export const deletePackage = (id) => axiosInstance.delete(`/packages/${id}`);
