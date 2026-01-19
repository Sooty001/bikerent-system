package org.example.notificationservice.listeners;

import com.example.bikerentcontracts.events.BookingCreatedEvent;
import com.example.bikerentcontracts.events.CustomerRegisteredEvent;
import com.example.bikerentcontracts.events.RentalEndedEvent;
import com.example.bikerentcontracts.events.RentalStartedEvent;
import com.rabbitmq.client.Channel;
import org.example.notificationservice.websocket.NotificationHandler;
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

    private static final String DLX = "dlx.bikerent";
    private final Set<String> processedEventIds = ConcurrentHashMap.newKeySet();

    public GlobalEventListener(NotificationHandler notificationHandler) {
        this.notificationHandler = notificationHandler;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    name = "notify.customer",
                    durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = DLX),
                            @Argument(name = "x-dead-letter-routing-key", value = "error.customer.registered")
                    }
            ),
            exchange = @Exchange(name = "bikerent.topic", type = "topic"),
            key = "customer.registered"
    ))
    public void onCustomerRegistered(@Payload CustomerRegisteredEvent event, Channel ch, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        if (!processedEventIds.add(event.customerId())) {
            log.warn("Duplicate event CustomerRegisteredEvent: {}. Skip it.", event.customerId());
            ch.basicAck(tag, false);
            return;
        }
        sendNotification("Новый клиент: " + event.fullName(), ch, tag);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    name = "notify.booking",
                    durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = DLX),
                            @Argument(name = "x-dead-letter-routing-key", value = "error.notify.booking")
                    }
            ),
            exchange = @Exchange(name = "bikerent.topic", type = "topic"),
            key = "booking.created"
    ))
    public void onBookingCreated(@Payload BookingCreatedEvent event, Channel ch, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        if (!processedEventIds.add(event.bookingId())) {
            log.warn("Duplicate event BookingCreatedEvent: {}.  Skip it.", event.bookingId());
            ch.basicAck(tag, false);
            return;
        }
        sendNotification("Бронь создана: " + event.bicycleId(), ch, tag);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    name = "notify.rental.start",
                    durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = DLX),
                            @Argument(name = "x-dead-letter-routing-key", value = "error.notify.rental.start")
                    }
            ),
            exchange = @Exchange(name = "bikerent.topic", type = "topic"),
            key = "rental.started"
    ))
    public void onRentalStarted(@Payload RentalStartedEvent event, Channel ch, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        sendNotification("Поездка началась!", ch, tag);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    name = "notify.financial",
                    durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = DLX),
                            @Argument(name = "x-dead-letter-routing-key", value = "error.notify.financial")
                    }
            ),
            exchange = @Exchange(name = "bikerent.financial", type = "fanout")
    ))
    public void onRentalEnded(@Payload RentalEndedEvent event, Channel ch, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        sendNotification(String.format("Пользователь: %s, Оплата: %.2f руб.", event.customerId(), event.finalPrice()), ch, tag);
    }

    private void sendNotification(String message, Channel channel, long tag) throws IOException {
        try {
            notificationHandler.broadcast(message);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("Failed to send notification. Sending to DLQ", e);
            channel.basicNack(tag, false, false);
        }
    }
}