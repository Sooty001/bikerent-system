package com.example.bikerentrest.graphql;

import com.example.bikerentapi.dto.request.CustomerRequest;
import com.example.bikerentapi.dto.response.CustomerResponse;
import com.example.bikerentrest.services.CustomerService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;

import java.util.List;
import java.util.UUID;

@DgsComponent
public class CustomerDataFetcher {

    private final CustomerService customerService;

    public CustomerDataFetcher(CustomerService customerService) {
        this.customerService = customerService;
    }

    @DgsQuery
    public List<CustomerResponse> customers() {
        return customerService.findAll();
    }

    @DgsQuery
    public CustomerResponse customerById(@InputArgument UUID id) {
        return customerService.findById(id);
    }

    @DgsMutation
    public CustomerResponse registerCustomer(@InputArgument("input") CustomerRequest request) {
        return customerService.registerCustomer(request);
    }

    @DgsMutation
    public boolean deleteCustomer(@InputArgument UUID id) {
        customerService.deleteById(id);
        return true;
    }
}