import { useState } from 'react';
import { authService } from '../services/authService';

export const useAuth = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const login = async (credentials) => {
    try {
      setLoading(true);
      setError(null);
      const data = await authService.login(credentials);
      return data;
    }
    catch (error) {
      setError(error.message);
      throw error;
    }
    finally {
      setLoading(false);
    }
  };

  const register = async (userData) => {
    try{
      setLoading(true);
      setError(null);
      const data = await authService.register(userData);
      return data
    }
    catch (error) {
      setError(error.message);
      throw error;
    }
    finally {
      setLoading(false);
    }
  };

  const logout = () => {
    authService.logout();
  };

  return {
    login,
    register,
    logout,
    loading,
    error,
    clearError: () => setError(null),
    isAuthenticated: authService.isAuthenticated(),
    currentUser: authService.getCurrentUser(),
  };
};