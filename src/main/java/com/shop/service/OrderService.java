package com.shop.service;

import com.shop.model.Cart;
import com.shop.model.Order;
import com.shop.model.User;
import com.shop.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartService cartService; // Dependency Injection для работы с корзиной

    public OrderService(OrderRepository orderRepository, CartService cartService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
    }

    // транзакционное создание заказа
    @Transactional
    public Order createOrder(User user, String firstName, String lastName, String phoneNumber, String city, String deliveryAddress) {
        Cart cart = cartService.getCartByUserId(user.getId());
        if (cart == null || cart.getItems().isEmpty()) {
            return null;
        }

        // создание заказа с данными доставки
        Order order = new Order();
        order.setUser(user);
        order.setFirstName(firstName);
        order.setLastName(lastName);
        order.setPhoneNumber(phoneNumber);
        order.setCity(city);
        order.setDeliveryAddress(deliveryAddress);
        
        // денормализация данных товаров
        BigDecimal total = BigDecimal.ZERO;
        for (var cartItem : cart.getItems()) {
            Order.OrderItem orderItem = new Order.OrderItem(
                cartItem.getProduct().getId(),
                cartItem.getProduct().getName(),
                cartItem.getProduct().getPrice(),
                cartItem.getQuantity()
            );
            
            // копия опций цепей
            orderItem.setChainLength1(cartItem.getChainLength1());
            orderItem.setChainNumber1(cartItem.getChainNumber1());
            orderItem.setChainLength2(cartItem.getChainLength2());
            orderItem.setChainNumber2(cartItem.getChainNumber2());
            
            order.getItems().add(orderItem);
            total = total.add(cartItem.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }
        
        order.setTotalAmount(total);
        cart.getItems().clear();
        
        return orderRepository.save(order);
    }

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId);
    }
    
    public org.springframework.data.domain.Page<Order> getUserOrders(Long userId, org.springframework.data.domain.Pageable pageable) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId, pageable);
    }
    
    public org.springframework.data.domain.Page<Order> getAllOrders(org.springframework.data.domain.Pageable pageable) {
        return orderRepository.findAllByOrderByOrderDateDesc(pageable);
    }
    
    @Transactional
    public void updateOrderStatus(Long orderId, com.shop.model.OrderStatus status) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(status);
            orderRepository.save(order);
        }
    }
    
    @Transactional
    public void cancelOrder(Long orderId) {
        updateOrderStatus(orderId, com.shop.model.OrderStatus.CANCELLED);
    }
}
