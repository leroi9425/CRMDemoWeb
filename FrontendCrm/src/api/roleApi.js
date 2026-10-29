import axiosInstance from "./axiosInstance";

export const getRoles = () => axiosInstance.get('/roles');
export const getRolePermission = (id) => axiosInstance.get(`/roles/${id}/permissions`);
export const putRoles = (id, data) => axiosInstance.put(`/roles/${id}`, data);
export const postRoles = (data) => axiosInstance.post(`/roles`, data);
export const postRolePermission = (id, permissionIds) => axiosInstance.post(`/roles/${id}/permissions`, {
        permissionIds
    });