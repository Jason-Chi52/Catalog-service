// src/pages/Products.jsx
import React, { useEffect, useState, useContext } from 'react';
import { Card, Button, Row, Col, Alert } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext.jsx';

export default function Products() {
  const [products, setProducts] = useState([]);
  const [error, setError]       = useState(null);
  const { user }                = useContext(AuthContext);

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const res = await fetch('http://localhost:8080/api/products', {
          headers: user?.token
            ? { Authorization: `Bearer ${user.token}` }
            : {}
        });

        if (!res.ok) {
          throw new Error(`Fetch failed: ${res.status} ${res.statusText}`);
        }

        const data = await res.json();
        setProducts(data);
      } catch (err) {
        setError(err.message);
      }
    };

    fetchProducts();
  }, [user]);

  const addToCart = async (productId) => {
    setError(null);
    try {
      const res = await fetch(
        `http://localhost:8080/api/cart?productId=${productId}`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${user.token}`
          }
        }
      );

      if (!res.ok) {
        throw new Error(`Add failed: ${res.status} ${res.statusText}`);
      }

      alert('✅ Added to cart!');
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <>
      {!user && (
        <Alert variant="info">
          Please <Link to="/login">log in</Link> to add products to your cart.
        </Alert>
      )}
      {error && <Alert variant="danger">{error}</Alert>}
      <Row xs={1} md={3} className="g-4">
        {products.map(prod => (
          <Col key={prod.id}>
            <Card>
              <Card.Body>
                <Card.Title>{prod.name}</Card.Title>
                <Card.Text>{prod.description}</Card.Text>
                <Card.Text>${prod.price}</Card.Text>
                <Button
                  disabled={!user}
                  onClick={() => addToCart(prod.id)}
                >
                  Add to Cart
                </Button>
              </Card.Body>
            </Card>
          </Col>
        ))}
      </Row>
    </>
  );
}
