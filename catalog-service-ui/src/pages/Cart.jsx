import React, { useState, useEffect } from 'react';

export default function Cart({ token }) {
  const [items, setItems] = useState([]);

  useEffect(() => {
    async function fetchCart() {
      const res = await fetch('/api/cart', {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (res.ok) {
        setItems(await res.json());
      }
    }
    if (token) fetchCart();
  }, [token]);

  return (
    <div>
      <h2>Your Cart</h2>
      {items.length === 0 ? (
        <p>Your cart is empty.</p>
      ) : (
        <ul>
          {items.map(item => (
            <li key={item.id}>
              {item.name} — Quantity: {item.quantity}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
