package com.shop.repository;

import com.shop.model.Category;
import com.shop.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тест репозитория ProductRepository
 * Проверяет основные операции с продуктами в базе данных
 */
@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testFindByCategoryIdWithPagination() {
        // Подготовка данных
        // Создаем категорию
        Category category = new Category();
        category.setName("Электроника");
        entityManager.persistAndFlush(category);

        // Создаем несколько продуктов в этой категории
        Product product1 = new Product();
        product1.setName("Смартфон");
        product1.setDescription("Современный смартфон");
        product1.setPrice(new BigDecimal("50000"));
        product1.setQuantity(10);
        product1.setCategory(category);
        entityManager.persistAndFlush(product1);

        Product product2 = new Product();
        product2.setName("Планшет");
        product2.setDescription("Планшет для работы");
        product2.setPrice(new BigDecimal("30000"));
        product2.setQuantity(5);
        product2.setCategory(category);
        entityManager.persistAndFlush(product2);

        // Создаем продукт в другой категории
        Category otherCategory = new Category();
        otherCategory.setName("Одежда");
        entityManager.persistAndFlush(otherCategory);

        Product product3 = new Product();
        product3.setName("Футболка");
        product3.setDescription("Хлопковая футболка");
        product3.setPrice(new BigDecimal("1500"));
        product3.setQuantity(20);
        product3.setCategory(otherCategory);
        entityManager.persistAndFlush(product3);

        // Выполнение теста
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Product> result = productRepository.findByCategoryId(category.getId(), pageRequest);

        // Проверка результатов
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(Product::getName)
                .containsExactlyInAnyOrder("Смартфон", "Планшет");
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
    }
}