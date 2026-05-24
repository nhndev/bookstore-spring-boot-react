import httpClent from './httpClient'

const AUTH_API = '/api/v1/auth';

const authService = {
    register: (data) => httpClent.post(`${AUTH_API}/register`, data),
    login: (data) => httpClent.post(`${AUTH_API}/login`, data),
    verifyEmail: ({ verificationCode, email }) =>
        httpClent.get(`${AUTH_API}/verify-email`, { params: { verificationCode, email } }),
    resendEmailVerification: (data) =>
        httpClent.post(`${AUTH_API}/resend-email-verification`, data),
    forgotPassword: (data) => httpClent.post(`${AUTH_API}/forgot-password`, data),
    resetPassword: (data) => httpClent.post(`${AUTH_API}/reset-password`, data),
    refreshToken: () => httpClent.post(`${AUTH_API}/refresh-token`),
    logout: () => httpClent.post(`${AUTH_API}/logout`, {}, { withCredentials: true }),
};

export default authService;
