package com.example.catalog_service;

import java.math.BigDecimal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.catalog_service.repo.ProductRepository;
import com.example.catalog_service.model.Product;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class CatalogServiceApplication {
	private final ProductRepository repo;
	public CatalogServiceApplication(ProductRepository repo) {
		this.repo = repo;
	}
	/**
	 * Main method to run the Catalog Service application.
	 *
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(CatalogServiceApplication.class, args);
	}
	/**
	 * Method to initialize the product repository with some sample data.
	 */
	@PostConstruct
	public void init() {
        repo.save(new Product("Chair", "Wooden chair", new BigDecimal("79.99")));
        repo.save(new Product("Lamp", "Desk lamp", new BigDecimal("29.99")));
	}
	

}
