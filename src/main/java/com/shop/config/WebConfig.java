package com.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${upload.path:src/main/resources/static/uploads/products}")
    private String uploadPath;
    
    @Value("${upload.categories.path:src/main/resources/static/uploads/categories}")
    private String categoriesUploadPath;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Настройка для отдачи загруженных изображений товаров
        registry.addResourceHandler("/uploads/products/**")
                .addResourceLocations("file:" + uploadPath + "/");
        
        // Настройка для отдачи загруженных изображений категорий
        registry.addResourceHandler("/uploads/categories/**")
                .addResourceLocations("file:" + categoriesUploadPath + "/");
    }
}