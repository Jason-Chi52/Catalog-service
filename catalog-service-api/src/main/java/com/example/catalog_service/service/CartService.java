package com.example.catalog_service.service;

import com.example.catalog_service.model.CartItem;
import com.example.catalog_service.repo.CartItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CartService {

  private final CartItemRepository cartItemRepo;

  public CartService(CartItemRepository cartItemRepo) {
    this.cartItemRepo = cartItemRepo;
  }

  @Transactional(readOnly = true)
  public List<CartItem> getCartItems(String username) {
    return cartItemRepo.findAllByUsername(username);
  }

  @Transactional
  public void updateQuantity(String username, Long cartItemId, int quantity) {
    CartItem ci = cartItemRepo.findByIdAndUsername(cartItemId, username)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));
    if (quantity <= 0) {
      cartItemRepo.delete(ci);
    } else {
      ci.setQuantity(quantity);
      cartItemRepo.save(ci);
    }
  }

  @Transactional
  public void removeItem(String username, Long cartItemId) {
    CartItem ci = cartItemRepo.findByIdAndUsername(cartItemId, username)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));
    cartItemRepo.delete(ci);
  }
}
