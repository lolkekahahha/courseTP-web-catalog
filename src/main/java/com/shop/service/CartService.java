package com.shop.service;

import com.shop.model.Cart;
import com.shop.model.CartItem;
import com.shop.model.Product;
import com.shop.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final ProductService productService; // Dependency Injection для остатков

    public CartService(CartRepository cartRepository, ProductService productService) {
        this.cartRepository = cartRepository;
        this.productService = productService;
    }

    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId).orElse(null);
    }

    // транзакционный метод добавления товара с резервированием
    @Transactional
    public boolean addToCart(Long userId, Long productId, int quantity, 
                            Integer chainLength1, String chainNumber1,
                            Integer chainLength2, String chainNumber2) {
        Cart cart = getCartByUserId(userId);
        Product product = productService.getProductById(productId);
        
        // проверка доступности товара
        if (cart == null || product == null || product.getQuantity() < quantity) {
            return false;
        }

        // поиск существующего товара в корзине
        CartItem existingItem = cart.getItems().stream()
            .filter(item -> item.getProduct().getId().equals(productId))
            .findFirst()
            .orElse(null);

        if (existingItem != null) {
            // обновление количества существующего товара
            int newQuantity = existingItem.getQuantity() + quantity;
            if (product.getQuantity() < newQuantity) {
                return false;
            }
            existingItem.setQuantity(newQuantity);
            // обновляем опции цепи для ювелирных изделий
            existingItem.setChainLength1(chainLength1);
            existingItem.setChainNumber1(chainNumber1);
            existingItem.setChainLength2(chainLength2);
            existingItem.setChainNumber2(chainNumber2);
        } else {
            CartItem newItem = new CartItem(cart, product, quantity);
            newItem.setChainLength1(chainLength1);
            newItem.setChainNumber1(chainNumber1);
            newItem.setChainLength2(chainLength2);
            newItem.setChainNumber2(chainNumber2);
            cart.getItems().add(newItem);
        }

        productService.reserveStock(productId, quantity);
        cartRepository.save(cart);
        return true;
    }
    
    // перегрузка для обратной совместимости
    @Transactional
    public boolean addToCart(Long userId, Long productId, int quantity) {
        return addToCart(userId, productId, quantity, null, null, null, null);
    }

    @Transactional
    public void updateCartItem(Long userId, Long itemId, int quantity) {
        Cart cart = getCartByUserId(userId);
        if (cart != null) {
            CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElse(null);
            
            if (item != null) {
                int diff = quantity - item.getQuantity();
                if (diff > 0) {
                    if (productService.reserveStock(item.getProduct().getId(), diff)) {
                        item.setQuantity(quantity);
                    }
                } else if (diff < 0) {
                    productService.restoreStock(item.getProduct().getId(), -diff);
                    item.setQuantity(quantity);
                }
                cartRepository.save(cart);
            }
        }
    }

    @Transactional
    public void removeFromCart(Long userId, Long itemId) {
        Cart cart = getCartByUserId(userId);
        if (cart != null) {
            CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElse(null);
            
            if (item != null) {
                productService.restoreStock(item.getProduct().getId(), item.getQuantity());
                cart.getItems().remove(item);
                cartRepository.save(cart);
            }
        }
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getCartByUserId(userId);
        if (cart != null) {
            for (CartItem item : cart.getItems()) {
                productService.restoreStock(item.getProduct().getId(), item.getQuantity());
            }
            cart.getItems().clear();
            cartRepository.save(cart);
        }
    }
}
