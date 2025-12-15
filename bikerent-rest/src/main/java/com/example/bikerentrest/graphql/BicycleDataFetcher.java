package com.example.bikerentrest.graphql;

import com.example.bikerentapi.dto.response.BicycleResponse;
import com.example.bikerentapi.dto.response.PagedResponse;
import com.example.bikerentrest.services.BicycleService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;

@DgsComponent
public class BicycleDataFetcher {

    private final BicycleService bicycleService;

    public BicycleDataFetcher(BicycleService bicycleService) {
        this.bicycleService = bicycleService;
    }

    @DgsQuery
    public PagedResponse<BicycleResponse> bicycles(
            @InputArgument String status,
            @InputArgument int page,
            @InputArgument int size) {
        return bicycleService.findAll(status, page, size);
    }

    @DgsQuery
    public BicycleResponse bicycleById(@InputArgument Long id) {
        return bicycleService.findById(id);
    }
}