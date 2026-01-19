package com.example.bikerentcore.services.grpc;

import com.example.bikerentapi.dto.response.StatisticsResponse;
import com.example.bikerentapi.exception.ExternalServiceException;
import com.example.grpc.stats.GetStatsRequest;
import com.example.grpc.stats.GetStatsResponse;
import com.example.grpc.stats.StatisticsServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StatisticsGrpcClient {

    private static final Logger log = LoggerFactory.getLogger(StatisticsGrpcClient.class);

    @GrpcClient("stats-service")
    private StatisticsServiceGrpc.StatisticsServiceBlockingStub statsClient;

    public StatisticsResponse getGeneralStatistics() {
        try {
            GetStatsRequest request = GetStatsRequest.newBuilder().build();
            GetStatsResponse response = statsClient.getGeneralStats(request);

            return new StatisticsResponse(
                    response.getTotalCustomers(),
                    response.getTotalRevenue(),
                    response.getTotalRentals(),
                    response.getAverageCheck(),
                    response.getTotalBookings(),
                    response.getActiveBicycles()
            );
        } catch (Exception e) {
            log.error("Failed to fetch statistics", e);
            throw new ExternalServiceException("Statistics Service", "Failed to retrieve statistics");
        }
    }
}