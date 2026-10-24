package com.example.domain;

public class DuplicateOrder extends RuntimeException {
    public DuplicateOrder(String orderNumber) {
        super("duplicate order: " + orderNumber);
    }
}
