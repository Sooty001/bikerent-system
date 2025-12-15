package com.example.bikerentrest.config;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE_NAME = "bikerent-exchange";
    public static final String ROUTING_KEY_CUSTOMER_REGISTERED = "customer.registered";
    public static final String ROUTING_KEY_BICYCLE_DELETED = "bicycle.deleted";
    public static final String FANOUT_EXCHANGE = "rental-rating-fanout"; // Имя нового обменника

    @Bean
    public TopicExchange bikeRentExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);

        // Callback for Publisher Confirms
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                System.err.println("RABBITMQ ERROR: Message not delivered to broker! Reason: " + cause);
            }
        });

        // Callback for Returned Messages
        rabbitTemplate.setReturnsCallback(returned -> {
            System.err.println("RABBITMQ RETURN: Message returned: " + returned.getMessage());
        });

        return rabbitTemplate;
    }

    @Bean
    public FanoutExchange ratingFanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE, true, false);
    }
}