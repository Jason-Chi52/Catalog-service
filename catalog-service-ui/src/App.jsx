import React, { useState } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Container } from 'react-bootstrap';

import AppNavbar from './components/Navbar';
import Home       from './pages/Home';
import Login      from './pages/Login';
import Products   from './pages/Products';

/**
 * Main application component, with routing and a responsive container.
 */
function App() {
  const [user, setUser] = useState(null);

  return (
    <BrowserRouter>
      {/* Navbar always full-width */}
      <AppNavbar isAuthenticated={!!user} onLogout={() => setUser(null)} />

      {/* Wrap all pages in a fixed-width responsive container */}
    <Container fluid className="my-4">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login onLogin={setUser} />} />
          <Route
            path="/products"
            element={user ? <Products /> : <Navigate to="/login" replace />}
          />
        </Routes>
      </Container>
    </BrowserRouter>
  );
}

export default App;
