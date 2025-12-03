import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './LoginSignup.css';
import { Login } from '../Login/Login';
import { SignUp } from '../SignUp/SignUp';
import { useAuth } from '../../context/AuthContext';

export const LoginSignup = () => {
  const [action, setAction] = useState("Sign Up");
  const { login, register, loading, error, clearError, isAuthenticated } = useAuth();
  const navigate = useNavigate();

  // will navigate automatically when the user is authenticated
  useEffect(() => {
    if(isAuthenticated) {
      navigate('/home', { replace: true });
    }
  }, [isAuthenticated, navigate]);

  const handleLogin = async (credentials) => {
    try {
      await login(credentials);
      console.log('Login Successful');
    }
    catch(error) {
      console.error('Login failed: ', error);
    }
  }

  const handleRegister = async (userData) => {
    try {
      await register(userData);
      console.log('Register Successful');
    }
    catch(error) {
      console.error('Registration failed: ', error);
    }
  }

  const switchAction = (next) => {
    if (action === next || loading) return; 
    clearError();
    setAction(next);
  }

  return (
    <div className="container">
      <div className="header">
        <div className="text">{action}</div>
        <div className="underline" />
      </div>
      
      {error && <div className='error-message'>{error}</div>}

      {action === "Login" ? (
        <Login onSubmit={handleLogin} loading={loading} />
        ) : (
        <SignUp onSubmit={handleRegister} loading={loading} />
      )}
      
      <div className='auth-switch'>
        {action === "Login" ? (
          <div>Don't have an account? <span onClick={() => switchAction("Sign Up")} disabled={loading}>Sign Up</span></div>
        )
        : ( 
        <div>Have an account? <span onClick={() => switchAction("Login")} disabled={loading}>Login</span></div>
        )}

      </div>
    </div>
  )
}
