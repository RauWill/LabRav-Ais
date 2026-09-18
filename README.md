# Order System (LabDevel)

Учебный проект системы управления заказами, разработанный с применением принципов **Domain-Driven Design (DDD)** и **Clean Architecture** на языке Java.

---

## 🛠 Технологический стек

* **Java**: 17
* **Система сборки**: Maven
* **Тестирование**: JUnit 5 / Maven Surefire Plugin

---

## 📁 Структура проекта

```text
LabDevel/
├── pom.xml
└── src/
    ├── main/
    │   └── java/
    │       └── com/
    │           └── example/
    │               ├── domain/                  # Доменный слой (Core Business Logic)
    │               │   ├── Money.java           # Value Object: Работа с деньгами
    │               │   ├── Order.java           # Aggregate Root: Сущность заказа
    │               │   ├── OrderId.java         # Value Object: Уникальный идентификатор заказа
    │               │   ├── OrderItem.java       # Позиция в заказе (товар, количество, цена)
    │               │   ├── OrderPolicy.java     # Бизнес-правила и политики заказа
    │               │   └── OrderStatus.java     # Enum: Статусы жизненного цикла заказа
    │               └── service/                 # Слой сервисов и инфраструктуры
    │                   ├── InMemoryOrderRepository.java  # Реализация хранилища в памяти
    │                   ├── OrderRepository.java          # Интерфейс репозитория
    │                   └── OrderService.java             # Прикладной сервис управления заказами
    └── test/
        └── java/
            └── com/
                └── example/
                    └── OrderSystemTest.java     # Модульные тесты системы
