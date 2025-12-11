package com.shop.model;

public enum OrderStatus {
    PROCESSING("В обработке"),
    SHIPPED("Отправлен"),
    COMPLETED("Выполнен"),
    CANCELLED("Отменен");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
