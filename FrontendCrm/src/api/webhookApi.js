import axiosInstance from "./axiosInstance";

export const getFacebookWebhook = (data) => axiosInstance.get("/facebook/connect", data);
export const getZaloWebhook = (data) => axiosInstance.get("/zalo/connect", data);