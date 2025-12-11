package com.shop.controller;

import com.shop.service.CartService;
import com.shop.service.OrderService;
import com.shop.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final UserService userService;
    private final OrderService orderService;

    public CartController(CartService cartService, UserService userService, OrderService orderService) {
        this.cartService = cartService;
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping
    public String viewCart(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        var user = userService.findByUsername(userDetails.getUsername());
        var cart = cartService.getCartByUserId(user.getId());
        model.addAttribute("cart", cart);
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(
        @RequestParam Long productId,
        @RequestParam(defaultValue = "1") int quantity,
        @RequestParam(required = false) Integer chainLength1,
        @RequestParam(required = false) String chainNumber1,
        @RequestParam(required = false) Integer chainLength2,
        @RequestParam(required = false) String chainNumber2,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        var user = userService.findByUsername(userDetails.getUsername());
        cartService.addToCart(user.getId(), productId, quantity, chainLength1, chainNumber1, chainLength2, chainNumber2);
        return "redirect:/cart";
    }

    @PostMapping("/update/{itemId}")
    public String updateCartItem(
        @PathVariable Long itemId,
        @RequestParam int quantity,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        var user = userService.findByUsername(userDetails.getUsername());
        cartService.updateCartItem(user.getId(), itemId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/remove/{itemId}")
    public String removeFromCart(
        @PathVariable Long itemId,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        var user = userService.findByUsername(userDetails.getUsername());
        cartService.removeFromCart(user.getId(), itemId);
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkoutForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        var user = userService.findByUsername(userDetails.getUsername());
        var cart = cartService.getCartByUserId(user.getId());
        
        if (cart == null || cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }
        
        model.addAttribute("cart", cart);
        return "checkout";
    }
    
    @PostMapping("/checkout")
    public String checkout(
        @RequestParam String firstName,
        @RequestParam String lastName,
        @RequestParam String phoneNumber,
        @RequestParam String city,
        @RequestParam String deliveryAddress,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        var user = userService.findByUsername(userDetails.getUsername());
        orderService.createOrder(user, firstName, lastName, phoneNumber, city, deliveryAddress);
        return "redirect:/orders";
    }

    @GetMapping("/orders")
    public String viewOrders(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        var user = userService.findByUsername(userDetails.getUsername());
        var orders = orderService.getUserOrders(user.getId());
        model.addAttribute("orders", orders);
        return "orders";
    }
}
