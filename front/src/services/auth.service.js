import httpClent from './httpClient'

const AUTH_API = '/api/v1/auth';

const authService = {
    register: (data) => {
        const url = AUTH_API + '/register'
        return httpClent.post(url, data)
    },
    login: (data) => {
        const url = AUTH_API + '/login'
        return httpClent.post(url, data)
    },
    activeAccount: ({active_code}) => {
        const url = 'auth/verify-email'
        return httpClent.get(url, { params: {active_code}})
    },
    requestActiveAccount: ({email}) => {
        const url = `auth/send-verification-email/${email}`
        return httpClent.get(url)
    },
    forgotPassword: (data) => {
        const url = 'auth/forgot-password'
        return httpClent.post(url, data)
    },
    resetPassword: (data) => {
        const url = 'auth/reset-password'
        return httpClent.patch(url, data)
    },
    getRefreshToken: () => {
        const url = 'auth/refresh-token'
        return httpClent.post(url)
    },
    me: () => {
        const url = 'auth/me'
        return httpClent.get(url)
    },
    logout: () => {
        const url = `auth/logout`
        return httpClent.get(url)
    }

}

export default authService
