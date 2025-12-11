package com.shop.controller;

import com.shop.model.Category;
import com.shop.model.Product;
import com.shop.repository.CategoryRepository;
import com.shop.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Set;

@Controller
public class ProductController {
    private final ProductService productService;
    private final CategoryRepository categoryRepository;

    public ProductController(ProductService productService, CategoryRepository categoryRepository) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/products/{id}")
    public String productDetail(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails userDetails,
        Model model
    ) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        var product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/";
        }
        
        model.addAttribute("product", product);
        
        // проверка нужна ли опция цепи
        boolean needsChainOption = checkIfNeedsChainOption(product);
        boolean isPaired = checkIfPaired(product);
        
        model.addAttribute("needsChainOption", needsChainOption);
        model.addAttribute("isPaired", isPaired);
        
        return "product-detail";
    }
    
    private boolean checkIfNeedsChainOption(Product product) {
        // ID категорий которым нужна опция цепи
        Set<Long> chainCategoryIds = Set.of(20L, 17L, 13L, 14L);
        
        if (product.getCategories() == null || product.getCategories().isEmpty()) {
            return false;
        }
        
        return product.getCategories().stream()
            .anyMatch(cat -> cat != null && chainCategoryIds.contains(cat.getId()));
    }
    
    private boolean checkIfPaired(Product product) {
        // ID категории которой нужны 2 цепи
        if (product.getCategories() == null || product.getCategories().isEmpty()) {
            return false;
        }
        
        return product.getCategories().stream()
            .anyMatch(cat -> cat != null && cat.getId() == 17L);
    }
    
    @GetMapping("/category/{id}")
    public String categoryProducts(
        @PathVariable Long id,
        @RequestParam(defaultValue = "0") int page,
        @AuthenticationPrincipal UserDetails userDetails,
        Model model
    ) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            return "redirect:/";
        }
        
        Page<Product> products = productService.getProductsByCategory(id, PageRequest.of(page, 12));
        
        model.addAttribute("category", category);
        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        
        return "category-products";
    }
}
