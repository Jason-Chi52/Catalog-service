// src/pages/Cart.jsx
import React, { useEffect, useState, useContext, useMemo } from 'react';
import { Table, Alert, Button, InputGroup, Form, Spinner } from 'react-bootstrap';
import { AuthContext } from '../context/AuthContext.jsx';

export default function Cart() {
  const { user } = useContext(AuthContext);
  const [items, setItems] = useState([]);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);
  const [savingIds, setSavingIds] = useState(new Set()); // cartItemIds being saved

  const API = 'http://localhost:8080/api/cart';

  useEffect(() => {
    if (!user) {
      setLoading(false);
      return;
    }

    const fetchCart = async () => {
      try {
        const res = await fetch(API, {
          headers: { Authorization: `Bearer ${user.token}` }
        });
        if (!res.ok) throw new Error(`Fetch failed: ${res.status} ${res.statusText}`);
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

  const total = useMemo(
    () =>
      items.reduce((sum, ci) => sum + Number(ci.product.price) * Number(ci.quantity), 0).toFixed(2),
    [items]
  );

  if (loading) return <p>Loading cart…</p>;
  if (!user) return <Alert variant="info">Please log in to view your cart.</Alert>;
  if (error) return <Alert variant="danger">{error}</Alert>;
  if (items.length === 0) return <p>Your cart is empty.</p>;

  // --- Helpers ---
  const setSaving = (id, isSaving) => {
    setSavingIds(prev => {
      const next = new Set(prev);
      if (isSaving) next.add(id);
      else next.delete(id);
      return next;
    });
  };

  const updateQuantity = async (cartItemId, newQty) => {
    // clamp & integerize
    const qty = Math.max(0, Number.isFinite(+newQty) ? Math.floor(+newQty) : 0);

    // Optimistic update
    const prev = items;
    const nextItems =
      qty === 0 ? prev.filter(ci => ci.id !== cartItemId) : prev.map(ci => (ci.id === cartItemId ? { ...ci, quantity: qty } : ci));
    setItems(nextItems);
    setSaving(cartItemId, true);
    setError(null);

    try {
      const res = await fetch(`${API}/items/${cartItemId}/quantity`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${user.token}`
        },
        body: JSON.stringify({ quantity: qty })
      });

      if (!res.ok) {
        // rollback
        setItems(prev);
        const text = await res.text();
        throw new Error(text || `Update failed: ${res.status}`);
      }
    } catch (e) {
      setError(e.message);
    } finally {
      setSaving(cartItemId, false);
    }
  };

  const increment = (id, current) => updateQuantity(id, Number(current) + 1);
  const decrement = (id, current) => updateQuantity(id, Math.max(0, Number(current) - 1));

  const removeItem = async (cartItemId) => {
    const prev = items;
    setItems(prev.filter(ci => ci.id !== cartItemId));
    setSaving(cartItemId, true);
    setError(null);

    try {
      const res = await fetch(`${API}/items/${cartItemId}`, {
        method: 'DELETE',
        headers: { Authorization: `Bearer ${user.token}` }
      });
      if (!res.ok) {
        setItems(prev); // rollback
        const text = await res.text();
        throw new Error(text || `Delete failed: ${res.status}`);
      }
    } catch (e) {
      setError(e.message);
    } finally {
      setSaving(cartItemId, false);
    }
  };

  return (
    <>
      <h2>Your Shopping Cart</h2>
      {error && <Alert variant="warning" className="py-2 my-2">{error}</Alert>}

      <Table striped bordered hover responsive>
        <thead>
          <tr>
            <th>Product</th>
            <th style={{ width: 160 }}>Qty</th>
            <th style={{ width: 120 }}>Price</th>
            <th style={{ width: 140 }}>Subtotal</th>
            <th style={{ width: 120 }}></th>
          </tr>
        </thead>
        <tbody>
          {items.map(ci => {
            const isSaving = savingIds.has(ci.id);
            const subtotal = (Number(ci.product.price) * Number(ci.quantity)).toFixed(2);

            return (
              <tr key={ci.id}>
                <td>{ci.product.name}</td>

                <td>
                  <InputGroup size="sm">
                    <Button
                      variant="outline-secondary"
                      onClick={() => decrement(ci.id, ci.quantity)}
                      disabled={isSaving}
                      aria-label="Decrease quantity"
                    >
                      −
                    </Button>
                    <Form.Control
                      type="number"
                      min={0}
                      step={1}
                      value={ci.quantity}
                      onChange={(e) => {
                        const val = e.target.value;
                        // immediate optimistic visual change; commit on blur/Enter
                        setItems(items.map(x => (x.id === ci.id ? { ...x, quantity: val === '' ? 0 : Math.max(0, Math.floor(+val || 0)) } : x)));
                      }}
                      onBlur={(e) => updateQuantity(ci.id, e.target.value)}
                      onKeyDown={(e) => {
                        if (e.key === 'Enter') {
                          e.currentTarget.blur();
                        }
                      }}
                      disabled={isSaving}
                      aria-label="Quantity"
                      style={{ textAlign: 'center' }}
                    />
                    <Button
                      variant="outline-secondary"
                      onClick={() => increment(ci.id, ci.quantity)}
                      disabled={isSaving}
                      aria-label="Increase quantity"
                    >
                      +
                    </Button>
                  </InputGroup>
                  {isSaving && (
                    <div className="mt-1" style={{ fontSize: 12 }}>
                      <Spinner animation="border" size="sm" /> saving…
                    </div>
                  )}
                </td>

                <td>${Number(ci.product.price).toFixed(2)}</td>
                <td>${subtotal}</td>

                <td className="text-center">
                  <Button
                    size="sm"
                    variant="outline-danger"
                    onClick={() => removeItem(ci.id)}
                    disabled={isSaving}
                  >
                    Remove
                  </Button>
                </td>
              </tr>
            );
          })}

          <tr>
            <td colSpan={3} className="text-end"><strong>Total:</strong></td>
            <td><strong>${total}</strong></td>
            <td />
          </tr>
        </tbody>
      </Table>

      <div className="d-flex justify-content-end gap-2">
        <Button variant="outline-secondary" onClick={() => window.history.back()}>
          Continue Shopping
        </Button>
        <Button variant="primary" disabled={items.length === 0}>
          Proceed to Checkout
        </Button>
      </div>
    </>
  );
}
