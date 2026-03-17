import axios from "axios";

const http = axios.create({
    baseURL: "",
    timeout: 10000,
});

http.interceptors.request.use((config) => {
    const token = localStorage.getItem("token");
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});
http.interceptors.response.use(
    (res) => res.data,
    (err) => {
        const status = err?.response?.status;

        if (status === 401) {
            localStorage.removeItem("token");
            localStorage.removeItem("userId");
            localStorage.removeItem("tokenType");

            // 避免循环跳转：只有不在 /login 才跳
            if (window.location.pathname !== "/login") {
                window.location.href = "/login";
            }
        }

        return Promise.reject(err);
    }
);
// http.interceptors.response.use(
//     (res) => res.data, // 直接拿到 {code,message,data}
//     (err) => {
//         // 先简单处理：抛出错误给页面
//         return Promise.reject(err);
//     }
// );

export default http;