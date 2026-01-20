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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GlobalEventListener {

    private static final Logger log = LoggerFactory.getLogger(GlobalEventListener.class);
    private final NotificationHandler notificationHandler;

    private static final String DLX = "dlx.bikerent";
    private final Set<String> processedEventIds = ConcurrentHashMap.newKeySet();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    public GlobalEventListener(NotificationHandler notificationHandler) {
        this.notificationHandler = notificationHandler;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "notify.customer", durable = "true", arguments = {@Argument(name = "x-dead-letter-exchange", value = DLX)}),
            exchange = @Exchange(name = "bikerent.topic", type = "topic"),
            key = "customer.registered"
    ))
    public void onCustomerRegistered(@Payload CustomerRegisteredEvent event, Channel ch, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        if (!processedEventIds.add(event.customerId())) {
            ch.basicAck(tag, false);
            return;
        }
        String msg = String.format("НОВЫЙ КЛИЕНТ: %s (%s) | Loyalty: %d",
                event.fullName(), event.email(), event.initialLoyaltyPoints());

        sendNotification(msg, ch, tag);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "notify.booking", durable = "true", arguments = {@Argument(name = "x-dead-letter-exchange", value = DLX)}),
            exchange = @Exchange(name = "bikerent.topic", type = "topic"),
            key = "booking.created"
    ))
    public void onBookingCreated(@Payload BookingCreatedEvent event, Channel ch, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        if (!processedEventIds.add(event.bookingId())) {
            ch.basicAck(tag, false);
            return;
        }

        String timeStr = "";
        try {
            timeStr = LocalDateTime.parse(event.plannedStartTime()).format(formatter);
        } catch (Exception e) { timeStr = event.plannedStartTime(); }

        String msg = String.format("БРОНЬ: Велосипед %s зарезервирован на %s. Клиент: %s",
                shortId(event.bicycleId()), timeStr, shortId(event.customerId()));

        sendNotification(msg, ch, tag);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "notify.rental.start", durable = "true", arguments = {@Argument(name = "x-dead-letter-exchange", value = DLX)}),
            exchange = @Exchange(name = "bikerent.topic", type = "topic"),
            key = "rental.started"
    ))
    public void onRentalStarted(@Payload RentalStartedEvent event, Channel ch, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        String msg = String.format("ПОЕЗДКА НАЧАЛАСЬ: Клиент %s забрал велосипед %s",
                shortId(event.customerId()), shortId(event.bicycleId()));

        sendNotification(msg, ch, tag);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "notify.financial", durable = "true", arguments = {@Argument(name = "x-dead-letter-exchange", value = DLX)}),
            exchange = @Exchange(name = "bikerent.financial", type = "fanout")
    ))
    public void onRentalEnded(@Payload RentalEndedEvent event, Channel ch, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        String msg = String.format("ОПЛАТА ПОЛУЧЕНА: %.2f %s. Велосипед %s возвращен.",
                event.finalPrice(), event.currency(), shortId(event.bicycleId()));

        sendNotification(msg, ch, tag);
    }

    private void sendNotification(String message, Channel channel, long tag) throws IOException {
        try {
            notificationHandler.broadcast(message);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("Failed to send notification", e);
            channel.basicNack(tag, false, false);
        }
    }

    private String shortId(String uuid) {
        if (uuid != null && uuid.length() > 8) {
            return uuid.substring(0, 8) + "..";
        }
        return uuid;
    }
}