// src/main/java/com/example/catalog_service/CatalogServiceApplication.java

package com.example.catalog_service;

import com.example.catalog_service.model.Product;
import com.example.catalog_service.model.User;
import com.example.catalog_service.repo.ProductRepository;
import com.example.catalog_service.repo.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Set;

@SpringBootApplication
public class CatalogServiceApplication {

    private final ProductRepository productRepo;

    public CatalogServiceApplication(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    public static void main(String[] args) {
        SpringApplication.run(CatalogServiceApplication.class, args);
    }

    /**
     * Seed some sample products on startup if the products table is empty.
     */
    @PostConstruct
    public void initProducts() {
        if (productRepo.count() == 0) {
            productRepo.save(new Product("Chair", "Wooden chair", new BigDecimal("79.99")));
            productRepo.save(new Product("Lamp",  "Desk lamp",    new BigDecimal("29.99")));
            System.out.println("🌱 Seeded initial products");
        }
    }

    /**
     * Seed a default admin user on startup if one doesn't already exist.
     * Username: admin / Password: admin
     */
    @Bean
    public ApplicationRunner seedAdmin(UserRepository userRepo, PasswordEncoder encoder) {
        return args -> {
            if (userRepo.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin")); 
                admin.setRoles(Set.of("USER", "ADMIN"));
                userRepo.save(admin);
                System.out.println("🌱 Created default admin account: admin/admin");
            }
        };
    }
}
