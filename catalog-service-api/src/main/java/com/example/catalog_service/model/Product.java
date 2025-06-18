package com.example.catalog_service.model;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;


/**
 * Entity class representing a Product in the catalog.
 * Each product has a name, description, and price.
 */
@Entity
@Data
public class Product {

    /**
     * The unique identifier for the product.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    
    //GigDecimal used for better financial calculation
    private BigDecimal price;

    /**
     * Default constructor for JPA.
     */
    public Product() {
    }

    public Product(String name, String description, BigDecimal price)
    {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    
}

