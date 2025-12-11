package com.shop.controller;

import com.shop.model.Product;
import com.shop.repository.CategoryRepository;
import com.shop.repository.OrderRepository;
import com.shop.service.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/seller")
public class SellerController {
    private final ProductService productService;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;

    public SellerController(ProductService productService, CategoryRepository categoryRepository, OrderRepository orderRepository) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/products")
    public String manageProducts(@RequestParam(defaultValue = "0") int page, Model model) {
        var products = productService.getAllProducts(
            PageRequest.of(page, 12, org.springframework.data.domain.Sort.by("id").ascending())
        );
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryRepository.findAll());
        return "seller/products";
    }

    @PostMapping("/products/add")
    public String addProduct(
        @RequestParam String name,
        @RequestParam String description,
        @RequestParam BigDecimal price,
        @RequestParam Integer quantity,
        @RequestParam(required = false) String material,
        @RequestParam(required = false) Long[] categoryIds,
        @RequestParam(required = false) String imageUrls
    ) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setQuantity(quantity);
        product.setMaterial(material);
        
        // добавляем категории
        if (categoryIds != null && categoryIds.length > 0) {
            for (Long catId : categoryIds) {
                categoryRepository.findById(catId).ifPresent(cat -> {
                    product.getCategories().add(cat);
                    // устанавливаем первую категорию как основную для обратной совместимости
                    if (product.getCategory() == null) {
                        product.setCategory(cat);
                    }
                });
            }
        }
        
        // обрабатываем URL изображений
        if (imageUrls != null && !imageUrls.trim().isEmpty()) {
            String[] urls = imageUrls.split("\n");
            java.util.List<String> imageList = new java.util.ArrayList<>();
            for (String url : urls) {
                url = url.trim();
                if (!url.isEmpty()) {
                    imageList.add(url);
                }
            }
            product.setImageList(imageList);
        }
        
        productService.saveProduct(product);
        return "redirect:/seller/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        var product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/seller/products";
        }
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryRepository.findAll());
        return "seller/edit-product";
    }
    
    @PostMapping("/products/update/{id}")
    public String updateProduct(
        @PathVariable Long id,
        @RequestParam String name,
        @RequestParam String description,
        @RequestParam BigDecimal price,
        @RequestParam Integer quantity,
        @RequestParam(required = false) String material,
        @RequestParam(required = false) Long[] categoryIds,
        @RequestParam(required = false) String imageUrls
    ) {
        var product = productService.getProductById(id);
        if (product != null) {
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setQuantity(quantity);
            product.setMaterial(material);
            
            // обновляем категории
            product.getCategories().clear();
            if (categoryIds != null && categoryIds.length > 0) {
                for (Long catId : categoryIds) {
                    categoryRepository.findById(catId).ifPresent(cat -> {
                        product.getCategories().add(cat);
                        if (product.getCategory() == null) {
                            product.setCategory(cat);
                        }
                    });
                }
            }
            
            // обновляем изображения
            if (imageUrls != null && !imageUrls.trim().isEmpty()) {
                String[] urls = imageUrls.split("\n");
                java.util.List<String> imageList = new java.util.ArrayList<>();
                for (String url : urls) {
                    url = url.trim();
                    if (!url.isEmpty()) {
                        imageList.add(url);
                    }
                }
                product.setImageList(imageList);
            }
            
            productService.saveProduct(product);
        }
        return "redirect:/seller/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/seller/products";
    }

    @GetMapping("/stats")
    public String viewStats(Model model) {
        var orders = orderRepository.findAll();
        BigDecimal totalRevenue = orders.stream()
            .map(order -> order.getTotalAmount())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // сортировка по ID по возрастанию
        var sortedOrders = orders.stream()
            .sorted((o1, o2) -> o1.getId().compareTo(o2.getId()))
            .toList();
        
        model.addAttribute("totalOrders", orders.size());
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("recentOrders", sortedOrders);
        return "seller/stats";
    }
    
    @GetMapping("/orders")
    public String viewAllOrders(@RequestParam(defaultValue = "0") int page, Model model) {
        var orders = orderRepository.findAllByOrderByOrderDateDesc(PageRequest.of(page, 5));
        model.addAttribute("orders", orders);
        return "seller/orders";
    }
    
    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            com.shop.model.OrderStatus orderStatus = com.shop.model.OrderStatus.valueOf(status);
            var order = orderRepository.findById(id).orElse(null);
            if (order != null) {
                order.setStatus(orderStatus);
                orderRepository.save(order);
            }
        } catch (IllegalArgumentException e) {

        }
        return "redirect:/seller/orders";
    }
    
    
    @GetMapping("/categories")
    public String manageCategories(@RequestParam(defaultValue = "0") int page, Model model) {
        var categories = categoryRepository.findAll(
            PageRequest.of(page, 12, org.springframework.data.domain.Sort.by("name").ascending())
        );
        model.addAttribute("categories", categories);
        return "seller/categories";
    }
    
    @PostMapping("/categories/add")
    public String addCategory(
        @RequestParam String name,
        @RequestParam(required = false) String imageUrl
    ) {
        com.shop.model.Category category = new com.shop.model.Category();
        category.setName(name);
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            category.setImageUrl(imageUrl.trim());
        }
        categoryRepository.save(category);
        return "redirect:/seller/categories";
    }
    
    @GetMapping("/categories/edit/{id}")
    public String editCategoryForm(@PathVariable Long id, Model model) {
        var category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            return "redirect:/seller/categories";
        }
        model.addAttribute("category", category);
        return "seller/edit-category";
    }
    
    @PostMapping("/categories/update/{id}")
    public String updateCategory(
        @PathVariable Long id,
        @RequestParam String name,
        @RequestParam(required = false) String imageUrl
    ) {
        var category = categoryRepository.findById(id).orElse(null);
        if (category != null) {
            category.setName(name);
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                category.setImageUrl(imageUrl.trim());
            } else {
                category.setImageUrl(null);
            }
            categoryRepository.save(category);
        }
        return "redirect:/seller/categories";
    }
    
    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        // проверяем, есть ли товары в этой категории
        var category = categoryRepository.findById(id).orElse(null);
        if (category != null) {
            categoryRepository.delete(category);
        }
        return "redirect:/seller/categories";
    }
}
