// src/pages/Cart.jsx

import React, { useEffect, useState, useContext } from 'react';
import { Table, Alert, Button } from 'react-bootstrap';
import { AuthContext } from '../context/AuthContext.jsx';

export default function Cart() {
  const { user } = useContext(AuthContext);
  const [items, setItems] = useState([]);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!user) {
      setLoading(false);
      return;
    }

    const fetchCart = async () => {
      try {
        const res = await fetch('http://localhost:8080/api/cart', {
          headers: {
            Authorization: `Bearer ${user.token}`
          }
        });
        if (!res.ok) {
          throw new Error(`Fetch failed: ${res.status} ${res.statusText}`);
        }
        const data = await res.json();
        setItems(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchCart();
  }, [user]);

  if (loading) {
    return <p>Loading cart…</p>;
  }

  if (!user) {
    return <Alert variant="info">Please log in to view your cart.</Alert>;
  }

  if (error) {
    return <Alert variant="danger">{error}</Alert>;
  }

  if (items.length === 0) {
    return <p>Your cart is empty.</p>;
  }

  const total = items
    .reduce((sum, ci) => sum + ci.product.price * ci.quantity, 0)
    .toFixed(2);

  return (
    <>
      <h2>Your Shopping Cart</h2>
      <Table striped bordered hover>
        <thead>
          <tr>
            <th>Product</th>
            <th style={{ width: '80px' }}>Qty</th>
            <th style={{ width: '100px' }}>Price</th>
            <th style={{ width: '120px' }}>Subtotal</th>
          </tr>
        </thead>
        <tbody>
          {items.map(ci => (
            <tr key={ci.id}>
              <td>{ci.product.name}</td>
              <td>{ci.quantity}</td>
              <td>${ci.product.price.toFixed(2)}</td>
              <td>${(ci.product.price * ci.quantity).toFixed(2)}</td>
            </tr>
          ))}
          <tr>
            <td colSpan={3} className="text-end"><strong>Total:</strong></td>
            <td><strong>${total}</strong></td>
          </tr>
        </tbody>
      </Table>
      <div className="text-end">
        <Button variant="primary" disabled>
          Proceed to Checkout
        </Button>
      </div>
    </>
  );
}
