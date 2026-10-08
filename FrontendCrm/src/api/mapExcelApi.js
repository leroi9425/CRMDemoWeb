import axiosInstance from "./axiosInstance";

export const getMapExcel = () => axiosInstance.get("/mapExcels");