package com.example.domain;

/** Human-readable business key, e.g. ORD-1042. */
public record OrderNumber(String value) {
    public OrderNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("OrderNumber cannot be blank");
        }
    }
}
