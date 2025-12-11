package com.shop.repository;

import com.shop.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ProductRepository extends JpaRepository<Product, Long> {
    // автогенерируемые методы Spring Data JPA
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCaseAndCategoryId(String name, Long categoryId, Pageable pageable);
    
    // JPQL запрос к сущностям JPA для поиска по дополнительным категориям
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c.id = :categoryId")
    Page<Product> findByCategoriesId(@Param("categoryId") Long categoryId, Pageable pageable);
    
    // запрос с сортировкой, в котором доступные товары первыми, затем по ID
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c.id = :categoryId ORDER BY CASE WHEN p.quantity > 0 THEN 0 ELSE 1 END, p.id ASC")
    Page<Product> findByCategoriesIdOrderByAvailability(@Param("categoryId") Long categoryId, Pageable pageable);
    
    // сортировка всех товаров: доступные первыми, затем недоступные
    @Query("SELECT p FROM Product p ORDER BY CASE WHEN p.quantity > 0 THEN 0 ELSE 1 END, p.id DESC")
    Page<Product> findAllOrderByAvailabilityAndNewest(Pageable pageable);
    
    // поиск по названию с сортировкой по доступности и новизне
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY CASE WHEN p.quantity > 0 THEN 0 ELSE 1 END, p.id DESC")
    Page<Product> findByNameContainingIgnoreCaseOrderByAvailability(@Param("name") String name, Pageable pageable);
    
    // поиск по названию и категории 
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND p.category.id = :categoryId ORDER BY CASE WHEN p.quantity > 0 THEN 0 ELSE 1 END, p.id DESC")
    Page<Product> findByNameContainingIgnoreCaseAndCategoryIdOrderByAvailability(@Param("name") String name, @Param("categoryId") Long categoryId, Pageable pageable);
    
    // поиск по категории
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId ORDER BY CASE WHEN p.quantity > 0 THEN 0 ELSE 1 END, p.id DESC")
    Page<Product> findByCategoryIdOrderByAvailability(@Param("categoryId") Long categoryId, Pageable pageable);
}
