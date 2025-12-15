package com.example.bikerentapi.endpoints;

import com.example.bikerentapi.dto.response.StatisticsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "statistics", description = "API для получения статистики (через gRPC)")
@RequestMapping("/api/admin/stats")
public interface StatisticsApi {

    @Operation(summary = "Получить общую статистику из Statistics Service")
    @ApiResponse(responseCode = "200", description = "Статистика успешно получена")
    @ApiResponse(responseCode = "503", description = "Сервис статистики недоступен")
    @GetMapping
    StatisticsResponse getGeneralStatistics();
}