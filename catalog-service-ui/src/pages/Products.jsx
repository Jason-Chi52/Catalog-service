import React, { useEffect, useState, useContext } from 'react';
import { Card, Button, Row, Col, Alert, Toast, ToastContainer } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext.jsx';

export default function Products() {
  const [products, setProducts] = useState([]);
  const [error, setError] = useState(null);
  const { user } = useContext(AuthContext);
  const [showToast, setShowToast] = useState(false);
  const [toastMsg, setToastMsg] = useState('');

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

  const addToCart = async (productId, qty = 1) => {
    try {
      const res = await fetch(
        `http://localhost:8080/api/cart?productId=${productId}&quantity=${qty}`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${user.token}`,
          },
        }
      );

      if (!res.ok) {
        throw new Error(`Add failed: ${res.status} ${res.statusText}`);
      }
      const item = await res.json(); // newly created/merged CartItem

      // Show toast on success
      setToastMsg('Item added to cart!');
      setShowToast(true);

      return item;
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <>
      {/* Toast notification */}
      <ToastContainer position="top-end" className="p-3">
        <Toast
          onClose={() => setShowToast(false)}
          show={showToast}
          delay={2000}
          autohide
          bg="success"
        >
          <Toast.Body className="text-white">{toastMsg}</Toast.Body>
        </Toast>
      </ToastContainer>
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