package com.example.service;

import com.example.domain.Order;
import com.example.domain.OrderId;

import java.util.Optional;

public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(OrderId id);
}