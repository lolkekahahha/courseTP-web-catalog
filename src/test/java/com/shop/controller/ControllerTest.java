package com.shop.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testHomePageLoads() throws Exception {
        // проверяем, что главная страница загружается без ошибок
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index")) // правильное имя представления
                .andExpect(model().attributeExists("categories")) // есть атрибут categories
                .andExpect(model().attributeExists("products")); // products
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testLoginPageLoads() throws Exception {
        // проверяем, что страница логина загружается
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testRegisterPageLoads() throws Exception {
        // проверяем, что страница регистрации загружается
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void testUnauthorizedAccessToCart() throws Exception {
        // проверяем, что неавторизованный доступ к корзине перенаправляет на логин
        mockMvc.perform(get("/cart"))
                .andExpect(status().is3xxRedirection()); // редирект на страницу логина
    }
}