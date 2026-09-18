package com.example.domain;

import java.math.BigDecimal;

public class OrderPolicy {
    private static final BigDecimal MIN_ORDER_AMOUNT = BigDecimal.valueOf(10.00);

    public boolean canBeCancelled(OrderStatus status) {
        return status == OrderStatus.NEW || status == OrderStatus.PAID;
    }

    public boolean canBeShipped(OrderStatus status) {
        return status == OrderStatus.PAID;
    }

    public boolean isValidTotal(Money total) {
        return total.amount().compareTo(MIN_ORDER_AMOUNT) >= 0;
    }
}