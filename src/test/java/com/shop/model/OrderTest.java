package com.shop.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void testOrderCreation() {
        User user = new User("testuser", "password", "test@example.com");
        Order order = new Order();
        order.setUser(user);
        order.setFirstName("Иван");
        order.setLastName("Иванов");
        order.setPhoneNumber("+79991234567");
        order.setCity("Москва");
        order.setDeliveryAddress("ул. Примерная, д. 1");
        order.setTotalAmount(new BigDecimal("5000"));
        
        assertEquals(user, order.getUser());
        assertEquals("Иван", order.getFirstName());
        assertEquals("+79991234567", order.getPhoneNumber());
        assertEquals(new BigDecimal("5000"), order.getTotalAmount());
    }

    @Test
    void testOrderItemWithChainOptions() {
        Order.OrderItem item = new Order.OrderItem(1L, "Кулон", new BigDecimal("1000"), 1);
        item.setChainLength1(40);
        item.setChainNumber1("123");
        
        assertEquals(40, item.getChainLength1());
        assertEquals("123", item.getChainNumber1());
    }

    @Test
    void testOrderStatusChange() {
        Order order = new Order();
        order.setStatus(OrderStatus.PROCESSING);
        assertEquals(OrderStatus.PROCESSING, order.getStatus());
        
        order.setStatus(OrderStatus.SHIPPED);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        
        order.setStatus(OrderStatus.COMPLETED);
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    void testPairedProductWithTwoChains() {
        Order.OrderItem item = new Order.OrderItem(1L, "Парные кулоны", new BigDecimal("2000"), 1);
        item.setChainLength1(40);
        item.setChainNumber1("123");
        item.setChainLength2(45);
        item.setChainNumber2("456");
        
        assertEquals(40, item.getChainLength1());
        assertEquals("123", item.getChainNumber1());
        assertEquals(45, item.getChainLength2());
        assertEquals("456", item.getChainNumber2());
    }
}
