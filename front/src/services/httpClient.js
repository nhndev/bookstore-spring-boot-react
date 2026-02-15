import axios from "axios";
// import jwt_decode from 'jwt-decode'
import messageUtil from "../utils/message.util";
import store from "../redux/store";
import { setErrorPage } from "../redux/slice/app.slice";

const httpClent = axios.create({
  // eslint-disable-next-line no-undef
  baseURL: process.env.REACT_APP_API_URL,
  headers: {
    "Content-Type": "application/json",
  },
  // withCredentials: true,
});

httpClent.interceptors.request.use(async (config) => {
  const accessToken = localStorage.getItem("accessToken");
  if (accessToken) {
    // const date = new Date()
    // const decodedToken = jwt_decode(accessToken)
    // if (decodedToken.exp < date.getTime() / 1000) {
    //   try {
    //     const res = await jwtAxios.post(`auth/refresh-token/`);
    //     const newAccessToken = res.data.token
    //     if (newAccessToken) {
    //       localStorage.setItem('accessToken', newAccessToken)
    //       config.headers.Authorization = `Bearer ${newAccessToken}`;
    //     }
    //   } catch (error) {
    //     if (error.response.status === 403 || error.response.status === 401) {
    //       localStorage.removeItem('accessToken')
    //     }
    //   }
    // } else {
    config.headers.Authorization = `Bearer ${accessToken}`;
    // }
  }
  return config;
});

httpClent.interceptors.response.use(
  (res) => res.data,
  (error) => {
    const status = error?.response?.status;
    let errorMessage = error?.response?.data?.errorMessage;
    if (!errorMessage) {
      const errorMessages = error?.response?.data?.errorMessages;
      if (errorMessages && errorMessages.length > 0) {
        errorMessage = errorMessages[0]?.message;
      }
    }
    const showErrorPage = status === 401 || status === 403 || status >= 500;
    if (showErrorPage) {
      if (!errorMessage) {
        if (status === 401) errorMessage = 'Bạn cần đăng nhập để truy cập trang này.';
        else if (status === 403) errorMessage = 'Bạn không có quyền truy cập trang này.';
        else if (status >= 500) errorMessage = 'Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.';
      }
      store.dispatch(setErrorPage({ status, message: errorMessage }));
      return Promise.reject(error);
    }
    messageUtil.showErrorMessage(errorMessage);
    return Promise.reject(error);
  }
);

export default httpClent;
