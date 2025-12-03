import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export const ProtectedRoute = ( { children }) => {
  const  { isAuthenticated, loading } = useAuth();

  if (loading) {
    return <div>Loading...</div>;
    // TODO: update to have a loading component
  }

  return isAuthenticated ? children : <Navigate to="/login" replace />

}
