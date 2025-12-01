import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext'
import { Navigate, useNavigate } from 'react-router-dom';

export const Home = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  }

  return (
    <div className='container'>
      <h1>Welcome Back, {user?.firstName}</h1>
      <p>Email: {user?.email}</p>
      <p>Username: {user?.username}</p>
      <button onClick={handleLogout}>Logout</button>
    </div>
  )
}