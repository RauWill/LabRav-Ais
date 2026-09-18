package com.example.domain;

import java.util.Objects;

public record OrderItem(String productId, int quantity, Money unitPrice) {
    public OrderItem {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        Objects.requireNonNull(unitPrice, "Unit price required");
    }

    public Money totalPrice() {
        return unitPrice.multiply(quantity);
    }
}