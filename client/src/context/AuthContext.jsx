import React, { createContext, useContext, useState, useEffect } from 'react';
import { authService } from '../services/authService';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Initialize on mount to ensure all variables are available in the DOM
  useEffect(() => {
    const initAuth = () => {
      try {
        const currentUser = authService.getCurrentUser;
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

}