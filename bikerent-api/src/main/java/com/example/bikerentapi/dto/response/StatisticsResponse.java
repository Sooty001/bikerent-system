package com.example.bikerentapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с расширенной статистикой проката")
public record StatisticsResponse(
        @Schema(description = "Общее количество зарегистрированных клиентов")
        long totalCustomers,

        @Schema(description = "Общая выручка сервиса")
        double totalRevenue,

        @Schema(description = "Общее количество завершенных аренд")
        long totalRentals,

        @Schema(description = "Средний чек одной аренды")
        double averageCheck,

        @Schema(description = "Общее количество созданных бронирований")
        long totalBookings,

        @Schema(description = "Текущее количество активных велосипедов")
        long activeBicycles
) {}