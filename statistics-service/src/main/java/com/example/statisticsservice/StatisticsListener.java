package com.example.statisticsservice;

import com.example.bikerentcontracts.events.*;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StatisticsListener {

    private static final Logger log = LoggerFactory.getLogger(StatisticsListener.class);
    private final StatsStore statsStore;

    private static final String DLX_NAME = "dlx.bikerent";

    public StatisticsListener(StatsStore statsStore) {
        this.statsStore = statsStore;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    name = "stats.customer",
                    durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = DLX_NAME),
                            @Argument(name = "x-dead-letter-routing-key", value = "error.stats.customer")
                    }
            ),
            exchange = @Exchange(name = "bikerent.topic", type = "topic"),
            key = "customer.registered"
    ))
    public void countCustomers(@Payload CustomerRegisteredEvent event, Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            statsStore.incrementCustomers();
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("Error processing customer", e);
            channel.basicNack(tag, false, false);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    name = "stats.booking",
                    durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = DLX_NAME),
                            @Argument(name = "x-dead-letter-routing-key", value = "error.stats.booking")
                    }
            ),
            exchange = @Exchange(name = "bikerent.topic", type = "topic"),
            key = "booking.created"
    ))
    public void countBookings(@Payload BookingCreatedEvent event, Channel channel,
                              @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            statsStore.incrementBookings();
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("Error processing booking", e);
            channel.basicNack(tag, false, false);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    name = "stats.bicycle",
                    durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = DLX_NAME),
                            @Argument(name = "x-dead-letter-routing-key", value = "error.stats.bicycle")
                    }
            ),
            exchange = @Exchange(name = "bikerent.topic", type = "topic"),
            key = "bicycle.deleted"
    ))
    public void countDeletedBicycles(@Payload BicycleDeletedEvent event, Channel channel,
                                     @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            statsStore.decrementBicycles();
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("Error processing bicycle", e);
            channel.basicNack(tag, false, false);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    name = "stats.financial",
                    durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = DLX_NAME),
                            @Argument(name = "x-dead-letter-routing-key", value = "error.stats.financial")
                    }
            ),
            exchange = @Exchange(name = "bikerent.financial", type = "fanout")
    ))
    public void countRevenue(@Payload RentalEndedEvent event, Channel channel,
                             @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            statsStore.recordRental(event.finalPrice());
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("Error processing revenue", e);
            channel.basicNack(tag, false, false);
        }
    }
}