package com.shop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

// изображения хранятся как JSON, поддержка множественных категорий

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Column(length = 1000)
    private String description;

    @NotNull
    @Min(0)
    private BigDecimal price;

    @NotNull
    @Min(0)
    private Integer quantity;

    // основная категория товара
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // дополнительные категории (связь через промежуточную таблицу)
    @ManyToMany
    @JoinTable(
        name = "product_categories",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    // изображения товара хранятся как JSON массив
    @Column(columnDefinition = "TEXT")
    private String images;

    private String material;

    public Product() {}

    public Product(String name, String description, BigDecimal price, Integer quantity, Category category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }

    public boolean isAvailable() {
        return quantity > 0;
    }

    // десериализация JSON изображений в список строк
    public List<String> getImageList() {
        if (images == null || images.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(images, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // сериализация списка изображений в JSON для хранения в БД
    public void setImageList(List<String> imageList) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.images = mapper.writeValueAsString(imageList);
        } catch (Exception e) {
            this.images = "[]";
        }
    }

    public String getMainImage() {
        List<String> imageList = getImageList();
        return imageList.isEmpty() ? "/images/logo.png" : imageList.get(0);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Set<Category> getCategories() { return categories; }
    public void setCategories(Set<Category> categories) { this.categories = categories; }

    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }
}
