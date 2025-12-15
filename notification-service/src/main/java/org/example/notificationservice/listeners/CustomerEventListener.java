//package org.example.notificationservice.listeners;
//
//import org.example.CustomerRegisteredEvent;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.amqp.rabbit.annotation.Exchange;
//import org.springframework.amqp.rabbit.annotation.Queue;
//import org.springframework.amqp.rabbit.annotation.QueueBinding;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Component;
//
//@Component
//public class CustomerEventListener {
//
//    private static final Logger log = LoggerFactory.getLogger(CustomerEventListener.class);
//
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(name = "notification.customer.registered", durable = "true"),
//            exchange = @Exchange(name = "bikerent-exchange", type = "topic"),
//            key = "customer.registered"
//    ))
//    public void handleCustomerRegisteredEvent(CustomerRegisteredEvent event) {
//        log.info("Received CustomerRegisteredEvent: {}", event);
//        log.info("Imitation of sending a welcome letter to email: {}", event.email());
//    }
//}
