import React, { useState, useEffect } from 'react';
import { Button, Modal, Form, Table } from 'react-bootstrap';

export default function AdminPanel({ token }) {
  const [products, setProducts] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [editProduct, setEditProduct] = useState(null);
  const [formData, setFormData] = useState({ name: '', price: '' });

  const fetchProducts = async () => {
    const res = await fetch('/api/admin/products', {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (res.ok) setProducts(await res.json());
  };

  useEffect(() => {
    if (token) fetchProducts();
  }, [token]);

  const handleShowAdd = () => {
    setEditProduct(null);
    setFormData({ name: '', price: '' });
    setShowModal(true);
  };

  const handleShowEdit = (product) => {
    setEditProduct(product);
    setFormData({ name: product.name, price: product.price });
    setShowModal(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this product?')) {
      const res = await fetch(`/api/admin/products/${id}`, {
        method: 'DELETE',
        headers: { Authorization: `Bearer ${token}` },
      });
      if (res.ok) fetchProducts();
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const url = editProduct
      ? `/api/admin/products/${editProduct.id}`
      : '/api/admin/products';
    const method = editProduct ? 'PUT' : 'POST';
    const res = await fetch(url, {
      method,
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({
        name: formData.name,
        price: parseFloat(formData.price),
      }),
    });
    if (res.ok) {
      setShowModal(false);
      fetchProducts();
    } else {
      alert('Error: ' + await res.text());
    }
  };

  return (
    <div>
      <h2>Admin Panel</h2>
      <Button variant="primary" onClick={handleShowAdd} className="mb-3">
        Add New Product
      </Button>

      <Table striped bordered hover>
        <thead>
          <tr><th>ID</th><th>Name</th><th>Price</th><th>Actions</th></tr>
        </thead>
        <tbody>
          {products.map(p => (
            <tr key={p.id}>
              <td>{p.id}</td>
              <td>{p.name}</td>
              <td>${p.price}</td>
              <td>
                <Button variant="warning" size="sm" onClick={() => handleShowEdit(p)}>
                  Edit
                </Button>{' '}
                <Button variant="danger" size="sm" onClick={() => handleDelete(p.id)}>
                  Delete
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>

      <Modal show={showModal} onHide={() => setShowModal(false)}>
        <Form onSubmit={handleSubmit}>
          <Modal.Header closeButton>
            <Modal.Title>
              {editProduct ? 'Edit Product' : 'Add Product'}
            </Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <Form.Group controlId="productName">
              <Form.Label>Name</Form.Label>
              <Form.Control
                type="text"
                value={formData.name}
                onChange={e => setFormData({ ...formData, name: e.target.value })}
                required
              />
            </Form.Group>
            <Form.Group controlId="productPrice" className="mt-3">
              <Form.Label>Price</Form.Label>
              <Form.Control
                type="number"
                step="0.01"
                value={formData.price}
                onChange={e => setFormData({ ...formData, price: e.target.value })}
                required
              />
            </Form.Group>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowModal(false)}>
              Cancel
            </Button>
            <Button variant="primary" type="submit">
              {editProduct ? 'Save Changes' : 'Add Product'}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </div>
  );
}
