import axiosInstance from "./axiosInstance";

export const getCustomers = () => axiosInstance.get("/customers");
export const getCustomersPage = (index) => axiosInstance.get(`/customers/page=${index}`);
export const filterCustomersPage = (index, data) => axiosInstance.post(`/customers/filter/page=${index}`, data);
export const getCustomerById = (id) => axiosInstance.get(`/customers/${id}`);
export const getCustomerDetail = (id) => axiosInstance.get(`/customers/detail/${id}`);
export const createCustomer = (data) => axiosInstance.post("/customers", data);
export const updateCustomer = (id, data) => axiosInstance.put(`/customers/${id}`, data);
export const deleteCustomer = (id) => axiosInstance.delete(`/customers/${id}`);

export const importCustomer = (data) => axiosInstance.post('/customers/import', data); 
export const exportCustomer = (data, fields) => {
    const fieldsQuery = fields && fields.length > 0 ? `?fields=${fields.join(',')}` : '';
    return axiosInstance.post(`/customers/export${fieldsQuery}`, data, {responseType: "blob"});
}

export const getExportTemplates = () => axiosInstance.get('/exportTemplates');
export const saveExportTemplate = (data) => axiosInstance.post('/exportTemplates', data);
