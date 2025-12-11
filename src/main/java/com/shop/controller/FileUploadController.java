package com.shop.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/upload")
public class FileUploadController {

    @Value("${upload.path:src/main/resources/static/uploads/products}")
    private String uploadPath;
    
    @Value("${upload.categories.path:src/main/resources/static/uploads/categories}")
    private String categoriesUploadPath;

    @PostMapping("/images")
    @ResponseBody
    public ResponseEntity<?> uploadImages(@RequestParam("files") MultipartFile[] files) {
        List<String> uploadedUrls = new ArrayList<>();
        
        try {
            System.out.println("Получено файлов: " + files.length);
            
            // создаем директорию если не существует
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                System.out.println("Создана директория: " + uploadDir.toAbsolutePath());
            }

            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    continue;
                }

                // генерируем уникальное имя файла
                String originalFilename = file.getOriginalFilename();
                String extension = originalFilename != null && originalFilename.contains(".") 
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
                String filename = UUID.randomUUID().toString() + extension;

                // сохраняем файл
                Path filePath = uploadDir.resolve(filename);
                Files.copy(file.getInputStream(), filePath);
                
                System.out.println("Файл сохранен: " + filePath.toAbsolutePath());

                // добавляем URL для доступа к файлу
                uploadedUrls.add("/uploads/products/" + filename);
            }

            System.out.println("Загружено URL: " + uploadedUrls);
            return ResponseEntity.ok(uploadedUrls);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Ошибка загрузки файлов: " + e.getMessage());
        }
    }

    @PostMapping("/categories/images")
    @ResponseBody
    public ResponseEntity<?> uploadCategoryImages(@RequestParam("files") MultipartFile[] files) {
        List<String> uploadedUrls = new ArrayList<>();
        
        try {
            System.out.println("Получено файлов категорий: " + files.length);
            
            // создаем директорию если не существует
            Path uploadDir = Paths.get(categoriesUploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                System.out.println("Создана директория категорий: " + uploadDir.toAbsolutePath());
            }

            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    continue;
                }

                // генерируем уникальное имя файла
                String originalFilename = file.getOriginalFilename();
                String extension = originalFilename != null && originalFilename.contains(".") 
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
                String filename = UUID.randomUUID().toString() + extension;

                // сохраняем файл
                Path filePath = uploadDir.resolve(filename);
                Files.copy(file.getInputStream(), filePath);
                
                System.out.println("Файл категории сохранен: " + filePath.toAbsolutePath());

                // добавляем URL для доступа к файлу
                uploadedUrls.add("/uploads/categories/" + filename);
            }

            System.out.println("Загружено URL категорий: " + uploadedUrls);
            return ResponseEntity.ok(uploadedUrls);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Ошибка загрузки файлов категорий: " + e.getMessage());
        }
    }

    @DeleteMapping("/images")
    @ResponseBody
    public ResponseEntity<?> deleteImage(@RequestParam("url") String imageUrl) {
        try {
            // извлекаем имя файла из URL
            String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Path filePath;
            
            // определяем, это изображение товара или категории
            if (imageUrl.contains("/uploads/categories/")) {
                filePath = Paths.get(categoriesUploadPath, filename);
            } else {
                filePath = Paths.get(uploadPath, filename);
            }
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                return ResponseEntity.ok("Файл удален");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Ошибка удаления файла: " + e.getMessage());
        }
    }
}
