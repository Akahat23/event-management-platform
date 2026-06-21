import React, { createContext, useState, useEffect } from 'react';
import axios from 'axios';

export const AuthContext = createContext();

const API_BASE = 'http://localhost:8080/api';

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Load persisted session on app start
    const storedUser = localStorage.getItem('el_user');
    const storedToken = localStorage.getItem('el_token');
    
    if (storedUser && storedToken) {
      setUser(JSON.parse(storedUser));
      setToken(storedToken);
      // Configure default axios authorization header
      axios.defaults.headers.common['Authorization'] = `Bearer ${storedToken}`;
    }
    setLoading(false);
  }, []);

  const login = async (email, password) => {
    try {
      const res = await axios.post(`${API_BASE}/auth/login`, { email, password });
      const { token, name, role } = res.data;
      
      const userData = { email, name, role };
      setUser(userData);
      setToken(token);
      
      localStorage.setItem('el_user', JSON.stringify(userData));
      localStorage.setItem('el_token', token);
      
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      return { success: true };
    } catch (err) {
      console.error('Login error:', err);
      const message = err.response?.data?.message || 'Invalid email or password.';
      return { success: false, message };
    }
  };

  const register = async (name, email, password) => {
    try {
      const res = await axios.post(`${API_BASE}/auth/register`, { name, email, password });
      const { token, role } = res.data;
      
      const userData = { email, name, role };
      setUser(userData);
      setToken(token);
      
      localStorage.setItem('el_user', JSON.stringify(userData));
      localStorage.setItem('el_token', token);
      
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      return { success: true };
    } catch (err) {
      console.error('Registration error:', err);
      const message = err.response?.data?.message || 
                      (err.response?.data?.validationErrors ? Object.values(err.response.data.validationErrors).join(', ') : 'Registration failed.');
      return { success: false, message };
    }
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('el_user');
    localStorage.removeItem('el_token');
    delete axios.defaults.headers.common['Authorization'];
  };

  return (
    <AuthContext.Provider value={{ user, token, login, register, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
};
