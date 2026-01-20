package com.example.bikerentcore.services.Impl;

import com.example.bikerentapi.dto.request.CustomerRequest;
import com.example.bikerentapi.dto.response.CustomerResponse;
import com.example.bikerentapi.exception.customer.CustomerNotFoundException;
import com.example.bikerentapi.exception.customer.CustomerPhoneAlreadyExistsException;
import com.example.bikerentcontracts.events.CustomerRegisteredEvent;
import com.example.bikerentcore.config.RabbitMQConfig;
import com.example.bikerentcore.entities.Customer;
import com.example.bikerentcore.repositories.CustomerRepository;
import com.example.bikerentcore.services.CustomerService;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final RabbitTemplate rabbitTemplate;

    public CustomerServiceImpl(CustomerRepository customerRepository, ModelMapper modelMapper, RabbitTemplate rabbitTemplate) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    @Transactional
    public CustomerResponse registerCustomer(CustomerRequest request) {
        if (customerRepository.findByPhoneNumber(request.phoneNumber()).isPresent()) {
            throw new CustomerPhoneAlreadyExistsException(request.phoneNumber());
        }

        Customer customer = modelMapper.map(request, Customer.class);
        customer.setRegistrationDate(LocalDateTime.now());
        customer.setLoyaltyPoints(0);
        customer.setDeleted(false);

        Customer saved = customerRepository.save(customer);

        CustomerRegisteredEvent event = new CustomerRegisteredEvent(
                saved.getId().toString(),
                saved.getFirstName() + " " + saved.getLastName(),
                saved.getEmail(),
                saved.getLoyaltyPoints()
        );

        rabbitTemplate.convertAndSend(RabbitMQConfig.TOPIC_EXCHANGE, RabbitMQConfig.KEY_CUSTOMER_REGISTERED, event);

        return modelMapper.map(saved, CustomerResponse.class);
    }

    @Override
    public void updateLoyaltyPoints(UUID customerId, int pointsDelta) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        int newPoints = customer.getLoyaltyPoints() + pointsDelta;
        customer.setLoyaltyPoints(newPoints);
        customerRepository.save(customer);
    }

    @Override
    public CustomerResponse findById(UUID id) {
        return customerRepository.findById(id)
                .filter(c -> !c.isDeleted())
                .map(c -> modelMapper.map(c, CustomerResponse.class))
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @Override
    public Customer findEntityById(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @Override
    public List<CustomerResponse> findAll() {
        return customerRepository.findByDeletedFalse().stream()
                .map(c -> modelMapper.map(c, CustomerResponse.class))
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        customer.setDeleted(true);
        customerRepository.save(customer);
    }
}