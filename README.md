# CSS-3008 · Lab 3 — Tests, SQL, JPA, Flyway (Order System)

Product: orders (`Order`, `OrderId`, `OrderStatus`, `OrderPolicy`) — same as Labs 1 and 2.

## Run

```
docker compose up -d      # only PostgreSQL (css / css on localhost:5432)
mvn -q verify             # Flyway runs V1, Hibernate validates, tests run
mvn spring-boot:run
```

Clean database (proves V1 builds the table, not your old psql session):

```
docker compose down -v && docker compose up -d
```

## Package diagram

```
  dto           client                    handler                    persistence   config
  ErpOrder-     InMemoryOrderRepository   OrderService               OrderJpa      Application
  StatusDto     LoggingShipmentNotifier   OrderRegistrationService   (JPA, SQL)    AppConfig
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

`src/main/resources/db/migration/V1__create_orders.sql` — one table `orders`: surrogate `id uuid`, `order_number` UNIQUE NOT NULL, `status` with a CHECK (`NEW`, `PAID`, `SHIPPED`, `CANCELLED`), `title`, `created_at`.

- **Flyway** owns the schema. It runs `V1` on startup, writes `flyway_schema_history`, then Hibernate validates. A change is a new file (`V2__...`); an applied `V1` is never edited.
- **Hibernate** only validates: `ddl-auto: validate`, `open-in-view: false`. No `create`, no `update`.
- `OrderEntity` (persistence) names the columns. No `@GeneratedValue`: the id is the UUID from `OrderId`. The entity never leaves persistence.
- `OrderMapper`: domain types in, column tokens out. The tokens equal the CHECK.
- `OrderJpa` (`EntityManager`): `insert` (= `persist`), `flush`, `count`, `findStatus`.
- `OrderRegistrationService`: `register` (one transaction per call) and `insertTwice` (one transaction, second INSERT fails → count 0). Both flush inside the `try`, so the UNIQUE violation (SQLSTATE 23505) surfaces there and becomes the unchecked `DuplicateOrder` (in `domain`); Spring rolls back.
- `OrderPersistenceTest` is not `@Transactional`. It runs `DELETE FROM orders` before each test and never drops the table.
- `domain` has no `jakarta.persistence`. No REST, no H2.
