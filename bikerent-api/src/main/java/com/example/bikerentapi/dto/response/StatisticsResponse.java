package com.example.bikerentapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с общей статистикой проката")
public record StatisticsResponse(
        @Schema(description = "Общее количество зарегистрированных клиентов")
        long totalCustomers,

        @Schema(description = "Информация о последнем событии")
        String lastActivity,

        @Schema(description = "Источник данных")
        String source
) {}