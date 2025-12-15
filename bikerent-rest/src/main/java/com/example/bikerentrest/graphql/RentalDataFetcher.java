package com.example.bikerentrest.graphql;

import com.example.bikerentapi.dto.request.RentalRequest;
import com.example.bikerentapi.dto.response.BicycleResponse;
import com.example.bikerentapi.dto.response.CustomerResponse;
import com.example.bikerentapi.dto.response.RentalResponse;
import com.example.bikerentrest.services.RentalService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import graphql.schema.DataFetchingEnvironment;

import java.time.LocalDateTime;

@DgsComponent
public class RentalDataFetcher {

    private final RentalService rentalService;

    public RentalDataFetcher(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @DgsQuery
    public RentalResponse rentalById(@InputArgument Long id) {
        return rentalService.findById(id);
    }

    @DgsMutation
    public RentalResponse startWalkInRental(@InputArgument Long customerId, @InputArgument Long bicycleId, @InputArgument String expectedReturnTime) {
        RentalRequest request = new RentalRequest(customerId, bicycleId, LocalDateTime.parse(expectedReturnTime));
        return rentalService.startWalkInRental(request);
    }

    @DgsMutation
    public RentalResponse startRentalFromBooking(@InputArgument Long bookingId) {
        return rentalService.startRentalFromBooking(bookingId);
    }

    @DgsMutation
    public RentalResponse completeRental(@InputArgument Long rentalId) {
        return rentalService.completeRental(rentalId);
    }

    @DgsData(parentType = "Rental", field = "customer")
    public CustomerResponse customerForRental(DataFetchingEnvironment dfe) {
        RentalResponse rental = dfe.getSource();
        return rental.getCustomer();
    }

    @DgsData(parentType = "Rental", field = "bicycle")
    public BicycleResponse bicycleForRental(DataFetchingEnvironment dfe) {
        RentalResponse rental = dfe.getSource();
        return rental.getBicycle();
    }
}