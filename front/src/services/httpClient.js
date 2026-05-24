import axios from "axios";
import store from "../redux/store";
import { setErrorPage } from "../redux/slice/app.slice";
import { showErrorDialog } from "../redux/slice/confirmDialog.slice";
import { clearUser } from "../redux/slice/auth.slice";

const httpClent = axios.create({
  baseURL: process.env.REACT_APP_API_URL,
  headers: { "Content-Type": "application/json" },
});

const refreshClient = axios.create({
  baseURL: process.env.REACT_APP_API_URL,
  headers: { "Content-Type": "application/json" },
  withCredentials: true,
});

let refreshTokenPromise = null;

httpClent.interceptors.request.use(async (config) => {
  const accessToken = localStorage.getItem("accessToken");
  if (accessToken) {
    config.headers.Authorization = `Bearer ${accessToken}`;
  }
  if (config.url?.includes("/auth/logout")) {
    config.withCredentials = true;
  }
  return config;
});

httpClent.interceptors.response.use(
  (res) => res.data,
  async (error) => {
    const originalRequest = error.config;
    const status = error?.response?.status;

    if (status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      if (!refreshTokenPromise) {
        refreshTokenPromise = refreshClient
          .post("/api/v1/auth/refresh-token")
          .then((res) => {
            const newAccessToken = res.data?.data?.accessToken;
            if (newAccessToken) {
              localStorage.setItem("accessToken", newAccessToken);
            }
            return newAccessToken;
          })
          .catch((refreshError) => {
            localStorage.removeItem("accessToken");
            store.dispatch(clearUser());
            window.location.href = "/dang-nhap";
            return Promise.reject(refreshError);
          })
          .finally(() => {
            refreshTokenPromise = null;
          });
      }

      try {
        const newToken = await refreshTokenPromise;
        if (newToken) {
          originalRequest.headers.Authorization = `Bearer ${newToken}`;
          return httpClent(originalRequest);
        }
      } catch {
        return Promise.reject(error);
      }
    }

    let errorMessage = error?.response?.data?.errorMessage;
    if (!errorMessage) {
      const errorMessages = error?.response?.data?.errorMessages;
      if (errorMessages && errorMessages.length > 0) {
        errorMessage = errorMessages[0]?.message;
      }
    }

    if (status === 403 || status >= 500) {
      if (!errorMessage) {
        if (status === 403) errorMessage = "Bạn không có quyền truy cập trang này.";
        else if (status >= 500) errorMessage = "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.";
      }
      store.dispatch(setErrorPage({ status, message: errorMessage }));
      return Promise.reject(error);
    }

    store.dispatch(showErrorDialog({ message: errorMessage || "Đã xảy ra lỗi." }));
    return Promise.reject(error);
  }
);

export default httpClent;
