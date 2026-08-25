import axiosInstance from "./axiosInstance";

export const getProducts = () => axiosInstance.get("/products");
export const getProductById = (id) => axiosInstance.get(`/products/${id}`);
export const createProduct = (data) => axiosInstance.post("/products/create", data);
export const updateProduct = (id, data) => axiosInstance.put(`/products/${id}`, data);
export const deleteProduct = (id) => axiosInstance.delete(`/products/${id}`);