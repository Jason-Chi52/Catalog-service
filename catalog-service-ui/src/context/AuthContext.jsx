import React, { createContext, useEffect, useState } from 'react';

export const AuthContext = createContext(null);

const API_BASE = 'http://localhost:8080'; // change if different

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem('auth_user')) || null;
    } catch {
      return null;
    }
  });

  useEffect(() => {
    if (user) localStorage.setItem('auth_user', JSON.stringify(user));
    else localStorage.removeItem('auth_user');
  }, [user]);

  const login = async (username, password) => {
    const res = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password }),
    });

    // try to read server message for nicer errors
    const text = await res.text();
    let data;
    try { data = text ? JSON.parse(text) : {}; } catch { data = {}; }

    if (!res.ok) {
      const msg = data?.message || `Login failed: ${res.status}`;
      throw new Error(msg);
    }

    // expected: { token, username } (adjust if your backend returns different)
    setUser({ username: data.username, token: data.token });
    return data;
  };

  const signup = async (username, password) => {
    // If your backend uses a different path (e.g. /api/auth/signup or /auth/register),
    // update it here:
    const res = await fetch(`${API_BASE}/auth/signup`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password }),
    });

    const text = await res.text();
    let data;
    try { data = text ? JSON.parse(text) : {}; } catch { data = {}; }

    if (!res.ok) {
      const msg = data?.message || `Signup failed: ${res.status}`;
      throw new Error(msg);
    }

    // Two options:
    // (A) If signup returns a token + username, log in immediately:
    if (data?.token && data?.username) {
      setUser({ username: data.username, token: data.token });
      return data;
    }
    // (B) Otherwise, auto-login via the login() call:
    return login(username, password);
  };

  const logout = () => setUser(null);

  return (
    <AuthContext.Provider value={{ user, login, signup, logout }}>
      {children}
    </AuthContext.Provider>
  );
}
