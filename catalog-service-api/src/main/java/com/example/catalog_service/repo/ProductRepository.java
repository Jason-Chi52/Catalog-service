package com.example.catalog_service.repo;

import com.example.catalog_service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for Product.
 * Extends JpaRepository to get built-in CRUD methods.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
    
}
