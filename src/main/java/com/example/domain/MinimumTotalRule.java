package com.example.domain;

/** Rule 2 (stop-factor): an order below the minimum total cannot be paid. */
public class MinimumTotalRule implements OrderRule {
    private final OrderPolicy policy;

    public MinimumTotalRule(OrderPolicy policy) {
        this.policy = policy;
    }

    @Override
    public void check(Order order, OrderAction action) {
        if (action == OrderAction.PAY && !policy.isValidTotal(order.calculateTotal())) {
            throw new IllegalStateException("Order total is below minimum threshold");
        }
    }
}
