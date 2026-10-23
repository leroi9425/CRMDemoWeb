import axiosInstance from "./axiosInstance";

export const getFacebookPages = (index) => axiosInstance.get(`/facebookPage?page=${index}`);
export const deleteFacebookPage = (id) => axiosInstance.delete(`/facebookPage/${id}`);

export const getZaloOAPages = (index) => axiosInstance.get(`/zaloOa?page=${index}`);
export const deleteZaloOa = (id) => axiosInstance.delete(`zaloOa/${id}`);
