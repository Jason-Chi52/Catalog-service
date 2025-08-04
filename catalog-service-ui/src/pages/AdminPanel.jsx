// src/pages/AdminPanel.jsx
import React, { useEffect, useState, useContext } from 'react';
import { Table, Button, Modal, Form, Alert } from 'react-bootstrap';
import { AuthContext } from '../context/AuthContext.jsx';

export default function AdminPanel() {
  const { user } = useContext(AuthContext);
  const token = user.token;

  const [products, setProducts] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [editProduct, setEditProduct] = useState(null);
  const [form, setForm] = useState({ name: '', description: '', price: '' });
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchProducts();
  }, []);

  async function fetchProducts() {
    try {
      const res = await fetch('http://localhost:8080/api/products');
      const data = await res.json();
      setProducts(data);
    } catch (e) {
      console.error(e);
    }
  }

  function openAdd() {
    setEditProduct(null);
    setForm({ name: '', description: '', price: '' });
    setShowModal(true);
  }

  function openEdit(p) {
    setEditProduct(p);
    setForm({
      name: p.name,
      description: p.description,
      price: p.price.toString()
    });
    setShowModal(true);
  }

  async function handleDelete(id) {
    if (!window.confirm('Delete this product?')) return;
    try {
      const res = await fetch(`http://localhost:8080/api/products/${id}`, {
        method: 'DELETE',
        headers: { Authorization: `Bearer ${token}` }
      });
      if (!res.ok) throw new Error('Delete failed');
      fetchProducts();
    } catch (e) {
      setError(e.message);
    }
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError(null);
    const payload = {
      name: form.name,
      description: form.description,
      price: parseFloat(form.price)
    };

    try {
      let res;
      if (editProduct) {
        res = await fetch(`http://localhost:8080/api/products/${editProduct.id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`
          },
          body: JSON.stringify(payload)
        });
      } else {
        res = await fetch('http://localhost:8080/api/products', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`
          },
          body: JSON.stringify(payload)
        });
      }
      if (!res.ok) throw new Error('Save failed');
      setShowModal(false);
      fetchProducts();
    } catch (e) {
      setError(e.message);
    }
  }

  return (
    <>
      <h2>Admin: Manage Products</h2>
      {error && <Alert variant="danger">{error}</Alert>}
      <Button className="mb-3" onClick={openAdd}>+ Add Product</Button>
      <Table striped bordered hover>
        <thead>
          <tr>
            <th>Name</th>
            <th>Description</th>
            <th>Price</th>
            <th style={{ width: '150px' }}>Actions</th>
          </tr>
        </thead>
        <tbody>
          {products.map(p => (
            <tr key={p.id}>
              <td>{p.name}</td>
              <td>{p.description}</td>
              <td>${p.price.toFixed(2)}</td>
              <td>
                <Button size="sm" onClick={() => openEdit(p)}>Edit</Button>{' '}
                <Button size="sm" variant="danger" onClick={() => handleDelete(p.id)}>Delete</Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>

      <Modal show={showModal} onHide={() => setShowModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>{editProduct ? 'Edit Product' : 'Add Product'}</Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleSubmit}>
          <Modal.Body>
            <Form.Group className="mb-2">
              <Form.Label>Name</Form.Label>
              <Form.Control
                value={form.name}
                onChange={e => setForm({ ...form, name: e.target.value })}
                required
              />
            </Form.Group>
            <Form.Group className="mb-2">
              <Form.Label>Description</Form.Label>
              <Form.Control
                value={form.description}
                onChange={e => setForm({ ...form, description: e.target.value })}
                required
              />
            </Form.Group>
            <Form.Group className="mb-2">
              <Form.Label>Price</Form.Label>
              <Form.Control
                type="number"
                step="0.01"
                value={form.price}
                onChange={e => setForm({ ...form, price: e.target.value })}
                required
              />
            </Form.Group>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowModal(false)}>
              Cancel
            </Button>
            <Button type="submit" variant="primary">
              Save
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </>
  );
}
