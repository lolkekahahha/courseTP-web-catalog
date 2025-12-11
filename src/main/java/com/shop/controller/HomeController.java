package com.shop.controller;

import com.shop.repository.CategoryRepository;
import com.shop.service.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// отображение товаров с пагинацией, поиск, фильтрация по категориям
@Controller
public class HomeController {
    private final ProductService productService; // бизнес-логика товаров
    private final CategoryRepository categoryRepository; // прямой доступ к категориям

    public HomeController(ProductService productService, CategoryRepository categoryRepository) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
    }

    // главная страница с каталогом товаров
    @GetMapping("/")
    public String home(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) Long categoryId,
        Model model
    ) {
        // получение товаров через сервисный слой с пагинацией
        var products = productService.searchProducts(search, categoryId, PageRequest.of(page, 12));
        
        // передача данных в визуальное представление
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        model.addAttribute("categoryId", categoryId);
        
        return "index";
    }
}
