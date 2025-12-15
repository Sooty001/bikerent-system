package com.example.bikerentrest.controllers;

import com.example.bikerentapi.dto.response.StatisticsResponse;
import com.example.bikerentapi.endpoints.StatisticsApi;
// Импорты сгенерированных gRPC классов
import com.example.grpc.stats.GetStatsRequest;
import com.example.grpc.stats.GetStatsResponse;
import com.example.grpc.stats.StatisticsServiceGrpc;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticsController implements StatisticsApi {

    // "stats-service" — это имя из application.properties (grpc.client.stats-service.address)
    @GrpcClient("stats-service")
    private StatisticsServiceGrpc.StatisticsServiceBlockingStub statsClient;

    @Override
    public StatisticsResponse getGeneralStatistics() {
        // 1. Создаем пустой запрос (так как нам не нужны фильтры)
        GetStatsRequest request = GetStatsRequest.newBuilder().build();

        try {
            // 2. Синхронный вызов gRPC сервера
            GetStatsResponse response = statsClient.getGeneralStats(request);

            // 3. Маппинг gRPC ответа в наш REST DTO
            return new StatisticsResponse(
                    response.getTotalCustomers(),
                    response.getLastEventMessage(),
                    "gRPC (Statistics Service)"
            );

        } catch (Exception e) {
            // В случае ошибки (сервис лежит) возвращаем заглушку или кидаем ошибку
            // Можно пробросить RuntimeException, который перехватит GlobalExceptionHandler
            throw new RuntimeException("Не удалось получить статистику: " + e.getMessage());
        }
    }
}