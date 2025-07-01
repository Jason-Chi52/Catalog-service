import React, { useEffect, useState } from 'react';
import { Container, Row, Col, Form, Button } from 'react-bootstrap';
import ProductCard from '../components/ProductCard';

/**
 * Products page: list, add, edit, delete products.
 */
export default function Products() {
  const [products, setProducts] = useState([]);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState({ name: '', description: '', price: '' });

  useEffect(() => {
    fetch('http://localhost:8080/products')
      .then(res => res.json())
      .then(setProducts);
  }, []);

  const resetForm = () => {
    setEditing(null);
    setForm({ name: '', description: '', price: '' });
  };

  const handleSubmit = e => {
    e.preventDefault();
    const method = editing ? 'PUT' : 'POST';
    const url = editing
      ? `http://localhost:8080/products/${editing.id}`
      : 'http://localhost:8080/products';

    fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ ...form, price: parseFloat(form.price) })
    })
      .then(res => res.json())
      .then(saved => {
        setProducts(current =>
          editing
            ? current.map(p => (p.id === saved.id ? saved : p))
            : [...current, saved]
        );
        resetForm();
      });
  };

  const handleDelete = id => {
    fetch(`http://localhost:8080/products/${id}`, { method: 'DELETE' })
      .then(() => setProducts(current => current.filter(p => p.id !== id)));
  };

  const startEdit = p => {
    setEditing(p);
    setForm({ name: p.name, description: p.description, price: p.price });
  };

  return (
    /* Full-width container for product grid */
    <Container fluid className="p-4">
      <h2 className="mb-4">Products</h2>

      {/* Form */}
      <Form onSubmit={handleSubmit} className="mb-5">
        <Row className="g-2">
          <Col xs={12} md>
            <Form.Control
              placeholder="Name"
              value={form.name}
              onChange={e => setForm({ ...form, name: e.target.value })}
              required
            />
          </Col>
          <Col xs={12} md>
            <Form.Control
              placeholder="Description"
              value={form.description}
              onChange={e =>
                setForm({ ...form, description: e.target.value })
              }
              required
            />
          </Col>
          <Col xs={12} md>
            <Form.Control
              type="number"
              step="0.01"
              placeholder="Price"
              value={form.price}
              onChange={e => setForm({ ...form, price: e.target.value })}
              required
            />
          </Col>
          <Col xs="auto">
            <Button variant="success" type="submit">
              {editing ? 'Update' : 'Add'}
            </Button>
          </Col>
          {editing && (
            <Col xs="auto">
              <Button variant="secondary" onClick={resetForm}>
                Cancel
              </Button>
            </Col>
          )}
        </Row>
      </Form>

      {/* Responsive grid of cards */}
      <Row xs={1} sm={2} md={3} lg={4} className="g-4">
        {products.map(p => (
          <Col key={p.id}>
            <ProductCard
              product={p}
              onEdit={startEdit}
              onDelete={handleDelete}
            />
          </Col>
        ))}
      </Row>
    </Container>
  );
}
