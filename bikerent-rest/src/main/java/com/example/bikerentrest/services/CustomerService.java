package com.example.bikerentrest.services;

import com.example.bikerentapi.dto.request.CustomerRequest;
import com.example.bikerentapi.dto.response.CustomerResponse;
import com.example.bikerentapi.exception.ResourceNotFoundException;
import com.example.bikerentrest.config.RabbitMQConfig;
import com.example.bikerentrest.entities.Customer;
import com.example.bikerentrest.repositories.CustomerRepository;
import org.example.CustomerRegisteredEvent;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final RabbitTemplate rabbitTemplate;

    public CustomerService(CustomerRepository customerRepository, ModelMapper modelMapper, RabbitTemplate rabbitTemplate) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    public CustomerResponse registerCustomer(CustomerRequest request) {
        if (customerRepository.findByPhoneNumber(request.phoneNumber()).isPresent()) {
            throw new IllegalStateException("Клиент с таким номером уже существует");
        }

        Customer customer = modelMapper.map(request, Customer.class);
        customer.setRegistrationDate(LocalDateTime.now());

        Customer saved = customerRepository.save(customer);

        CustomerRegisteredEvent event = new CustomerRegisteredEvent(
                saved.getId(),
                saved.getFirstName() + " " + saved.getLastName(),
                saved.getEmail()
        );
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_CUSTOMER_REGISTERED, event);

        return modelMapper.map(saved, CustomerResponse.class);
    }

    @Transactional(readOnly = true)
    public CustomerResponse findById(Long id) {
        return customerRepository.findById(id)
                .filter(c -> !c.isDeleted())
                .map(c -> modelMapper.map(c, CustomerResponse.class))
                .orElseThrow(() -> new ResourceNotFoundException("Клиент", id));
    }

    public Customer findEntityById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Клиент", id));
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> findAll() {
        return customerRepository.findByDeletedFalse().stream()
                .map(c -> modelMapper.map(c, CustomerResponse.class))
                .toList();
    }

    public void deleteById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Клиент", id));
        customer.setDeleted(true);
        customerRepository.save(customer);
    }
}