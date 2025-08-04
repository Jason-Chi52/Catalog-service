// src/App.jsx

import React, { useContext } from 'react'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { Container } from 'react-bootstrap'

import { AuthContext } from './context/AuthContext.jsx'
import AppNavbar  from './components/Navbar.jsx'
import Home       from './pages/Home.jsx'
import Login      from './pages/Login.jsx'
import Signup     from './pages/Signup.jsx'
import Products   from './pages/Products.jsx'
import Cart       from './pages/Cart.jsx'
import AdminPanel from './pages/AdminPanel.jsx'

export default function App() {
  const { user, logout } = useContext(AuthContext)
  const token = user?.token

  return (
    <BrowserRouter>
      <AppNavbar isAuthenticated={!!token} onLogout={logout} />

      <Container className="my-4">
        <Routes>
          <Route path="/" element={<Home />} />

          <Route
            path="/login"
            element={
              !token
                ? <Login />
                : <Navigate to="/" replace />
            }
          />

          <Route
            path="/signup"
            element={
              !token
                ? <Signup />
                : <Navigate to="/" replace />
            }
          />

          {/* Public products listing */}
          <Route path="/products" element={<Products />} />

          {/* Cart page, only for authenticated users */}
          <Route
            path="/cart"
            element={
              token
                ? <Cart />
                : <Navigate to="/login" replace />
            }
          />

          {/* Admin panel, only for users with ADMIN role */}
          <Route
            path="/admin"
            element={
              user?.roles?.includes('ADMIN')
                ? <AdminPanel />
                : <Navigate to="/login" replace />
            }
          />

          {/* Catch-all redirects to home */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Container>
    </BrowserRouter>
  )
}
