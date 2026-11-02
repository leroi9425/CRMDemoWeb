import axiosInstance from "./axiosInstance";

export const getPermissionTreeUserColumn = () => axiosInstance.get("/permissions/tree/user/column");
export const getPermissionTreeAdminColumn = () => axiosInstance.get("/permissions/tree/admin/column");
