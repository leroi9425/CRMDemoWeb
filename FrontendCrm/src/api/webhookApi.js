import axiosInstance from "./axiosInstance";

export const getFacebookWebHook = (data) => axiosInstance.get("/facebook/connect", data);