package com.example.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * The row, not the rule. Names the columns of V1__create_orders.sql.
 * Lives in persistence: domain never imports jakarta.persistence.
 * No @GeneratedValue: the id is the UUID that comes from OrderId.
 */
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    private UUID id;

    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    /** Token from the CHECK: NEW / PAID / SHIPPED / CANCELLED. */
    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String title;

    /** Filled by DEFAULT now() in PostgreSQL. Java never sends it. */
    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
}
