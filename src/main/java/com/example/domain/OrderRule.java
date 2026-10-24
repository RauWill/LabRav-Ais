package com.example.domain;

/** A business rule. Throws IllegalStateException if the action is not allowed. */
public interface OrderRule {
    void check(Order order, OrderAction action);
}
