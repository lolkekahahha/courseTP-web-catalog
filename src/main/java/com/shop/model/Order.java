package com.shop.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // связь ManyToOne покупателя
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    
    // статус заказа в БД
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    // данные доставки с использованием денормализации для сохранения истории
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String city;
    private String deliveryAddress;

    // товары заказа как встроенная коллекция с денормализацией данных товаров
    @ElementCollection
    @CollectionTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"))
    private List<OrderItem> items = new ArrayList<>();

    @Embeddable
    public static class OrderItem {
        // денормализованные данные товара 
        private Long productId;
        private String productName;
        private BigDecimal price;
        private Integer quantity;
        
        // опции цепи 
        private Integer chainLength1;
        private String chainNumber1; 
        private Integer chainLength2; 
        private String chainNumber2;   

        public OrderItem() {}

        public OrderItem(Long productId, String productName, BigDecimal price, Integer quantity) {
            this.productId = productId;
            this.productName = productName;
            this.price = price;
            this.quantity = quantity;
        }

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        
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

    public Order() {
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PROCESSING;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
}
