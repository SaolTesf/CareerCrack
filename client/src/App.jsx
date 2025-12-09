import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import logo from './Components/Assets/CareerCrack-logo.png';
import { ProtectedRoute } from './routes/ProtectedRoute';
import { LoginSignup } from './Components/LoginSignUp/LoginSignup';
import { useAuth } from './context/AuthContext';
import { Home } from './Components/Home/Home';
import { AuthProvider } from './context/AuthContext';
import { Navbar } from './components/NavBar/Navbar';
function App() {
  const { isAuthenticated } = useAuth();
  return (
    <BrowserRouter>
      <AuthProvider>
        {isAuthenticated && location.pathname !== '/login' && <Navbar />}
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
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
