// src/context/AuthContext.jsx

import React, { createContext, useState, useEffect } from 'react'

export const AuthContext = createContext({
  user: null,
  login:   async () => {},
  signup:  async () => {},
  logout:  () => {}
})

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)

  // On mount, rehydrate from localStorage
  useEffect(() => {
    const token    = localStorage.getItem('token')
    const username = localStorage.getItem('username')

    // Safely parse roles, guarding against the literal string "undefined"
    let roles = []
    const stored = localStorage.getItem('roles')
    if (stored && stored !== 'undefined') {
      try {
        roles = JSON.parse(stored)
      } catch {
        // ignore parse errors
        roles = []
      }
    }

    if (token && username) {
      setUser({ username, roles, token })
    }
  }, [])

  /**
   * Sign up a new user, store JWT/roles/username, then set context.
   */
  const signup = async (username, password) => {
    const res = await fetch('http://localhost:8080/auth/signup', {
      method:  'POST',
      headers: { 'Content-Type': 'application/json' },
      body:    JSON.stringify({ username, password })
    })
    if (!res.ok) {
      const text = await res.text()
      throw new Error(text || 'Signup failed')
    }
    const { token, roles } = await res.json()

    // persist values
    localStorage.setItem('token', token)
    localStorage.setItem('username', username)
    localStorage.setItem('roles', JSON.stringify(roles))

    setUser({ username, roles, token })
  }

  /**
   * Log in an existing user.
   */
  const login = async (username, password) => {
    const res = await fetch('http://localhost:8080/auth/login', {
      method:  'POST',
      headers: { 'Content-Type': 'application/json' },
      body:    JSON.stringify({ username, password })
    })
    if (!res.ok) {
      const text = await res.text()
      throw new Error(text || 'Login failed')
    }
    const { token, roles } = await res.json()

    localStorage.setItem('token', token)
    localStorage.setItem('username', username)
    localStorage.setItem('roles', JSON.stringify(roles))

    setUser({ username, roles, token })
  }

  /**
   * Clear everything on logout.
   */
  const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('roles')
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, signup, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}
