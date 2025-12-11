package com.shop.service;

import com.shop.model.Category;
import com.shop.model.Product;
import com.shop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        Category category = new Category("Кулоны");
        testProduct = new Product("Кулон", "Описание", new BigDecimal("1000"), 10, category);
        testProduct.setId(1L);
    }

    @Test
    void testReserveStock_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        boolean result = productService.reserveStock(1L, 5);

        assertTrue(result);
        assertEquals(5, testProduct.getQuantity());
        verify(productRepository).save(testProduct);
    }

    @Test
    void testReserveStock_InsufficientQuantity() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        boolean result = productService.reserveStock(1L, 15);

        assertFalse(result);
        assertEquals(10, testProduct.getQuantity());
        verify(productRepository, never()).save(any());
    }

    @Test
    void testRestoreStock() {
        testProduct.setQuantity(5);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        productService.restoreStock(1L, 3);

        assertEquals(8, testProduct.getQuantity());
        verify(productRepository).save(testProduct);
    }

    @Test
    void testGetProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        Product found = productService.getProductById(1L);

        assertNotNull(found);
        assertEquals("Кулон", found.getName());
        assertEquals(new BigDecimal("1000"), found.getPrice());
    }

    @Test
    void testSaveProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product saved = productService.saveProduct(testProduct);

        assertNotNull(saved);
        verify(productRepository).save(testProduct);
    }

    @Test
    void testDeleteProduct() {
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository).deleteById(1L);
    }
}
