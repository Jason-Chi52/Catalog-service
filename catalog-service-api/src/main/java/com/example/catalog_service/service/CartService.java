package com.example.catalog_service.service;

import com.example.catalog_service.model.CartItem;
import com.example.catalog_service.model.Product;
import com.example.catalog_service.repo.CartItemRepository;
import com.example.catalog_service.repo.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CartService {

  private final CartItemRepository cartItemRepo;
  private final ProductRepository productRepo;

  public CartService(CartItemRepository cartItemRepo, ProductRepository productRepo) {
    this.cartItemRepo = cartItemRepo;
    this.productRepo = productRepo;
  }

  @Transactional(readOnly = true)
  public List<CartItem> getCartItems(String username) {
    return cartItemRepo.findAllByUsername(username);
  }

  @Transactional
  public CartItem addItem(String username, Long productId, int quantity) {
    if (quantity <= 0) quantity = 1;

    Product product = productRepo.findById(productId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

    // merge with existing row for same product
    CartItem existing = cartItemRepo.findByUsernameAndProduct_Id(username, productId).orElse(null);
    if (existing != null) {
      existing.setQuantity(existing.getQuantity() + quantity);
      return cartItemRepo.save(existing);
    }

    CartItem ci = new CartItem(username, product, quantity);
    return cartItemRepo.save(ci);
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
