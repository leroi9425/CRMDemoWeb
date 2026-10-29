import axiosInstance from "./axiosInstance";

export const getPermissionTree = () => axiosInstance.get("/permission/tree");