import React, { useContext } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Container } from 'react-bootstrap';

import { AuthContext } from './context/AuthContext';
import AppNavbar from './components/Navbar';
import Home from './pages/Home';
import Login from './pages/Login';
import Signup from './pages/Signup';
import Products from './pages/Products';
import Cart from './pages/Cart';
import AdminPanel from './pages/AdminPanel';

export default function App() {
  const { user, logout } = useContext(AuthContext);

  return (
    <BrowserRouter>
      <AppNavbar isAuthenticated={!!user} onLogout={logout} />
      <Container className="my-4">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={
            user ? <Navigate to="/" replace /> : <Login />
          } />
          <Route path="/signup" element={
            user ? <Navigate to="/" replace /> : <Signup />
          } />
          <Route path="/products" element={
            <Products token={user?.token} />
          } />
          <Route path="/cart" element={
            user ? <Cart token={user.token} /> : <Navigate to="/login" replace />
          } />
          <Route path="/admin" element={
            user?.roles.includes('ADMIN')
              ? <AdminPanel token={user.token} />
              : <Navigate to="/login" replace />
          } />
        </Routes>
      </Container>
    </BrowserRouter>
  );
}
