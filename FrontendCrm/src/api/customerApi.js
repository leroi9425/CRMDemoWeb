import axiosInstance from "./axiosInstance";

export const getCustomers = () => axiosInstance.get("/customers");
export const getCustomersPage = (index) => axiosInstance.get(`/customers/page=${index}`);
export const getCustomerById = (id) => axiosInstance.get(`/customers/${id}`);
export const createCustomer = (data) => axiosInstance.post("/customers", data);
export const updateCustomer = (id, data) => axiosInstance.put(`/customers/${id}`, data);
export const deleteCustomer = (id) => axiosInstance.delete(`/customers/${id}`);
export const importCustomer = (data) => axiosInstance.post('/customers/import', data); 
