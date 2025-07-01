import React from 'react';
import { Container } from 'react-bootstrap';

/**
 * Home page for the Catalog Service UI.
 */
export default function Home() {
  return (
    <Container className="text-center mt-5">
      {/* Styled box replacing Jumbotron */}
      <div className="p-5 mb-4 bg-light rounded-3">
        <h1 className="display-4">Welcome to Catalog Service</h1>
        <p className="lead text-muted">
          Manage your products quickly and easily.
        </p>
      </div>
    </Container>
  );
}
