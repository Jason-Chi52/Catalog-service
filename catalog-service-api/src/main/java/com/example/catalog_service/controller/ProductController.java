package com.example.catalog_service.controller;
import com.example.catalog_service.model.Product;
import com.example.catalog_service.repo.ProductRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for managing products in the catalog.
 */
@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "http://localhost:5173") // React frontend later
public class ProductController {

    private final ProductRepository repo;

    public ProductController(ProductRepository repo) {
        this.repo = repo;
    }

    /**
     * Get all products.
     */
    @GetMapping
    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    /**
     * Get a single product by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = repo.findById(id);
        return product.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new product.
     */
    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return repo.save(product);
    }

    /**
     * Update an existing product.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product updated) {
        return repo.findById(id).map(product -> {
            product.setName(updated.getName());
            product.setDescription(updated.getDescription());
            product.setPrice(updated.getPrice());
            return ResponseEntity.ok(repo.save(product));
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a product by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}