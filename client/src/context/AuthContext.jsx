import React, { createContext, useContext, useState, useEffect } from 'react';
import { authService } from '../services/authService';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [fieldErrors, setFieldErrors] = useState({});
  const [formError, setFormError] = useState(null);

  // Initialize on mount to ensure all variables are available in the DOM
  useEffect(() => {
    const initAuth = () => {
      try {
        const currentUser = authService.getCurrentUser();
        const isAuthenticated = authService.isAuthenticated();

        if(isAuthenticated && currentUser) {
          setUser(currentUser);
        }
      }
      catch(error) {
        console.error('Auth initialization failed: ', error)
      }
      finally {
        setLoading(false);
      }
    };

    initAuth();
  }, []);

    const login = async (credentials) => {
    try {
      setLoading(true);
      setFormError(null);
      setFieldErrors(null);
      const data = await authService.login(credentials);
      setUser(data.user);
      return data;
    }
    catch (error) {
      setFormError(error.message);
      setFieldErrors(error.fieldErrors);
      throw error;
    }
    finally {
      setLoading(false);
    }
  };

  const register = async (userData) => {
    try{
      setLoading(true);
      setFormError(null);
      setFieldErrors(null);
      const data = await authService.register(userData);
      setUser(data.user);
      return data
    }
    catch (error) {
      setFormError(error.message);
      setFieldErrors(error.fieldErrors);
      throw error;
    }
    finally {
      setLoading(false);
    }
  };

  const logout = () => {
    authService.logout();
    setUser(null);
  };

  const value = {
    user,
    loading,
    formError,
    fieldErrors,
    login,
    register,
    logout,
    clearError: () => { setFormError(null); setFieldErrors(null); },
    isAuthenticated: !!user,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}