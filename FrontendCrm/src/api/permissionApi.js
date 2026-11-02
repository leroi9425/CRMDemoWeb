import axiosInstance from "./axiosInstance";

export const getPermissionTree = () => axiosInstance.get("/permission/tree");
export const getPermissionTreeColumn = () => axiosInstance.get("/permissions/tree/column");
