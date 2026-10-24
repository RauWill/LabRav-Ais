package com.example.client;

import com.example.domain.OrderId;
import com.example.domain.ShipmentNotifier;

/** Stand-in adapter; a real HTTP client comes in a later lab. */
public class LoggingShipmentNotifier implements ShipmentNotifier {
    @Override
    public void shipped(OrderId id) {
        System.out.println("notify: order " + id.value() + " shipped");
    }
}
