package com.example;

import com.example.domain.*;
import com.example.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderSystemTest {

    private OrderService service;

    @BeforeEach
    void setUp() {
        service = new OrderService(new InMemoryOrderRepository(), new OrderPolicy());
    }

    @Test
    @DisplayName("Complete order lifecycle: create -> add item -> pay -> ship")
    void shouldExecuteFullLifecycleSuccessfully() {
        OrderId id = service.createOrder();
        assertEquals(OrderStatus.NEW, service.getStatus(id));

        service.addItem(id, "PROD-10", 2, Money.usd(15.00));
        service.payOrder(id);
        assertEquals(OrderStatus.PAID, service.getStatus(id));

        service.shipOrder(id);
        assertEquals(OrderStatus.SHIPPED, service.getStatus(id));
    }

    @Test
    @DisplayName("Should reject payment if total order amount is below minimum threshold")
    void shouldRejectPaymentIfTotalIsTooLow() {
        OrderId id = service.createOrder();
        service.addItem(id, "PROD-1", 1, Money.usd(5.00));

        assertThrows(IllegalStateException.class, () -> service.payOrder(id));
    }

    @Test
    @DisplayName("Should not cancel an order that has already been shipped")
    void shouldNotCancelShippedOrder() {
        OrderId id = service.createOrder();
        service.addItem(id, "PROD-1", 1, Money.usd(50.00));
        service.payOrder(id);
        service.shipOrder(id);

        assertThrows(IllegalStateException.class, () -> service.cancelOrder(id));
    }
}