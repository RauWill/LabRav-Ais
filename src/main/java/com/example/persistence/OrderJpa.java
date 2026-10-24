package com.example.persistence;

import com.example.domain.OrderNumber;
import com.example.domain.OrderStatus;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * EntityManager.persist always means INSERT. Spring Data save() with a ready UUID would SELECT first.
 * The entity never leaves this class: callers see domain types only.
 */
@Repository
public class OrderJpa {
    /** PostgreSQL SQLSTATE for unique_violation. */
    private static final String UNIQUE_VIOLATION = "23505";

    private final EntityManager em;

    public OrderJpa(EntityManager em) {
        this.em = em;
    }

    /** persist only remembers the row. Call flush() to send the INSERT. */
    public void insert(UUID id, OrderNumber number, OrderStatus status, String title) {
        em.persist(OrderMapper.toEntity(id, number, status, title));
    }

    public void flush() {
        em.flush();
    }

    /** The query names the field (orderNumber), not the column (order_number). */
    public int count(OrderNumber number) {
        Long n = em.createQuery("""
                        select count(o) from OrderEntity o
                        where o.orderNumber = :number
                        """, Long.class)
                .setParameter("number", number.value())
                .getSingleResult();
        return n.intValue();
    }

    public Optional<OrderStatus> findStatus(OrderNumber number) {
        List<String> rows = em.createQuery("""
                        select o.status from OrderEntity o
                        where o.orderNumber = :number
                        """, String.class)
                .setParameter("number", number.value())
                .getResultList();
        return rows.stream().findFirst().map(OrderMapper::toStatus);
    }

    /** True when the cause chain holds PostgreSQL 23505 (UNIQUE), not a CHECK or NOT NULL failure. */
    public static boolean isUniqueViolation(Throwable ex) {
        for (Throwable t = ex; t != null; t = t.getCause()) {
            if (t instanceof SQLException sql && UNIQUE_VIOLATION.equals(sql.getSQLState())) {
                return true;
            }
        }
        return false;
    }
}
