package com.example.service;

import com.example.domain.*;

public class OrderService {
    private final OrderRepository repository;
    private final OrderPolicy policy;

    public OrderService(OrderRepository repository, OrderPolicy policy) {
        this.repository = repository;
        this.policy = policy;
    }

    public OrderId createOrder() {
        OrderId id = OrderId.generate();
        Order order = new Order(id, policy);
        repository.save(order);
        return id;
    }

    public void addItem(OrderId id, String productId, int qty, Money price) {
        Order order = getOrder(id);
        order.addItem(new OrderItem(productId, qty, price));
        repository.save(order);
    }

    public void payOrder(OrderId id) {
        Order order = getOrder(id);
        order.pay();
        repository.save(order);
    }

    public void shipOrder(OrderId id) {
        Order order = getOrder(id);
        order.ship();
        repository.save(order);
    }

    public void cancelOrder(OrderId id) {
        Order order = getOrder(id);
        order.cancel();
        repository.save(order);
    }

    public OrderStatus getStatus(OrderId id) {
        return getOrder(id).getStatus();
    }

    private Order getOrder(OrderId id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
    }
}