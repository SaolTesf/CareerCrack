import React, { useState } from 'react';

import { BrowserRouter, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import logo from './Components/Assets/CareerCrack-logo.png';
import { ProtectedRoute } from './routes/ProtectedRoute';
import { LoginSignup } from './Components/LoginSignUp/LoginSignup';
import { Home } from './Components/Home/Home';
import { AuthProvider, useAuth } from './context/AuthContext';
import { Navbar } from './components/NavBar/Navbar';

function AppRouter() {
  const location = useLocation();
  const { isAuthenticated } = useAuth();
  const showNavbar = isAuthenticated && location.pathname !== '/login';
  //const showNavbar = false;

  const [isNavbarMinimized, setIsNavbarMinimized] = useState(false);

  return (
    <>
      <div className={(showNavbar ? 'main-content-navbar ' : 'main-content ') + (isNavbarMinimized ? 'minimized' : '')}>
        { showNavbar && (
          <Navbar 
            isMinimized={isNavbarMinimized}
            setIsMinimized={setIsNavbarMinimized}
          />
        )}
        <Routes>
          <Route path='/login' element={<LoginSignup />} />
          <Route 
            path='/home'
            element={
              <ProtectedRoute>
                <Home />
              </ProtectedRoute>
            }
          />
          <Route path='/' element={<Navigate to="/home" replace />} />
          <Route
            path='/problems'
            element={
              <ProtectedRoute>
                <div>Put Problem Component Here</div>
              </ProtectedRoute>
            }
          />
          <Route 
            path='/applications'
            element={
              <ProtectedRoute>
                <div>Put Application Component Here</div>
              </ProtectedRoute>
            }
          />

          <Route
            path='/settings'
            element={
              <ProtectedRoute>
                <div>Put Settings Component Here</div>
              </ProtectedRoute>
            }
          />
        </Routes>
      </div>
    </>
  );
}

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppRouter />
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
