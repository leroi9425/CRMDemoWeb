import axiosInstance from "./axiosInstance";

export const getCompanies = () => axiosInstance.get('/companies');
export const getCompaniesById = (id) => axiosInstance.get(`/companies/${id}`);
export const createCompany = (data) => axiosInstance.post("/companies", data);
export const updateCompany = (data) => axiosInstance.put(`/companies/${id}`, data);
export const deleteCompany = (id) => axiosInstance.delete(`/companies/${id}`);
export const getCompanyDetail = (id) => axiosInstance.get(`/companies/${id}/detail`);
export const getCompanyPerPage = (index) => axiosInstance.get(`/companies/page=${index}`);
