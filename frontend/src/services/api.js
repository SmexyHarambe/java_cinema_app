import axios from 'axios'

const API_BASE_URL = '/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('auth-storage')
    if (token) {
      try {
        const parsed = JSON.parse(token)
        if (parsed?.state?.token) {
          config.headers.Authorization = `Bearer ${parsed.state.token}`
        }
      } catch (e) {
        console.error('Error parsing auth token', e)
      }
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status
    // Backend mengembalikan 403 (bukan cuma 401) untuk token yang hilang/
    // kedaluwarsa, karena tidak ada 401 entry point yang dikonfigurasi di
    // SecurityConfig. Anggap sesi habis -> paksa login ulang. Guard path
    // auth agar tidak redirect loop dari halaman login/register sendiri.
    if (status === 401 || status === 403) {
      const path = window.location.pathname
      if (!path.startsWith('/login') && !path.startsWith('/register')) {
        localStorage.removeItem('auth-storage')
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

export const authAPI = {
  login: (username, password) => api.post('/auth/login', { username, password }),
  register: (data) => api.post('/auth/register', data),
}

export const ticketAPI = {
  getAll: () => api.get('/public/tickets'),
  getById: (id) => api.get(`/public/tickets/${id}`),
  getFlashSale: () => api.get('/public/tickets/flash-sale'),
  search: (keyword) => api.get(`/public/tickets/search?keyword=${keyword}`),
  create: (data) => api.post('/admin/tickets', data),
  update: (id, data) => api.put(`/admin/tickets/${id}`, data),
  delete: (id) => api.delete(`/admin/tickets/${id}`),
}

export const customerAPI = {
  getBalance: () => api.get('/customer/balance'),
  topup: (amount) => api.post('/customer/topup', { amount }),
  getTransactions: () => api.get('/customer/transactions'),
  createTransaction: (data) => api.post('/customer/transactions', data),
  cancelTransaction: (id) => api.delete(`/customer/transactions/${id}`),
}

export const notificationAPI = {
  getUnread: () => api.get('/notifications/unread'),
  markAsRead: (id) => api.put(`/notifications/${id}/read`),
  delete: (id) => api.delete(`/notifications/${id}`),
}

export default api
