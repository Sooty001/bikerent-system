package com.example.bikerentcore.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);

    public static final String TOPIC_EXCHANGE = "bikerent.topic";
    public static final String FANOUT_EXCHANGE = "bikerent.financial";
    public static final String KEY_CUSTOMER_REGISTERED = "customer.registered";
    public static final String KEY_BOOKING_CREATED = "booking.created";
    public static final String KEY_RENTAL_STARTED = "rental.started";
    public static final String KEY_BICYCLE_DELETED = "bicycle.deleted";

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE, true, false);
    }

    @Bean
    public FanoutExchange financialExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE, true, false);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);

        rabbitTemplate.setMandatory(true);

        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
            } else {
                log.error("RABBITMQ ERROR: Message not delivered to broker! Reason: {}", cause);
            }
        });

        rabbitTemplate.setReturnsCallback(returned -> {
            log.warn("RABBITMQ RETURN: Message sent to exchange '{}' with key '{}' was returned. Reason: {}",
                    returned.getExchange(), returned.getRoutingKey(), returned.getReplyText());
        });

        return rabbitTemplate;
    }
}