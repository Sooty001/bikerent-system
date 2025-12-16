package com.example.bikerentrest.controllers;

import com.example.bikerentapi.dto.response.StatisticsResponse;
import com.example.bikerentapi.endpoints.StatisticsApi;
import com.example.grpc.stats.GetStatsRequest;
import com.example.grpc.stats.GetStatsResponse;
import com.example.grpc.stats.StatisticsServiceGrpc;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticsController implements StatisticsApi {

    @GrpcClient("stats-service")
    private StatisticsServiceGrpc.StatisticsServiceBlockingStub statsClient;

    @Override
    public StatisticsResponse getGeneralStatistics() {
        GetStatsRequest request = GetStatsRequest.newBuilder().build();

        try {
            GetStatsResponse response = statsClient.getGeneralStats(request);

            return new StatisticsResponse(
                    response.getTotalCustomers(),
                    response.getLastEventMessage(),
                    "gRPC (Statistics Service)"
            );

        } catch (Exception e) {
            throw new RuntimeException("Не удалось получить статистику: " + e.getMessage());
        }
    }
}