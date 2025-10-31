import { apiClient } from './apiClient'

export const authService = {
  async login(credentials) {
    const data = await apiClient.post('/auth/login', credentials);
    if (data.token) {
      localStorage.setItem('authToken', data.token);
      localStorage.setItem('user', JSON.stringify(data.user));
    }
    return data;
  },

  async register(userData) {
    const data = await apiClient.post('/auth/register', credentials);
    if (data.token) {
      localStorage.setItem('authToken', data.token);
      localStorage.setItem('user', JSON.stringify(data.user));
    }
    return data;
  },

  logout () {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
  },

  getCurrentUser() {
    const user = localStorage.getItem('user')
    return user ? JSON.stringify(user) : null;
  },

  isAuthenticated() {
    return !!localStorage.getItem('authToken');
  },

};