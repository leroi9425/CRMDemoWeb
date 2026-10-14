import axiosInstance from "./axiosInstance";

export const getFacebookPage = (index) => axiosInstance.get(`/facebookPage?page=${index}`);
