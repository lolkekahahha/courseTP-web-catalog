package com.shop.integration;

import com.shop.model.*;
import com.shop.repository.*;
import com.shop.service.CartService;
import com.shop.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("ordertest_" + System.currentTimeMillis());
        testUser.setPassword("password");
        testUser.setEmail("ordertest_" + System.currentTimeMillis() + "@example.com");
        testUser.setRoles(new HashSet<>());
        testUser.getRoles().add("BUYER");
        testUser = userRepository.save(testUser);

        Cart cart = new Cart();
        cart.setUser(testUser);
        cartRepository.save(cart);

        Category category = new Category();
        category.setName("Test Category");
        category = categoryRepository.save(category);

        testProduct = new Product();
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(BigDecimal.valueOf(100));
        testProduct.setQuantity(10);
        testProduct.setCategory(category);
        testProduct = productRepository.save(testProduct);
    }

    @Test
    void testCreateOrder_Success() {
        cartService.addToCart(testUser.getId(), testProduct.getId(), 2);
        
        Order order = orderService.createOrder(testUser, "John", "Doe", 
            "1234567890", "Test City", "123 Test St");
        
        assertNotNull(order);
        assertNotNull(order.getId());
        assertEquals(testUser.getId(), order.getUser().getId());
        assertEquals(1, order.getItems().size());
        assertEquals(BigDecimal.valueOf(200), order.getTotalAmount());
    }

    @Test
    void testCreateOrder_EmptyCart() {
        Order order = orderService.createOrder(testUser, "John", "Doe", 
            "1234567890", "Test City", "123 Test St");
        
        assertNull(order);
    }

    @Test
    void testGetUserOrders() {
        cartService.addToCart(testUser.getId(), testProduct.getId(), 2);
        orderService.createOrder(testUser, "John", "Doe", 
            "1234567890", "Test City", "123 Test St");
        
        List<Order> orders = orderService.getUserOrders(testUser.getId());
        
        assertEquals(1, orders.size());
    }

    @Test
    void testGetUserOrders_WithPagination() {
        cartService.addToCart(testUser.getId(), testProduct.getId(), 2);
        orderService.createOrder(testUser, "John", "Doe", 
            "1234567890", "Test City", "123 Test St");
        
        Page<Order> page = orderService.getUserOrders(testUser.getId(), PageRequest.of(0, 5));
        
        assertEquals(1, page.getTotalElements());
    }

    @Test
    void testUpdateOrderStatus() {
        cartService.addToCart(testUser.getId(), testProduct.getId(), 2);
        Order order = orderService.createOrder(testUser, "John", "Doe", 
            "1234567890", "Test City", "123 Test St");
        
        orderService.updateOrderStatus(order.getId(), OrderStatus.SHIPPED);
        
        List<Order> orders = orderService.getUserOrders(testUser.getId());
        assertEquals(OrderStatus.SHIPPED, orders.get(0).getStatus());
    }

    @Test
    void testCancelOrder() {
        cartService.addToCart(testUser.getId(), testProduct.getId(), 2);
        Order order = orderService.createOrder(testUser, "John", "Doe", 
            "1234567890", "Test City", "123 Test St");
        
        orderService.cancelOrder(order.getId());
        
        List<Order> orders = orderService.getUserOrders(testUser.getId());
        assertEquals(OrderStatus.CANCELLED, orders.get(0).getStatus());
    }
}
