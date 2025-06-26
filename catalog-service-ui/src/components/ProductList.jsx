import { useEffect, useState } from "react";

/**
 * ProductList component fetches and displays all products from the backend API.
 */
export default function ProductList() {
  const [products, setProducts] = useState([]);

  useEffect(() => {
    fetch("http://localhost:8080/products")
      .then((res) => res.json())
      .then((data) => setProducts(data))
      .catch((err) => console.error("Failed to fetch products:", err));
  }, []);

  return (
    <div>
      <h2>Product Catalog</h2>
      {products.length === 0 ? (
        <p>No products available.</p>
      ) : (
        <ul>
          {products.map((product) => (
            <li key={product.id}>
              <strong>{product.name}</strong> - ${product.price} <br />
              <em>{product.description}</em>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
