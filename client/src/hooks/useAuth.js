import { useState } from 'react';
import { authService } from '../services/authService';

export const useAuth = () => {
  const [action, setAction] = useState("Sign Up");
  const { login, register, loading, error } = useAuth();
};