package com.shop.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void testProductAvailability() {
        Category category = new Category("Кулоны");
        Product product = new Product("Кулон", "Описание", new BigDecimal("1000"), 5, category);
        
        assertTrue(product.isAvailable());
        
        product.setQuantity(0);
        assertFalse(product.isAvailable());
    }

    @Test
    void testMainImageDefault() {
        Category category = new Category("Кулоны");
        Product product = new Product("Кулон", "Описание", new BigDecimal("1000"), 5, category);
        
        assertEquals("/images/logo.png", product.getMainImage());
    }

    @Test
    void testMainImageFromList() {
        Category category = new Category("Кулоны");
        Product product = new Product("Кулон", "Описание", new BigDecimal("1000"), 5, category);
        
        List<String> images = new ArrayList<>();
        images.add("/uploads/image1.jpg");
        images.add("/uploads/image2.jpg");
        product.setImageList(images);
        
        assertEquals("/uploads/image1.jpg", product.getMainImage());
    }

    @Test
    void testMultipleCategories() {
        Category category1 = new Category("Кулоны");
        Category category2 = new Category("Подарки");
        
        Product product = new Product("Кулон", "Описание", new BigDecimal("1000"), 5, category1);
        product.getCategories().add(category1);
        product.getCategories().add(category2);
        
        assertEquals(2, product.getCategories().size());
    }
}
