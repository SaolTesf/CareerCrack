import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import logo from './Components/Assets/CareerCrack-logo.png';
import { ProtectedRoute } from './routes/ProtectedRoute';
import { LoginSignup } from './Components/LoginSignUp/LoginSignup';
import { Home } from './Components/Home/Home';
import { AuthProvider } from './context/AuthContext';

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
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
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
