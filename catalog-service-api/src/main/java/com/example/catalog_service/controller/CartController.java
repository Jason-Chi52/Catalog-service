package com.example.catalog_service.controller;

import com.example.catalog_service.model.CartItem;
import com.example.catalog_service.service.CartService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /** Add a product to the authenticated user’s cart. */
    @PostMapping
    public List<CartItem> addToCart(
         @AuthenticationPrincipal User principal,
         @RequestParam Long productId
    ) {
        return cartService.addToCart(principal.getUsername(), productId);
    }

    /** Get current user’s cart items. */
    @GetMapping
    public List<CartItem> getCart(@AuthenticationPrincipal User principal) {
        return cartService.getCart(principal.getUsername());
    }
}
