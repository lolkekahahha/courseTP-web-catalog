package com.shop.integration;

import com.shop.model.*;
import com.shop.repository.CartRepository;
import com.shop.repository.CategoryRepository;
import com.shop.repository.ProductRepository;
import com.shop.repository.UserRepository;
import com.shop.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CartServiceIntegrationTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User testUser;
    private Product testProduct;
    private Cart testCart;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("carttest_" + System.currentTimeMillis());
        testUser.setPassword("password");
        testUser.setEmail("carttest_" + System.currentTimeMillis() + "@example.com");
        testUser.setRoles(new HashSet<>());
        testUser.getRoles().add("BUYER");
        testUser = userRepository.save(testUser);

        testCart = new Cart();
        testCart.setUser(testUser);
        testCart = cartRepository.save(testCart);

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
    void testGetCartByUserId() {
        Cart cart = cartService.getCartByUserId(testUser.getId());
        assertNotNull(cart);
        assertEquals(testUser.getId(), cart.getUser().getId());
    }

    @Test
    void testAddToCart_NewItem() {
        boolean result = cartService.addToCart(testUser.getId(), testProduct.getId(), 2);
        
        assertTrue(result);
        Cart cart = cartService.getCartByUserId(testUser.getId());
        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }

    @Test
    void testAddToCart_InsufficientStock() {
        boolean result = cartService.addToCart(testUser.getId(), testProduct.getId(), 20);
        
        assertFalse(result);
        Cart cart = cartService.getCartByUserId(testUser.getId());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void testRemoveFromCart() {
        cartService.addToCart(testUser.getId(), testProduct.getId(), 2);
        Cart cart = cartService.getCartByUserId(testUser.getId());
        Long itemId = cart.getItems().get(0).getId();

        cartService.removeFromCart(testUser.getId(), itemId);
        
        cart = cartService.getCartByUserId(testUser.getId());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void testClearCart() {
        cartService.addToCart(testUser.getId(), testProduct.getId(), 2);
        
        cartService.clearCart(testUser.getId());
        
        Cart cart = cartService.getCartByUserId(testUser.getId());
        assertEquals(0, cart.getItems().size());
    }
}
