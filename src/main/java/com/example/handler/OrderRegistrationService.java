package com.example.handler;

import com.example.domain.DuplicateOrder;
import com.example.domain.OrderId;
import com.example.domain.OrderNumber;
import com.example.domain.OrderStatus;
import com.example.persistence.OrderJdbc;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrderRegistrationService {
    private final OrderJdbc orders;

    public OrderRegistrationService(OrderJdbc orders) {
        this.orders = orders;
    }

    /** One transaction per call. A duplicate number throws; the first row stays. */
    @Transactional
    public void register(OrderId id, OrderNumber number, String title) {
        try {
            orders.insert(OrderJdbc.uuidOf(id), number, OrderStatus.NEW, title);
        } catch (DuplicateKeyException ex) {
            throw new DuplicateOrder(number.value()); // unchecked -> rollback
        }
    }

    /** ONE transaction, two INSERTs. The second fails, so nothing is kept. */
    @Transactional
    public void insertTwice(OrderNumber number) {
        orders.insert(UUID.randomUUID(), number, OrderStatus.NEW, "First");
        try {
            orders.insert(UUID.randomUUID(), number, OrderStatus.NEW, "Second");
        } catch (DuplicateKeyException ex) {
            throw new DuplicateOrder(number.value()); // unchecked -> rollback
        }
    }
}
