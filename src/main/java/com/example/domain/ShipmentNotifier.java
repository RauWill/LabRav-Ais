package com.example.domain;

/** Outbound port: tells the outside world that an order was shipped. */
public interface ShipmentNotifier {
    void shipped(OrderId id);
}
