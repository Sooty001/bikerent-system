package com.example.bikerentrest.controllers;

import com.example.bikerentapi.dto.request.CustomerRequest;
import com.example.bikerentapi.dto.response.CustomerResponse;
import com.example.bikerentapi.endpoints.CustomerApi;
import com.example.bikerentrest.assemblers.CustomerModelAssembler;
import com.example.bikerentrest.services.CustomerService;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CustomerController implements CustomerApi {

    private final CustomerService customerService;
    private final CustomerModelAssembler customerAssembler;

    public CustomerController(CustomerService customerService, CustomerModelAssembler customerAssembler) {
        this.customerService = customerService;
        this.customerAssembler = customerAssembler;
    }

    @Override
    public CollectionModel<EntityModel<CustomerResponse>> getAllCustomers() {
        List<CustomerResponse> customers = customerService.findAll();
        return customerAssembler.toCollectionModel(customers);
    }

    @Override
    public ResponseEntity<EntityModel<CustomerResponse>> registerCustomer(@Valid CustomerRequest request) {
        CustomerResponse newCustomer = customerService.registerCustomer(request);
        EntityModel<CustomerResponse> entityModel = customerAssembler.toModel(newCustomer);
        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    @Override
    public EntityModel<CustomerResponse> getCustomerById(Long id) {
        CustomerResponse customer = customerService.findById(id);
        return customerAssembler.toModel(customer);
    }

    @Override
    public ResponseEntity<Void> deleteCustomer(Long id) {
        customerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
