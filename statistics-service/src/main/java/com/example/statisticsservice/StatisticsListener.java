package com.example.statisticsservice;

import com.rabbitmq.client.Channel;
import org.example.CustomerRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class StatisticsListener {

    private static final Logger log = LoggerFactory.getLogger(StatisticsListener.class);
    private final StatsStore statsStore;

    public StatisticsListener(StatsStore statsStore) {
        this.statsStore = statsStore;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "stats.customer.registered", durable = "true"),
            exchange = @Exchange(name = "bikerent-exchange", type = "topic", durable = "true"),
            key = "customer.registered"
    ))
    public void countCustomers(@Payload CustomerRegisteredEvent event, Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        long currentCount = statsStore.incrementCustomers();
        statsStore.setLastEvent("Registered: " + event.fullName());

        log.info("[Stats] +1 Customer. Total: {}", currentCount);
        channel.basicAck(deliveryTag, false);
    }
}