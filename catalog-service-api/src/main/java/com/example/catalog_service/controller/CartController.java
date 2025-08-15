package com.example.catalog_service.controller;

import com.example.catalog_service.model.CartItem;
import com.example.catalog_service.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class CartController {

  private final CartService cartService;
  public CartController(CartService cartService) { this.cartService = cartService; }

  @GetMapping("/api/cart")
  public List<CartItem> getCart(Authentication auth) {
    return cartService.getCartItems(auth.getName());
  }

  @PutMapping("/api/cart/items/{cartItemId}/quantity")
  public ResponseEntity<Void> updateQuantity(@PathVariable Long cartItemId,
                                             @RequestBody Map<String, Integer> payload,
                                             Authentication auth) {
    int qty = payload.getOrDefault("quantity", 0);
    cartService.updateQuantity(auth.getName(), cartItemId, qty);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/api/cart/items/{cartItemId}")
  public ResponseEntity<Void> removeItem(@PathVariable Long cartItemId, Authentication auth) {
    cartService.removeItem(auth.getName(), cartItemId);
    return ResponseEntity.noContent().build();
  }
}
