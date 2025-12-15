package com.example.statisticsservice;

import com.example.grpc.stats.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class StatsGrpcService extends StatisticsServiceGrpc.StatisticsServiceImplBase {

    private final StatsStore statsStore;
    private static final Logger log = LoggerFactory.getLogger(StatsGrpcService.class);

    public StatsGrpcService(StatsStore statsStore) {
        this.statsStore = statsStore;
    }

    @Override
    public void getGeneralStats(GetStatsRequest request, StreamObserver<GetStatsResponse> responseObserver) {
        // Логика как в AnalyticsServiceImpl: создаем ответ, отправляем onNext, закрываем onCompleted
        // add comment 3
        GetStatsResponse response = GetStatsResponse.newBuilder()
                .setTotalCustomers(statsStore.getTotalCustomers())
                .setLastEventMessage(statsStore.getLastEvent())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void calculateRentalRating(CalculateRatingRequest request, StreamObserver<CalculateRatingResponse> responseObserver) {
        // Простая логика для лабы: чем дороже поездка, тем круче рейтинг
        log.info("Calculating rating for rental ID: {}", request.getRentalId());
        int score = (int) request.getTotalCost() * 10;
        String level = (score > 500) ? "GOLD" : "STANDARD";

        CalculateRatingResponse response = CalculateRatingResponse.newBuilder()
                .setRentalId(request.getRentalId())
                .setScore(score)
                .setLevel(level)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
