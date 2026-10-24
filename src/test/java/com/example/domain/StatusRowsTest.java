package com.example.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/** Week 4: the four README status rows. */
class StatusRowsTest {
    private final OrderRule statusTable = new StatusTransitionRule(new OrderPolicy());
    private final OrderRule allowAll = (order, action) -> { };

    private Order orderIn(OrderStatus status) {
        Order order = new Order(OrderId.generate());
        switch (status) {
            case NEW -> { }
            case PAID -> order.pay(allowAll);
            case SHIPPED -> { order.pay(allowAll); order.ship(allowAll); }
            case CANCELLED -> order.cancel(allowAll);
        }
        return order;
    }

    @ParameterizedTest(name = "{0} + {1} allowed={2}")
    @CsvSource({
            "NEW,     PAY,    true",
            "PAID,    SHIP,   true",
            "SHIPPED, CANCEL, false",
            "NEW,     SHIP,   false"
    })
    void statusRows(OrderStatus from, OrderAction action, boolean allowed) {
        Order order = orderIn(from);
        if (allowed) {
            assertDoesNotThrow(() -> statusTable.check(order, action));
        } else {
            assertThrows(IllegalStateException.class, () -> statusTable.check(order, action));
        }
    }

    @org.junit.jupiter.api.Test
    void blankOrNullIdThrows() {
        assertThrows(IllegalArgumentException.class, () -> new OrderId(null));
        assertThrows(IllegalArgumentException.class, () -> new OrderId("  "));
        assertThrows(IllegalArgumentException.class, () -> new OrderNumber(""));
    }
}
