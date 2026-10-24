package com.example.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    private final OrderId id;
    private OrderStatus status;
    private final List<OrderItem> items = new ArrayList<>();

    public Order(OrderId id) {
        this.id = id;
        this.status = OrderStatus.NEW;
    }

    public void addItem(OrderItem item) {
        if (status != OrderStatus.NEW) {
            throw new IllegalStateException("Cannot modify order in status " + status);
        }
        items.add(item);
    }

    public Money calculateTotal() {
        return items.stream()
                .map(OrderItem::totalPrice)
                .reduce(Money::add)
                .orElse(Money.usd(0));
    }

    public void pay(OrderRule rules) {
        rules.check(this, OrderAction.PAY);
        this.status = OrderStatus.PAID;
    }

    public void ship(OrderRule rules) {
        rules.check(this, OrderAction.SHIP);
        this.status = OrderStatus.SHIPPED;
    }

    public void cancel(OrderRule rules) {
        rules.check(this, OrderAction.CANCEL);
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
