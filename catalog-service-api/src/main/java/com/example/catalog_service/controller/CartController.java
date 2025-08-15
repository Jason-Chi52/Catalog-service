package com.example.catalog_service.controller;

import com.example.catalog_service.model.CartItem;
import com.example.catalog_service.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
public class CartController {

  private final CartService cartService;
  public CartController(CartService cartService) { this.cartService = cartService; }

  // ✅ Add to cart: POST /api/cart?productId=1&quantity=2
  @PostMapping("/api/cart")
  public ResponseEntity<CartItem> addToCart(@RequestParam Long productId,
                                            @RequestParam(required = false, defaultValue = "1") Integer quantity,
                                            Authentication auth) {
    CartItem saved = cartService.addItem(auth.getName(), productId, quantity == null ? 1 : quantity);
    return ResponseEntity.created(URI.create("/api/cart/items/" + saved.getId())).body(saved);
  }

  // GET items
  @GetMapping("/api/cart")
  public List<CartItem> getCart(Authentication auth) {
    return cartService.getCartItems(auth.getName());
  }

  // Update quantity (0 deletes)
  @PutMapping("/api/cart/items/{cartItemId}/quantity")
  public ResponseEntity<Void> updateQuantity(@PathVariable Long cartItemId,
                                             @RequestBody Map<String, Integer> payload,
                                             Authentication auth) {
    int qty = payload.getOrDefault("quantity", 0);
    cartService.updateQuantity(auth.getName(), cartItemId, qty);
    return ResponseEntity.noContent().build();
  }

  // Remove item
  @DeleteMapping("/api/cart/items/{cartItemId}")
  public ResponseEntity<Void> removeItem(@PathVariable Long cartItemId, Authentication auth) {
    cartService.removeItem(auth.getName(), cartItemId);
    return ResponseEntity.noContent().build();
  }
}
