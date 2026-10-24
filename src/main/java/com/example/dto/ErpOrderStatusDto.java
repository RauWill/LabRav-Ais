package com.example.dto;

import com.example.domain.OrderStatus;

/** Vendor (ERP) JSON shape; mapped onto our OrderStatus. */
public record ErpOrderStatusDto(String code) {
    public OrderStatus toStatus() {
        return switch (code) {
            case "OPEN" -> OrderStatus.NEW;
            case "PAID" -> OrderStatus.PAID;
            case "DELIVERED" -> OrderStatus.SHIPPED;
            case "VOID" -> OrderStatus.CANCELLED;
            default -> throw new IllegalArgumentException("Unknown ERP code: " + code);
        };
    }
}
