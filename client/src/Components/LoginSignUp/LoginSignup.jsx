import React, { useState } from 'react';
import './LoginSignup.css';
import { Login } from '../Login/Login';
import { SignUp } from '../SignUp/SignUp';
import { useAuth } from '../../hooks/useAuth';

export const LoginSignup = () => {
  const [action, setAction] = useState("Sign Up");
  const { login, register, loading, error } = useAuth();

  const handleLogin = async (credentials) => {
    try {
      await login(credentials);
      console.log('Login Successful');
      // add redirect logic after done
    }
    catch(error) {
      console.error('Login failed: ', error);
    }
  }

  const handleRegister = async (userData) => {
    try {
      await register(userData);
      console.log('Register Successful');
      // add redirect logic after done
    }
    catch(error) {
      console.error('Registration failed: ', error);
    }
  }

  return (
    <div className="container">
      <div className="header">
        <div className="text">{action}</div>
        <div className="underline" />
      </div>

      {action === "Login" ? <Login /> : <SignUp />}

      {action === "Login" && (
        <div className="forgot-password">
          Forgot Password? <span>Click Here!</span>
        </div>
      )}

      <div className="submit-container">
        <div 
          className={action === "Login" ? "submit gray" : "submit"} 
          onClick={() => setAction("Sign Up")}>Sign Up</div>
        <div 
          className={action === "Sign Up" ? "submit gray" : "submit"} 
          onClick={() => setAction("Login")}>Login</div>
        </div>
    </div>
  )
}
