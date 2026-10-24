package com.example.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderRulesTest {
    private final OrderPolicy policy = new OrderPolicy();

    private Order orderWith(double price) {
        Order o = new Order(OrderId.generate());
        o.addItem(new OrderItem("P", 1, Money.usd(price)));
        return o;
    }

    @Test
    void statusRuleAllowsPayingNewOrder() {
        assertDoesNotThrow(() -> new StatusTransitionRule(policy).check(orderWith(50), OrderAction.PAY));
    }

    @Test
    void statusRuleRejectsShippingNewOrder() {
        assertThrows(IllegalStateException.class,
                () -> new StatusTransitionRule(policy).check(orderWith(50), OrderAction.SHIP));
    }

    @Test
    void minimumTotalRuleRejectsCheapPayment() {
        assertThrows(IllegalStateException.class,
                () -> new MinimumTotalRule(policy).check(orderWith(5), OrderAction.PAY));
    }

    @Test
    void minimumTotalRuleIgnoresOtherActions() {
        assertDoesNotThrow(() -> new MinimumTotalRule(policy).check(orderWith(5), OrderAction.CANCEL));
    }

    @Test
    void compositeRunsAllRules() {
        OrderRule chain = new CompositeOrderRule(new StatusTransitionRule(policy), new MinimumTotalRule(policy));
        assertDoesNotThrow(() -> chain.check(orderWith(50), OrderAction.PAY));
        assertThrows(IllegalStateException.class, () -> chain.check(orderWith(5), OrderAction.PAY));
    }
}
