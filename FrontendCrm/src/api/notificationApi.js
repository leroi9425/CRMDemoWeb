import axiosInstance from "./axiosInstance";

// tự nghĩ nên có gì có thể lỗi sẽ nằm ở đây
export const notifiToSpring = (subcription) => axiosInstance.post("/push/subcription", subcription);