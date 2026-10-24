package com.example.config;

import com.example.domain.Money;
import com.example.domain.OrderId;
import com.example.handler.OrderService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "com.example")
@EntityScan("com.example.persistence")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner demo(OrderService service) {
        return args -> {
            OrderId id = service.createOrder();
            service.addItem(id, "PROD-10", 2, Money.usd(15.00));
            service.payOrder(id);
            service.shipOrder(id);
            System.out.println("Order " + id.value() + " -> " + service.getStatus(id));
        };
    }
}
