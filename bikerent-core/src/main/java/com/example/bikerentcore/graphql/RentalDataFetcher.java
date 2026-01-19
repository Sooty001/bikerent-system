package com.example.bikerentcore.graphql;

import com.example.bikerentapi.dto.request.RentalRequest;
import com.example.bikerentapi.dto.response.BicycleResponse;
import com.example.bikerentapi.dto.response.CustomerResponse;
import com.example.bikerentapi.dto.response.RentalResponse;
import com.example.bikerentcore.services.BicycleService;
import com.example.bikerentcore.services.CustomerService;
import com.example.bikerentcore.services.RentalService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import graphql.schema.DataFetchingEnvironment;

import java.time.LocalDateTime;
import java.util.UUID;

@DgsComponent
public class RentalDataFetcher {

    private final RentalService rentalService;
    private final CustomerService customerService;
    private final BicycleService bicycleService;

    public RentalDataFetcher(RentalService rentalService,
                             CustomerService customerService,
                             BicycleService bicycleService) {
        this.rentalService = rentalService;
        this.customerService = customerService;
        this.bicycleService = bicycleService;
    }

    @DgsQuery
    public RentalResponse rentalById(@InputArgument UUID id) {
        return rentalService.findById(id);
    }

    @DgsMutation
    public RentalResponse startWalkInRental(@InputArgument UUID customerId, @InputArgument UUID bicycleId, @InputArgument String expectedReturnTime) {
        RentalRequest request = new RentalRequest(customerId, bicycleId, LocalDateTime.parse(expectedReturnTime));
        return rentalService.startWalkInRental(request);
    }

    @DgsMutation
    public RentalResponse startRentalFromBooking(@InputArgument UUID bookingId) {
        return rentalService.startRentalFromBooking(bookingId);
    }

    @DgsMutation
    public RentalResponse completeRental(@InputArgument UUID rentalId) {
        return rentalService.completeRental(rentalId);
    }


    @DgsData(parentType = "Rental", field = "customer")
    public CustomerResponse customerForRental(DataFetchingEnvironment dfe) {
        RentalResponse rental = dfe.getSource();
        return customerService.findById(rental.getCustomerId());
    }

    @DgsData(parentType = "Rental", field = "bicycle")
    public BicycleResponse bicycleForRental(DataFetchingEnvironment dfe) {
        RentalResponse rental = dfe.getSource();
        return bicycleService.findById(rental.getBicycleId());
    }
}