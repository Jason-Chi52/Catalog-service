// src/main/java/com/example/catalog_service/service/CartService.java
package com.example.catalog_service.service;

import com.example.catalog_service.model.CartItem;
import com.example.catalog_service.model.Product;
import com.example.catalog_service.repo.CartItemRepository;
import com.example.catalog_service.repo.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartService {
    private final CartItemRepository cartRepo;
    private final ProductRepository   productRepo;

    public CartService(CartItemRepository cartRepo,
                       ProductRepository productRepo) {
        this.cartRepo    = cartRepo;
        this.productRepo = productRepo;
    }

    /** Add one unit of the given product to the user’s cart. */
    @Transactional
    public List<CartItem> addToCart(String username, Long productId) {
        Product p = productRepo.findById(productId)
                         .orElseThrow(() -> new IllegalArgumentException("No product " + productId));

        // see if already in cart
        CartItem existing = cartRepo.findByUsername(username)
            .stream()
            .filter(ci -> ci.getProduct().getId().equals(productId))
            .findFirst()
            .orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + 1);
            cartRepo.save(existing);
        } else {
            CartItem ci = new CartItem();
            ci.setUsername(username);
            ci.setProduct(p);
            ci.setQuantity(1);
            cartRepo.save(ci);
        }
        return cartRepo.findByUsername(username);
    }

    /** Fetch the full cart for a user. */
    @Transactional(readOnly = true)
    public List<CartItem> getCart(String username) {
        return cartRepo.findByUsername(username);
    }
}
