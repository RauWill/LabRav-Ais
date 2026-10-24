package com.example.domain;

import java.util.List;

/** Chain of rules: all of them must pass. */
public class CompositeOrderRule implements OrderRule {
    private final List<OrderRule> rules;

    public CompositeOrderRule(OrderRule... rules) {
        this.rules = List.of(rules);
    }

    @Override
    public void check(Order order, OrderAction action) {
        for (OrderRule rule : rules) {
            rule.check(order, action);
        }
    }
}
