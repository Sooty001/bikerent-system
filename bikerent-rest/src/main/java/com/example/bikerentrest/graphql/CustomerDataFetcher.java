package com.example.bikerentrest.graphql;

import com.example.bikerentapi.dto.request.CustomerRequest;
import com.example.bikerentapi.dto.response.CustomerResponse;
import com.example.bikerentrest.services.CustomerService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;

import java.util.List;
import java.util.Map;

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
    public CustomerResponse customerById(@InputArgument Long id) {
        return customerService.findById(id);
    }

    @DgsMutation
    public CustomerResponse registerCustomer(@InputArgument("input") Map<String, String> input) {
        CustomerRequest request = new CustomerRequest(
                input.get("firstName"),
                input.get("lastName"),
                input.get("patronymic"),
                input.get("phoneNumber"),
                input.get("email")
        );
        return customerService.registerCustomer(request);
    }
}
