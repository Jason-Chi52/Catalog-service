import React, { useState, useEffect } from 'react'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { Container } from 'react-bootstrap'

import AppNavbar from './components/Navbar'
import Home       from './pages/Home'
import Login      from './pages/Login'
import Signup     from './pages/Signup'
import Products   from './pages/Products'

export default function App() {
  // Load saved token (if any) on startup
  const [token, setToken] = useState(() => localStorage.getItem('token'))

  // Whenever token changes, sync to localStorage
  useEffect(() => {
    if (token) localStorage.setItem('token', token)
    else localStorage.removeItem('token')
  }, [token])

  return (
    <BrowserRouter>
      <AppNavbar
        isAuthenticated={!!token}
        onLogout={() => setToken(null)}
      />

      <Container className="my-4">
        <Routes>
          <Route path="/" element={<Home />} />

          <Route
            path="/login"
            element={<Login onLogin={setToken} />}
          />

          <Route
            path="/signup"
            element={<Signup onSignup={setToken} />}
          />

          <Route
            path="/products"
            element={
              token
                ? <Products token={token} />
                : <Navigate to="/login" replace />
            }
          />
        </Routes>
      </Container>
    </BrowserRouter>
  )
}
