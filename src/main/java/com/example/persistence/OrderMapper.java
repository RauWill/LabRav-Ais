package com.example.persistence;

import com.example.domain.OrderId;
import com.example.domain.OrderNumber;
import com.example.domain.OrderStatus;

import java.util.UUID;

/** Domain types in, column values out. Only translates; the policy decides what is legal. */
public final class OrderMapper {

    private OrderMapper() { }

    public static OrderEntity toEntity(UUID id, OrderNumber number, OrderStatus status, String title) {
        var row = new OrderEntity();
        row.setId(id);
        row.setOrderNumber(number.value());
        row.setTitle(title);
        row.setStatus(toColumn(status));
        return row;
    }

    /** Explicit tokens: they must equal the CHECK in V1, not whatever the enum constant is called. */
    static String toColumn(OrderStatus status) {
        return switch (status) {
            case NEW -> "NEW";
            case PAID -> "PAID";
            case SHIPPED -> "SHIPPED";
            case CANCELLED -> "CANCELLED";
        };
    }

    /** Text from the database -> enum. An unknown value is a bug, never a new status. */
    public static OrderStatus toStatus(String text) {
        try {
            return OrderStatus.valueOf(text);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("Unknown status in database: " + text, ex);
        }
    }

    public static UUID uuidOf(OrderId id) {
        return UUID.fromString(id.value());
    }
}
