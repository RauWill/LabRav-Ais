package com.example.handler;

import com.example.domain.DuplicateOrder;
import com.example.domain.OrderId;
import com.example.domain.OrderNumber;
import com.example.domain.OrderStatus;
import com.example.persistence.OrderJpa;
import com.example.persistence.OrderMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrderRegistrationService {
    private final OrderJpa orders;

    public OrderRegistrationService(OrderJpa orders) {
        this.orders = orders;
    }

    /** One transaction per call. A duplicate number throws; the first row stays. */
    @Transactional
    public void register(OrderId id, OrderNumber number, String title) {
        try {
            orders.insert(OrderMapper.uuidOf(id), number, OrderStatus.NEW, title);
            orders.flush(); // persist only remembers; without flush the error comes at commit, past this catch
        } catch (DataIntegrityViolationException ex) {
            throw translate(ex, number);
        }
    }

    /** ONE transaction, two INSERTs. The second fails, so nothing is kept. */
    @Transactional
    public void insertTwice(OrderNumber number) {
        try {
            orders.insert(UUID.randomUUID(), number, OrderStatus.NEW, "First");
            orders.insert(UUID.randomUUID(), number, OrderStatus.NEW, "Second"); // a second UUID, the same number
            orders.flush();
        } catch (DataIntegrityViolationException ex) {
            throw translate(ex, number);
        }
    }

    /** UNIQUE (23505) becomes the unchecked DuplicateOrder -> Spring rolls back. Anything else stays as is. */
    private static RuntimeException translate(DataIntegrityViolationException ex, OrderNumber number) {
        return OrderJpa.isUniqueViolation(ex) ? new DuplicateOrder(number.value()) : ex;
    }
}
