import axiosInstance from "./axiosInstance";

export const getProducts = () => axiosInstance.get("/products");
export const getProductById = (id) => axiosInstance.get(`/products/`);
export const createProduct = (data) => axiosInstance.post("/products", data);
export const updateProduct = (id, data) => axiosInstance.put(`/products/`, data);
export const deleteProduct = (id) => axiosInstance.delete(`/products/`);
