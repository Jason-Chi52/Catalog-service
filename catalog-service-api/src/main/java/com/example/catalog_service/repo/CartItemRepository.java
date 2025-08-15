package com.example.catalog_service.repo;

import com.example.catalog_service.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
  List<CartItem> findAllByUsername(String username);
  Optional<CartItem> findByIdAndUsername(Long id, String username);
  Optional<CartItem> findByUsernameAndProduct_Id(String username, Long productId);
}
