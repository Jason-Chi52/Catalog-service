import React, { useState } from 'react';
import { Form, Button, Container, Card } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';

/**
 * Login page (mock auth).
 */
export default function Login({ onLogin }) {
  const [username, setUsername] = useState('');
  const navigate = useNavigate();

  function handleSubmit(e) {
    e.preventDefault();
    if (username.trim()) {
      onLogin(username);
      navigate('/products');
    }
  }

  return (
    /* Centered full-height container */
    <Container
      className="d-flex justify-content-center align-items-center"
      style={{ minHeight: '60vh' }}
    >
      <Card className="p-4" style={{ maxWidth: '400px', width: '100%' }}>
        <h2 className="mb-4 text-center">Sign In</h2>
        <Form onSubmit={handleSubmit}>
          <Form.Group controlId="username" className="mb-3">
            <Form.Label>Username</Form.Label>
            <Form.Control
              type="text"
              placeholder="Enter username"
              value={username}
              onChange={e => setUsername(e.target.value)}
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
