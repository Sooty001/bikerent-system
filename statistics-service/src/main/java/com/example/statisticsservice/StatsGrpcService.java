package com.example.statisticsservice;

import com.example.grpc.stats.GetStatsRequest;
import com.example.grpc.stats.GetStatsResponse;
import com.example.grpc.stats.StatisticsServiceGrpc;
import net.devh.boot.grpc.server.service.GrpcService;
import io.grpc.stub.StreamObserver;

@GrpcService
public class StatsGrpcService extends StatisticsServiceGrpc.StatisticsServiceImplBase {

    private final StatsStore statsStore;

    public StatsGrpcService(StatsStore statsStore) {
        this.statsStore = statsStore;
    }

    @Override
    public void getGeneralStats(GetStatsRequest request, StreamObserver<GetStatsResponse> responseObserver) {
        GetStatsResponse response = GetStatsResponse.newBuilder()
                .setTotalCustomers(statsStore.getTotalCustomers())
                .setTotalRevenue(statsStore.getTotalRevenue())
                .setTotalRentals(statsStore.getTotalRentals())
                .setAverageCheck(statsStore.getAverageCheck())
                .setTotalBookings(statsStore.getTotalBookings())
                .setActiveBicycles(statsStore.getActiveBicycles())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}