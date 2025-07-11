// src/pages/Signup.jsx

import React, { useState, useContext } from 'react';
import { Form, Button, Container, Card, Alert } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

export default function Signup() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error,    setError]    = useState(null);
  const navigate = useNavigate();
  const { signup } = useContext(AuthContext);

  async function handleSubmit(e) {
    e.preventDefault();
    setError(null);

    try {
      // call the new signup() from context
      await signup(username, password);
      // on success, redirect into your protected area
      navigate('/products');
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <Container
      className="d-flex justify-content-center align-items-center"
      style={{ minHeight: '60vh' }}
    >
      <Card className="p-4" style={{ maxWidth: '400px', width: '100%' }}>
        <h2 className="mb-4 text-center">Sign Up</h2>
        {error && <Alert variant="danger">{error}</Alert>}
        <Form onSubmit={handleSubmit}>
          <Form.Group controlId="signupUsername" className="mb-3">
            <Form.Label>Username</Form.Label>
            <Form.Control
              type="text"
              value={username}
              onChange={e => setUsername(e.target.value)}
              required
            />
          </Form.Group>
          <Form.Group controlId="signupPassword" className="mb-3">
            <Form.Label>Password</Form.Label>
            <Form.Control
              type="password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              required
            />
          </Form.Group>
          <Button variant="primary" type="submit" className="w-100">
            Sign Up
          </Button>
        </Form>
      </Card>
    </Container>
  );
}
