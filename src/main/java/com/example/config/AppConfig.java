package com.example.config;

import com.example.client.InMemoryOrderRepository;
import com.example.domain.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public OrderPolicy orderPolicy() {
        return new OrderPolicy();
    }

    @Bean
    public OrderRule orderRule(OrderPolicy policy) {
        return new CompositeOrderRule(
                new StatusTransitionRule(policy),
                new MinimumTotalRule(policy));
    }

    @Bean
    public OrderRepository orderRepository() {
        return new InMemoryOrderRepository();
    }
}
