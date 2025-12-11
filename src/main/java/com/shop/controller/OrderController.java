package com.shop.controller;

import com.shop.service.OrderService;
import com.shop.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping
    public String viewOrders(
        @AuthenticationPrincipal UserDetails userDetails,
        @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
        Model model
    ) {
        var user = userService.findByUsername(userDetails.getUsername());
        var orders = orderService.getUserOrders(user.getId(), org.springframework.data.domain.PageRequest.of(page, 5));
        model.addAttribute("orders", orders);
        return "orders";
    }
    
    @PostMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        var user = userService.findByUsername(userDetails.getUsername());
        var order = orderService.getUserOrders(user.getId()).stream()
            .filter(o -> o.getId().equals(id))
            .findFirst()
            .orElse(null);
        
        if (order != null && (order.getStatus() == null || order.getStatus() == com.shop.model.OrderStatus.PROCESSING)) {
            orderService.cancelOrder(id);
        }
        
        return "redirect:/orders";
    }
}
