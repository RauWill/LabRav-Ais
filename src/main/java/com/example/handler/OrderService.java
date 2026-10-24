package com.example.handler;

import com.example.domain.*;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final OrderRepository repository;
    private final OrderRule rules;

    public OrderService(OrderRepository repository, OrderRule rules) {
        this.repository = repository;
        this.rules = rules;
    }

    public OrderId createOrder() {
        OrderId id = OrderId.generate();
        repository.save(new Order(id));
        return id;
    }

    public void addItem(OrderId id, String productId, int qty, Money price) {
        Order order = getOrder(id);
        order.addItem(new OrderItem(productId, qty, price));
        repository.save(order);
    }

    public void payOrder(OrderId id) {
        Order order = getOrder(id);
        order.pay(rules);
        repository.save(order);
    }

    public void shipOrder(OrderId id) {
        Order order = getOrder(id);
        order.ship(rules);
        repository.save(order);
    }

    public void cancelOrder(OrderId id) {
        Order order = getOrder(id);
        order.cancel(rules);
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
