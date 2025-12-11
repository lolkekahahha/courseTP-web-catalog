package com.shop.repository;

import com.shop.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);
    Page<Order> findByUserIdOrderByOrderDateDesc(Long userId, Pageable pageable);
    Page<Order> findAllByOrderByOrderDateDesc(Pageable pageable);
}
