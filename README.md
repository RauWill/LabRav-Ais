# CSS-3008 · Lab 3 — Tests, SQL, JDBC (Order System)

Product: orders (`Order`, `OrderId`, `OrderStatus`, `OrderPolicy`) — same as Labs 1 and 2.

## Run

```
docker compose up -d      # only PostgreSQL (css / css on localhost:5432)
mvn -q verify
mvn spring-boot:run
```

## Package diagram

```
  dto           client                    handler                    persistence   config
  ErpOrder-     InMemoryOrderRepository   OrderService               OrderJdbc     Application
  StatusDto     LoggingShipmentNotifier   OrderRegistrationService   (JDBC, SQL)   AppConfig
       \              \                   (@Service, @Transactional)      /           /
        \              \                          |                      /           /
                                      domain (no Spring, no java.sql)
        OrderId OrderNumber OrderStatus OrderPolicy Order Money OrderItem
        OrderRule + StatusTransitionRule + MinimumTotalRule (+ CompositeOrderRule)
        DuplicateOrder  ShipmentNotifier (port)  OrderRepository
```

## Week 4 status rows (`StatusRowsTest`, `@CsvSource`)

| From    | Action | Allowed |
| ------- | ------ | ------- |
| NEW     | PAY    | yes     |
| PAID    | SHIP   | yes     |
| SHIPPED | CANCEL | no (`IllegalStateException`) |
| NEW     | SHIP   | no (`IllegalStateException`) |

Null or blank `OrderId` / `OrderNumber` throws. One outbound port (`ShipmentNotifier`) is mocked in `OrderServicePortTest`.

## Database

`src/main/resources/db/schema.sql` — one table `orders`: surrogate `id uuid`, `order_number` UNIQUE NOT NULL, `status` with a CHECK (`NEW`, `PAID`, `SHIPPED`, `CANCELLED`), `title`, `created_at`.

- `OrderJdbc` (JdbcTemplate, `?` placeholders): `insert`, `count`, `findStatus`.
- `OrderRegistrationService`: `register` (one transaction per call) and `insertTwice` (one transaction, second INSERT fails → count 0).
- A duplicate `order_number` becomes the unchecked `DuplicateOrder` (in `domain`), so Spring rolls back.
- `OrderPersistenceTest` is not `@Transactional` and rebuilds the table from `schema.sql` before each test.
- No JPA, no Flyway, no REST, no H2.
