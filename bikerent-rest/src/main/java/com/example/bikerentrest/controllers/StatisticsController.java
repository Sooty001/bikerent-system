package com.example.bikerentrest.controllers;

import com.example.bikerentapi.dto.response.StatisticsResponse;
import com.example.bikerentapi.endpoints.StatisticsApi;
import com.example.bikerentrest.services.grpc.StatisticsGrpcClient;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticsController implements StatisticsApi {

    private final StatisticsGrpcClient statisticsGrpcClient;

    public StatisticsController(StatisticsGrpcClient statisticsGrpcClient) {
        this.statisticsGrpcClient = statisticsGrpcClient;
    }

    @Override
    public StatisticsResponse getGeneralStatistics() {
        return statisticsGrpcClient.getGeneralStatistics();
    }
}