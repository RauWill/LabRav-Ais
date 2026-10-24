package com.example.handler;

import com.example.client.InMemoryOrderRepository;
import com.example.domain.*;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

/** Week 4: one outbound port is mocked; no real HTTP is called. */
class OrderServicePortTest {

    @Test
    void shippingNotifiesThePort() {
        OrderPolicy policy = new OrderPolicy();
        OrderRule rules = new CompositeOrderRule(
                new StatusTransitionRule(policy), new MinimumTotalRule(policy));
        ShipmentNotifier notifier = mock(ShipmentNotifier.class);
        OrderService service = new OrderService(new InMemoryOrderRepository(), rules, notifier);

        OrderId id = service.createOrder();
        service.addItem(id, "PROD-1", 1, Money.usd(50.00));
        service.payOrder(id);
        verifyNoInteractions(notifier);

        service.shipOrder(id);
        verify(notifier).shipped(id);
    }
}
