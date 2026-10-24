package com.example.client;

import com.example.domain.Order;
import com.example.domain.OrderId;
import com.example.domain.OrderRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Outbound adapter (storage). Wired as a bean in config. */
public class InMemoryOrderRepository implements OrderRepository {
    private final Map<OrderId, Order> storage = new ConcurrentHashMap<>();

    @Override
    public void save(Order order) {
        storage.put(order.getId(), order);
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return Optional.ofNullable(storage.get(id));
    }
}
