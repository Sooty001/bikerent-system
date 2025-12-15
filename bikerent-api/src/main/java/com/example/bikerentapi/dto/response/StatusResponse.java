package com.example.bikerentapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Стандартизированный ответ для статуса операции или ошибки")
public record StatusResponse(
        @Schema(description = "Статус операции (success или error)", example = "error")
        String status,

        @Schema(description = "Сообщение об ошибке, если она произошла", example = "Велосипед с ID 15 не найден")
        String message
) {}