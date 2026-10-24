# CSS-3008 · Lab 2 — Spring Boot joined to the Order System

Product: orders (`Order`, `OrderId`, `OrderStatus`, `OrderPolicy`) — same as Lab 1.

Run: `mvn -q verify` · `mvn spring-boot:run`

## Package diagram

```
  dto                client                 handler          config
  ErpOrderStatusDto  InMemoryOrderRepository OrderService    Application
  (JSON later)       (HTTP/storage)          @Service        AppConfig (@Bean)
       \                  \                   /                  |
        \                  \                 /            injects OrderRule
                          domain
        OrderId  OrderStatus  OrderPolicy  Order  Money  OrderItem
        OrderRule + StatusTransitionRule + MinimumTotalRule
        (+ CompositeOrderRule, OrderRepository)
                         (no Spring)
```

Arrows point inward. `domain` never imports `org.springframework`.

## Rules
- `StatusTransitionRule` — status table: NEW → PAID → SHIPPED, cancel only from NEW/PAID.
- `MinimumTotalRule` — stop-factor: an order below the minimum total cannot be paid.
- Chain is wired with a `@Bean` in `config/AppConfig`; `OrderService` gets `OrderRule` by constructor injection.
