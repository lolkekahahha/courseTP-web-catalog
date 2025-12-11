package com.shop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Min(1)
    private Integer quantity;

    private Integer chainLength1;
    private String chainNumber1;
    private Integer chainLength2;
    private String chainNumber2;

    public CartItem() {}

    public CartItem(Cart cart, Product product, Integer quantity) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getChainLength1() { return chainLength1; }
    public void setChainLength1(Integer chainLength1) { this.chainLength1 = chainLength1; }

    public String getChainNumber1() { return chainNumber1; }
    public void setChainNumber1(String chainNumber1) { this.chainNumber1 = chainNumber1; }

    public Integer getChainLength2() { return chainLength2; }
    public void setChainLength2(Integer chainLength2) { this.chainLength2 = chainLength2; }

    public String getChainNumber2() { return chainNumber2; }
    public void setChainNumber2(String chainNumber2) { this.chainNumber2 = chainNumber2; }
}
