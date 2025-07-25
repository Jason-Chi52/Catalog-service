package com.example.catalog_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // which user’s cart this belongs to
    private String username;

    @ManyToOne(fetch = FetchType.EAGER)
    private Product product;

    private int quantity;
}
