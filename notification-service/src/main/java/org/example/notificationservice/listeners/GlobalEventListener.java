package org.example.notificationservice.listeners;

import org.example.RentalRatedEvent;
import org.example.notificationservice.websocket.NotificationHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.example.BicycleDeletedEvent;
import org.example.CustomerRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GlobalEventListener {

    private static final Logger log = LoggerFactory.getLogger(GlobalEventListener.class);

    private final NotificationHandler notificationHandler;
    private final ObjectMapper objectMapper; // Для JSON

    // Idempotency check set
    private final Set<Long> processedBicycleDeletions = ConcurrentHashMap.newKeySet();

    public GlobalEventListener(NotificationHandler notificationHandler) {
        this.notificationHandler = notificationHandler;
        this.objectMapper = new ObjectMapper();
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "notification.customer.registered", durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = "dlx-exchange"),
                            @Argument(name = "x-dead-letter-routing-key", value = "dlq.notifications")
                    }),
            exchange = @Exchange(name = "bikerent-exchange", type = "topic", durable = "true"),
            key = "customer.registered"
    ))
    public void handleCustomerRegistered(@Payload CustomerRegisteredEvent event, Channel channel,
                                         @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            log.info("[Email Service] Sending welcome email to: {}", event.email());
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("Error processing customer event: {}", event, e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "notification.bicycle.deleted", durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = "dlx-exchange"),
                            @Argument(name = "x-dead-letter-routing-key", value = "dlq.notifications")
                    }),
            exchange = @Exchange(name = "bikerent-exchange", type = "topic", durable = "true"),
            key = "bicycle.deleted"
    ))
    public void handleBicycleDeleted(@Payload BicycleDeletedEvent event, Channel channel,
                                     @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            // Idempotency check
            if (!processedBicycleDeletions.add(event.bicycleId())) {
                log.warn("Duplicate bicycle deletion event for ID: {}. Skipping.", event.bicycleId());
                channel.basicAck(deliveryTag, false);
                return;
            }

            log.info("[Admin Alert] Bicycle ID {} was deleted. Notifying admins.", event.bicycleId());

            // Simulation of a fatal error for DLQ test (Targeting ID 2)
            if (event.bicycleId() == 2) {
                throw new RuntimeException("Simulated error for DLQ test!");
            }

            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("Error processing bicycle deletion: {}", event, e);
            // Send to DLQ (requeue = false)
            channel.basicNack(deliveryTag, false, false);
        }
    }

    // DLQ Listener
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "notification-dlq", durable = "true"),
            exchange = @Exchange(name = "dlx-exchange", type = "topic", durable = "true"),
            key = "dlq.notifications"
    ))
    public void handleDlqMessages(@Payload Object failedMessage,
                                  Channel channel,
                                  @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        log.error("!!! ALARM: Message moved to DLQ (Dead Letter): {}", failedMessage);
        channel.basicAck(deliveryTag, false);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "q.notification.rating", durable = "true"),
            exchange = @Exchange(name = "rental-rating-fanout", type = "fanout")
    ))
    public void handleRatingEvent(@Payload RentalRatedEvent event,
                                  Channel channel,
                                  @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            log.info("Received Rating Event: {}", event);

            // 1. Превращаем событие в JSON-строку для браузера
            String jsonMessage = objectMapper.writeValueAsString(event);

            // 2. Отправляем всем подключенным браузерам
            notificationHandler.broadcast(jsonMessage);

            log.info("Sent to WebSockets: {}", jsonMessage);

            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("Failed to process rating event", e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}