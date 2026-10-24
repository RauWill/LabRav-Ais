package com.example.persistence;

import com.example.domain.OrderId;
import com.example.domain.OrderNumber;
import com.example.domain.OrderStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class OrderJdbc {
    private final JdbcTemplate jdbc;

    public OrderJdbc(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void insert(UUID id, OrderNumber number, OrderStatus status, String title) {
        jdbc.update("""
                INSERT INTO orders (id, order_number, status, title)
                VALUES (?, ?, ?, ?)
                """, id, number.value(), status.name(), title);
    }

    public int count(OrderNumber number) {
        Integer n = jdbc.queryForObject(
                "SELECT count(*) FROM orders WHERE order_number = ?",
                Integer.class,
                number.value());
        return n == null ? 0 : n;
    }

    public Optional<OrderStatus> findStatus(OrderNumber number) {
        List<String> rows = jdbc.queryForList(
                "SELECT status FROM orders WHERE order_number = ?",
                String.class,
                number.value());
        return rows.stream().findFirst().map(OrderJdbc::toStatus);
    }

    /** Text from the database -> enum. An unknown value is an error, never a new status. */
    static OrderStatus toStatus(String text) {
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
