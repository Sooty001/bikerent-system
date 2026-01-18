package com.example.bikerentrest.graphql;

import com.example.bikerentapi.dto.response.StatisticsResponse;
import com.example.bikerentrest.services.grpc.StatisticsGrpcClient;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;

@DgsComponent
public class StatisticsDataFetcher {

    private final StatisticsGrpcClient statisticsGrpcClient;

    public StatisticsDataFetcher(StatisticsGrpcClient statisticsGrpcClient) {
        this.statisticsGrpcClient = statisticsGrpcClient;
    }

    @DgsQuery
    public StatisticsResponse generalStatistics() {
        return statisticsGrpcClient.getGeneralStatistics();
    }
}