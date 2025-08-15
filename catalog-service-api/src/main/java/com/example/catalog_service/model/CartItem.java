package com.example.catalog_service.model;

import jakarta.persistence.*;

@Entity
public class CartItem {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // cart owner (no Cart entity needed)
  @Column(nullable = false)
  private String username;

  // Product must expose name + price
  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  private Product product;

  @Column(nullable = false)
  private int quantity;

  protected CartItem() {}

  public CartItem(String username, Product product, int quantity) {
    this.username = username;
    this.product = product;
    this.quantity = quantity;
  }

  public Long getId() { return id; }
  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  public Product getProduct() { return product; }
  public int getQuantity() { return quantity; }
  public void setQuantity(int quantity) { this.quantity = quantity; }
}
