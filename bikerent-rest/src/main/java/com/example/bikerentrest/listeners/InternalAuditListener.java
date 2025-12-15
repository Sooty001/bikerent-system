package com.example.bikerentrest.listeners;

import org.example.RentalRatedEvent;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

@Component
public class InternalAuditListener {

    // Слушаем Fanout обменник, создаем СВОЮ уникальную очередь
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "q.rest.audit", durable = "true"),
            exchange = @Exchange(name = "rental-rating-fanout", type = "fanout")
    ))
    public void auditRating(RentalRatedEvent event) {
        System.out.println(">>> [REST INTERNAL LOG] Аренда " + event.rentalId() +
                " получила статус " + event.level());
    }
}