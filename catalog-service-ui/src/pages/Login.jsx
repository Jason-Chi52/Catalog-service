// src/pages/Login.jsx

import React, { useState, useContext } from 'react';
import { Form, Button, Container, Card, Alert } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext.jsx';

export default function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error,    setError]    = useState(null);
  const navigate = useNavigate();

  // grab login() from context instead of props
  const { login } = useContext(AuthContext);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    try {
      // call the context login()
      await login(username, password);
      // on success, send user to products
      navigate('/products');
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <Container
      className="d-flex justify-content-center align-items-center"
      style={{ minHeight: '60vh' }}
    >
      <Card className="p-4" style={{ maxWidth: '400px', width: '100%' }}>
        <h2 className="mb-4 text-center">Login</h2>
        {error && <Alert variant="danger">{error}</Alert>}
        <Form onSubmit={handleSubmit}>
          <Form.Group controlId="loginUsername" className="mb-3">
            <Form.Label>Username</Form.Label>
            <Form.Control
              type="text"
              value={username}
              onChange={e => setUsername(e.target.value)}
              required
            />
          </Form.Group>

          <Form.Group controlId="loginPassword" className="mb-3">
            <Form.Label>Password</Form.Label>
            <Form.Control
              type="password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              required
            />
          </Form.Group>

          <Button variant="primary" type="submit" className="w-100">
            Login
          </Button>
        </Form>
      </Card>
    </Container>
  );
}
