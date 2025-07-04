// src/pages/Products.jsx

import React, { useEffect, useState } from 'react'
import {
  Container,
  Row,
  Col,
  Form,
  Button,
  Alert,
  Spinner
} from 'react-bootstrap'
import ProductCard from '../components/ProductCard'

/**
 * Products page: fetches, lists, adds, edits, and deletes products
 * using a JWT for authorization, with full error & loading handling.
 */
export default function Products({ token }) {
  const [products, setProducts] = useState([])
  const [loading, setLoading]   = useState(true)
  const [error, setError]       = useState('')
  const [form, setForm]         = useState({ name: '', description: '', price: '' })
  const [editing, setEditing]   = useState(null)

  const authHeaders = {
    'Content-Type':  'application/json',
    'Authorization': `Bearer ${token}`
  }

  // Load products on mount / when token changes
  useEffect(() => {
    setLoading(true)
    setError('')

    fetch('http://localhost:8080/products', { headers: authHeaders })
      .then(res => {
        if (!res.ok) throw new Error(`Fetch failed: ${res.status}`)
        return res.json()
      })
      .then(data => setProducts(data))
      .catch(err => setError(err.message))
      .finally(() => setLoading(false))
  }, [token])

  // Show spinner while loading
  if (loading) {
    return (
      <Container className="py-5 text-center">
        <Spinner animation="border" />
      </Container>
    )
  }

  // Show error alert if something went wrong
  if (error) {
    return (
      <Container className="py-5">
        <Alert variant="danger">{error}</Alert>
      </Container>
    )
  }

  // Reset form to “add” mode
  const resetForm = () => {
    setEditing(null)
    setForm({ name: '', description: '', price: '' })
  }

  // Handle add/update submission
  const handleSubmit = e => {
    e.preventDefault()
    setError('')

    const method = editing ? 'PUT' : 'POST'
    const url = editing
      ? `http://localhost:8080/products/${editing.id}`
      : 'http://localhost:8080/products'

    fetch(url, {
      method,
      headers: authHeaders,
      body: JSON.stringify({
        name:        form.name,
        description: form.description,
        price:       parseFloat(form.price)
      })
    })
      .then(res => {
        if (!res.ok) throw new Error(`Save failed: ${res.status}`)
        return res.json()
      })
      .then(saved => {
        setProducts(curr =>
          editing
            ? curr.map(p => (p.id === saved.id ? saved : p))
            : [...curr, saved]
        )
        resetForm()
      })
      .catch(err => setError(err.message))
  }

  // Handle delete action
  const handleDelete = id => {
    setError('')

    fetch(`http://localhost:8080/products/${id}`, {
      method:  'DELETE',
      headers: authHeaders
    })
      .then(res => {
        if (!res.ok) throw new Error(`Delete failed: ${res.status}`)
        setProducts(curr => curr.filter(p => p.id !== id))
      })
      .catch(err => setError(err.message))
  }

  // Start editing a product
  const startEdit = p => {
    setEditing(p)
    setForm({
      name:        p.name,
      description: p.description,
      price:       p.price
    })
  }

  return (
    <Container fluid className="p-4">
      <h2 className="mb-4">Products</h2>

      {/* Error alert above the form */}
      {error && <Alert variant="danger">{error}</Alert>}

      {/* Add / Edit form */}
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
              onChange={e => setForm({ ...form, description: e.target.value })}
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

      {/* Responsive grid of product cards */}
      <Row xs={1} sm={2} md={3} lg={4} className="g-4">
        {products.map(p => (
          <Col key={p.id}>
            <ProductCard
              product={p}
              onEdit={() => startEdit(p)}
              onDelete={() => handleDelete(p.id)}
            />
          </Col>
        ))}
      </Row>
    </Container>
  )
}
