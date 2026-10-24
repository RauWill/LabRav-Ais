package com.example.persistence;

import com.example.domain.DuplicateOrder;
import com.example.domain.OrderId;
import com.example.domain.OrderNumber;
import com.example.domain.OrderStatus;
import com.example.handler.OrderRegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Needs PostgreSQL on localhost:5432 (docker compose up -d).
 * Flyway builds the table when the context starts; Hibernate only validates it.
 * Deliberately NOT @Transactional: asserts see what was really committed.
 */
@SpringBootTest
class OrderPersistenceTest {

    private static final OrderNumber KEY = new OrderNumber("ORD-1042");

    @Autowired JdbcTemplate jdbc;
    @Autowired OrderJpa orders;
    @Autowired OrderRegistrationService service;

    /** Delete rows, never drop the table: flyway_schema_history would still say V1 ran. */
    @BeforeEach
    void empty() {
        jdbc.update("DELETE FROM orders");
    }

    @Test
    void flywayCreatedTheTableFromV1() {
        Integer applied = jdbc.queryForObject(
                "SELECT count(*) FROM flyway_schema_history WHERE version = '1' AND success",
                Integer.class);
        assertEquals(1, applied);
    }

    @Test
    void secondStatementRollsBack() {
        assertThrows(DuplicateOrder.class, () -> service.insertTwice(KEY));
        assertEquals(0, orders.count(KEY));
    }

    @Test
    void secondRequestKeepsTheFirst() {
        service.register(OrderId.generate(), KEY, "Laptop");
        assertThrows(DuplicateOrder.class,
                () -> service.register(OrderId.generate(), KEY, "Again"));
        assertEquals(1, orders.count(KEY));
    }

    @Test
    void registeredOrderStartsAsNew() {
        service.register(OrderId.generate(), KEY, "Laptop");
        assertEquals(OrderStatus.NEW, orders.findStatus(KEY).orElseThrow());
    }

    @Test
    void checkConstraintRejectsUnknownStatus() {
        assertThrows(DataIntegrityViolationException.class, () -> jdbc.update(
                "INSERT INTO orders (id, order_number, status, title) VALUES (?, ?, ?, ?)",
                UUID.randomUUID(), "ORD-1", "BOGUS", "x"));
    }

    @Test
    void unknownStatusTextIsNotInventedAsAStatus() {
        assertThrows(IllegalStateException.class, () -> OrderMapper.toStatus("BOGUS"));
    }
}
