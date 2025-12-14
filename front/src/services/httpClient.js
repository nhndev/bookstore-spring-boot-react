import axios from "axios";
// import jwt_decode from 'jwt-decode'
import messageUtil from "../utils/message.util";

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
    let errorMessage = error?.response?.data.errorMessage;
    if (!errorMessage) {
      const errorMessages = error?.response?.data?.errorMessages;
      if (errorMessages && errorMessages.length > 0) {
        errorMessage = errorMessages[0]?.message;
      }
    }
    messageUtil.showErrorMessage(errorMessage);
    return Promise.reject(error);
  }
);

export default httpClent;
