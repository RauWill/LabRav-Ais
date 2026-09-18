package com.example.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    private final OrderId id;
    private OrderStatus status;
    private final List<OrderItem> items = new ArrayList<>();
    private final OrderPolicy policy;

    public Order(OrderId id, OrderPolicy policy) {
        this.id = id;
        this.policy = policy;
        this.status = OrderStatus.NEW;
    }

    public void addItem(OrderItem item) {
        if (status != OrderStatus.NEW) {
            throw new IllegalStateException("Cannot modify order in status " + status);
        }
        items.add(item);
    }

    public Money calculateTotal() {
        if (items.isEmpty()) {
            return Money.usd(0);
        }
        return items.stream()
                .map(OrderItem::totalPrice)
                .reduce(Money::add)
                .orElse(Money.usd(0));
    }

    public void pay() {
        if (status != OrderStatus.NEW) {
            throw new IllegalStateException("Only NEW orders can be paid");
        }
        if (!policy.isValidTotal(calculateTotal())) {
            throw new IllegalStateException("Order total is below minimum threshold");
        }
        this.status = OrderStatus.PAID;
    }

    public void ship() {
        if (!policy.canBeShipped(status)) {
            throw new IllegalStateException("Order cannot be shipped from status " + status);
        }
        this.status = OrderStatus.SHIPPED;
    }

    public void cancel() {
        if (!policy.canBeCancelled(status)) {
            throw new IllegalStateException("Order cannot be cancelled from status " + status);
        }
        this.status = OrderStatus.CANCELLED;
    }

    public OrderId getId() {
        return id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}