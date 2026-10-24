package com.example.domain;

/** Rule 1: the status table (NEW -> PAID -> SHIPPED, cancel from NEW/PAID). */
public class StatusTransitionRule implements OrderRule {
    private final OrderPolicy policy;

    public StatusTransitionRule(OrderPolicy policy) {
        this.policy = policy;
    }

    @Override
    public void check(Order order, OrderAction action) {
        OrderStatus status = order.getStatus();
        boolean ok = switch (action) {
            case PAY -> policy.canBePaid(status);
            case SHIP -> policy.canBeShipped(status);
            case CANCEL -> policy.canBeCancelled(status);
        };
        if (!ok) {
            throw new IllegalStateException("Cannot " + action + " order in status " + status);
        }
    }
}
